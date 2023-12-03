<script lang="ts">
    import "./style.css";

    import Navbar from "$lib/components/Navbar.svelte";
</script>

<div class="flex min-h-full flex-col">
    <Navbar />
    <main class="flex flex-1 flex-col items-center justify-center p-16">
        <slot />
    </main>
</div>
