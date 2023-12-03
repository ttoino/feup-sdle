<script lang="ts">
    import ShoppingListItem from "$lib/components/home/shoppingList.svelte";
    import type ShoppingList from "$lib/list";

    export let shoppingLists: ShoppingList[] = [];
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

    <form method="POST" class="flex flex-col items-center gap-4">
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
