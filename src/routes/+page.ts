import { deserialize } from "$lib/list";
import type { PageLoad } from "./$types";
import localForage from "localforage";

export const load: PageLoad = async () => {
    const shoppingLists = [];

    const keys = await localForage.keys();

    for (const key of keys) {
        const value = (await localForage.getItem(key)) as string;

        shoppingLists.push(deserialize(value));
    }

    return { shoppingLists };
};
