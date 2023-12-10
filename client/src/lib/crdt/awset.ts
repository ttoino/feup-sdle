import HashSet from "$lib/set";
import DotsContext from "./dotscontext";
import zod from "zod";

type DottedValue<V> = [string, number, V];

export default class AWSet<V> {
    protected _value: HashSet<DottedValue<V>>;
    protected dots: DotsContext;

    static readonly schema = (valueType: zod.ZodSchema = zod.any()) =>
        zod.array(zod.tuple([zod.string(), zod.number(), valueType]));

    constructor(
        _value: Iterable<DottedValue<V>> | HashSet<DottedValue<V>> = [],
        dots = new DotsContext(),
    ) {
        this._value = _value instanceof HashSet ? _value : new HashSet(_value);
        this.dots = dots;
    }

    get value(): ReadonlySet<V> {
        return new Set([...this._value.values()].map(([, , v]) => v));
    }

    add(id: string, v: V) {
        const dot = this.dots.next(id);
        this._value.add([...dot, v]);

        return this.value;
    }

    remove(v: V) {
        this._value.forEach((e) => {
            if (e[2] === v) this._value.delete(e);
        });

        return this.value;
    }

    reset() {
        this._value.clear();

        return this.value;
    }

    private f(a: HashSet<DottedValue<V>>, b: DotsContext) {
        return new HashSet([...a.values()].filter(([id, dot]) => !b.has(id, dot)));
    }

    merge(other: AWSet<V>, mergeDots = true) {
        const a = this.f(this._value, other.dots);
        const b = this.f(other._value, this.dots);

        this._value = this._value.intersection(other._value).union(a).union(b);

        if (mergeDots) this.dots.merge(other.dots);

        return this.value;
    }

    toJSON() {
        return [...this._value.values()];
    }
}
