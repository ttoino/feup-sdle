<script lang="ts">
    import { Check, Icon, Swatch } from "svelte-hero-icons";
    import themesMap from "daisyui/src/theming/themes";
    import selectedTheme from "$lib/stores/theme";

    const themes = ["default", ...Object.keys(themesMap)];

    const onChange = (event: Event) => {
        const target = event.target as HTMLInputElement;
        selectedTheme.set(target.value);
        localStorage.setItem("theme", target.value);
    };
</script>

<div class="dropdown">
    <div tabindex="0" role="button" class="btn btn-ghost max-md:btn-square">
        <span class="sr-only md:not-sr-only">Theme</span>
        <Icon src={Swatch} class="h-6 w-6 md:hidden" />
    </div>
    <ul
        tabindex="0"
        class="menu dropdown-content !fixed right-2 mt-4 h-96 max-h-[calc(100vh-6rem)] flex-nowrap gap-2 overflow-y-auto rounded-box bg-base-200 p-2 shadow-xl"
    >
        {#each themes as theme}
            <li
                class="btn btn-primary btn-wide relative flex-row justify-between"
                data-theme={theme}
                class:btn-outline={theme !== $selectedTheme}
                class:bg-base-100={theme !== $selectedTheme}
            >
                <label
                    class="!bg-transparent !p-0 !text-inherit after:absolute after:inset-0"
                    for="theme-{theme}"
                >
                    {theme}
                </label>
                <input
                    type="radio"
                    name="theme"
                    id="theme-{theme}"
                    class="peer theme-controller hidden"
                    value={theme}
                    aria-label={theme}
                    checked={theme === $selectedTheme}
                    on:change={onChange}
                />
                <Icon
                    src={Check}
                    class="h-4 w-4 !bg-transparent !p-0 !text-inherit peer-[:not(:checked)]:hidden"
                />
            </li>
        {/each}
    </ul>
</div>
