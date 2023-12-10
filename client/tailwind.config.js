import typography from "@tailwindcss/typography";
import daisyui from "daisyui";

/** @type {import("tailwindcss").Config & {daisyui: import("daisyui").Config}} */
export default {
    content: ["./src/**/*.{html,js,svelte,ts}"],

    theme: {
        extend: {},
    },

    daisyui: {
        themes: true,
    },

    plugins: [typography, daisyui],
};
