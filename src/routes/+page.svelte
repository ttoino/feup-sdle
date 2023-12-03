<script lang="ts">
    import { goto } from "$app/navigation";
    import ShoppingListItem from "$lib/components/home/ShoppingList.svelte";
    import ShoppingList from "$lib/list";

    import * as localForage from "localforage";

    // TODO: get from localForage
    export let shoppingLists: ShoppingList[] = [];

    async function createShoppingList(event: Event) {
        const data = new FormData(event.target as HTMLFormElement);

        const listName = data.get("name")! as string;

        const list = ShoppingList.new(listName);

        const listId = list.id;

        try {
            console.log("Serializing:", list);
            const serializedList = list.serialize();
            console.log("Serialized:", serializedList);

            await localForage.setItem(listId, serializedList);
        } catch (encodeURIError) {
            console.log("Error", encodeURIError);
        }

        shoppingLists = [...shoppingLists, list];

        goto(`/${listId}`);
    }
</script>

{#if shoppingLists.length > 0}
    <h1 class="mb-2 w-4/5 text-start text-4xl font-bold">
        Your shopping lists:
    </h1>
    <h2 class="my-2 w-4/5 text-start text-xl">Click on one to open it!</h2>
    <div class="divider mx-auto w-3/4"></div>
    <ul class="join join-vertical w-3/4 gap-5">
        {#each shoppingLists as shoppingList}
            <ShoppingListItem {shoppingList} />
        {/each}
    </ul>
{:else}
    <h1 class="mb-2 text-center text-4xl font-bold">
        You don't have any shopping lists yet.
    </h1>
    <h2 class="my-2 text-center text-2xl">Create one!</h2>

    <form
        method="POST"
        class="flex flex-col items-center gap-4"
        on:submit|preventDefault={createShoppingList}
    >
        <label class="form-control">
            <input
                type="text"
                name="name"
                id="name"
                class="input input-bordered"
                placeholder="Name"
                required
            />
        </label>
        <button type="submit" class="btn btn-outline">Create</button>
    </form>
{/if}
