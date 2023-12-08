import AWMap from "./crdt/awmap";
import CCounter from "./crdt/ccounter";
import DotsContext from "./crdt/dotscontext";
import MVRegister from "./crdt/mvregister";
import { v1 as uuidv1 } from "uuid";
import zod from "zod";
export class ShoppingListItem {
    readonly name: MVRegister<string>;
    readonly count: CCounter;

    static schema = zod.object({
        name: MVRegister.schema,
        count: CCounter.schema,
    });

    constructor(name: MVRegister<string>, count: CCounter) {
        this.name = name;
        this.count = count;
    }

    merge(other: ShoppingListItem, mergeDots = true) {
        this.name.merge(other.name, false);
        this.count.merge(other.count, false);

        if (mergeDots) {
            this.name.merge(other.name, true);
            this.count.merge(other.count, true);
        }

        return this;
    }

    toJSON() {
        return {
            name: this.name.toJSON(),
            count: this.count.toJSON(),
        };
    }

    static new(id: string, name: string, dots: DotsContext) {
        const nameRegister = new MVRegister<string>([], dots);
        nameRegister.assign(id, name);

        const count = new CCounter([], dots);

        const item = new ShoppingListItem(nameRegister, count);

        return item;
    }
}

export default class ShoppingList {
    readonly id: string;
    readonly name: MVRegister<string>;
    readonly items: AWMap<string, ShoppingListItem>;
    readonly dots: DotsContext;

    static schema = zod.object({
        id: zod.string(),
        name: MVRegister.schema,
        items: AWMap.schema,
        dots: DotsContext.schema,
    });

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

    static new(listId: string, dots = new DotsContext()) {
        const nameRegister = new MVRegister<string>([], dots);
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
        this.name.merge(other.name, false);
        this.items.merge(other.items, false);
        this.dots.merge(other.dots);

        return this;
    }

    toJSON() {
        return {
            id: this.id,
            name: this.name.toJSON(),
            items: this.items.toJSON(),
            dots: this.dots.toJSON(),
        };
    }

    static fromJSON(json: ReturnType<ShoppingList["toJSON"]>) {
        const dots = new DotsContext(json.dots);

        const name = new MVRegister<string>(json.name, dots);

        const items = new AWMap<string, ShoppingListItem>(
            json.items.keys,
            json.items.map.map(
                ([key, value]) =>
                    [
                        key,
                        new ShoppingListItem(
                            new MVRegister(value.name, dots),
                            new CCounter(value.count, dots),
                        ),
                    ] as const,
            ),
            dots,
        );

        return new ShoppingList(json.id, name, items, dots);
    }
}

export type ShoppingListJSON = ReturnType<ShoppingList["toJSON"]>;
