import AWSet from "./awset";
import DotsContext from "./dotscontext";

export default class MVRegister<V> {
    private set: AWSet<V>;

    constructor(
        value: ConstructorParameters<typeof AWSet<V>>[0] = [],
        dots = new DotsContext(),
    ) {
        this.set = new AWSet(value, dots);
    }

    get value(): ReadonlySet<V> {
        return this.set.value;
    }

    assign(id: string, v: V) {
        this.set.reset();

        return this.set.add(id, v);
    }

    merge(other: MVRegister<V>, mergeDots = true) {
        this.set.merge(other.set, mergeDots);

        return this.value;
    }

    toJSON() {
        return this.set.toJSON();
    }
}
