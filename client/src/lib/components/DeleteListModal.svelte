<script lang="ts">
    import { Icon, Check, XMark } from "svelte-hero-icons";
    import NewList from "./NewList.svelte";
    import { onNavigate } from "$app/navigation";
    import localforage from "localforage";
    import { addNotification } from "$lib/stores/notifications";
    import type ShoppingList from "$lib/list";

    let dialog: HTMLDialogElement;

    export let shoppingList: ShoppingList;
    export let deleteShoppingList: (id: string) => unknown;

    const deleteThis = async () => {
        console.log("Deleting shopping list", shoppingList.id);

        try {
            await localforage.removeItem(shoppingList.id);
            deleteShoppingList(shoppingList.id);
        } catch (error) {
            console.error("Error deleting shopping list", error);
            addNotification("Error deleting shopping list", {
                type: "error",
            });
        }
    };
    onNavigate(() => dialog.close());
</script>

<button
    class="btn btn-square btn-outline btn-error join-item z-40"
    on:click={() => dialog.showModal()}
>
    <Icon src={XMark} class="h-6 w-6" />
</button>

<dialog bind:this={dialog} class="modal">
    <div class="modal-box">
        Are you sure you want to delete this list?
        <div class="block">
        <button
            type="button"
            class="btn btn mr-4 mt-4"
            on:click|preventDefault={deleteThis}>
            Yes, I am sure
        </button>
        <button
            type="button"
            class="btn btn-error mr-4 mt-4 self-end"
            on:click={() => dialog.close()}
        >
            Cancel
        </button>
    </div>
    </div>
    <form method="dialog" class="modal-backdrop">
        <button class="cursor-default">Close</button>
    </form>
</dialog>
