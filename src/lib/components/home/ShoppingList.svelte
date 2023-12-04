<script lang="ts">
    import type ShoppingList from "$lib/list";
    import MVRegister from "../MVRegister.svelte";

    import { Icon, XMark } from "svelte-hero-icons";

    export let shoppingList: ShoppingList;

    import { goto } from "$app/navigation";
    import localforage from "localforage";

    import { addNotification } from "$lib/stores/notifications";

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

<li class="btn btn-outline join-item relative justify-between">
    <a
        href="/{shoppingList.id}"
        class="before:absolute before:inset-0 before:z-10"
    >
        <MVRegister
            register={shoppingList.name}
            let:value
            defaultValue="Unnamed shopping list"
        >
            {value}
        </MVRegister>
    </a>
    <div class="flex items-center gap-2">
        <span class="badge badge-accent"
            >{shoppingList.items.value.size} items</span
        >
        <button
            class="btn btn-square btn-error btn-sm z-40"
            on:click|preventDefault={deleteShoppingList}
        >
            <Icon src={XMark} class="h-6 w-6" />
        </button>
    </div>
</li>
