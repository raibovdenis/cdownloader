package com.ecwid.cdownloader;

import com.ecwid.cdownloader.Throttle.Throttle;
import com.ecwid.cdownloader.Util.FileUtil;
import com.ecwid.cdownloader.Util.FormatterUtil;
import com.ecwid.cdownloader.Util.StringUtil;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Console downloader
 */
public class ConsoleDownloader {
    private Throttle throttle;
    private ConsoleArgs consoleArgs;

    private int countThreads;
    private long maxBytesPerSecond;
    private String pathToFileWithReferences;
    private String destinationDirectoryPath;
    private Map<String, List<String>> listReference = null;

    /**
     * Get throttle
     *
     * @return
     */
    public Throttle getThrottle() {
        return throttle;
    }

    /**
     * Set throttle
     *
     * @param throttle
     */
    public ConsoleDownloader setThrottle(Throttle throttle) {
        if (!Throttle.class.isInstance(throttle)) {
            throw new IllegalArgumentException("Object throttle not instance of Throttle");
        }

        this.throttle = throttle;

        return this;
    }

    /**
     * @return
     */
    public ConsoleArgs getConsoleArgs() {
        return consoleArgs;
    }

    public ConsoleDownloader setConsoleArgs(ConsoleArgs consoleArgs) {
        if (!ConsoleArgs.class.isInstance(consoleArgs)) {
            throw new IllegalArgumentException("Object consoleArgs not instance of ConsoleArgs");
        }

        this.consoleArgs = consoleArgs;

        return this;
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
     * Set count threads
     *
     * @param countThreads
     * @return
     * @throws IllegalArgumentException
     */
    public ConsoleDownloader setCountThreads(int countThreads) throws IllegalArgumentException {
        if (countThreads < 1) {
            throw new IllegalArgumentException("Count threads must be greater than 0");
        }

        this.countThreads = countThreads;
        return this;
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
     * @return
     */
    public ConsoleDownloader setMaxBytesPerSecond(String maxBytesPerSecond) {
        if (StringUtil.isNullOrEmpty(maxBytesPerSecond)) {
            throw new IllegalArgumentException("Max bytes per second");
        }

        long formatMaxBytesPerSecond = FormatterUtil.parseByteSize(maxBytesPerSecond);

        return setMaxBytesPerSecond(formatMaxBytesPerSecond);
    }

    /**
     * Set max bytes per second
     *
     * @param maxBytesPerSecond
     * @return
     */
    public ConsoleDownloader setMaxBytesPerSecond(long maxBytesPerSecond) {
        if (maxBytesPerSecond < 1) {
            throw new IllegalArgumentException("Max bytes per second must be greater than 0");
        }

        this.maxBytesPerSecond = maxBytesPerSecond;
        return this;
    }

    /**
     * Get destination directory path
     *
     * @return
     */
    public String getDestinationDirectoryPath() {
        return destinationDirectoryPath;
    }

    /**
     * Set destination directory path
     *
     * @param destinationDirectoryPath
     * @return
     */
    public ConsoleDownloader setDestinationDirectoryPath(String destinationDirectoryPath) {
        if (StringUtil.isNullOrEmpty(destinationDirectoryPath)) {
            throw new IllegalArgumentException("Destination directory path can not be empty");
        }

        this.destinationDirectoryPath = destinationDirectoryPath;
        return this;
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
     * Get path to file with references
     *
     * @param pathToFileWithReferences
     * @return
     */
    public ConsoleDownloader setPathToFileWithReferences(String pathToFileWithReferences) {
        if (StringUtil.isNullOrEmpty(pathToFileWithReferences)) {
            throw new IllegalArgumentException("Path to file with references");
        }

        this.pathToFileWithReferences = pathToFileWithReferences;

        /** Reset list */
        resetListReference();

        return this;
    }

    /**
     * Reset list reference
     *
     * @return
     */
    public ConsoleDownloader resetListReference() {
        listReference = null;

        return this;
    }

    /**
     * Get list reference
     *
     * @return
     * @throws Exception
     */
    public Map<String, List<String>> getListReference() throws Exception {
        if (listReference == null) {
            /** Create map */
            Map<String, List<String>> result = new HashMap<String, List<String>>();

            /** Get list strings */
            List<String> listStrings = FileUtil.readFileToStringList(getPathToFileWithReferences());

            for (String listString : listStrings) {
                /** Split each string by whitespace */
                List<String> splitItems = Arrays.asList(listString.split("\\s+"));

                /** Check data */
                if (splitItems.size() == 2 && !StringUtil.isNullOrEmpty(splitItems.get(0)) && !StringUtil.isNullOrEmpty(splitItems.get(1))) {
                    String key = splitItems.get(0);
                    String value = splitItems.get(1);

                    /** Add key/empty list */
                    if (!result.containsKey(key)) {
                        result.put(key, new ArrayList<String>());
                    }

                    /** Add only unique value by key */
                    if (!result.get(key).contains(value)) {
                        result.get(key).add(value);
                    }
                } else {
                    throw new Exception("Incorrect data in file " + getPathToFileWithReferences());
                }
            }

            if (result.isEmpty()) {
                throw new Exception("File " + getPathToFileWithReferences() + " is empty or incorrect");
            }

            listReference = result;
        }

        return listReference;
    }

    /**
     * Parse args
     *
     * @param args
     */
    public ConsoleDownloader parseArgs(String[] args) {
        /** Parse args */
        consoleArgs.parseArgs(args);

        /** Set args */
        setCountThreads(consoleArgs.getCountThreads());
        setMaxBytesPerSecond(consoleArgs.getMaxBytesPerSecond());
        setPathToFileWithReferences(consoleArgs.getPathToFileWithReferences());
        setDestinationDirectoryPath(consoleArgs.getDestinationDirectoryPath());

        return this;
    }

    /**
     * Run console downloader
     *
     * @throws Exception
     */
    public void run() throws Exception {
        System.out.println("ConsoleDownloader: Start");

        long countBytesDownloaded = 0;
        long start = System.currentTimeMillis();

        /** Get list reference */
        getListReference();

        /** Get count threads */
        int nThreads = Math.min(getCountThreads(), listReference.size());

        /** Create thread pool */
        ExecutorService pool = Executors.newFixedThreadPool(nThreads);

        /** Create task list */
        List<Future<List<DownloadTaskResult>>> taskList = new ArrayList<Future<List<DownloadTaskResult>>>();

        /** Set max bytes per second */
        throttle.setMaxBytesPerSecond(getMaxBytesPerSecond());

        /** Add tasks to pool && task list */
        for (Map.Entry<String, List<String>> reference : listReference.entrySet()) {
            /** Get params */
            String sourceFileUrl = reference.getKey();
            List<String> destinationFileNames = reference.getValue();
            String destinationDirectoryPath = getDestinationDirectoryPath();

            /** Create task and submit task to be executed by thread pool */
            Future<List<DownloadTaskResult>> task = pool.submit(new DownloadTask(sourceFileUrl, destinationFileNames, destinationDirectoryPath, throttle));

            /** Add task to list */
            taskList.add(task);
        }

        System.out.println("  DownloadTask:");

        /** Get result for each task */
        for (Future<List<DownloadTaskResult>> task : taskList) {
            try {
                /** Get result */
                List<DownloadTaskResult> result = task.get();

                /** On each result item show message */
                for (DownloadTaskResult resultItem : result) {
                    if (!resultItem.getIsException()) {
                        countBytesDownloaded += resultItem.getFileSize();
                        System.out.println("    +++ " + resultItem.toString());
                    } else {
                        System.out.println("    --- " + resultItem.toString());
                    }
                }
            } catch (Exception e) {
                System.out.println("    --- " + e.getCause().getMessage());
            }
        }

        /** Threads do not keep running */
        pool.shutdown();

        /** Get execution time */
        long executionTime = System.currentTimeMillis() - start;

        /** Show result */
        System.out.println("ConsoleDownloader: End");
        System.out.println(String.format(
                "ConsoleDownloader: Statistics " +
                        "\n    Execution time = %s" +
                        "\n    Bytes downloaded = %s" +
                        "\n    Average speed = %s" +
                        "\n    Max speed = %s",
                FormatterUtil.formatTime(executionTime, true),
                FormatterUtil.formatByteSize(countBytesDownloaded),
                FormatterUtil.formatByteSize(countBytesDownloaded / FormatterUtil.formatTime(executionTime)),
                FormatterUtil.formatByteSize(throttle.getMaxBytesPerSecond())
        ));
    }
}
