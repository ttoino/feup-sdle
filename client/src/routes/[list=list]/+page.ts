import * as listService from "$lib/service/list";
import type { PageLoad } from "./$types";
import { error } from "@sveltejs/kit";

export const load: PageLoad = async ({ params, fetch }) => {
    const listId = params.list;

    const list = await listService.get(listId, fetch);

    if (!list) throw error(404, "List not found");

    return { list };
};
