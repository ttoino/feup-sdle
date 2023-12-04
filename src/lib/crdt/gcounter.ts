export default class GCounter {
    private _value: number;

    constructor(value = 0) {
        this._value = value;
    }

    get value() {
        return this._value;
    }

    inc(v = 1) {
        if (v < 0) throw new Error("Cannot decrement a GCounter");

        return (this._value += v);
    }

    merge(other: GCounter) {
        return (this._value = Math.max(this._value, other._value));
    }

    toJSON() {
        return this.value;
    }
}
