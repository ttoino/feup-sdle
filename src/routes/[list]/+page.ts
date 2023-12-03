import MVRegister from "$lib/crdt/mvregister";
import type ShoppingList from "$lib/list";
import type { PageLoad } from "./$types";

import localForage from 'localforage';

import { error } from '@sveltejs/kit';

export const load: PageLoad = async ({ params }) => {

    if (!params.list) {
        throw error(400, "Missing list name");
    }

    const listId = params.list;

    if (!await localForage.getItem(listId)) {
        throw error(404, `List with id "${listId}" not found`);
    }

    const list: ShoppingList = await localForage.getItem<string>(listId).then((val) => eval(val!));

    const register = new MVRegister<string>();
    register.assign("asjdnjkas", "Name 1");
    const register2 = new MVRegister<string>();
    register2.assign("asjdnjkasasdnasd", "Name 2");

    list.name.merge(register);
    list.name.merge(register2);

    list.newItem("asdklnasd", "Item 1");
    list.newItem("asldnakjsndasd", "Item 2", "1");

    const register3 = new MVRegister<string>();
    register3.assign("askndkjasdn", "Name 1");
    const register4 = new MVRegister<string>();
    register4.assign("asndkjansdjknaksjd", "Name 2");

    list.items.get("1")?.name.merge(register3);
    list.items.get("2")?.name.merge(register4);

    return { list };
};
