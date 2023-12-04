import type { ParamMatcher } from "@sveltejs/kit";

export const pattern = /^[a-z0-9][a-z0-9\-_]*$/i;

export const match: ParamMatcher = (param) => pattern.test(param);
