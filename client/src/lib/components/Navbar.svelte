<script lang="ts">
    import online from "$lib/stores/online";
    import { Icon, Home, Signal, SignalSlash } from "svelte-hero-icons";
    import NewListModal from "./NewListModal.svelte";
    import ThemePicker from "./ThemePicker.svelte";
</script>

<nav class="navbar sticky top-0 z-50 gap-2 bg-base-300 md:gap-4">
    <a href="/" class="btn btn-square btn-ghost">
        <Icon src={Home} class="h-6 w-6" />
    </a>
    <div class="flex-1">
        <h1 class="text-xl">
            <span class="sr-only md:not-sr-only">
                Smart Distributed List Engine
            </span>
            <span class="md:hidden" aria-hidden="true">SDLE</span>
        </h1>
    </div>
    <ThemePicker />
    <button class="btn btn-ghost max-md:btn-square">
        <span class="sr-only md:not-sr-only"
            >{$online ? "Online" : "Offline"}</span
        >
        <Icon src={$online ? Signal : SignalSlash} class="h-6 w-6 md:hidden" />
    </button>
    <NewListModal />
</nav>
