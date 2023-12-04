import { writable } from "svelte/store";
import { v1 as uuidV1 } from "uuid";

export type NotificationType = "info" | "success" | "warning" | "error";

export interface Notification {
    id: string;
    type: NotificationType;
    message: string;
    dismissible: boolean;
    timeout?: number;
}

export const DEFAULT_NOTIFICATION_TIMEOUT = 3000;

export const notifications = writable<Notification[]>([]);

export const addNotification = (
    message: string,
    options: Partial<Omit<Notification, "id" | "message">>,
) => {
    // Setup some sensible defaults for a toast.
    const notification: Notification = {
        id: generateNotificationId(),
        message,
        type: options.type ?? "info",
        dismissible: options.dismissible ?? true,
        timeout: options.timeout,
    };

    // Push the toast to the top of the list of toasts
    notifications.update((all) => [notification, ...all]);

    // If toast is dismissible, dismiss it after "timeout" amount of time.
    if (notification.timeout)
        setTimeout(
            () => dismissNotification(notification.id),
            notification.timeout,
        );
};

export const dismissNotification = (id: Notification["id"]) => {
    notifications.update((all) => all.filter((n) => n.id !== id));
};

const generateNotificationId = (): string => uuidV1();
