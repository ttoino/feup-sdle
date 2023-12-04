<script lang="ts">
    // taken from https://svelte.dev/repl/0091c8b604b74ed88bb7b6d174504f50?version=3.35.0

    import { createEventDispatcher } from "svelte";
    import { fade } from "svelte/transition";
    import SuccessIcon from "./SuccessIcon.svelte";
    import ErrorIcon from "./ErrorIcon.svelte";
    import InfoIcon from "./InfoIcon.svelte";
    import CloseIcon from "./CloseIcon.svelte";
    import type { NotificationType } from "$lib/stores/notifications";

    const dispatch = createEventDispatcher();

    export let type: NotificationType = "success";
    export let dismissible = true;
</script>

<article
    class="alert max-w-[32em]"
    class:alert-success={type === "success"}
    class:alert-warning={type === "warning"}
    class:alert-error={type === "error"}
    class:alert-info={type === "info"}
    role="alert"
    transition:fade
>
    {#if type === "success"}
        <SuccessIcon width="1.1em" />
    {:else if type === "error"}
        <ErrorIcon width="1.1em" />
    {:else if type === "info"}
        <InfoIcon width="1.1em" />
    {/if}

    <div class="">
        <slot />
    </div>

    {#if dismissible}
        <button on:click={() => dispatch("dismiss")}>
            <CloseIcon width="0.8em" />
        </button>
    {/if}
</article>
