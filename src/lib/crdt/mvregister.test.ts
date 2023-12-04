import MVRegister from "./mvregister";
import { describe, expect, it } from "vitest";

const id1 = "a";
const id2 = "b";

describe("MVRegister", () => {
    it("should be able to assign a value", () => {
        const register = new MVRegister();

        expect(register.value).toEqual(new Set());
        expect(register.assign(id1, 1)).toEqual(new Set([1]));
    });

    it("should be able to assign a value multiple times", () => {
        const register = new MVRegister();

        expect(register.value).toEqual(new Set());
        expect(register.assign(id1, 1)).toEqual(new Set([1]));
        expect(register.assign(id1, 1)).toEqual(new Set([1]));
        expect(register.assign(id1, 1)).toEqual(new Set([1]));
    });

    it("should be able to assign a value to a different id", () => {
        const register = new MVRegister();

        expect(register.value).toEqual(new Set());
        expect(register.assign(id1, 1)).toEqual(new Set([1]));
        expect(register.assign(id2, 2)).toEqual(new Set([2]));
    });

    it("should be able to assign a value to a different id multiple times", () => {
        const register = new MVRegister();

        expect(register.value).toEqual(new Set());
        expect(register.assign(id1, 1)).toEqual(new Set([1]));
        expect(register.assign(id2, 2)).toEqual(new Set([2]));
        expect(register.assign(id1, 1)).toEqual(new Set([1]));
        expect(register.assign(id2, 2)).toEqual(new Set([2]));
        expect(register.assign(id1, 1)).toEqual(new Set([1]));
        expect(register.assign(id2, 2)).toEqual(new Set([2]));
    });

    it("should be able to merge with itself", () => {
        const register = new MVRegister();

        register.assign(id1, 1);

        expect(register.merge(register)).toEqual(new Set([1]));
    });

    it("should be able to merge with another register", () => {
        const register1 = new MVRegister();
        const register2 = new MVRegister();

        register1.assign(id1, 1);
        register2.merge(register1);
        register2.assign(id2, 2);

        expect(register1.merge(register2)).toEqual(new Set([2]));
    });

    it("should be able to merge with another register with conflicting values", () => {
        const register1 = new MVRegister();
        const register2 = new MVRegister();

        register1.assign(id1, 1);
        register2.assign(id2, 2);

        expect(register1.merge(register2)).toEqual(new Set([1, 2]));
    });

    it("should be able to be converted to JSON", () => {
        const register = new MVRegister();

        register.assign(id1, 1);
        register.assign(id2, 2);

        expect(JSON.stringify(register)).toEqual(
            `{"value":[["${id2}",1,2]],"dots":{"${id1}":1,"${id2}":1}}`,
        );
    });
});
