<script lang="ts">
    import type ShoppingList from "$lib/list";
    import MVRegister from "../MVRegister.svelte";

    export let shoppingList: ShoppingList;
</script>

<li class="btn btn-outline join-item justify-between relative">
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
    <span class="badge badge-accent"
        >{shoppingList.items.value.size} items</span
    >
</li>
