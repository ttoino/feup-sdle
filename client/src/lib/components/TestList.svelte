<script lang="ts">
    import { Icon, Play } from "svelte-hero-icons";
    import ShoppingList, { type ShoppingListJSON } from "$lib/list";
    import zod from "zod";

    const PUBLIC_SERVER_URL = "http://localhost:8080/";
    const syncResponseSchema = zod.object({
        list: ShoppingList.schema(),
    });

    let counter_success_post = 0;

    async function testGetRequest() {
        for (let i = 0; i < 1000; i++) {
           const listId = 'list' + i;
           const list = ShoppingList.new(listId)

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
            } catch {
                console.log("oops");
            }
        }
        console.log(counter_success_post + "/1000 success");
    }
</script>

<main>
        <button class="btn text-white max-md:btn-square mt-4 ml-4"
        on:click={testGetRequest}><Icon src={Play} class="h-6 w-6" /></button>
</main>