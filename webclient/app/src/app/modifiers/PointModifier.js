const entityDefault = {
    xvalue: "",
    yvalue: "",
    id: undefined
};

const entityFields = [
    {
        name: "xvalue",
        type: "bigdecimal",
        regex: null,
        max: 1,
        notNull: true
    },
    {
        name: "yvalue",
        type: "bigdecimal",
        regex: null,
        max: 1,
        notNull: true
    }
];

const pointOverviewFields = [
    {name: "xvalue", type: "bigdecimal", regex: null},
    {name: "yvalue", type: "bigdecimal", regex: null}
];

export {
    entityDefault,
    entityFields,
    pointOverviewFields
};
