import React, {useMemo, useEffect} from "react";
import {useParams} from "react-router-dom";
import {useImmer} from "use-immer";
import ImageRest from "../../services/ImageRest";
import PolygonRest from "../../services/PolygonRest";
import {
    entityDefault,
    entityFields
} from "../../modifiers/ImageModifier";
import {EntityDetail, addSelectLists} from "@starwit/react-starwit";

function ImageDetail() {
    const [entity, setEntity] = useImmer(entityDefault);
    const [fields, setFields] = useImmer(entityFields);
    const entityRest = useMemo(() => new ImageRest(), []);
    const polygonRest = useMemo(() => new PolygonRest(), []);
    const {id} = useParams();

    useEffect(() => {
        reloadSelectLists();
    }, [id]);

    function reloadSelectLists() {
        const selectLists = [];
        const functions = [
            polygonRest.findAllWithoutImage(id),
        ];
        Promise.all(functions).then(values => {
            selectLists.push({name: "polygon", data: values[0].data});
            if (id) {
                entityRest.findById(id).then(response => {
                    setEntity(response.data);
                    addSelectLists(response.data, fields, setFields, selectLists);
                });
            } else {
                addSelectLists(entity, fields, setFields, selectLists);
            }
        });
    }

    return (
        <>
            <EntityDetail
                id={id}
                entity={entity}
                setEntity={setEntity}
                fields={fields}
                setFields={setFields}
                entityRest={entityRest}
                prefix="image"
            />
        </>

    );
}

export default ImageDetail;
