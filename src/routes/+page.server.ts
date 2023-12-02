import type { Actions } from './$types';

import { redirect } from '@sveltejs/kit';

export const actions = {	
    default: async (_event) => {
        console.log(_event);		
        throw redirect(302, '/teste');
    },
} satisfies Actions;