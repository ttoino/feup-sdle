import { describe, expect, it } from "vitest";
import PNCounter from "./pncounter";

describe("PNCounter", () => {
    it("should be able to increment", () => {
        const counter = new PNCounter();

        expect(counter.value).toBe(0);
        expect(counter.inc()).toBe(1);
    });

    it("should be able to increment multiple times", () => {
        const counter = new PNCounter();

        expect(counter.value).toBe(0);
        expect(counter.inc()).toBe(1);
        expect(counter.inc()).toBe(2);
        expect(counter.inc()).toBe(3);
    });

    it("should be able to increment by any positive value", () => {
        const counter = new PNCounter();

        expect(counter.value).toBe(0);
        expect(counter.inc(5)).toBe(5);
        expect(counter.inc(3)).toBe(8);
        expect(counter.inc(2)).toBe(10);
    });

    it("should be able to decrement", () => {
        const counter = new PNCounter();

        expect(counter.value).toBe(0);
        expect(counter.dec()).toBe(-1);
    });

    it("should be able to decrement multiple times", () => {
        const counter = new PNCounter();

        expect(counter.value).toBe(0);
        expect(counter.dec()).toBe(-1);
        expect(counter.dec()).toBe(-2);
        expect(counter.dec()).toBe(-3);
    });

    it("should be able to decrement by any positive value", () => {
        const counter = new PNCounter();

        expect(counter.value).toBe(0);
        expect(counter.dec(5)).toBe(-5);
        expect(counter.dec(3)).toBe(-8);
        expect(counter.dec(2)).toBe(-10);
    });

    it("should be able to increment by a negative value", () => {
        const counter = new PNCounter();

        expect(counter.value).toBe(0);
        expect(counter.inc(-1)).toBe(-1);
    });

    it("should be able to decrement by a negative value", () => {
        const counter = new PNCounter();

        expect(counter.value).toBe(0);
        expect(counter.dec(-1)).toBe(1);
    });

    it("should be able to increment and decrement", () => {
        const counter = new PNCounter();

        expect(counter.value).toBe(0);
        expect(counter.inc()).toBe(1);
        expect(counter.dec()).toBe(0);
    });

    it("should be able to start with a value", () => {
        const counter = new PNCounter(5);

        expect(counter.value).toBe(5);
    });

    it("should be able to merge with itself", () => {
        const counter = new PNCounter();

        counter.inc();
        counter.inc();

        expect(counter.merge(counter)).toBe(2);
    });

    it("should be able to merge with another counter", () => {
        const counter1 = new PNCounter();
        const counter2 = new PNCounter();

        counter1.inc();
        counter1.inc();
        counter2.dec();

        expect(counter1.merge(counter2)).toBe(1);
    });

    it("should be able to be converted to JSON", () => {
        const counter = new PNCounter();

        counter.inc();
        counter.inc();
        counter.dec();

        expect(counter.toJSON()).toEqual({ increments: 2, decrements: 1 });
        expect(JSON.stringify(counter)).toEqual('{"increments":2,"decrements":1}');
    });
});
