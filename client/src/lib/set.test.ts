import "./set";
import HashSet from "./set";
import { describe, expect, it } from "vitest";

describe("Set.isSubset", () => {
    it("should return true if a set is a subset of another set", () => {
        expect(new HashSet([1, 2]).isSubsetOf(new HashSet([1, 2, 3]))).toBe(
            true,
        );
    });

    it("should return false if a set is not a subset of another set", () => {
        expect(new HashSet([1, 2]).isSubsetOf(new HashSet([1, 3]))).toBe(false);
    });
});

describe("Set.isSuperset", () => {
    it("should return true if a set is a superset of another set", () => {
        expect(new HashSet([1, 2, 3]).isSupersetOf(new HashSet([1, 2]))).toBe(
            true,
        );
    });

    it("should return false if a set is not a superset of another set", () => {
        expect(new HashSet([1, 3]).isSupersetOf(new HashSet([1, 2]))).toBe(
            false,
        );
    });
});

describe("Set.union", () => {
    it("should return a set containing all elements of both sets", () => {
        expect(new HashSet([1, 2]).union(new HashSet([2, 3]))).toEqual(
            new HashSet([1, 2, 3]),
        );
    });
});

describe("Set.intersection", () => {
    it("should return a set containing all elements that are in both sets", () => {
        expect(new HashSet([1, 2]).intersection(new HashSet([2, 3]))).toEqual(
            new HashSet([2]),
        );
    });

    it.skip("should return a set containing all elements that are in both sets when elements are reference values", () => {
        expect(
            new HashSet([
                [1, 2],
                [3, 4],
            ]).intersection(new HashSet([[1, 2]])),
        ).toEqual(new HashSet([[1, 2]]));
    });
});

describe("Set.difference", () => {
    it("should return a set containing all elements that are in the first set but not the second", () => {
        expect(new HashSet([1, 2]).difference(new HashSet([2, 3]))).toEqual(
            new HashSet([1]),
        );
    });
});

describe("Set.symmetricDifference", () => {
    it("should return a set containing all elements that are in either set but not both", () => {
        expect(
            new HashSet([1, 2]).symmetricDifference(new HashSet([2, 3])),
        ).toEqual(new HashSet([1, 3]));
    });
});
