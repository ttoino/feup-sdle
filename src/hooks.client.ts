import localforage from "localforage";

localforage.config({
    name: "shopping-list",
    storeName: "lists",
    version: 1,
    description: "Shopping List App"
});

console.log("localforage configured");