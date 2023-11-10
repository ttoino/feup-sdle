interface Set<T> {
    /**
     * Checks if the set is a superset of another set.
     * 
     * @param other Another set
     * 
     * @returns `true` if the set is a superset of another set, `false` otherwise
     */
    isSuperset(other: ReadonlySet<T>): boolean;

    /**
     * Checks if the set is a subset of another set.
     * 
     * @param other Another set
     * 
     * @returns `true` if the set is a subset of another set, `false` otherwise
     */
    isSubset(other: ReadonlySet<T>): boolean;

    /**
     * Returns the union of the set and another set.
     * 
     * @param other Another set
     * 
     * @returns The union of the set and another set
     */
    union(other: ReadonlySet<T>): Set<T>;

    /**
     * Returns the intersection of the set and another set.
     * 
     * @param other Another set
     * 
     * @returns The intersection of the set and another set
     */
    intersection(other: ReadonlySet<T>): Set<T>;

    /**
     * Returns the difference of the set and another set.
     * 
     * @param other Another set
     * 
     * @returns The difference of the set and another set
     */
    difference(other: ReadonlySet<T>): Set<T>;

    /**
     * Returns the symmetric difference of the set and another set.
     * 
     * @param other Another set
     * 
     * @returns The symmetric difference of the set and another set
     */
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
