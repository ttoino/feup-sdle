<script lang="ts">
    import ShoppingList from "./ShoppingList.svelte";
    import type { PageData } from "./$types";
    import NewList from "./NewList.svelte";

    export let data: PageData;

    const { shoppingLists } = data;
</script>

<div class="mx-auto flex w-full max-w-screen-lg flex-col gap-4 self-stretch">
    {#if shoppingLists.length > 0}
        <h1 class="text-4xl font-bold">Your shopping lists</h1>
        <ul class="join join-vertical">
            {#each shoppingLists as shoppingList}
                <ShoppingList {shoppingList} />
            {/each}
        </ul>
    {:else}
        <h1 class="text-center text-4xl font-bold">
            You don't have any shopping lists yet
        </h1>
    {/if}

    <h2 class="text-xl">New shopping list</h2>

    <NewList />
</div>
