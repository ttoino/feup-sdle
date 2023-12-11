import localforage from "localforage";
import "localforage-observable";

const setup = () => {
    localforage.ready(() => {
        localforage.config({
            name: "shopping-list",
            storeName: "lists",
            version: 1,
            description: "Shopping List App",
        });
        localforage.configObservables({
            crossTabNotification: true,
        });
    });
};

export default setup;
