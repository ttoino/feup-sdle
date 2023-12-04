import AWMap from "./awmap";
import AWSet from "./awset";
import CCounter from "./ccounter";
import DotsContext from "./dotscontext";
import MVRegister from "./mvregister";
import { describe, expect, it } from "vitest";

const id1 = "a";
const id2 = "b";

describe("AWMap", () => {
    it("should be able to add an AWSet element", () => {
        const map = new AWMap<string, AWSet<number>>();

        expect(map.value).toEqual(new Map());
        expect(map.set(id1, "test", new AWSet<number>())).toEqual(
            new Map([["test", new AWSet<number>()]]),
        );
    });
    it("should be able to add a CCounter element", () => {
        const map = new AWMap<string, CCounter>();

        expect(map.value).toEqual(new Map());
        expect(map.set(id1, "test", new CCounter())).toEqual(
            new Map([["test", new CCounter()]]),
        );
    });
    it("should be able to add an MVRegister element", () => {
        const map = new AWMap<string, MVRegister<number>>();

        expect(map.value).toEqual(new Map());
        expect(map.set(id1, "test", new MVRegister<number>())).toEqual(
            new Map([["test", new MVRegister<number>()]]),
        );
    });

    it("should be able to add multiple AWSet elements", () => {
        const map = new AWMap<string, AWSet<number>>();

        expect(map.value).toEqual(new Map());
        expect(map.set(id1, "test1", new AWSet<number>())).toEqual(
            new Map([["test1", new AWSet<number>()]]),
        );
        expect(map.set(id1, "test2", new AWSet<number>())).toEqual(
            new Map([
                ["test1", new AWSet<number>()],
                ["test2", new AWSet<number>()],
            ]),
        );
        expect(map.set(id1, "test3", new AWSet<number>())).toEqual(
            new Map([
                ["test1", new AWSet<number>()],
                ["test2", new AWSet<number>()],
                ["test3", new AWSet<number>()],
            ]),
        );
    });
    it("should be able to add multiple CCounter elements", () => {
        const map = new AWMap<string, CCounter>();

        expect(map.value).toEqual(new Map());
        expect(map.set(id1, "test1", new CCounter())).toEqual(
            new Map([["test1", new CCounter()]]),
        );
        expect(map.set(id1, "test2", new CCounter())).toEqual(
            new Map([
                ["test1", new CCounter()],
                ["test2", new CCounter()],
            ]),
        );
        expect(map.set(id1, "test3", new CCounter())).toEqual(
            new Map([
                ["test1", new CCounter()],
                ["test2", new CCounter()],
                ["test3", new CCounter()],
            ]),
        );
    });
    it("should be able to add multiple MVRegister elements", () => {
        const map = new AWMap<string, MVRegister<number>>();

        expect(map.value).toEqual(new Map());
        expect(map.set(id1, "test1", new MVRegister<number>())).toEqual(
            new Map([["test1", new MVRegister<number>()]]),
        );
        expect(map.set(id1, "test2", new MVRegister<number>())).toEqual(
            new Map([
                ["test1", new MVRegister<number>()],
                ["test2", new MVRegister<number>()],
            ]),
        );
        expect(map.set(id1, "test3", new MVRegister<number>())).toEqual(
            new Map([
                ["test1", new MVRegister<number>()],
                ["test2", new MVRegister<number>()],
                ["test3", new MVRegister<number>()],
            ]),
        );
    });

    it("should be able to add an AWSet element multiple times", () => {
        const map = new AWMap<string, AWSet<number>>();

        expect(map.value).toEqual(new Map());
        expect(map.set(id1, "test", new AWSet<number>())).toEqual(
            new Map([["test", new AWSet<number>()]]),
        );
        expect(map.set(id1, "test", new AWSet<number>())).toEqual(
            new Map([["test", new AWSet<number>()]]),
        );
        expect(map.set(id1, "test", new AWSet<number>())).toEqual(
            new Map([["test", new AWSet<number>()]]),
        );
    });
    it("should be able to add a CCounter element multiple times", () => {
        const map = new AWMap<string, CCounter>();

        expect(map.value).toEqual(new Map());
        expect(map.set(id1, "test", new CCounter())).toEqual(
            new Map([["test", new CCounter()]]),
        );
        expect(map.set(id1, "test", new CCounter())).toEqual(
            new Map([["test", new CCounter()]]),
        );
        expect(map.set(id1, "test", new CCounter())).toEqual(
            new Map([["test", new CCounter()]]),
        );
    });
    it("should be able to add an MVRegister element multiple times", () => {
        const map = new AWMap<string, MVRegister<number>>();

        expect(map.value).toEqual(new Map());
        expect(map.set(id1, "test", new MVRegister<number>())).toEqual(
            new Map([["test", new MVRegister<number>()]]),
        );
        expect(map.set(id1, "test", new MVRegister<number>())).toEqual(
            new Map([["test", new MVRegister<number>()]]),
        );
        expect(map.set(id1, "test", new MVRegister<number>())).toEqual(
            new Map([["test", new MVRegister<number>()]]),
        );
    });

    it("should be able to remove an AWSet element", () => {
        const map = new AWMap<string, AWSet<number>>();

        expect(map.value).toEqual(new Map());
        expect(map.set(id1, "test", new AWSet<number>())).toEqual(
            new Map([["test", new AWSet<number>()]]),
        );
        expect(map.remove("test")).toEqual(new Map());
    });
    it("should be able to remove a CCounter element", () => {
        const map = new AWMap<string, CCounter>();

        expect(map.value).toEqual(new Map());
        expect(map.set(id1, "test", new CCounter())).toEqual(
            new Map([["test", new CCounter()]]),
        );
        expect(map.remove("test")).toEqual(new Map());
    });
    it("should be able to remove an MVRegister element", () => {
        const map = new AWMap<string, MVRegister<number>>();

        expect(map.value).toEqual(new Map());
        expect(map.set(id1, "test", new MVRegister<number>())).toEqual(
            new Map([["test", new MVRegister<number>()]]),
        );
        expect(map.remove("test")).toEqual(new Map());
    });

    it("should be able to remove multiple AWSet elements", () => {
        const map = new AWMap<string, AWSet<number>>();

        expect(map.value).toEqual(new Map());
        expect(map.set(id1, "test1", new AWSet<number>())).toEqual(
            new Map([["test1", new AWSet<number>()]]),
        );
        expect(map.set(id1, "test2", new AWSet<number>())).toEqual(
            new Map([
                ["test1", new AWSet<number>()],
                ["test2", new AWSet<number>()],
            ]),
        );
        expect(map.set(id1, "test3", new AWSet<number>())).toEqual(
            new Map([
                ["test1", new AWSet<number>()],
                ["test2", new AWSet<number>()],
                ["test3", new AWSet<number>()],
            ]),
        );
        expect(map.remove("test1")).toEqual(
            new Map([
                ["test2", new AWSet<number>()],
                ["test3", new AWSet<number>()],
            ]),
        );
        expect(map.remove("test2")).toEqual(
            new Map([["test3", new AWSet<number>()]]),
        );
        expect(map.remove("test3")).toEqual(new Map());
    });
    it("should be able to remove multiple CCounter elements", () => {
        const map = new AWMap<string, CCounter>();

        expect(map.value).toEqual(new Map());
        expect(map.set(id1, "test1", new CCounter())).toEqual(
            new Map([["test1", new CCounter()]]),
        );
        expect(map.set(id1, "test2", new CCounter())).toEqual(
            new Map([
                ["test1", new CCounter()],
                ["test2", new CCounter()],
            ]),
        );
        expect(map.set(id1, "test3", new CCounter())).toEqual(
            new Map([
                ["test1", new CCounter()],
                ["test2", new CCounter()],
                ["test3", new CCounter()],
            ]),
        );
        expect(map.remove("test1")).toEqual(
            new Map([
                ["test2", new CCounter()],
                ["test3", new CCounter()],
            ]),
        );
        expect(map.remove("test2")).toEqual(
            new Map([["test3", new CCounter()]]),
        );
        expect(map.remove("test3")).toEqual(new Map());
    });
    it("should be able to remove multiple MVRegister elements", () => {
        const map = new AWMap<string, MVRegister<number>>();

        expect(map.value).toEqual(new Map());
        expect(map.set(id1, "test1", new MVRegister<number>())).toEqual(
            new Map([["test1", new MVRegister<number>()]]),
        );
        expect(map.set(id1, "test2", new MVRegister<number>())).toEqual(
            new Map([
                ["test1", new MVRegister<number>()],
                ["test2", new MVRegister<number>()],
            ]),
        );
        expect(map.set(id1, "test3", new MVRegister<number>())).toEqual(
            new Map([
                ["test1", new MVRegister<number>()],
                ["test2", new MVRegister<number>()],
                ["test3", new MVRegister<number>()],
            ]),
        );
        expect(map.remove("test1")).toEqual(
            new Map([
                ["test2", new MVRegister<number>()],
                ["test3", new MVRegister<number>()],
            ]),
        );
        expect(map.remove("test2")).toEqual(
            new Map([["test3", new MVRegister<number>()]]),
        );
        expect(map.remove("test3")).toEqual(new Map());
    });

    it("should be able to remove an AWSet element multiple times", () => {
        const map = new AWMap<string, AWSet<number>>();

        expect(map.value).toEqual(new Map());
        expect(map.set(id1, "test", new AWSet<number>())).toEqual(
            new Map([["test", new AWSet<number>()]]),
        );
        expect(map.remove("test")).toEqual(new Map());
        expect(map.remove("test")).toEqual(new Map());
        expect(map.remove("test")).toEqual(new Map());
    });
    it("should be able to remove a CCounter element multiple times", () => {
        const map = new AWMap<string, CCounter>();

        expect(map.value).toEqual(new Map());
        expect(map.set(id1, "test", new CCounter())).toEqual(
            new Map([["test", new CCounter()]]),
        );
        expect(map.remove("test")).toEqual(new Map());
        expect(map.remove("test")).toEqual(new Map());
        expect(map.remove("test")).toEqual(new Map());
    });
    it("should be able to remove an MVRegister element multiple times", () => {
        const map = new AWMap<string, MVRegister<number>>();

        expect(map.value).toEqual(new Map());
        expect(map.set(id1, "test", new MVRegister<number>())).toEqual(
            new Map([["test", new MVRegister<number>()]]),
        );
        expect(map.remove("test")).toEqual(new Map());
        expect(map.remove("test")).toEqual(new Map());
        expect(map.remove("test")).toEqual(new Map());
    });

    it("should be able to start with a value with AWSet elements", () => {
        const map = new AWMap(
            [
                [id1, 1, "test1"],
                [id1, 2, "test2"],
                [id1, 3, "test3"],
            ],
            [
                ["test1", new AWSet<number>()],
                ["test2", new AWSet<number>()],
                ["test3", new AWSet<number>()],
            ],
        );

        expect(map.value).toEqual(
            new Map([
                ["test1", new AWSet<number>()],
                ["test2", new AWSet<number>()],
                ["test3", new AWSet<number>()],
            ]),
        );
    });
    it("should be able to start with a value with CCounter elements", () => {
        const map = new AWMap(
            [
                [id1, 1, "test1"],
                [id1, 2, "test2"],
                [id1, 3, "test3"],
            ],
            [
                ["test1", new CCounter()],
                ["test2", new CCounter()],
                ["test3", new CCounter()],
            ],
        );

        expect(map.value).toEqual(
            new Map([
                ["test1", new CCounter()],
                ["test2", new CCounter()],
                ["test3", new CCounter()],
            ]),
        );
    });
    it("should be able to start with a value with MVRegister elements", () => {
        const map = new AWMap(
            [
                [id1, 1, "test1"],
                [id1, 2, "test2"],
                [id1, 3, "test3"],
            ],
            [
                ["test1", new MVRegister<number>()],
                ["test2", new MVRegister<number>()],
                ["test3", new MVRegister<number>()],
            ],
        );

        expect(map.value).toEqual(
            new Map([
                ["test1", new MVRegister<number>()],
                ["test2", new MVRegister<number>()],
                ["test3", new MVRegister<number>()],
            ]),
        );
    });

    it("should be able to merge with itself with AWSet elements", () => {
        const map = new AWMap<string, AWSet<number>>();

        map.set(id1, "test1", new AWSet<number>());
        map.set(id1, "test2", new AWSet<number>());

        expect(map.merge(map)).toEqual(
            new Map([
                ["test1", new AWSet<number>()],
                ["test2", new AWSet<number>()],
            ]),
        );
    });
    it("should be able to merge with itself with CCounter elements", () => {
        const map = new AWMap<string, CCounter>();

        map.set(id1, "test1", new CCounter());
        map.set(id1, "test2", new CCounter());

        expect(map.merge(map)).toEqual(
            new Map([
                ["test1", new CCounter()],
                ["test2", new CCounter()],
            ]),
        );
    });
    it("should be able to merge with itself with MVRegister elements", () => {
        const map = new AWMap<string, MVRegister<number>>();

        map.set(id1, "test1", new MVRegister<number>());
        map.set(id1, "test2", new MVRegister<number>());

        expect(map.merge(map)).toEqual(
            new Map([
                ["test1", new MVRegister<number>()],
                ["test2", new MVRegister<number>()],
            ]),
        );
    });

    it("should be able to merge with another map with AWSet elements", () => {
        const map1 = new AWMap<string, AWSet<number>>();
        const map2 = new AWMap<string, AWSet<number>>();

        map1.set(id1, "test1", new AWSet<number>());
        map1.set(id1, "test2", new AWSet<number>());
        map2.set(id2, "test3", new AWSet<number>());

        expect(map1.merge(map2)).toEqual(
            new Map([
                ["test1", new AWSet<number>()],
                ["test2", new AWSet<number>()],
                ["test3", new AWSet<number>()],
            ]),
        );
    });
    it("should be able to merge with another map with CCounter elements", () => {
        const map1 = new AWMap<string, CCounter>();
        const map2 = new AWMap<string, CCounter>();

        map1.set(id1, "test1", new CCounter());
        map1.set(id1, "test2", new CCounter());
        map2.set(id2, "test3", new CCounter());

        expect(map1.merge(map2)).toEqual(
            new Map([
                ["test1", new CCounter()],
                ["test2", new CCounter()],
                ["test3", new CCounter()],
            ]),
        );
    });
    it("should be able to merge with another map with MVRegister elements", () => {
        const map1 = new AWMap<string, MVRegister<number>>();
        const map2 = new AWMap<string, MVRegister<number>>();

        map1.set(id1, "test1", new MVRegister<number>());
        map1.set(id1, "test2", new MVRegister<number>());
        map2.set(id2, "test3", new MVRegister<number>());

        expect(map1.merge(map2)).toEqual(
            new Map([
                ["test1", new MVRegister<number>()],
                ["test2", new MVRegister<number>()],
                ["test3", new MVRegister<number>()],
            ]),
        );
    });

    it("should be able to merge with another map with AWSet elements with conflicting adds", () => {
        const map1 = new AWMap<string, AWSet<number>>();
        const map2 = new AWMap<string, AWSet<number>>();

        map1.set(id1, "test", new AWSet<number>());
        map1.set(id1, "test", new AWSet<number>());

        expect(map1.merge(map2)).toEqual(
            new Map([["test", new AWSet<number>()]]),
        );
    });
    it("should be able to merge with another map with CCounter elements with conflicting adds", () => {
        const map1 = new AWMap<string, CCounter>();
        const map2 = new AWMap<string, CCounter>();

        map1.set(id1, "test", new CCounter());
        map1.set(id1, "test", new CCounter());

        expect(map1.merge(map2)).toEqual(new Map([["test", new CCounter()]]));
    });
    it("should be able to merge with another map with MVRegister elements with conflicting adds", () => {
        const map1 = new AWMap<string, MVRegister<number>>();
        const map2 = new AWMap<string, MVRegister<number>>();

        map1.set(id1, "test", new MVRegister<number>());
        map1.set(id1, "test", new MVRegister<number>());

        expect(map1.merge(map2)).toEqual(
            new Map([["test", new MVRegister<number>()]]),
        );
    });

    it("should be able to merge with another map with AWSet elements with conflicting removes", () => {
        const map1 = new AWMap<string, AWSet<number>>();
        const map2 = new AWMap<string, AWSet<number>>();

        map1.set(id1, "test", new AWSet<number>());
        map1.remove("test");
        map2.remove("test");

        expect(map1.merge(map2)).toEqual(new Map());
    });
    it("should be able to merge with another map with CCounter elements with conflicting removes", () => {
        const map1 = new AWMap<string, CCounter>();
        const map2 = new AWMap<string, CCounter>();

        map1.set(id1, "test", new CCounter());
        map1.remove("test");
        map2.remove("test");

        expect(map1.merge(map2)).toEqual(new Map());
    });
    it("should be able to merge with another map with MVRegister elements with conflicting removes", () => {
        const map1 = new AWMap<string, MVRegister<number>>();
        const map2 = new AWMap<string, MVRegister<number>>();

        map1.set(id1, "test", new MVRegister<number>());
        map1.remove("test");
        map2.remove("test");

        expect(map1.merge(map2)).toEqual(new Map());
    });

    it("should be able to merge with another map with AWSet elements with conflicting adds and removes", () => {
        const map1 = new AWMap<string, AWSet<number>>();
        const map2 = new AWMap<string, AWSet<number>>();

        map1.set(id1, "test", new AWSet<number>());
        map1.remove("test");
        map2.set(id2, "test", new AWSet<number>());

        expect(map1.merge(map2)).toEqual(
            new Map([["test", new AWSet<number>()]]),
        );
    });
    it("should be able to merge with another map with CCounter elements with conflicting adds and removes", () => {
        const map1 = new AWMap<string, CCounter>();
        const map2 = new AWMap<string, CCounter>();

        map1.set(id1, "test", new CCounter());
        map1.remove("test");
        map2.set(id2, "test", new CCounter());

        expect(map1.merge(map2)).toEqual(new Map([["test", new CCounter()]]));
    });
    it("should be able to merge with another map with MVRegister elements with conflicting adds and removes", () => {
        const map1 = new AWMap<string, MVRegister<number>>();
        const map2 = new AWMap<string, MVRegister<number>>();

        map1.set(id1, "test", new MVRegister<number>());
        map1.remove("test");
        map2.set(id2, "test", new MVRegister<number>());

        expect(map1.merge(map2)).toEqual(
            new Map([["test", new MVRegister<number>()]]),
        );
    });

    it("should be able to merge with another map with AWSet elements with conflicting values", () => {
        const map1 = new AWMap<string, AWSet<number>>();
        const map2 = new AWMap<string, AWSet<number>>();

        map1.set(id1, "test", new AWSet<number>([], map1.dots));
        map1.get("test")?.add(id1, 1);
        map2.set(id2, "test", new AWSet<number>([], map2.dots));
        map2.get("test")?.add(id2, 2);

        expect(map1.merge(map2)).toEqual(
            new Map([
                [
                    "test",
                    new AWSet<number>(
                        [
                            [id1, 2, 1],
                            [id2, 2, 2],
                        ],
                        new DotsContext([
                            [id1, 2],
                            [id2, 2],
                        ]),
                    ),
                ],
            ]),
        );
    });
    it("should be able to merge with another map with CCounter elements with conflicting values", () => {
        const map1 = new AWMap<string, CCounter>();
        const map2 = new AWMap<string, CCounter>();

        map1.set(id1, "test", new CCounter([], map1.dots));
        map1.get("test")?.inc(id1, 10);
        map2.set(id2, "test", new CCounter([], map2.dots));
        map2.get("test")?.dec(id2, 3);

        expect(map1.merge(map2)).toEqual(
            new Map([
                [
                    "test",
                    new CCounter(
                        [
                            [id1, 2, 10],
                            [id2, 2, -3],
                        ],
                        new DotsContext([
                            [id1, 2],
                            [id2, 2],
                        ]),
                    ),
                ],
            ]),
        );
    });
    it("should be able to merge with another map with MVRegister elements with conflicting values", () => {
        const map1 = new AWMap<string, MVRegister<number>>();
        const map2 = new AWMap<string, MVRegister<number>>();

        map1.set(id1, "test", new MVRegister<number>([], map1.dots));
        map1.get("test")?.assign(id1, 1);
        map2.set(id2, "test", new MVRegister<number>([], map2.dots));
        map2.get("test")?.assign(id2, 2);

        expect(map1.merge(map2)).toEqual(
            new Map([
                [
                    "test",
                    new MVRegister<number>(
                        [
                            [id1, 2, 1],
                            [id2, 2, 2],
                        ],
                        new DotsContext([
                            [id1, 2],
                            [id2, 2],
                        ]),
                    ),
                ],
            ]),
        );
    });
});
