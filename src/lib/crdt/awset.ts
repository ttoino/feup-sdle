import DotsContext from "./dotscontext";

type DottedValue<V> = [string, number, V];

export default class AWSet<V> {
    private _value: Set<DottedValue<V>>;
    private dots: DotsContext;

    constructor(
        value: Iterable<DottedValue<V>> = [],
        dots = new DotsContext(),
    ) {
        this._value = new Set(value);
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

    private f(a: Set<DottedValue<V>>, b: DotsContext) {
        return new Set(
            [...a.values()].filter(([id, dot]) => !b.contains(id, dot)),
        );
    }

    merge(other: AWSet<V>) {
        this._value
            .intersection(other._value)
            .union(this.f(other._value, this.dots))
            .union(this.f(this._value, other.dots));

        return this.value;
    }
}
