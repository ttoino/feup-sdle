<script lang="ts" generics="V">
    /* global V */
    import type MVRegister from "$lib/crdt/mvregister";

    export let register: MVRegister<V>;
    export let defaultValue: V | undefined = undefined;

    let value: V | null = null;

    $: {
        const set = register.value;
        
        if (set.size === 0 && defaultValue !== undefined) {
            value = defaultValue;
        } else if (set.size === 1) {
            value = set.values().next().value;
        } else {
            value = null;
        }

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
