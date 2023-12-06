import ShoppingList, { type ShoppingListJSON } from "$lib/list";
import localforage from "$lib/localforage";
import type { PageLoad } from "./$types";
import { error } from "@sveltejs/kit";

export const load: PageLoad = async ({ params }) => {
    const listObject = await localforage.getItem<ShoppingListJSON>(params.list);

    if (!listObject) {
        // TODO: fetch from remote

        throw error(404, `List not found`);
    }

    const list = ShoppingList.fromJSON(listObject);

    return { list };
};
