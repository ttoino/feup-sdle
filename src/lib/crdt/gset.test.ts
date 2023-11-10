import { describe, expect, it } from "vitest";
import GSet from "./gset";

describe("GSet", () => {
    it("should be able to add an element", () => {
        const set = new GSet();

        expect(set.value).toEqual(new Set());
        expect(set.add(1)).toEqual(new Set([1]));
    });

    it("should be able to add multiple elements", () => {
        const set = new GSet();

        expect(set.value).toEqual(new Set());
        expect(set.add(1)).toEqual(new Set([1]));
        expect(set.add(2)).toEqual(new Set([1, 2]));
        expect(set.add(3)).toEqual(new Set([1, 2, 3]));
    });

    it("should be able to add an element multiple times", () => {
        const set = new GSet();

        expect(set.value).toEqual(new Set());
        expect(set.add(1)).toEqual(new Set([1]));
        expect(set.add(1)).toEqual(new Set([1]));
        expect(set.add(1)).toEqual(new Set([1]));
    });

    it("should be able to start with a value", () => {
        const set = new GSet(new Set([1, 2, 3]));

        expect(set.value).toEqual(new Set([1, 2, 3]));
    });

    it("should be able to merge with itself", () => {
        const set = new GSet();

        set.add(1);
        set.add(2);

        expect(set.merge(set)).toEqual(new Set([1, 2]));
    });

    it("should be able to merge with another set", () => {
        const set1 = new GSet();
        const set2 = new GSet();

        set1.add(1);
        set1.add(2);
        set2.add(3);

        expect(set1.merge(set2)).toEqual(new Set([1, 2, 3]));
    });

    it("should be able to be converted to JSON", () => {
        const set = new GSet();

        set.add(1);
        set.add(2);
        set.add(3);

        expect(JSON.stringify(set)).toEqual("[1,2,3]");
    });
});
