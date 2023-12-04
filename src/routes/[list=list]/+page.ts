import ShoppingList from "$lib/list";
import type { PageLoad } from "./$types";
import { error } from "@sveltejs/kit";
import localForage from "localforage";

export const load: PageLoad = async ({ params }) => {
    if (!params.list) {
        throw error(400, "Missing list name");
    }

    const listId = params.list;

    if (!(await localForage.getItem(listId))) {
        // TODO: fetch from remote

        throw error(404, `List with id "${listId}" not found`);
    }

    const list = await localForage
        .getItem<ReturnType<ShoppingList["toJSON"]>>(listId)
        .then((val) => ShoppingList.fromJSON(val!));

    return { list };
};
