<script lang="ts">
    import { Icon, Play } from "svelte-hero-icons";
    import ShoppingList, { type ShoppingListJSON } from "$lib/list";
    import zod from "zod";

    const PUBLIC_SERVER_URL = "http://localhost:8080/";
    const syncResponseSchema = zod.object({
        list: ShoppingList.schema(),
    });

    async function testRequests() {
        let counter_success_post = 0;
        let counter_success_get = 0;

        for (let i = 0; i < 1000; i++) {
            const listId = "list" + i;
            const list = ShoppingList.new(listId);

            const syncEndpoint = `${PUBLIC_SERVER_URL}/list/${listId}`;
            try {
                const response = await fetch(syncEndpoint, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({
                        list: list.toJSON(),
                    }),
                });

                if (response.ok) {
                    counter_success_post++;
                }
            } catch (err) {
                console.log("Error ocurred! " + err);
            }
        }
        console.log(counter_success_post + "/1000 success - POST");

        for (let i = 0; i < 1000; i++) {
            const listId = "list" + i;

            const endpoint = `${PUBLIC_SERVER_URL}/list/${listId}`;
            try {
                const response = await fetch(endpoint, {
                    method: "GET",
                    headers: {
                        Accepts: "application/json",
                    },
                });

                if (!response.ok) {
                    counter_success_get++;
                }

                const parsedResponseData = syncResponseSchema.safeParse(
                    await response.json(),
                );

                if (!parsedResponseData.success) {
                    console.log("Not well parsed! POST");
                }
            } catch (err) {
                console.log("Error ocurred! " + err);
            }
        }
        console.log(counter_success_post + "/1000 success - GET");
    }
</script>

<main>
    <button
        class="btn max-md:btn-square ml-4 mt-4 text-white"
        on:click={testRequests}><Icon src={Play} class="h-6 w-6" /></button
    >
</main>
