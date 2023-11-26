<script lang="ts" generics="V">
    import type MVRegister from "$lib/crdt/mvregister";

    export let register: MVRegister<V>;

    let value: V | null = null;

    $: {
        const set = register.value;
        value = set.size > 0 ? set.values().next().value as V : null;

        console.log("MVRegister", register, value)
    }
</script>

{#if value !== null}
    <slot {value} />
{:else}
    Conflict!
    {#each register.value.values() as value}
        <slot {value} />
    {/each}
{/if}
