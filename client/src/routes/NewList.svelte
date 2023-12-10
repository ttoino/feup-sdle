<script lang="ts">
    import ShoppingList, { type ShoppingListJSON } from "$lib/list";
    import localforage from "localforage";
    import { v4 as uuidv4 } from "uuid";
    import id from "$lib/stores/id";
    import WrappingInput from "$lib/components/WrappingInput.svelte";
    import { goto } from "$app/navigation";
    import { Plus } from "svelte-hero-icons";


    const defaultName = "My new shopping list";
    let listName = "";

    const defaultId = uuidv4();
    let listId = "";

    async function createShoppingList() {
        listName = listName || defaultName;
        listId = listId || defaultId;

        const list = ShoppingList.new(listId);
        list.name.assign($id!, listName);

        try {
            await localforage.setItem<ShoppingListJSON>(listId, list.toJSON());

            goto(`/${listId}`);
        } catch (encodeURIError) {
            console.log("Error", encodeURIError);
        }
    }
</script>

<form
    class="flex-col md:flex-row md:gap-4"
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
            class="textarea-bordered invalid:textarea-error"
            placeholder={defaultId}
            pattern="[a-zA-Z0-9][a-zA-Z0-9\-_]*"
            bind:value={listId}
        />
    </label>

    <button type="submit" class="btn text-black bg-success mt-4 md:m-0 md:mt-9">
        Create
    </button>
</form>
