<script lang="ts">
    import "./style.css";

    import Navbar from "$lib/components/Navbar.svelte";
</script>

<Navbar />
<main class="flex min-h-full flex-col items-center justify-center p-16">
    <slot />
</main>
