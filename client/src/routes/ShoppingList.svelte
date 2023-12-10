<script lang="ts">
    import type ShoppingList from "$lib/list";
    import "$lib/components/DeleteListModal.svelte"
    import { Icon, XMark } from "svelte-hero-icons";
    import localforage from "localforage";
    import { addNotification } from "$lib/stores/notifications";
    import MVRegister from "$lib/components/MVRegister.svelte";
    import DeleteListModal from "$lib/components/DeleteListModal.svelte";

    export let shoppingList: ShoppingList;
    export let deleteShoppingList: (id: string) => unknown;
</script>

<li class="join join-item join-horizontal">
    <div class="btn btn-outline join-item relative flex-1 gap-4">
        <div class="flex flex-1 flex-col text-start">
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
        <span class="text-xs opacity-50">{shoppingList.id}</span>
    </div>
        <span class="badge badge-accent">
            {shoppingList.items.value.size} items
        </span>
    </div>

    <DeleteListModal shoppingList={shoppingList} deleteShoppingList={deleteShoppingList} />
    
</li>
