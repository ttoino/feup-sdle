import { browser } from "$app/environment";
import ShoppingList, { type ShoppingListJSON } from "$lib/list";
import localforage from "$lib/localforage";
import type { PageLoad } from "./$types";

export const load: PageLoad = async () => {
    const shoppingLists = new Map<string, ShoppingList>();

    if (browser) {
        const keys = await localforage.keys();

        for (const key of keys) {
            const value = await localforage.getItem<ShoppingListJSON>(key);

            shoppingLists.set(key, ShoppingList.fromJSON(value!));
        }
    }

    return { shoppingLists };
};
