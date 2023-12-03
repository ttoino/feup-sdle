<script lang="ts">
    import type ShoppingList from "$lib/list";

    export let shoppingList: ShoppingList;
</script>

<li class="join-item">
    <a href="/{shoppingList.id}">
        <div class="flex items-center justify-between">
            <h2 class="text-xl font-bold">{shoppingList.name}</h2>
            <span class="text-sm text-gray-500"
                >{shoppingList.items.value.size} items</span
            >
        </div>
    </a>
</li>
