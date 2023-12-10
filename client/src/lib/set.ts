export interface SetLike<T> {
    get size(): number;
    has(value: T): boolean;
    keys(): IterableIterator<T>;
}

export default class HashSet<T> implements SetLike<T>, Set<T> {
    private readonly map: Map<string, T>;
    private readonly hash: (value: T) => string;

    constructor(iterable?: Iterable<T>, hash: (value: T) => string = String) {
        this.map = new Map();
        this.hash = hash;

        if (iterable) for (const value of iterable) this.add(value);
    }

    get [Symbol.toStringTag]() {
        return "HashSet";
    }

    get size() {
        return this.map.size;
    }

    add(value: T) {
        this.map.set(this.hash(value), value);

        return this;
    }

    clear() {
        this.map.clear();
    }

    delete(value: T) {
        return this.map.delete(this.hash(value));
    }

    difference(other: SetLike<T>) {
        const result = new HashSet<T>();

        for (const value of this.keys())
            if (!other.has(value)) result.add(value);

        return result;
    }

    *entries() {
        for (const value of this.keys()) yield [value, value] as [T, T];
    }

    forEach(
        callback: (value: T, value2: T, set: HashSet<T>) => void,
        thisArg?: unknown,
    ) {
        return this.map.forEach((v, _k, _m) => callback(v, v, this), thisArg);
    }

    has(value: T) {
        return this.map.has(this.hash(value));
    }

    intersection(other: SetLike<T>) {
        const result = new HashSet<T>();

        for (const value of this.keys())
            if (other.has(value)) result.add(value);

        return result;
    }

    isDisjointFrom(other: SetLike<T>) {
        for (const value of this.keys()) if (other.has(value)) return false;

        return true;
    }

    isSubsetOf(other: SetLike<T>) {
        for (const value of this.keys()) if (!other.has(value)) return false;

        return true;
    }

    isSupersetOf(other: SetLike<T>) {
        for (const value of other.keys()) if (!this.has(value)) return false;

        return true;
    }

    keys() {
        return this.map.values();
    }

    symmetricDifference(other: SetLike<T>) {
        const result = new HashSet<T>();

        for (const value of this.keys())
            if (!other.has(value)) result.add(value);
        for (const value of other.keys())
            if (!this.has(value)) result.add(value);

        return result;
    }

    union(other: SetLike<T>) {
        const result = new HashSet<T>();

        for (const value of this.keys()) result.add(value);
        for (const value of other.keys()) result.add(value);

        return result;
    }

    values() {
        return this.map.values();
    }

    [Symbol.iterator]() {
        return this.map.values();
    }
}
