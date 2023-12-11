import CCounter from "./ccounter";
import { describe, expect, it } from "vitest";

const id1 = "a";
const id2 = "b";

describe("CCounter", () => {
    it("should be able to increment", () => {
        const counter = new CCounter();

        expect(counter.value).toBe(0);
        expect(counter.inc(id1)).toBe(1);
    });

    it("should be able to increment multiple times", () => {
        const counter = new CCounter();

        expect(counter.value).toBe(0);
        expect(counter.inc(id1)).toBe(1);
        expect(counter.inc(id1)).toBe(2);
        expect(counter.inc(id1)).toBe(3);
    });

    it("should be able to increment by any positive value", () => {
        const counter = new CCounter();

        expect(counter.value).toBe(0);
        expect(counter.inc(id1, 5)).toBe(5);
        expect(counter.inc(id1, 3)).toBe(8);
        expect(counter.inc(id1, 2)).toBe(10);
    });

    it("should be able to decrement", () => {
        const counter = new CCounter();

        expect(counter.value).toBe(0);
        expect(counter.dec(id1)).toBe(-1);
    });

    it("should be able to decrement multiple times", () => {
        const counter = new CCounter();

        expect(counter.value).toBe(0);
        expect(counter.dec(id1)).toBe(-1);
        expect(counter.dec(id1)).toBe(-2);
        expect(counter.dec(id1)).toBe(-3);
    });

    it("should be able to decrement by any positive value", () => {
        const counter = new CCounter();

        expect(counter.value).toBe(0);
        expect(counter.dec(id1, 5)).toBe(-5);
        expect(counter.dec(id1, 3)).toBe(-8);
        expect(counter.dec(id1, 2)).toBe(-10);
    });

    it("should be able to increment by a negative value", () => {
        const counter = new CCounter();

        expect(counter.value).toBe(0);
        expect(counter.inc(id1, -1)).toBe(-1);
    });

    it("should be able to decrement by a negative value", () => {
        const counter = new CCounter();

        expect(counter.value).toBe(0);
        expect(counter.dec(id1, -1)).toBe(1);
    });

    it("should be able to increment and decrement", () => {
        const counter = new CCounter();

        expect(counter.value).toBe(0);
        expect(counter.inc(id1)).toBe(1);
        expect(counter.dec(id1)).toBe(0);
    });

    it("should be able to start with a value", () => {
        const counter = new CCounter([[id1, 5, 5]]);

        expect(counter.value).toBe(5);
    });

    it("should be able to merge with itself", () => {
        const counter = new CCounter();

        counter.inc(id1);
        counter.inc(id1);

        expect(counter.merge(counter)).toBe(2);
    });

    it("should be able to merge with another counter", () => {
        const counter1 = new CCounter();
        const counter2 = new CCounter();

        counter1.inc(id1);
        counter2.inc(id2);

        expect(counter1.merge(counter2)).toBe(2);
    });
});
