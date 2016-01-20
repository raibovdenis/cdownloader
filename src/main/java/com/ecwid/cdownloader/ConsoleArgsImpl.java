package com.ecwid.cdownloader;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

/**
 * Console downloader args
 */
public class ConsoleArgsImpl implements ConsoleArgs {
    @Option(name = "-n", usage = "Count threads")
    public int countThreads;

    @Option(name = "-l", usage = "Max bytes per second")
    public String maxBytesPerSecond;

    @Option(name = "-f", usage = "Path to file with references")
    public String pathToFileWithReferences;

    @Option(name = "-o", usage = "Destination directory path")
    public String destinationDirectoryPath;

    /**
     * Parse args
     *
     * @param args
     */
    public void parseArgs(String[] args) {
        /** Create */
        CmdLineParser parser = new CmdLineParser(this);

        try {
            /** Parse the arguments */
            parser.parseArgument(args);
        } catch (CmdLineException | IllegalArgumentException e) {
            throw new IllegalArgumentException("Error on parse args. " + e.getMessage(), e);
        }
    }

    /**
     * Get count threads
     *
     * @return
     */
    public int getCountThreads() {
        return countThreads;
    }

    /**
     * Get max bytes per second
     *
     * @return
     */
    public String getMaxBytesPerSecond() {
        return maxBytesPerSecond;
    }

    /**
     * Get path to file with references
     *
     * @return
     */
    public String getPathToFileWithReferences() {
        return pathToFileWithReferences;
    }

    /**
     * Get destination directory path
     *
     * @return
     */
    public String getDestinationDirectoryPath() {
        return destinationDirectoryPath;
    }
}
