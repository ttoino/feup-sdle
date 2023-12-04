<script lang="ts">
    import type ShoppingList from "$lib/list";
    import MVRegister from "../MVRegister.svelte";

    import { Icon, XMark } from "svelte-hero-icons";

    import { goto } from "$app/navigation";
    import localforage from "localforage";

    import { addNotification } from "$lib/stores/notifications";

    export let shoppingList: ShoppingList;

    async function deleteShoppingList() {
        console.log("Deleting shopping list", shoppingList.id);

        try {
            await localforage.removeItem(shoppingList.id);
            addNotification(
                `Deleted ${shoppingList.name.value.values().next().value}`,
                {
                    type: "success",
                },
            );
        } catch (error) {
            console.error("Error deleting shopping list", error);
            addNotification("Error deleting shopping list", {
                type: "error",
            });
        }

        goto("/");
    }
</script>

<li class="btn btn-outline join-item relative gap-4">
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
    <button
        class="btn btn-square btn-outline btn-error btn-sm z-40"
        on:click|preventDefault={deleteShoppingList}
    >
        <Icon src={XMark} class="h-6 w-6" />
    </button>
</li>
