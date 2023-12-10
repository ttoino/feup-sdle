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
        <div class="flex flex-1 flex-col text-start">
            <a
                href="/{shoppingList.id}"
                class="text-start before:absolute before:inset-0"
            >
                <MVRegister
                    register={shoppingList.name}
                    let:value
                    defaultValue="Unnamed shopping list"
                >
                    {value}
                </MVRegister>
            </a>
            <span class="text-xs opacity-50">{shoppingList.id}</span>
        </div>
        <span class="badge badge-accent gap-[1ch]">
            {shoppingList.items.value.size}
            <span class="sr-only md:not-sr-only">items</span>
        </span>
    </div>
    <button
        class="btn btn-square btn-outline btn-error join-item z-10"
        on:click|preventDefault={deleteThis}
    >
        <Icon src={XMark} class="h-6 w-6" />
    </button>
</li>
