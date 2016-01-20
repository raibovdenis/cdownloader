package com.ecwid.cdownloader.Throttle;

import org.isomorphism.util.TokenBucket;
import org.isomorphism.util.TokenBuckets;

import java.util.concurrent.TimeUnit;

/**
 * Throttle bandwidth
 *
 * This class wrapper for org.isomorphism.util.TokenBucket
 */
public class TokenBucketThrottleImpl implements Throttle {
    private long maxBytesPerSecond;
    private TokenBucket bucket;

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
    public TokenBucketThrottleImpl setMaxBytesPerSecond(long maxBytesPerSecond) {
        checkMaxBytesPerSecond(maxBytesPerSecond);

        this.maxBytesPerSecond = maxBytesPerSecond;

        bucket = TokenBuckets.builder()
                .withCapacity(maxBytesPerSecond)
                .withFixedIntervalRefillStrategy(maxBytesPerSecond, 1, TimeUnit.SECONDS)
                .build();

        return this;
    }

    /**
     * Acquires the given number of readBytes from this {@code RateLimiter}, blocking until the
     * request be granted.
     *
     * @param readBytes
     */
    public void acquire(int readBytes) {
        bucket.consume(readBytes);
    }
}
