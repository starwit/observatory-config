import axios from "axios";
import {useTranslation} from "react-i18next";
import {toast} from "react-toastify";

function ErrorHandler(props) {
    const {t} = useTranslation();

    if (axios.interceptors.response.handlers.length === 0) {
        axios.interceptors.response.use(
            function (response) {
                // Any status code that lie within the range of 2xx cause this function to trigger
                // Do something with response data
                return response;
            },
            function (error) {
                let errorMessage = "error.unknown";

                if (error?.request) {
                    if (navigator.onLine) {
                        errorMessage = "error.serverOffline";
                        console.error("Server cannot be reached.");
                    } else {
                        errorMessage = "error.userOffline";
                        console.log("User seems to be offline. Cannot complete request.");
                    }
                }
                if (error?.response) {
                    const {config, data, status} = error.response;
                    switch (config.method) {
                        case "get":
                            errorMessage = "error.general.get";
                            break;
                        case "delete":
                            errorMessage = "error.general.delete";
                            break;
                        case "post":
                            errorMessage = "error.general.create";
                            break;
                        case "put":
                            errorMessage = "error.general.update";
                            break;
                        default:
                            errorMessage = "error.unknown";
                    }

                    console.error(`A ${config.method} request failed with status code ${status}:`, data, config);

                    // Requests with responseType "blob" deliver the error body as a Blob, which can only be read asynchronously
                    if (data instanceof Blob) {
                        const fallbackMessage = errorMessage;
                        data.text()
                            .then(text => toast.error(t(JSON.parse(text).messageKey ?? fallbackMessage)))
                            .catch(() => toast.error(t(fallbackMessage)));
                        return Promise.reject(error);
                    }

                    if (data?.messageKey) {
                        errorMessage = data.messageKey;
                    }
                }

                toast.error(t(errorMessage));

                return Promise.reject(error);
            }
        );
    }


    return props.children;
}

export default ErrorHandler;