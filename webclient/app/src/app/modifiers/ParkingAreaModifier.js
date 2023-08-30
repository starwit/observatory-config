const entityDefault = {
    name: "",
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
        name: "parkingConfig",
        type: "OneToMany",
        regex: null,
        selectList: [],
        display: [
            "name"
        ],
        selectedIds: []
    },
    {
        name: "selectedTestConfig",
        type: "OneToOne",
        regex: null,
        selectList: [],
        display: [
            "name"
        ],
        selectedIds: []
    },
    {
        name: "selectedProdConfig",
        type: "OneToOne",
        regex: null,
        selectList: [],
        display: [
            "name"
        ],
        selectedIds: []
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
