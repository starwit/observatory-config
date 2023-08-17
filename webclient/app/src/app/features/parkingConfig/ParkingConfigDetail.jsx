import React, {useMemo, useEffect} from "react";
import {useParams} from "react-router-dom";
import {useImmer} from "use-immer";
import ParkingConfigRest from "../../services/ParkingConfigRest";
import ImageRest from "../../services/ImageRest";
import {
    entityDefault,
    entityFields
} from "../../modifiers/ParkingConfigModifier";
import {EntityDetail, addSelectLists} from "@starwit/react-starwit";

function ParkingConfigDetail() {
    const [entity, setEntity] = useImmer(entityDefault);
    const [fields, setFields] = useImmer(entityFields);
    const entityRest = useMemo(() => new ParkingConfigRest(), []);
    const imageRest = useMemo(() => new ImageRest(), []);
    const {id} = useParams();

    useEffect(() => {
        reloadSelectLists();
    }, [id]);

    function reloadSelectLists() {
        const selectLists = [];
        const functions = [
            imageRest.findAllWithoutParkingConfig(id),
        ];
        Promise.all(functions).then(values => {
            selectLists.push({name: "image", data: values[0].data});
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
                prefix="parkingConfig"
            />
        </>

    );
}

export default ParkingConfigDetail;
