<script lang="ts">
    import NewList from "./NewList.svelte";

    let dialog: HTMLDialogElement;
</script>

<button class="btn btn-primary" on:click={() => dialog.showModal()}>
    New Shopping List
</button>

<dialog bind:this={dialog} class="modal">
    <div class="modal-box">
        <NewList>
            <button
                type="button"
                class="btn btn-error mr-4 mt-4"
                on:click={() => dialog.close()}
            >
                Cancel
            </button>
        </NewList>
    </div>
    <form method="dialog" class="modal-backdrop">
        <button class="cursor-default">Close</button>
    </form>
</dialog>
