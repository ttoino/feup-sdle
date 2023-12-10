import { writable } from "svelte/store";

const theme = writable(localStorage?.getItem("theme") ?? "default");

addEventListener("storage", (event: StorageEvent) => {
    if (event.key === "theme") theme.set(event.newValue!);
});

export default theme;
