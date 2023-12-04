<script lang="ts">
    import ExpandingInput from "$lib/components/ExpandingInput.svelte";
    import MvRegister from "$lib/components/MVRegister.svelte";
    import WrappingInput from "$lib/components/WrappingInput.svelte";
    import type { ShoppingListItem } from "$lib/list";
    import id from "$lib/stores/id";

    export let item: ShoppingListItem;
    export let persistList: () => unknown;

    const changeName = (name?: string) => (e: Event) => {
        item.name.assign($id!, name ?? (e.target as HTMLInputElement).value);

        persistList();
        item = item;
    };

    const changeCount = () => (e: Event) => {
        const target = e.target as HTMLInputElement;

        if (!target.checkValidity())
            return (target.valueAsNumber = item.count.value);

        item.count.inc($id!, target.valueAsNumber - item.count.value);

        persistList();
        item = item;
    };
</script>

<li class="join join-item join-horizontal items-stretch">
    <MvRegister
        register={item.name}
        let:value
        defaultValue=""
        conflictClass="textarea join-item textarea-bordered flex-1"
    >
        <WrappingInput
            class="join-item textarea-bordered flex-1"
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
    </MvRegister>
    <ExpandingInput
        class="join-item input-bordered h-auto min-w-[3rem] max-w-[50%] text-center"
        inputmode="numeric"
        pattern="-?[0-9]+"
        required
        on:change={changeCount()}
        value={item.count.value.toString()}
    />
</li>
