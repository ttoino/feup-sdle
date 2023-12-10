<script lang="ts">
    import ShoppingListComponent from "./ShoppingList.svelte";
    import type { PageData } from "./$types";
    import NewList from "./NewList.svelte";
    import localforage from "$lib/localforage";
    import ShoppingList from "$lib/list";
    import { Icon, FaceFrown } from "svelte-hero-icons";

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


<button class="btn bg-indigo-500 text-white absolute top-20 right-5" onclick="my_modal_3.showModal()">
    New Shopping List
</button>
<dialog id="my_modal_3" class="modal">
    <div class="modal-box">
        <form method="dialog">
            <button
                class="btn btn-sm btn-circle btn-ghost absolute right-2 top-2"
                >✕</button
            >
        </form>
        <NewList />
    </div>
</dialog>

<div class="mx-auto flex w-full max-w-screen-lg flex-col gap-4 self-stretch">
    {#if shoppingLists.size > 0}
        <h1 class="text-4xl font-bold">Your shopping lists</h1>
        <ul class="join join-vertical">
            {#each shoppingLists as [_, shoppingList]}
                <ShoppingListComponent {shoppingList} {deleteShoppingList} />
            {/each}
        </ul>
    {:else}        
    <Icon src={FaceFrown} class="h-12 w-12"/>
        <h1 class="text-4xl font-bold">
            You don't have any shopping lists yet
        </h1>

    {/if}
</div>
