package com.infrahub.watcher.monitoring.domain;

public enum ServiceStatus {
    /** Not checked yet (or URL changed since the last check). */
    UNKNOWN,
    /** Responded with a 2xx/3xx status. */
    UP,
    /** Responded with 4xx/5xx, timed out, or was unreachable. */
    DOWN
}
