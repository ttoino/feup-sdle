<script lang="ts">
    import type ShoppingList from "$lib/list";
    import "$lib/components/DeleteListModal.svelte"
    import { Icon, XMark } from "svelte-hero-icons";
    import localforage from "localforage";
    import { addNotification } from "$lib/stores/notifications";
    import MVRegister from "$lib/components/MVRegister.svelte";
    import DeleteListModal from "$lib/components/DeleteListModal.svelte";

    export let shoppingList: ShoppingList;
</script>

<li class="join join-item join-horizontal max-w-full">
    <div class="btn btn-outline join-item relative flex-1 gap-4 flex-nowrap">
        <div class="flex flex-1 flex-col text-start">
            <a
                href="/{shoppingList.id}"
                class="text-start before:absolute before:inset-0 truncate"
            >
                <MVRegister
                    register={shoppingList.name}
                    let:value
                    defaultValue="Unnamed shopping list"
                >
                    {value}
                </MVRegister>
            </a>
            <span class="text-xs opacity-50 truncate">{shoppingList.id}</span>
        </div>
        <span class="badge badge-accent gap-[1ch]">
            {shoppingList.items.value.size}
            <span class="sr-only md:not-sr-only">items</span>
        </span>
    </div>

    <DeleteListModal shoppingList={shoppingList} />
    
</li>
