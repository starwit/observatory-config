import i18n from "i18next";
import {initReactI18next} from "react-i18next";
import LanguageDetector from "i18next-browser-languagedetector";
import translationDeDE from "./translation-de-DE";
import translationEnEN from "./translation-en-EN";
import visualizerEnEN from "../visualizer/localization/translation-en-EN";
import visualizerDeDE from "../visualizer/localization/translation-de-DE";

const resources = {
    "de-DE": {translation: translationDeDE},
    "en-US": {translation: translationEnEN}
};

const lngDetectinOptions = {
    order: ["navigator", "cookie", "localStorage", "querystring", "htmlTag", "path", "subdomain"]
};

i18n
    .use(LanguageDetector)
    .use(initReactI18next)
    .init({
        resources,
        detection: lngDetectinOptions,
        fallbackLng: ["de-DE"],
        keySeparator: false,
        interpolation: {
            escapeValue: false
        }
    }, null).then(null, null);

// Merge the SAE visualizer translations into the same instance/namespace.
// deep=true, overwrite=false -> observatory-config keys win on any collision.
i18n.addResourceBundle("en-US", "translation", visualizerEnEN, true, false);
i18n.addResourceBundle("de-DE", "translation", visualizerDeDE, true, false);

export default i18n;
