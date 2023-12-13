import { browser } from "$app/environment";
import setup from "./setup";
import localForage from "localforage";
import { extendPrototype } from "localforage-observable";

const localforage = extendPrototype(localForage);

if (browser)
    setup();

export default localforage;
