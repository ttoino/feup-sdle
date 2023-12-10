<script lang="ts">
    import ShoppingList, { type ShoppingListJSON } from "$lib/list";
    import zod from "zod";

    const PUBLIC_SERVER_URL = "http://localhost:8080/";
    const syncResponseSchema = zod.object({
        list: ShoppingList.schema(),
    });

    let counter_success = 0;

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
                    counter_success++;
                }
            } catch {
                console.log("oops");
            }
        }
        console.log(counter_success + "/1000 success");
    }
</script>

<main>
        <button class="btn btn-primary text-white max-md:btn-square"
        on:click={testGetRequest}>Run Tests</button>
</main>