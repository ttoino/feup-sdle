import AWSet from "./awset";
import type CCounter from "./ccounter";
import DotsContext from "./dotscontext";
import type MVRegister from "./mvregister";

type DotsCRDT =
    | AWMap<unknown, DotsCRDT>
    | AWSet<unknown>
    | CCounter
    | MVRegister<unknown>;

export default class AWMap<K, V extends DotsCRDT> {
    private _set: AWSet<K>;
    private map: Map<K, V>;
    private _dots: DotsContext;

    constructor(
        value: ConstructorParameters<typeof AWSet<K>>[0] = [],
        map: ConstructorParameters<typeof Map<K, V>>[0] = [],
        dots = new DotsContext(),
    ) {
        this._set = new AWSet(value, dots);
        this.map = new Map(map);
        this._dots = dots;
    }

    get value(): ReadonlyMap<K, V> {
        return this.map;
    }

    get dots() {
        return this._dots;
    }

    get(key: K) {
        return this.map.get(key);
    }

    set(id: string, key: K, value: V) {
        if (!this._set.value.has(key)) this._set.add(id, key);

        if (!this.map.has(key)) this.map.set(key, value);
        // @ts-expect-error: Typescript isn't smart enough to know the type of merge
        else this.map.get(key).merge(value);

        return this.value;
    }

    remove(key: K) {
        this._set.remove(key);
        this.map.delete(key);
        return this.value;
    }

    merge(other: AWMap<K, V>, mergeDots = true) {
        this._set.merge(other._set, false);

        for (const key of this.map.keys())
            if (!this._set.value.has(key)) this.map.delete(key);

        for (const [key, value] of other.map)
            if (this._set.value.has(key))
                // @ts-expect-error: Typescript isn't smart enough to know the type of merge
                this.map.get(key)?.merge(value, false);

        if (mergeDots) this._dots.merge(other._dots);

        return this.value;
    }
}