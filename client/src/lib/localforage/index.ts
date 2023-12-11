import { browser } from "$app/environment";
import setup from "./setup";
import localForage from "localforage";
import { extendPrototype } from "localforage-observable";
import Observable from "zen-observable";

const localforage = extendPrototype(localForage);

if (browser) {
    // @ts-expect-error: Extending the global object
    globalThis.Observable = Observable;

    setup();
}

export default localforage;
