package com.ecwid.cdownloader;

/**
 * Console args interface
 */
public interface ConsoleArgs {
    /**
     * Parse args
     *
     * @param args
     */
    public void parseArgs(String[] args);

    /**
     * Get count threads
     *
     * @return
     */
    public int getCountThreads();

    /**
     * Get max bytes per second
     *
     * @return
     */
    public String getMaxBytesPerSecond();

    /**
     * Get path to file with references
     *
     * @return
     */
    public String getPathToFileWithReferences();

    /**
     * Get destination directory path
     *
     * @return
     */
    public String getDestinationDirectoryPath();
}
