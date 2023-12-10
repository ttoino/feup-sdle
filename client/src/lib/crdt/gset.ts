import HashSet from "$lib/set";

export default class GSet<V> {
    private _value: HashSet<V>;

    constructor(value: Iterable<V> = []) {
        this._value = new HashSet(value);
    }

    get value(): ReadonlySet<V> {
        return new Set(this._value);
    }

    add(v: V) {
        this._value.add(v);
    
        return this.value;
    }

    merge(other: GSet<V>) {
        other.value.forEach((v) => this._value.add(v));

        return this.value;
    }

    toJSON() {
        return [...this._value.values()];
    }
}
