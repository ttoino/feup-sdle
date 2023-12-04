<script lang="ts">
    import ExpandingInput from "$lib/components/ExpandingInput.svelte";
    import MvRegister from "$lib/components/MVRegister.svelte";
    import WrappingInput from "$lib/components/WrappingInput.svelte";
    import type { ShoppingListItem } from "$lib/list";
    import id from "$lib/stores/id";
    import { Icon, XMark } from "svelte-hero-icons";

    export let item: ShoppingListItem;
    export let persistList: () => Promise<unknown>;
    export let deleteThis: () => unknown;

    const changeName = (name?: string) => async (e: Event) => {
        item.name.assign($id!, name ?? (e.target as HTMLInputElement).value);

        await persistList();
        item = item;
    };

    const changeCount = () => async (e: Event) => {
        const target = e.target as HTMLInputElement;

        // Using target.valueAsNumber is being funky
        const value = parseInt(target.value);

        if (!target.checkValidity())
            return (target.value = item.count.value.toString());

        item.count.inc($id!, value - item.count.value);

        await persistList();
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
    <button
        class="btn btn-square btn-outline btn-error join-item h-auto"
        on:click={deleteThis}
    >
        <Icon src={XMark} class="h-6 w-6" />
    </button>
</li>
