import type { Actions } from "./$types";

import ShoppingList from "$lib/list";

import { uneval } from 'devalue';
import * as localForage from 'localforage';

import { redirect } from '@sveltejs/kit';

// import { browser } from '$app/environment';

export const actions = {	
    default: async ({request}) => {
        const data = await request.formData();

        const listName = data.get("name")! as string;

        const list = ShoppingList.new(listName);

        const listId = list.id;
        try {
            const serializedList = uneval(list, (value) => value instanceof ShoppingList ? JSON.stringify({id: value.id, dots: value.dots, name: value.name.toJSON(), items: value.items.toJSON()}) : "false");
            
            console.log(serializedList);

            await localForage.setItem(listId, serializedList);
        } catch (encodeURIError) {
            console.error(encodeURIError);
        }

        throw redirect(302, `/${encodeURI(listId)}`);
    },
} satisfies Actions;
