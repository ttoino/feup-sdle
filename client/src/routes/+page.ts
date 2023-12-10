import type ShoppingList from "$lib/list";
import * as listService from "$lib/service/list";
import type { PageLoad } from "./$types";

export const load: PageLoad = async () => {
    const shoppingLists = new Map<string, ShoppingList>(
        ((await listService.getAll()).filter((l) => l) as ShoppingList[]).map(
            (list) => [list.id, list],
        ),
    );

    return { shoppingLists };
};
