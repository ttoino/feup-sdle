import ShoppingList from "$lib/list";
import type { PageLoad } from "./$types";

export const load: PageLoad = ({ params }) => {
    const list = ShoppingList.new(params.list);

    return { list };
};
