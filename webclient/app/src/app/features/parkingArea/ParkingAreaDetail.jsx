import React, {useMemo, useEffect} from "react";
import {useParams} from "react-router";
import {useImmer} from "use-immer";
import ParkingAreaRest from "../../services/ParkingAreaRest";
import ParkingConfigRest from "../../services/ParkingConfigRest";
import {
    entityDefault,
    entityFields
} from "../../modifiers/ParkingAreaModifier";
import {EntityDetail, addSelectLists} from "@starwit/react-starwit";

function ParkingAreaDetail() {
    const [entity, setEntity] = useImmer(entityDefault);
    const [fields, setFields] = useImmer(entityFields);
    const entityRest = useMemo(() => new ParkingAreaRest(), []);
    const parkingconfigRest = useMemo(() => new ParkingConfigRest(), []);
    const {id} = useParams();

    useEffect(() => {
        reloadSelectLists();
    }, [id]);

    function reloadSelectLists() {
        const selectLists = [];
        const functions = [
            parkingconfigRest.findAllWithoutParkingArea(id)
        ];
        Promise.all(functions).then(values => {
            selectLists.push({name: "parkingConfig", data: values[0].data});
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
                prefix="parkingArea"
            />
        </>

    );
}

export default ParkingAreaDetail;
