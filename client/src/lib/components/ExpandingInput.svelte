<script lang="ts">
    import { onMount } from "svelte";
    import type { HTMLInputAttributes } from "svelte/elements";

    interface $$Props extends HTMLInputAttributes {}

    let input: HTMLInputElement;

    export let value: string = "";
    $: resize(input, value);

    const resize = (input: HTMLInputElement, value?: string) => {
        if (!input) return;

        if (value) input.value = value;

        const styles = getComputedStyle(input);
        input.style.width = "0px";
        input.style.width = `calc(${input.scrollWidth}px + ${styles.borderBlockStartWidth} + ${styles.borderBlockEndWidth})`;
    };

    onMount(() => resize(input));

    const onInput = (e: Event) => {
        const input = e.target as HTMLInputElement;

        input.value = input.value.replace(/\n/g, "");
        resize(input);

        $$restProps["on:input"]?.(e);
    };
</script>

<input
    {...$$restProps}
    {value}
    class="input {$$restProps.class}"
    bind:this={input}
    on:input={onInput}
    on:change
/>
