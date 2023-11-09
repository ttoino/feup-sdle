export default class GSet<V> {
    private _value: Set<V>;

    constructor(value: Iterable<V> = []) {
        this._value = new Set(value);
    }

    get value(): ReadonlySet<V> {
        return this._value;
    }

    add(v: V): ReadonlySet<V> {
        return this._value.add(v);
    }

    merge(other: GSet<V>) {
        other.value.forEach((v) => this._value.add(v));

        return this.value;
    }
}
