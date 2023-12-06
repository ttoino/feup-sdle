import { browser } from "$app/environment";
import localForage from "localforage";
import { extendPrototype } from "localforage-observable";
import Observable from "zen-observable";

const localforage = extendPrototype(localForage);

if (browser) {
    // @ts-expect-error: Extending the global object
    globalThis.Observable = Observable;

    localforage.ready(() => {
        localForage.config({});
        localforage.configObservables({
            crossTabNotification: true,
        });
    });
}

export default localforage;
