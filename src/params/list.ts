import type { ParamMatcher } from "@sveltejs/kit";

export const pattern = /^[a-z][a-z0-9-]*$/i;

export const match: ParamMatcher = (param) => pattern.test(param);
