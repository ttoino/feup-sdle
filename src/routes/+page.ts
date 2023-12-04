import ShoppingList from "$lib/list";
import type { PageLoad } from "./$types";
import localForage from "localforage";

export const load: PageLoad = async () => {
    const shoppingLists = [];

    const keys = await localForage.keys();

    for (const key of keys) {
        const value =
            await localForage.getItem<ReturnType<ShoppingList["toJSON"]>>(key);

        shoppingLists.push(ShoppingList.fromJSON(value!));
    }

    return { shoppingLists };
};
