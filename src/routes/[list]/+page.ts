import {deserialize} from "$lib/list";
import type { PageLoad } from "./$types";

import localForage from 'localforage';

import { error } from '@sveltejs/kit';

export const load: PageLoad = async ({ params }) => {

    if (!params.list) {
        throw error(400, "Missing list name");
    }

    const listId = params.list;

    if (!await localForage.getItem(listId)) {
        throw error(404, `List with id "${listId}" not found`);
    }

    const list = await localForage.getItem<string>(listId).then((val) => deserialize(val!));

    return { list };
};
