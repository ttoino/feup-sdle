import { readable } from "svelte/store";
import { v4 as uuidv4 } from "uuid";

let _id = localStorage.getItem("id");

if (!_id) {
    _id = uuidv4();
    localStorage.setItem("id", _id);
}

const id = readable(_id);

export default id;
