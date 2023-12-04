<script lang="ts">
    import WrappingInput from "$lib/components/WrappingInput.svelte";
    import { Icon, Plus } from "svelte-hero-icons";

    export let newItem: (name: string) => unknown;

    let name = "";

    const onSubmit = (e: Event) => {
        newItem(name);
        (e.target as HTMLFormElement).reset();
    };
</script>

<form
    class="join join-horizontal items-stretch"
    on:submit|preventDefault={onSubmit}
>
    <WrappingInput
        class="join-item textarea-bordered flex-1"
        placeholder="New item"
        name="name"
        required
        bind:value={name}
    />
    <button class="btn btn-square btn-outline btn-primary join-item h-auto">
        <Icon src={Plus} class="h-6 w-6" />
    </button>
</form>
