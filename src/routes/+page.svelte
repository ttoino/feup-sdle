<script lang="ts">
    type ShopingList = {
        id: string;
        name: string;
    };

    export let shoppingLists: ShopingList[] = [];
</script>

{#if shoppingLists.length > 0}
    <main class="flex min-h-full flex-col items-center justify-center p-16">
        <slot />
    </main>
{:else}
    <h1 class="mb-2 text-center text-4xl font-bold">
        You don't have any shopping lists yet.
    </h1>
    <h2 class="my-2 text-center text-2xl">Create one!</h2>

    <form method="POST" class="flex flex-col items-center gap-4">
        <label class="form-control">
            <input
                type="text"
                name="name"
                id="name"
                class="input input-bordered"
                placeholder="Name"
            />
        </label>
        <button type="submit" class="btn btn-outline">Create</button>
    </form>
{/if}
