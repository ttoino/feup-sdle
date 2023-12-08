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

        const locallyStoredListData =
            await localforage.getItem<ShoppingListJSON>(responseListId);

        console.log("locallyStoredListData", locallyStoredListData);

        if (locallyStoredListData !== null) {
            const locallyStoredList = ShoppingList.fromJSON(
                locallyStoredListData,
            );

            console.log("local", locallyStoredList, "remote", responseList);

            locallyStoredList.merge(responseList);

            console.log(
                "Merged remote list into local list",
                locallyStoredListData,
            );

            // await localforage.setItem(responseListId, locallyStoredList);
        } else {
            // If the list is not already stored locally, we can just store the remote list.
            // await localforage.setItem(responseListId, responseList);
        }
    } catch (error) {
        // We only get here if the fetch() itself fails, not if the server returns an error code.
        // This is a network error, not a server error.
        // Since the application is designed to be local-first, this does not impose a problem.
        // The user can still use the application, and the data will be synced when the network is available again.

        console.error(error);
    }
};
