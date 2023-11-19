import AWSet from "./awset";
import DotsContext from "./dotscontext";

type DottedValue = [string, number, number];

class PermissiveAWSet extends AWSet<number> {
    get internalValue() {
        return this._value;
    }
}

export default class CCounter {
    private set: PermissiveAWSet;

    constructor(
        value: Iterable<DottedValue> | Set<DottedValue> = [],
        dots = new DotsContext(),
    ) {
        this.set = new PermissiveAWSet(value, dots);
    }

    get value(): number {
        return [...this.set.value.values()].reduce((a, b) => a + b, 0);
    }

    inc(id: string, v = 1): number {
        let val = undefined;

        this.set.internalValue.forEach((d) => {
            if (d[0] !== id) return;

            this.set.internalValue.delete(d);
            val = d[2];
        });

        val ??= 0;
        val += v;

        this.set.add(id, val);

        return this.value;
    }

    dec(id: string, v = 1): number {
        return this.inc(id, -v);
    }

    merge(other: CCounter) {
        this.set.merge(other.set);

        return this.value;
    }

    toJSON() {
        return this.set.toJSON();
    }
}
