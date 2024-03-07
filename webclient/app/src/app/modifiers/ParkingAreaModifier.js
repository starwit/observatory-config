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
    },
    {
        name: "geoReferenced",
        type: "boolean",
        regex: null,
        notNull: false
    },
    {
        name: "centerlatitude",
        type: "bigdecimal",
        regex: null,
        min: -90,
        max: 90,
        notNull: false
    },
    {
        name: "centerlongitude",
        type: "bigdecimal",
        regex: null,
        min: -180,
        max: 180,
        notNull: false
    },
    {
        name: "topleftlatitude",
        type: "bigdecimal",
        regex: null,
        min: -90,
        max: 90,
        notNull: false
    },
    {
        name: "topleftlongitude",
        type: "bigdecimal",
        regex: null,
        min: -180,
        max: 180,
        notNull: false
    },
    {
        name: "degreeperpixelx",
        type: "bigdecimal",
        regex: null,
        min: 0,
        notNull: false
    },
    {
        name: "degreeperpixely",
        type: "bigdecimal",
        regex: null,
        min: 0,
        notNull: false
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
