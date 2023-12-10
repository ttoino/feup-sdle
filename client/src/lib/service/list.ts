import { PUBLIC_SERVER_URL } from "$env/static/public";
import ShoppingList, { type ShoppingListJSON } from "$lib/list";
import localforage from "$lib/localforage";
import zod from "zod";

const responseSchema = zod.object({
    list: ShoppingList.schema(),
});

/**
 * Synchronizes a list with the server.
 *
 * @param list the list to send to the server to synchronize
 * @returns
 */
export const syncRemote = async (
    list: ShoppingList,
    fetch: typeof globalThis.fetch = globalThis.fetch,
) => {
    const endpoint = `${PUBLIC_SERVER_URL}/list/${list.id}`;

    try {
        const response = await fetch(endpoint, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                list: list.toJSON(),
            }),
        });

        if (!response.ok) return;

        const parsedResponseData = responseSchema.safeParse(
            await response.json(),
        );

        if (!parsedResponseData.success) return;

        return ShoppingList.fromJSON(parsedResponseData.data.list);
    } catch (_) {
        return;
    }
};

export const syncLocal = async (list: ShoppingList) => {
    list.merge(await getLocal(list.id));

    await localforage.setItem(list.id, list.toJSON());

    return list;
};

export const sync = async (
    list: ShoppingList,
    fetch: typeof globalThis.fetch = globalThis.fetch,
) => {
    const local = await syncLocal(list);

    const remote = await syncRemote(list, fetch);

    return remote ?? local;
};

export const syncAll = async (
    lists: ShoppingList[],
    fetch: typeof globalThis.fetch = globalThis.fetch,
) => {
    const syncedLists = await Promise.all(lists.map((l) => sync(l, fetch)));

    return syncedLists;
};

export const getRemote = async (
    listId: string,
    fetch: typeof globalThis.fetch = globalThis.fetch,
) => {
    const endpoint = `${PUBLIC_SERVER_URL}/list/${listId}`;

    try {
        const response = await fetch(endpoint, {
            method: "GET",
            headers: {
                Accepts: "application/json",
            },
        });

        if (!response.ok) return;

        const parsedResponseData = responseSchema.safeParse(
            await response.json(),
        );

        if (!parsedResponseData.success) return;

        return ShoppingList.fromJSON(parsedResponseData.data.list);
    } catch (_) {
        return;
    }
};

export const getLocal = async (listId: string) => {
    const storedList = await localforage.getItem<ShoppingListJSON>(listId);

    if (storedList) return ShoppingList.fromJSON(storedList);
};

export const get = async (
    listId: string,
    fetch: typeof globalThis.fetch = globalThis.fetch,
) => {
    const local = await getLocal(listId);

    if (local) return local;

    const remote = await getRemote(listId, fetch);

    if (remote) return remote;
};

export const getAll = async () => {
    const lists = await localforage.keys();

    return Promise.all(lists.map(getLocal));
};
