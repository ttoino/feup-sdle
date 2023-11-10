import { describe, expect, it } from "vitest";
import AWSet from "./awset";

const id1 = "a";
const id2 = "b";

describe("AWSet", () => {
    it("should be able to add an element", () => {
        const set = new AWSet();

        expect(set.value).toEqual(new Set());
        expect(set.add(id1, 1)).toEqual(new Set([1]));
    });

    it("should be able to add multiple elements", () => {
        const set = new AWSet();

        expect(set.value).toEqual(new Set());
        expect(set.add(id1, 1)).toEqual(new Set([1]));
        expect(set.add(id1, 2)).toEqual(new Set([1, 2]));
        expect(set.add(id1, 3)).toEqual(new Set([1, 2, 3]));
    });

    it("should be able to add an element multiple times", () => {
        const set = new AWSet();

        expect(set.value).toEqual(new Set());
        expect(set.add(id1, 1)).toEqual(new Set([1]));
        expect(set.add(id1, 1)).toEqual(new Set([1]));
        expect(set.add(id1, 1)).toEqual(new Set([1]));
    });

    it("should be able to remove an element", () => {
        const set = new AWSet();

        expect(set.value).toEqual(new Set());
        expect(set.add(id1, 1)).toEqual(new Set([1]));
        expect(set.remove(1)).toEqual(new Set());
    });

    it("should be able to remove multiple elements", () => {
        const set = new AWSet();

        expect(set.value).toEqual(new Set());
        expect(set.add(id1, 1)).toEqual(new Set([1]));
        expect(set.add(id1, 2)).toEqual(new Set([1, 2]));
        expect(set.add(id1, 3)).toEqual(new Set([1, 2, 3]));
        expect(set.remove(1)).toEqual(new Set([2, 3]));
        expect(set.remove(2)).toEqual(new Set([3]));
        expect(set.remove(3)).toEqual(new Set());
    });

    it("should be able to remove an element multiple times", () => {
        const set = new AWSet();

        expect(set.value).toEqual(new Set());
        expect(set.add(id1, 1)).toEqual(new Set([1]));
        expect(set.remove(1)).toEqual(new Set());
        expect(set.remove(1)).toEqual(new Set());
        expect(set.remove(1)).toEqual(new Set());
    });

    it("should be able to start with a value", () => {
        const set = new AWSet(
            new Set([
                [id1, 1, 1],
                [id1, 2, 2],
                [id1, 3, 3],
            ]),
        );

        expect(set.value).toEqual(new Set([1, 2, 3]));
    });

    it("should be able to merge with itself", () => {
        const set = new AWSet();

        set.add(id1, 1);
        set.add(id1, 2);

        expect(set.merge(set)).toEqual(new Set([1, 2]));
    });

    it("should be able to merge with another set", () => {
        const set1 = new AWSet();
        const set2 = new AWSet();

        set1.add(id1, 1);
        set1.add(id1, 2);
        set2.add(id2, 3);

        expect(set1.merge(set2)).toEqual(new Set([1, 2, 3]));
    });

    it("should be able to merge with another set with conflicting adds", () => {
        const set1 = new AWSet();
        const set2 = new AWSet();

        set1.add(id1, 1);
        set2.add(id2, 1);

        expect(set1.merge(set2)).toEqual(new Set([1]));
    });

    it("should be able to merge with another set with conflicting removes", () => {
        const set1 = new AWSet();
        const set2 = new AWSet();

        set1.add(id1, 1);
        set1.remove(1);
        set2.remove(1);

        expect(set1.merge(set2)).toEqual(new Set());
    });

    it("should be able to merge with another set with conflicting adds and removes", () => {
        const set1 = new AWSet();
        const set2 = new AWSet();

        set1.add(id1, 1);
        set1.remove(1);
        set2.add(id2, 1);

        expect(set1.merge(set2)).toEqual(new Set([1]));
    });

    it("should be able to be converted to JSON", () => {
        const set = new AWSet();

        set.add(id1, 1);
        set.add(id1, 2);
        set.add(id1, 3);

        const expected = {
            value: [
                [id1, 1, 1],
                [id1, 2, 2],
                [id1, 3, 3],
            ],
            dots: {
                [id1]: 3,
            },
        } as const;

        expect(set.toJSON()).toEqual(expected);
        expect(JSON.stringify(set)).toEqual(
            `{"value":[["${id1}",1,1],["${id1}",2,2],["${id1}",3,3]],"dots":{"${id1}":3}}`,
        );
    });
});
