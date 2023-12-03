import MVRegister from "$lib/crdt/mvregister";
import ShoppingList from "$lib/list";
import type { PageLoad } from "./$types";

export const load: PageLoad = ({ params }) => {
    const list = ShoppingList.new(params.list);

    const register = new MVRegister<string>();
    register.assign("asjdnjkas", "Name 1");
    const register2 = new MVRegister<string>();
    register2.assign("asjdnjkasasdnasd", "Name 2");

    list.name.merge(register);
    list.name.merge(register2);

    list.newItem("asdklnasd", "Item 1");
    list.newItem("asldnakjsndasd", "Item 2", "1");

    const register3 = new MVRegister<string>();
    register3.assign("askndkjasdn", "Name 1");
    const register4 = new MVRegister<string>();
    register4.assign("asndkjansdjknaksjd", "Name 2");

    list.items.get("1")?.name.merge(register3);
    list.items.get("2")?.name.merge(register4);

    return { list };
};
