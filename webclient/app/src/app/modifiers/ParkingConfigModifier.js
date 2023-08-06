const entityDefault = {
    name: "",
    version: "",
    id: undefined
};

const entityFields = [
    {
        name: "name",
        type: "string",
        regex: null,
        notNull: true
    },
    {
        name: "version",
        type: "integer",
        regex: null,
        notNull: true
    },
    {
        name: "image",
        type: "OneToMany",
        regex: null,
        selectList: [],
        display: [
            "src",
            "name"
        ],
        selectedIds: []
    },
];

const parkingConfigOverviewFields = [
    {name: "name", type: "string", regex: null},
    {name: "version", type: "integer", regex: null}
];

export {
    entityDefault,
    entityFields,
    parkingConfigOverviewFields
};
