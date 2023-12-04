<script lang="ts">
    import type ShoppingList from "$lib/list";
    import { Icon, XMark } from "svelte-hero-icons";
    import localforage from "localforage";
    import { addNotification } from "$lib/stores/notifications";
    import MVRegister from "$lib/components/MVRegister.svelte";

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
</script>

<li class="join join-item join-horizontal">
    <div class="btn btn-outline join-item relative flex-1 gap-4">
        <a
            href="/{shoppingList.id}"
            class="flex-1 text-start before:absolute before:inset-0 before:z-10"
        >
            <MVRegister
                register={shoppingList.name}
                let:value
                defaultValue="Unnamed shopping list"
            >
                {value}
            </MVRegister>
        </a>
        <span class="badge badge-accent">
            {shoppingList.items.value.size} items
        </span>
    </div>
    <button
        class="btn btn-square btn-outline btn-error join-item z-40"
        on:click|preventDefault={deleteThis}
    >
        <Icon src={XMark} class="h-6 w-6" />
    </button>
</li>
