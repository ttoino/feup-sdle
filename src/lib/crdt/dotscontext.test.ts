import { describe, expect, it } from "vitest";
import DotsContext from "./dotscontext";

describe("DotsContext", () => {
    it("should be able to get next dot", () => {
        const dots = new DotsContext();

        expect(dots.next("a")).toEqual(["a", 1]);
        expect(dots.next("a")).toEqual(["a", 2]);
        expect(dots.next("a")).toEqual(["a", 3]);
    });

    it("should be able to get next dot for multiple ids", () => {
        const dots = new DotsContext();

        expect(dots.next("a")).toEqual(["a", 1]);
        expect(dots.next("b")).toEqual(["b", 1]);
        expect(dots.next("a")).toEqual(["a", 2]);
        expect(dots.next("b")).toEqual(["b", 2]);
        expect(dots.next("a")).toEqual(["a", 3]);
        expect(dots.next("b")).toEqual(["b", 3]);
    });

    it("should be able to get max dot", () => {
        const dots = new DotsContext();

        expect(dots.max("a")).toEqual(0);
        expect(dots.next("a")).toEqual(["a", 1]);
        expect(dots.max("a")).toEqual(1);
        expect(dots.next("a")).toEqual(["a", 2]);
        expect(dots.max("a")).toEqual(2);
        expect(dots.next("a")).toEqual(["a", 3]);
        expect(dots.max("a")).toEqual(3);
    });

    it("should be able to get max dot for multiple ids", () => {
        const dots = new DotsContext();

        expect(dots.max("a")).toEqual(0);
        expect(dots.next("a")).toEqual(["a", 1]);
        expect(dots.max("a")).toEqual(1);
        expect(dots.next("b")).toEqual(["b", 1]);
        expect(dots.max("b")).toEqual(1);
        expect(dots.next("a")).toEqual(["a", 2]);
        expect(dots.max("a")).toEqual(2);
        expect(dots.next("b")).toEqual(["b", 2]);
        expect(dots.max("b")).toEqual(2);
        expect(dots.next("a")).toEqual(["a", 3]);
        expect(dots.max("a")).toEqual(3);
        expect(dots.next("b")).toEqual(["b", 3]);
        expect(dots.max("b")).toEqual(3);
    });

    it("should be able to check if dot is in context", () => {
        const dots = new DotsContext();

        expect(dots.has("a", 1)).toBe(false);
        expect(dots.next("a")).toEqual(["a", 1]);
        expect(dots.has("a", 1)).toBe(true);
        expect(dots.has("a", 2)).toBe(false);
        expect(dots.next("a")).toEqual(["a", 2]);
        expect(dots.has("a", 2)).toBe(true);
        expect(dots.has("a", 3)).toBe(false);
        expect(dots.next("a")).toEqual(["a", 3]);
        expect(dots.has("a", 3)).toBe(true);
    });

    it("should be able to check if dot is in context for multiple ids", () => {
        const dots = new DotsContext();

        expect(dots.has("a", 1)).toBe(false);
        expect(dots.next("a")).toEqual(["a", 1]);
        expect(dots.has("a", 1)).toBe(true);
        expect(dots.has("b", 1)).toBe(false);
        expect(dots.next("b")).toEqual(["b", 1]);
        expect(dots.has("b", 1)).toBe(true);
        expect(dots.has("a", 2)).toBe(false);
        expect(dots.next("a")).toEqual(["a", 2]);
        expect(dots.has("a", 2)).toBe(true);
        expect(dots.has("b", 2)).toBe(false);
        expect(dots.next("b")).toEqual(["b", 2]);
        expect(dots.has("b", 2)).toBe(true);
        expect(dots.has("a", 3)).toBe(false);
        expect(dots.next("a")).toEqual(["a", 3]);
        expect(dots.has("a", 3)).toBe(true);
        expect(dots.has("b", 3)).toBe(false);
        expect(dots.next("b")).toEqual(["b", 3]);
        expect(dots.has("b", 3)).toBe(true);
    });

    it("should be able to merge with itself", () => {
        const dots = new DotsContext();

        dots.next("a");
        dots.next("a");
        dots.next("a");

        expect(dots.merge(dots)).toEqual(dots);
    });

    it("should be able to merge with another context", () => {
        const dots1 = new DotsContext();
        const dots2 = new DotsContext();

        dots1.next("a");
        dots1.next("a");
        dots2.next("a");
        dots2.next("a");
        dots2.next("a");

        expect(dots1.merge(dots2)).toEqual(new DotsContext({ a: 3 }));
    });

    it("should be able to merge with another context for multiple ids", () => {
        const dots1 = new DotsContext();
        const dots2 = new DotsContext();

        dots1.next("a");
        dots1.next("a");
        dots2.next("b");
        dots2.next("b");
        dots2.next("b");

        expect(dots1.merge(dots2)).toEqual(new DotsContext({ a: 2, b: 3 }));
    });
});
