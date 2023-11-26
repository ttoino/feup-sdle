import { v1 as uuidv1 } from "uuid";
import AWMap from "./crdt/awmap";
import CCounter from "./crdt/ccounter";
import DotsContext from "./crdt/dotscontext";
import MVRegister from "./crdt/mvregister";

export class ShoppingListItem extends AWMap<
    "name" | "count",
    MVRegister<string> | CCounter
> {
    static new(id: string, name: string, dots: DotsContext) {
        const nameRegister = new MVRegister<string>([], dots);
        nameRegister.assign(id, name);

        const count = new CCounter([], dots);

        const item = new ShoppingListItem([], [], dots);

        item.set(id, "name", nameRegister);
        item.set(id, "count", count);

        return item;
    }

    get name() {
        return this.get("name") as MVRegister<string>;
    }

    get count() {
        return this.get("count") as CCounter;
    }

    get(key: "name"): MVRegister<string>;
    get(key: "count"): CCounter;
    override get(key: "name" | "count") {
        return super.get(key);
    }

    set(
        id: string,
        key: "name",
        value: MVRegister<string>,
    ): ReadonlyMap<"name" | "count", MVRegister<string> | CCounter>;
    set(
        id: string,
        key: "count",
        value: CCounter,
    ): ReadonlyMap<"name" | "count", MVRegister<string> | CCounter>;
    override set(
        id: string,
        key: "name" | "count",
        value: MVRegister<string> | CCounter,
    ) {
        return super.set(id, key, value);
    }

    remove(never: never): never;
    override remove(
        _key: "name" | "count",
    ): ReadonlyMap<"name" | "count", MVRegister<string> | CCounter> {
        throw new Error("Method not allowed");
    }
}

export default class ShoppingList {
    readonly id: string;
    readonly name: MVRegister<string>;
    readonly items: AWMap<string, ShoppingListItem>;
    readonly dots: DotsContext;

    constructor(
        id: string,
        name: MVRegister<string>,
        items: AWMap<string, ShoppingListItem>,
        dots: DotsContext,
    ) {
        this.id = id;
        this.name = name;
        this.items = items;
        this.dots = dots;
    }

    static new(id:string, listId: string, name: string, dots = new DotsContext()) {
        const nameRegister = new MVRegister<string>([], dots);
        nameRegister.assign(id, name);

        const items = new AWMap<string, ShoppingListItem>([], [], dots);

        return new ShoppingList(listId, nameRegister, items, dots);
    }

    newItem(id: string, name: string, itemId: string): ShoppingListItem;
    newItem(id: string, name: string): ShoppingListItem;
    newItem(id: string, name: string, itemId?: string) {
        itemId ??= uuidv1();

        const item = ShoppingListItem.new(id, name, this.dots);

        this.items.set(id, itemId, item);

        return item;
    }

    merge(other: ShoppingList) {
        this.name.merge(other.name);
        this.items.merge(other.items);

        return this;
    }
}
