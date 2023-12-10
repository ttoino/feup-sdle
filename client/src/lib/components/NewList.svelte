<script lang="ts">
    import ShoppingList from "$lib/list";
    import { v4 as uuidv4 } from "uuid";
    import id from "$lib/stores/id";
    import WrappingInput from "$lib/components/WrappingInput.svelte";
    import { goto } from "$app/navigation";
    import * as listService from "$lib/service/list";

    const defaultName = "My new shopping list";
    let listName = "";

    const defaultId = uuidv4();
    let listId = "";

    async function createShoppingList() {
        listName = listName || defaultName;
        listId = listId || defaultId;

        const list = ShoppingList.new(listId);
        list.name.assign($id!, listName);

        await listService.syncLocal(list);
        await goto(`/${listId}`);
        listService.sync(list);
    }
</script>

<form
    class="flex flex-row flex-wrap justify-end"
    on:submit|preventDefault={createShoppingList}
>
    <label class="form-control w-full" for="name">
        <span class="label label-text">Name</span>

        <WrappingInput
            name="name"
            id="name"
            class="textarea-bordered"
            placeholder="My new shopping list"
            bind:value={listName}
        />
    </label>

    <label class="form-control w-full" for="id">
        <span class="label label-text">ID</span>
        <WrappingInput
            name="id"
            id="id"
            class="textarea-bordered invalid:textarea-error"
            placeholder={defaultId}
            pattern="[a-zA-Z0-9][a-zA-Z0-9\-_]*"
            bind:value={listId}
        />
    </label>

    <slot />

    <button type="submit" class="btn btn-success mt-4 self-end">Create</button>
</form>
