const entityDefault = {
    open: "",
    id: undefined
};

const entityFields = [
    {
        name: "open",
        type: "boolean",
        regex: null,
        notNull: false
    },
    {
        name: "classification",
        type: "ManyToMany",
        regex: null,
        selectList: [],
        display: [
            "name"
        ],
        selectedIds: []
    },
    {
        name: "point",
        type: "OneToMany",
        regex: null,
        selectList: [],
        display: [
            "xvalue",
            "yvalue"
        ],
        selectedIds: []
    }
];

const polygonOverviewFields = [
    {name: "open", type: "boolean", regex: null}
];

export {
    entityDefault,
    entityFields,
    polygonOverviewFields
};
