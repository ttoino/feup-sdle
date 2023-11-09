import GCounter from "./gcounter";

export default class PNCounter {
    private increments: GCounter;
    private decrements: GCounter;

    constructor(value = 0) {
        this.increments = new GCounter(value);
        this.decrements = new GCounter();
    }

    get value() {
        return this.increments.value - this.decrements.value;
    }

    inc(v = 1) {
        return this.increments.inc(v) - this.decrements.value;
    }

    dec(v = 1) {
        return this.increments.value - this.decrements.inc(v);
    }

    merge(other: PNCounter) {
        this.increments.merge(other.increments);
        this.decrements.merge(other.decrements);
    }
}
