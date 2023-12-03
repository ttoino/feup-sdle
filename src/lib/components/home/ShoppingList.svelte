<script lang="ts">
    import type ShoppingList from "$lib/list";
    import MVRegister from "../MVRegister.svelte";

    export let shoppingList: ShoppingList;
</script>

<li class="join-item">
    <a href="/{shoppingList.id}">
        <div class="flex items-center justify-between">
            <MVRegister
                register={shoppingList.name}
                let:value
                defaultValue="Unnamed Shopping List"
                conflictClass="input input-ghost input-lg"
            >
                {value}
            </MVRegister>
            <span class="text-sm text-gray-500"
                >{shoppingList.items.value.size} items</span
            >
        </div>
    </a>
</li>
