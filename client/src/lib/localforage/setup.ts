import localforage from "localforage";
import "localforage-observable";
import Observable from "zen-observable";

const setup = () => {
    // @ts-expect-error: Extending the global object
    globalThis.Observable = Observable;

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
