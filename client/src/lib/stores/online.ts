import { readonly, writable } from "svelte/store";

import { addNotification } from "./notifications";

const onlineWritable = writable(navigator.onLine);

addEventListener("online", () => {
    onlineWritable.set(true);

    addNotification("You are now online", {
        type: "success",
        timeout: 3000,
        dismissible: true
    })
});
addEventListener("offline", () => {
    onlineWritable.set(false);

    addNotification("You are now offline", {
        type: "warning",
        timeout: 3000,
        dismissible: true
    })
});

const online = readonly(onlineWritable);

export default online;
