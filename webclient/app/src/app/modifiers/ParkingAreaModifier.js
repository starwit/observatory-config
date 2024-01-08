const entityDefault = {
    name: "",
    id: ""
};

const entityFields = [
    {
        name: "name",
        type: "string",
        regex: null,
        notNull: true,
        max: 150
    }
];

const parkingAreaOverviewFields = [
    {name: "name", type: "string", regex: null}
];

export {
    entityDefault,
    entityFields,
    parkingAreaOverviewFields
};
