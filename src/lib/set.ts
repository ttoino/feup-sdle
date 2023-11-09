interface Set<T> {
    isSuperset(other: ReadonlySet<T>): boolean;
    isSubset(other: ReadonlySet<T>): boolean;
    union(other: ReadonlySet<T>): Set<T>;
    intersection(other: ReadonlySet<T>): Set<T>;
    difference(other: ReadonlySet<T>): Set<T>;
    symmetricDifference(other: ReadonlySet<T>): Set<T>;
}

Set.prototype.isSuperset = function <T>(other: ReadonlySet<T>) {
    for (const v of other) if (!this.has(v)) return false;

    return true;
};

Set.prototype.isSubset = function <T>(other: ReadonlySet<T>) {
    for (const v of this) if (!other.has(v)) return false;

    return true;
};

Set.prototype.union = function <T>(other: ReadonlySet<T>): Set<T> {
    for (const v of other) this.add(v);

    return this;
};

Set.prototype.intersection = function <T>(other: ReadonlySet<T>): Set<T> {
    for (const v of this) if (!other.has(v)) this.delete(v);

    return this;
};

Set.prototype.difference = function <T>(other: ReadonlySet<T>): Set<T> {
    for (const v of other) this.delete(v);

    return this;
};

Set.prototype.symmetricDifference = function <T>(
    other: ReadonlySet<T>,
): Set<T> {
    for (const v of other)
        if (this.has(v)) this.delete(v);
        else this.add(v);

    return this;
};
