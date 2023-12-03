<script lang="ts">
    import { Icon, Plus, Share } from "svelte-hero-icons";
    import id from "$lib/stores/id";
    import MVRegister from "$lib/components/mvregister.svelte";
    import type { ShoppingListItem } from "$lib/list";
    import type { PageData } from "./$types";
    import { copy } from "svelte-copy";

    export let data: PageData;

    let list = data.list;

    let name = "";

    const newItem = () => {
        list.newItem($id!, name);
        // Reassigning list to itself to trigger reactivity
        list = list;
        name = "";
    };

    const changeName = (item: ShoppingListItem) => (e: Event) => {
        item.name.assign($id!, (e.target as HTMLInputElement).value);
        list = list;
    };

    const changeCount = (item: ShoppingListItem) => (e: Event) => {
        item.count.inc(
            $id!,
            parseInt((e.target as HTMLInputElement).value) - item.count.value,
        );
        list = list;
    };
</script>

<div class="flex w-full max-w-screen-lg flex-col gap-4 p-4">
    <header class="flex justify-between pr-3">
        <MVRegister register={list.name} let:value defaultValue="List Name">
            <h1 class="text-2xl">{value}</h1>
        </MVRegister>
        <button use:copy={`/${list.id}`}>
            <Icon src={Share} class="h-6 w-6" />
        </button>
    </header>

    <ul class="join join-vertical">
        {#each list.items.value as item}
            <li class="join join-item join-horizontal">
                <MVRegister register={item[1].name} let:value defaultValue="">
                    <input
                        class="input join-item input-bordered flex-1"
                        on:change={changeName(item[1])}
                        {value}
                    />
                </MVRegister>
                <input
                    class="input join-item input-bordered aspect-square px-0 text-center"
                    inputmode="numeric"
                    on:change={changeCount(item[1])}
                    value={item[1].count.value.toString()}
                />
            </li>
        {/each}
    </ul>

    <div class="join join-horizontal">
        <input
            bind:value={name}
            class="input join-item input-bordered flex-1"
            placeholder="New item"
        />
        <button on:click={newItem} class="btn btn-square btn-outline join-item">
            <Icon src={Plus} class="h-6 w-6" />
        </button>
    </div>

    <div class="mockup-code w-full max-w-screen-lg">
        {#each JSON.stringify(list, null, 4).split("\n") as s}
            <pre><code>{s}</code></pre>
        {/each}
    </div>
</div>
