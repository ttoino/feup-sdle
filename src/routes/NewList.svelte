<script lang="ts">
    import ShoppingList from "$lib/list";
    import localforage from "localforage";
    import { v4 as uuidv4 } from "uuid";
    import id from "$lib/stores/id";
    import WrappingInput from "$lib/components/WrappingInput.svelte";
    import { goto } from "$app/navigation";

    const defaultName = "My new shopping list";
    let listName = "";

    const defaultId = uuidv4();
    let listId = "";

    async function createShoppingList() {
        const list = ShoppingList.new(listId || defaultId);
        list.name.assign($id!, listName || defaultName);

        try {
            const serializedList = list.serialize();

            await localforage.setItem(listId, serializedList);

            goto(`/${listId}`);
        } catch (encodeURIError) {
            console.log("Error", encodeURIError);
        }
    }
</script>

<form
    class="flex flex-col md:flex-row md:gap-4"
    on:submit|preventDefault={createShoppingList}
>
    <label class="form-control flex-1" for="name">
        <span class="label label-text">Name</span>

        <WrappingInput
            name="name"
            id="name"
            class="textarea-bordered"
            placeholder="My new shopping list"
            bind:value={listName}
        />
    </label>

    <label class="form-control flex-1" for="id">
        <span class="label label-text">ID</span>
        <WrappingInput
            name="id"
            id="id"
            class="textarea-bordered"
            placeholder={defaultId}
            bind:value={listId}
        />
    </label>

    <button type="submit" class="btn btn-outline mt-4 md:m-0 md:mt-9">
        Create
    </button>
</form>
