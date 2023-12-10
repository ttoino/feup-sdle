<script lang="ts">
    import { Icon, Plus } from "svelte-hero-icons";
    import NewList from "./NewList.svelte";
    import { onNavigate } from "$app/navigation";

    let dialog: HTMLDialogElement;

    onNavigate(() => dialog.close());
</script>

<button
    class="btn btn-primary max-md:btn-square"
    on:click={() => dialog.showModal()}
>
    <span class="sr-only md:not-sr-only"> New Shopping List </span>
    <Icon src={Plus} class="h-6 w-6 md:hidden" />
</button>

<dialog bind:this={dialog} class="modal">
    <div class="modal-box">
        <NewList onSubmit={() => dialog.close()}>
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
