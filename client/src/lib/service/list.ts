import { PUBLIC_BASE_URL } from "$env/static/public";
import ShoppingList, { type ShoppingListJSON } from "$lib/list";
import localforage from "$lib/localforage";
import { addNotification } from "$lib/stores/notifications";
import zod from "zod";

// TODO: Change this to the actual API URL
const API_URL = `${PUBLIC_BASE_URL}/list`;

const syncResponseSchema = zod.object({
    list: ShoppingList.schema(),
});

/**
 * Synchronizes a list with the server.
 * 
 * @param list the list to send to the server to synchronize
 * @returns 
 */
export const sync = async (list: ShoppingList) => {
    const syncEndpoint = `${API_URL}/test`;

    try {
        const response = await fetch(syncEndpoint, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                list: list.toJSON(),
            }),
        });

        if (!response.ok) {
            // TODO: handle request rejection

            addNotification("Error attempting to backup data", {
                type: "error",
                dismissible: true,
                timeout: 5000,
            });
            return;
        }

        const parsedResponseData = syncResponseSchema.safeParse(
            await response.json(),
        );

        if (!parsedResponseData.success) {
            // Malformed server response data

            addNotification(
                "Malformed response data when syncing, reach out to one of the app's maintainers",
                {
                    type: "error",
                    dismissible: true,
                    timeout: 5000,
                },
            );
            return;
        }

        const responseList: ShoppingList = ShoppingList.fromJSON(
            parsedResponseData.data.list,
        );
        const responseListId = responseList.id;

        // This should be the same as the list we sent to the server, but it doesn't hurt to be safe.
        const locallyStoredListData =
            await localforage.getItem<ShoppingListJSON>(responseListId);

        if (locallyStoredListData !== null) {
            const locallyStoredList = ShoppingList.fromJSON(
                locallyStoredListData,
            );

            // locallyStoredList.merge(responseList);

            await localforage.setItem(responseListId, locallyStoredList.toJSON());
        } else {
            // If the list is not already stored locally, we can just store the remote list.

            // Here we parse it just to serialize it again. These might be extra steps but:
            // 1. We ensure that the data is valid
            // 2. This problem only arises if we reach this branch, which should not happen (TODO: to be revised).
            await localforage.setItem(responseListId, responseList.toJSON());
        }
    } catch (error) {
        // We only get here if the fetch() itself fails, not if the server returns an error code.
        // This is a network error, not a server error.
        // Since the application is designed to be local-first, this does not impose a problem.
        // The user can still use the application, and the data will be synced when the network is available again.

        console.error(error);
    }
};
