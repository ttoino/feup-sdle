import GCounter from "./gcounter";
import { describe, expect, it } from "vitest";

describe("GCounter", () => {
    it("should be able to increment", () => {
        const counter = new GCounter();

        expect(counter.value).toBe(0);
        expect(counter.inc()).toBe(1);
    });

    it("should be able to increment multiple times", () => {
        const counter = new GCounter();

        expect(counter.value).toBe(0);
        expect(counter.inc()).toBe(1);
        expect(counter.inc()).toBe(2);
        expect(counter.inc()).toBe(3);
    });

    it("should be able to increment by any positive value", () => {
        const counter = new GCounter();

        expect(counter.value).toBe(0);
        expect(counter.inc(5)).toBe(5);
        expect(counter.inc(3)).toBe(8);
        expect(counter.inc(2)).toBe(10);
    });

    it("should not be able to increment by a negative value", () => {
        const counter = new GCounter();

        expect(counter.value).toBe(0);
        expect(() => counter.inc(-1)).toThrow();
    });

    it("should be able to start with a value", () => {
        const counter = new GCounter(5);

        expect(counter.value).toBe(5);
    });

    it("should be able to merge with itself", () => {
        const counter = new GCounter();

        counter.inc();
        counter.inc();

        expect(counter.merge(counter)).toBe(2);
    });

    it("should be able to merge with another counter", () => {
        const counter1 = new GCounter();
        const counter2 = new GCounter();

        counter1.inc();
        counter1.inc();
        counter2.inc();

        expect(counter1.merge(counter2)).toBe(2);
    });
});
