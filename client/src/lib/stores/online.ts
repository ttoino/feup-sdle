import { readonly, writable } from "svelte/store";

const onlineWritable = writable(navigator.onLine);

addEventListener("online", () => onlineWritable.set(true));
addEventListener("offline", () => onlineWritable.set(false));

const online = readonly(onlineWritable);

export default online;
