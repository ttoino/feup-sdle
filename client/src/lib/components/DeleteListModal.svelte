<script lang="ts">
    import { Icon, XMark } from "svelte-hero-icons";
    import { onNavigate } from "$app/navigation";
    import type ShoppingList from "$lib/list";
    import * as listService from "$lib/service/list";

    let dialog: HTMLDialogElement;

    export let shoppingList: ShoppingList;

    const deleteThis = async () => {
        dialog.close();
        listService.deleteLocal(shoppingList.id);
    };

    onNavigate(() => dialog.close());
</script>

<dialog bind:this={dialog} class="modal">
    <div class="modal-box flex flex-row flex-wrap gap-4">
        <p class="w-full">Are you sure you want to delete this list?</p>

        <button type="button" class="btn" on:click|preventDefault={deleteThis}>
            Yes, I am sure
        </button>

        <button
            type="button"
            class="btn btn-error"
            on:click={() => dialog.close()}
        >
            Cancel
        </button>
    </div>
    <form method="dialog" class="modal-backdrop">
        <button class="cursor-default">Close</button>
    </form>
</dialog>

<button
    class="btn btn-square btn-outline btn-error join-item z-10"
    on:click={() => dialog.showModal()}
>
    <Icon src={XMark} class="h-6 w-6" />
</button>
