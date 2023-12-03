import type { PageLoad } from "./$types";

import localForage from 'localforage';

import { deserialize } from "$lib/list";

export const load: PageLoad = async () => {

    const lists = [];

    const keys = await localForage.keys();

    for (const key of keys) {
        const value = await localForage.getItem(key) as string;
        
        lists.push(deserialize(value));
    }

    return {shoppingLists: lists};
};
