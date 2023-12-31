<script lang="ts">
    import { Icon, Share } from "svelte-hero-icons";
    import id from "$lib/stores/id";
    import MVRegister from "$lib/components/MVRegister.svelte";
    import type { PageData } from "./$types";
    import Item from "./Item.svelte";
    import { copy } from "svelte-copy";
    import WrappingInput from "$lib/components/WrappingInput.svelte";
    import { addNotification } from "$lib/stores/notifications";
    import localforage from "$lib/localforage";
    import NewItem from "./NewItem.svelte";

    import ShoppingList from "$lib/list";

    import * as listService from "$lib/service/list";

    export let data: PageData;

    let list = data.list;
    // This changes the list when navigating
    $: list = data.list;

    let listId = list.id;
    let subscription = localforage
        .newObservable({
            key: listId,
            crossTabNotification: true,
        })
        .subscribe({
            next(value) {
                list = ShoppingList.fromJSON(value.newValue);
            },
        });

    // Change subscription when list changes
    $: {
        if (listId !== list.id) {
            listId = list.id;
            subscription.unsubscribe();
            subscription = localforage
                .newObservable({
                    key: listId,
                    crossTabNotification: true,
                })
                .subscribe({
                    next(value) {
                        list = ShoppingList.fromJSON(value.newValue);
                    },
                });
        }
    }

    const persistList = async () => listService.sync(list);

    const changeName = (name?: string) => (e: Event) => {
        list.name.assign($id!, name ?? (e.target as HTMLInputElement).value);
        persistList();
    };

    const newItem = (name: string) => {
        list.newItem($id!, name);

        addNotification(`Added ${name}`, {
            type: "success",
            dismissible: true,
            timeout: 2000,
        });

        persistList();
    };

    const deleteItem = (item: string) => () => {
        list.items.remove(item);

        persistList();
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
            use:copy={`${window.location.origin}/${list.id}`}
            on:click={() =>
                addNotification("Copied list URL to clipboard", {
                    type: "success",
                    dismissible: true,
                    timeout: 3000,
                })}
            on:svelte-copy:error={() =>
                addNotification("Error copying list URL to clipboard", {
                    type: "success",
                    dismissible: true,
                })}
        >
            <Icon src={Share} class="h-6 w-6" />
        </button>
    </header>

    {#if list.items.value.size > 0}
        <ul class="join join-vertical">
            {#each list.items.value as [id, item]}
                <Item {item} {persistList} deleteThis={deleteItem(id)} />
            {/each}
        </ul>
    {:else}
        <p class="text-center text-gray-500">No items yet. Add one below!</p>
    {/if}

    <NewItem {newItem} />

    <div class="mockup-code w-full max-w-screen-lg">
        {#each JSON.stringify(list, null, 4).split("\n") as s}
            <pre><code>{s}</code></pre>
        {/each}
    </div>
</div>
