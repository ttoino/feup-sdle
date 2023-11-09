export default class DotsContext {
    private dots: Map<string, number>;

    constructor(
        dots:
            | Map<string, number>
            | Record<string, number>
            | Iterable<[string, number]> = [],
    ) {
        this.dots = new Map(
            dots instanceof Map || dots instanceof Array
                ? dots
                : Object.entries(dots),
        );
    }

    max(id: string) {
        return this.dots.get(id) ?? 0;
    }

    next(id: string) {
        const dot = (this.dots.get(id) ?? 0) + 1;
        this.dots.set(id, dot);
        return [id, dot] as const;
    }

    has(id: string, dot: number) {
        return (this.dots.get(id) ?? 0) >= dot;
    }

    merge(other: DotsContext) {
        for (const [id, dot] of other.dots) {
            this.dots.set(id, Math.max(this.dots.get(id) ?? 0, dot));
        }

        return this;
    }
}
