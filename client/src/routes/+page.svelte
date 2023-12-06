<script lang="ts">
    import ShoppingListComponent from "./ShoppingList.svelte";
    import type { PageData } from "./$types";
    import NewList from "./NewList.svelte";
    import localforage from "$lib/localforage";
    import ShoppingList from "$lib/list";

    export let data: PageData;

    let { shoppingLists } = data;

    localforage
        .newObservable({
            crossTabNotification: true,
            setItem: true,
            removeItem: true,
        })
        .subscribe({
            next(value) {
                if (value.methodName === "setItem") {
                    shoppingLists.set(
                        value.key,
                        ShoppingList.fromJSON(value.newValue),
                    );
                } else if (value.methodName === "removeItem") {
                    shoppingLists.delete(value.key);
                } else if (value.methodName === "clear") {
                    shoppingLists.clear();
                }

                shoppingLists = shoppingLists;
            },
        });

    const deleteShoppingList = (id: string) => {
        shoppingLists.delete(id);
        shoppingLists = shoppingLists;
    };
</script>

<div class="mx-auto flex w-full max-w-screen-lg flex-col gap-4 self-stretch">
    {#if shoppingLists.size > 0}
        <h1 class="text-4xl font-bold">Your shopping lists</h1>
        <ul class="join join-vertical">
            {#each shoppingLists as [_, shoppingList]}
                <ShoppingListComponent {shoppingList} {deleteShoppingList} />
            {/each}
        </ul>
        <div class="divider"></div>
    {:else}
        <h1 class="text-4xl font-bold">
            You don't have any shopping lists yet
        </h1>
    {/if}

    <h2 class="text-xl">New shopping list</h2>

    <NewList />
</div>
