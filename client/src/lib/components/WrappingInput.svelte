<script lang="ts">
    import type { HTMLTextareaAttributes } from "svelte/elements";
    import { onMount } from "svelte";

    interface $$Props extends HTMLTextareaAttributes {
        value?: string;
        pattern?: string;
    }

    export let value: string = "";
    export let pattern: string | undefined = undefined;

    let textarea: HTMLTextAreaElement;
    let dummyInput = document.createElement("input") as HTMLInputElement;

    const resize = (textarea: HTMLTextAreaElement) => {
        const styles = getComputedStyle(textarea);
        textarea.style.height = "0px";
        textarea.style.height = `calc(${textarea.scrollHeight}px + ${styles.borderBlockStartWidth} + ${styles.borderBlockEndWidth})`;
    };

    onMount(() => resize(textarea));

    const onInput = (e: Event) => {
        const textarea = e.target as HTMLTextAreaElement;

        textarea.value = textarea.value.replace(/\n/g, "");
        resize(textarea);

        value = textarea.value;

        if (pattern) {
            dummyInput.pattern = pattern;
            dummyInput.value = value;
            dummyInput.checkValidity();
            textarea.setCustomValidity(dummyInput.validationMessage);
        }

        $$restProps["on:input"]?.(e);
    };

    const onKeypress = (e: KeyboardEvent) => {
        if (e.key === "Enter") {
            e.preventDefault();
            e.target?.dispatchEvent(new Event("change"));
            (e.target as HTMLTextAreaElement)?.form?.requestSubmit();
        }

        $$restProps["on:keypress"]?.(e);
    };
</script>

<svelte:window on:resize={() => resize(textarea)} />

<textarea
    {...$$restProps}
    class="textarea resize-none overflow-hidden {$$restProps.class}"
    on:input={onInput}
    on:keypress={onKeypress}
    on:change
    bind:this={textarea}>{value}</textarea
>
