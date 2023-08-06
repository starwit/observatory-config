const entityDefault = {
    src: "",
    name: "",
    id: undefined
};

const entityFields = [
    {
        name: "src",
        type: "string",
        regex: null,
        notNull: false
    },
    {
        name: "name",
        type: "string",
        regex: null,
        notNull: false
    },
    {
        name: "polygon",
        type: "OneToMany",
        regex: null,
        selectList: [],
        display: [
            "open"
        ],
        selectedIds: []
    },
];

const imageOverviewFields = [
    {name: "src", type: "string", regex: null},
    {name: "name", type: "string", regex: null}
];

export {
    entityDefault,
    entityFields,
    imageOverviewFields
};
