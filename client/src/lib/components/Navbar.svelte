<script lang="ts">
    import online from "$lib/stores/online";
    import { Icon, Home, Signal, SignalSlash } from "svelte-hero-icons";
    import NewListModal from "./NewListModal.svelte";
</script>

<nav class="navbar sticky top-0 z-10 gap-4 bg-base-300">
    <a href="/" class="btn btn-square btn-ghost">
        <Icon src={Home} class="h-6 w-6" />
    </a>
    <div class="flex-1">
        <h1 class="text-xl">
            <span class="sr-only sm:not-sr-only">
                Smart Distributed List Engine
            </span>
            <span class="sm:hidden" aria-hidden="true"> SDLE </span>
        </h1>
    </div>
    <div class="tooltip tooltip-left" data-tip={$online ? "Online" : "Offline"}>
        <button class="btn btn-square btn-ghost">
            <Icon src={$online ? Signal : SignalSlash} class="h-6 w-6" />
        </button>
    </div>
    <NewListModal />
</nav>
