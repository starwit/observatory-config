import React, {useMemo, useEffect} from "react";
import {useParams} from "react-router";
import {useImmer} from "use-immer";
import PolygonRest from "../../services/PolygonRest";
import ClassificationRest from "../../services/ClassificationRest";
import PointRest from "../../services/PointRest";
import {
    entityDefault,
    entityFields
} from "../../modifiers/PolygonModifier";
import {EntityDetail, addSelectLists} from "@starwit/react-starwit";

function PolygonDetail() {
    const [entity, setEntity] = useImmer(entityDefault);
    const [fields, setFields] = useImmer(entityFields);
    const entityRest = useMemo(() => new PolygonRest(), []);
    const classificationRest = useMemo(() => new ClassificationRest(), []);
    const pointRest = useMemo(() => new PointRest(), []);
    const {id} = useParams();

    useEffect(() => {
        reloadSelectLists();
    }, [id]);

    function reloadSelectLists() {
        const selectLists = [];
        const functions = [
            classificationRest.findAll(),
            pointRest.findAllWithoutPolygon(id)
        ];
        Promise.all(functions).then(values => {
            selectLists.push({name: "classification", data: values[0].data});
            selectLists.push({name: "point", data: values[1].data});
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
                prefix="polygon"
            />
        </>

    );
}

export default PolygonDetail;
