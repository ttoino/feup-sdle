/// <reference types="@sveltejs/kit" />
/// <reference no-default-lib="true"/>
/// <reference lib="esnext" />
/// <reference lib="webworker" />
import { PUBLIC_SERVER_URL } from "$env/static/public";
import type ShoppingList from "$lib/list";
import setup from "$lib/localforage/setup";
import * as listService from "$lib/service/list";
import { base, build, files, prerendered, version } from "$service-worker";

const sw = self as unknown as ServiceWorkerGlobalScope;

const cacheName = `cache-${version}`;
const cachedAssets = [...build, ...files, ...prerendered, `${base}/`];

setup();

let syncing = false;

sw.addEventListener("install", (event) => {
    const addAssetsToCache = async () => {
        const cache = await caches.open(cacheName);
        await cache.addAll(cachedAssets);
    };

    event.waitUntil(addAssetsToCache());
});

sw.addEventListener("activate", (event) => {
    const deleteOldCaches = async () => {
        await Promise.allSettled(
            (await caches.keys()).map(
                (name) => name !== cacheName && caches.delete(name),
            ),
        );

        await sw.clients.claim();
    };

    event.waitUntil(deleteOldCaches());
});

sw.addEventListener("fetch", (event) => {
    const respondWithCachedAsset = async () => {
        let asset = await caches.match(event.request);

        if (
            event.request.method === "GET" &&
            !asset &&
            !event.request.url.includes(PUBLIC_SERVER_URL)
        ) {
            const request = new Request(
                event.request.url.split("/").slice(0, -1).join("/"),
                { ...event.request },
            );
            console.log(request);
            asset ??= await caches.match(request);
        }

        asset ??= await fetch(event.request);

        return asset;
    };

    if (!syncing) {
        syncing = true;
        sync();
    }

    event.respondWith(respondWithCachedAsset());
});

const sync = async () => {
    if ((await sw.clients.matchAll()).length === 0) return (syncing = false);

    await listService.syncAll(
        (await listService.getAll()).filter((l) => l) as ShoppingList[],
    );

    setTimeout(sync, 1000 * 5);
};
