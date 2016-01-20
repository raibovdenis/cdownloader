package com.ecwid.cdownloader.Throttle;

import com.google.common.util.concurrent.RateLimiter;

/**
 * Throttle bandwidth
 * 
 * This class wrapper for com.google.common.util.concurrent.RateLimiter
 */
public class GuavaThrottleImpl implements Throttle {
    private long maxBytesPerSecond;
    private RateLimiter rateLimiter;


    /**
     * Check max bytes per second
     *
     * @param maxBytesPerSecond
     */
    private void checkMaxBytesPerSecond(long maxBytesPerSecond) {
        if (maxBytesPerSecond < 0) {
            throw new IllegalArgumentException("Max bytes per second must be positive");
        }
    }

    /**
     * Get max bytes per second
     *
     * @return
     */
    public long getMaxBytesPerSecond() {
        return maxBytesPerSecond;
    }

    /**
     * Set max bytes per second
     *
     * @param maxBytesPerSecond
     */
    public GuavaThrottleImpl setMaxBytesPerSecond(long maxBytesPerSecond) {
        checkMaxBytesPerSecond(maxBytesPerSecond);

        this.maxBytesPerSecond = maxBytesPerSecond;

        rateLimiter = RateLimiter.create(maxBytesPerSecond);

        return this;
    }

    /**
     * Acquires the given number of readBytes from this {@code RateLimiter}, blocking until the
     * request be granted.
     *
     * @param readBytes
     */
    public void acquire(int readBytes) {
        this.rateLimiter.acquire(readBytes);
    }
}
