<script lang="ts">
    import "./style.css";

    import Navbar from "$lib/components/Navbar.svelte";
    import TestList from "$lib/components/TestList.svelte";
    import NotificationDisplay from "$lib/components/notifications/NotificationDisplay.svelte";
</script>

<div class="flex min-h-full flex-col">
    <NotificationDisplay />
    <Navbar />
    <TestList/>
    <main class="flex flex-1 flex-col items-center justify-center p-8 md:p-16">
        <slot />
    </main>
</div>
