<script lang="ts">
    import { Icon, Plus, Share } from "svelte-hero-icons";
    import id from "$lib/stores/id";
    import MVRegister from "$lib/components/MVRegister.svelte";
    import type { ShoppingListItem } from "$lib/list";
    import type { PageData } from "./$types";
    import { copy } from "svelte-copy";
    import WrappingInput from "$lib/components/WrappingInput.svelte";

    import localforage from "localforage";

    export let data: PageData;

    let list = data.list;

    let name = "";

    const changeName = (name?: string) => async (e: Event) => {
        list.name.assign($id!, name ?? (e.target as HTMLInputElement).value);
        await localforage.setItem(list.id, list.serialize());
        list = list;
    };

    const newItem = () => {
        list.newItem($id!, name);
        // Reassigning list to itself to trigger reactivity
        list = list;
        name = "";
    };

    const changeItemName =
        (item: ShoppingListItem, name?: string) => (e: Event) => {
            item.name.assign(
                $id!,
                name ?? (e.target as HTMLInputElement).value,
            );
            list = list;
        };

    const changeItemCount = (item: ShoppingListItem) => (e: Event) => {
        item.count.inc(
            $id!,
            parseInt((e.target as HTMLInputElement).value) - item.count.value,
        );
        list = list;
    };
</script>

<div class="flex w-full max-w-screen-lg flex-col gap-4 p-4">
    <header class="flex items-center justify-between gap-4">
        <h1 class="flex-1 text-2xl">
            <MVRegister
                register={list.name}
                let:value
                defaultValue=""
                conflictClass="input input-ghost input-lg"
            >
                <WrappingInput
                    class="textarea-ghost textarea-lg w-full !text-2xl"
                    placeholder="List name"
                    on:change={changeName()}
                    {value}
                />

                <svelte:fragment slot="conflictValue" let:value>
                    <button
                        class="badge badge-outline badge-lg transition-colors hover:badge-primary hover:badge-outline"
                        on:click={changeName(value)}
                    >
                        {value}
                    </button>
                </svelte:fragment>
            </MVRegister>
        </h1>
        <button
            class="btn btn-square btn-outline btn-secondary"
            use:copy={`/${list.id}`}
        >
            <Icon src={Share} class="h-6 w-6" />
        </button>
    </header>

    <ul class="join join-vertical">
        {#each list.items.value as item}
            <li class="join join-item join-horizontal items-stretch">
                <MVRegister
                    register={item[1].name}
                    let:value
                    defaultValue=""
                    conflictClass="textarea join-item textarea-bordered flex-1"
                >
                    <WrappingInput
                        class="join-item textarea-bordered flex-1"
                        on:change={changeItemName(item[1])}
                        {value}
                    />

                    <svelte:fragment slot="conflictValue" let:value>
                        <button
                            class="badge badge-outline badge-lg transition-colors hover:badge-primary hover:badge-outline"
                            on:click={changeItemName(item[1], value)}
                        >
                            {value}
                        </button>
                    </svelte:fragment>
                </MVRegister>
                <input
                    class="input join-item input-bordered h-auto min-w-[3rem] text-center"
                    inputmode="numeric"
                    on:change={changeItemCount(item[1])}
                    value={item[1].count.value.toString()}
                />
            </li>
        {/each}
    </ul>

    <div class="join join-horizontal items-stretch">
        <WrappingInput
            bind:value={name}
            class="join-item textarea-bordered flex-1"
            placeholder="New item"
        />
        <button
            on:click={newItem}
            class="btn btn-square btn-outline btn-primary join-item h-auto"
        >
            <Icon src={Plus} class="h-6 w-6" />
        </button>
    </div>

    <div class="mockup-code w-full max-w-screen-lg">
        {#each JSON.stringify(list, null, 4).split("\n") as s}
            <pre><code>{s}</code></pre>
        {/each}
    </div>
</div>
