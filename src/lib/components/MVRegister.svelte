<script lang="ts" generics="V">
    /* global V */
    import { Icon, ExclamationCircle } from "svelte-hero-icons";
    import type MVRegister from "$lib/crdt/mvregister";

    interface $$Slots {
        conflictValue: { value: V };
        default: { value: V };
    }

    export let register: MVRegister<V>;
    export let defaultValue: V | undefined = undefined;
    export let conflictClass: string = "";

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
    }
</script>

{#if value !== null}
    <slot {value} />
{:else}
    <div class="flex flex-row items-center gap-4 text-error {conflictClass}">
        <Icon class="h-6 w-6" src={ExclamationCircle} />

        <div class="flex flex-row flex-wrap gap-2">
            {#each register.value.values() as value}
                {#if $$slots.conflictValue}
                    <slot name="conflictValue" {value} />
                {:else}
                    <slot {value} />
                {/if}
            {/each}
        </div>
    </div>
{/if}
