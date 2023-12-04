<script lang="ts">
    import { goto } from "$app/navigation";
    import ShoppingListItem from "$lib/components/home/ShoppingList.svelte";
    import ShoppingList from "$lib/list";

    import * as localForage from "localforage";
    import type { PageData } from "./$types";
    import WrappingInput from "$lib/components/WrappingInput.svelte";

    import id from "$lib/stores/id";
    import { v1 as uuidV1 } from "uuid";

    export let data: PageData;

    const { shoppingLists } = data;

    async function createShoppingList(event: Event) {
        const data = new FormData(event.target as HTMLFormElement);

        const listName = data.get("name")! as string;

        // TODO: is this right?
        const list = ShoppingList.new(uuidV1());
        list.name.assign($id!, listName);

        const listId = list.id;

        try {
            const serializedList = list.serialize();

            await localForage.setItem(listId, serializedList);

            goto(`/${listId}`);
        } catch (encodeURIError) {
            console.log("Error", encodeURIError);
        }
    }
</script>

<div class="mx-auto flex w-full max-w-screen-lg flex-col gap-4 self-stretch">
    {#if shoppingLists.length > 0}
        <h1 class="text-4xl font-bold">Your shopping lists</h1>
        <h2 class="text-xl">Click on one to open it!</h2>
        <ul class="join join-vertical">
            {#each shoppingLists as shoppingList}
                <ShoppingListItem {shoppingList} />
            {/each}
        </ul>
    {:else}
        <h1 class="text-center text-4xl font-bold">
            You don't have any shopping lists yet
        </h1>
    {/if}

    <h2 class="text-center text-xl">Create one!</h2>
    <form
        method="POST"
        class="contents"
        on:submit|preventDefault={createShoppingList}
    >
        <label class="form-control">
            <WrappingInput
                name="name"
                id="name"
                class="textarea-bordered"
                placeholder="Name"
                required
            />
        </label>
        <button type="submit" class="btn btn-outline">Create</button>
    </form>
</div>
