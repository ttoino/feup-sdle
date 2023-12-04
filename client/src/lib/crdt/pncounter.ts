import GCounter from "./gcounter";

export default class PNCounter {
    private increments: GCounter;
    private decrements: GCounter;

    constructor(increments = 0, decrements = 0) {
        this.increments = new GCounter(increments);
        this.decrements = new GCounter(decrements);
    }

    get value() {
        return this.increments.value - this.decrements.value;
    }

    inc(v = 1): number {
        if (v < 0) return this.dec(-v);

        return this.increments.inc(v) - this.decrements.value;
    }

    dec(v = 1): number {
        if (v < 0) return this.inc(-v);

        return this.increments.value - this.decrements.inc(v);
    }

    merge(other: PNCounter) {
        this.increments.merge(other.increments);
        this.decrements.merge(other.decrements);

        return this.value;
    }

    toJSON() {
        return {
            increments: this.increments.value,
            decrements: this.decrements.value,
        };
    }
}
