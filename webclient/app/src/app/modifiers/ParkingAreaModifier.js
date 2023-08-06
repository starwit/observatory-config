const entityDefault = {
    name: "",
    activeConfigVersion: "",
    testConfigVersion: "",
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
        name: "activeConfigVersion",
        type: "integer",
        regex: null,
        notNull: false
    },
    {
        name: "testConfigVersion",
        type: "integer",
        regex: null,
        notNull: false
    },
    {
        name: "parkingConfig",
        type: "OneToMany",
        regex: null,
        selectList: [],
        display: [
            "name",
            "version"
        ],
        selectedIds: []
    }
];

const parkingAreaOverviewFields = [
    {name: "name", type: "string", regex: null},
    {name: "activeConfigVersion", type: "integer", regex: null},
    {name: "testConfigVersion", type: "integer", regex: null}
];

export {
    entityDefault,
    entityFields,
    parkingAreaOverviewFields
};
