package com.ecwid.cdownloader.Throttle;

/**
 * Throttle bandwidth interface
 */
public interface Throttle {

    /**
     * Get max bytes per second
     *
     * @return
     */
    public long getMaxBytesPerSecond();

    /**
     * Get max bytes per second
     *
     * @param maxBytesPerSecond
     * @return
     */
    public Throttle setMaxBytesPerSecond(long maxBytesPerSecond);

    /**
     *
     *
     * @param readBytes
     */
    public void acquire(int readBytes);
}
