const MapStyles = {

    outerBox: theme => ({
        position: "absolute",
        height: "100%",
        zIndex: "3"
    }),
    innerBox: theme => ({
        zIndex: "3",
        position: "absolute",
        width: 500,
        marginLeft: 5,
        marginTop: 20,
        boxShadow: 2,
        padding: 0
    }),
    title: theme => ({
        margin: "auto",
        width: '300px',
        flexShrink: 0
    })
};
export default MapStyles;
