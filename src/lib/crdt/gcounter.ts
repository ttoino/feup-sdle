export default class GCounter {
    private _value: number;

    constructor(value = 0) {
        this._value = value;
    }

    get value() {
        return this._value;
    }

    inc(v = 1) {
        return (this._value += v);
    }

    merge(other: GCounter) {
        return (this._value = other._value);
    }
}
