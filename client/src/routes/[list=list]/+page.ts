import { PUBLIC_BASE_URL } from "$env/static/public";
import ShoppingList, { type ShoppingListJSON } from "$lib/list";
import localforage from "$lib/localforage";
import type { PageLoad } from "./$types";
import { error } from "@sveltejs/kit";

const API_URL = `${PUBLIC_BASE_URL}/list`;

export const load: PageLoad = async ({ params, fetch }) => {
    const listId = params.list;

    // we can just get the local replica of the shopping list since it will eventually sync up with the cloud version.
    let listObject = await localforage.getItem<ShoppingListJSON>(listId);

    const notPresent = listObject === null;

    if (notPresent) {
        let response: Response;
        try {
            // fetch from remote
            response = await fetch(`${API_URL}/${listId}`, {
                method: "GET",
                headers: {
                    Accepts: "application/json",
                },
            });
        } catch (_) {
            throw error(523, "Could not download list.");
        }

        if (!response.ok) {
            if (response.status === 404) {
                throw error(404, "List not found");
            } else {
                throw error(400, "Unknown error");
            }
        }

        // Safe to destructure because we checked for 404 status code
        const data = await response.json();
        listObject = data.list as ShoppingListJSON;
    }

    const list = ShoppingList.fromJSON(listObject!);

    if (notPresent)
        // save the list locally if it is the first time we are loading it
        await localforage.setItem(listId, list.toJSON());

    return { list };
};
