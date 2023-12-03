import type { Actions } from "./$types";

import ShoppingList from "$lib/list";

import { redirect } from "@sveltejs/kit";

export const actions = {
    default: async ({ request }) => {
        const data = await request.formData();

        const listName = data.get("name")! as string;

        const list = ShoppingList.new(listName);

        const listId = list.id;

        throw redirect(302, `/${encodeURI(listId)}`);
    },
} satisfies Actions;
