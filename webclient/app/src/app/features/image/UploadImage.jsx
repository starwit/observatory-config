import {useState, useMemo} from "react";
import ImageRest from "../../services/ImageRest";

export default function UploadImage() {
    const [result, setResult] = useState("");
    const imageRest = useMemo(() => new ImageRest(), []);

    const handleImageUpload = event => {
        event.preventDefault();
        const file = event.currentTarget["fileInput"].files[0];

        const formData = new FormData();
        formData.append("image", file);

        imageRest.test(formData)
            .then(response => {
                setResult(response.data);
            })
            .catch(error => {
                console.error(error);
            });
    };
    return (
        <div>
            <form onSubmit={handleImageUpload}>
                <input id="fileInput" type="file" />
                <input type="submit" />
            </form>
            <br />
            <br />
            Result:
            <br />
            <pre>{JSON.stringify(result, null, 2)}</pre>
        </div>
    );
}
