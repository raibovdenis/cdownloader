package com.ecwid.cdownloader;

import com.ecwid.cdownloader.Throttle.Throttle;
import com.ecwid.cdownloader.Util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * Download thread
 */
public class DownloadTask implements Callable<List<DownloadTaskResult>> {
    private final String sourceFileUrl;
    private final List<String> destinationFileNames;
    private final String destinationDirectoryPath;
    private final Throttle throttle;

    /**
     * Constructor
     *
     * @param sourceFileUrl
     * @param destinationFileNames
     * @param destinationDirectoryPath
     * @param throttle
     */
    public DownloadTask(String sourceFileUrl, List<String> destinationFileNames, String destinationDirectoryPath, Throttle throttle) {
        this.sourceFileUrl = sourceFileUrl;
        this.destinationFileNames = destinationFileNames;
        this.destinationDirectoryPath = destinationDirectoryPath;
        this.throttle = throttle;
    }

    @Override
    public List<DownloadTaskResult> call() throws Exception {
        try {
            return downloadFile(sourceFileUrl, destinationFileNames, destinationDirectoryPath, throttle);
        } catch (Exception e) {
            throw new Exception("DownloadTask broken", e);
        }
    }

    private List<DownloadTaskResult> downloadFile(String sourceFileUrl, List<String> destinationFileNames, String destinationDirectoryPath, Throttle throttle) throws Exception {
        if (destinationFileNames.isEmpty()) {
            throw new IllegalArgumentException("Destination file names can not be empty");
        }

        int destinationFileNamesSize = destinationFileNames.size();
        List<DownloadTaskResult> result = new ArrayList<DownloadTaskResult>();
        List<String> failFileList = new ArrayList<String>();
        List<String> remainingFileList = new ArrayList<String>();
        int indexDownloadedFile = -1;

        /** Try in loop download only one file if possible */
        for (int i = 0; i < destinationFileNamesSize; i++) {
            try {
                long start = System.currentTimeMillis();

                /** Download file */
                File fileResult = FileUtil.downloadFile(sourceFileUrl, destinationFileNames.get(i), destinationDirectoryPath, throttle);

                long downloadTime = System.currentTimeMillis() - start;

                /** Add result */
                DownloadTaskResult resultItem = new DownloadTaskResult(
                        sourceFileUrl,
                        fileResult.getCanonicalPath(),
                        fileResult.length(),
                        downloadTime,
                        "Download file from " + sourceFileUrl + " to " + fileResult.getCanonicalPath(),
                        false
                );
                result.add(resultItem);

                /** Save index for downloaded file and break loop */
                indexDownloadedFile = i;
                break;
            } catch (Exception e) {
                /** Suppress exception and add fail file in fail list */
                failFileList.add(destinationFileNames.get(i));
            }
        }

        /** If exists destination files with different names (ex. "http://xx" => {"1.zip", "2.zip", ... "n.zip"}) */
        if (destinationFileNamesSize > 1) {
            /** One file download - try copy remaining */
            if (indexDownloadedFile != -1) {
                /** Fill remaining file list for copy */
                /** Add fail file */
                remainingFileList.addAll(failFileList);

                /** Add next files after downloaded file */
                remainingFileList.addAll(destinationFileNames.subList(indexDownloadedFile + 1, destinationFileNamesSize));

                /** Try in loop copy remaining files */
                for (String destinationFileName : remainingFileList) {
                    /** Get destination file path */
                    String destinationFilePath = FileUtil.getFilePath(destinationFileName, destinationDirectoryPath);

                    try {
                        /** Copy first downloaded file */
                        File fileResult = FileUtil.copyFile(result.get(0).getFilePath(), destinationFilePath);

                        /** Add result */
                        DownloadTaskResult resultItem = new DownloadTaskResult(
                                sourceFileUrl,
                                fileResult.getCanonicalPath(),
                                0, //Set 0 to ignore the statistics
                                0, //Set 0 to ignore the statistics
                                "Copy file (" + sourceFileUrl + ") from " + result.get(0).getFilePath() + " to " + fileResult.getCanonicalPath(),
                                false
                        );
                        result.add(resultItem);

                    } catch (Exception e) {
                        /** Add result */
                        DownloadTaskResult resultItem = new DownloadTaskResult(
                                sourceFileUrl,
                                destinationFilePath,
                                0, //Set 0 to ignore the statistics
                                0, //Set 0 to ignore the statistics
                                "Can not copy file (" + sourceFileUrl + ") from " + result.get(0).getFilePath() + " to " + destinationFilePath + ". " + e.getMessage(),
                                true
                        );
                        result.add(resultItem);
                    }
                }
            } else {
                /** Add result */
                DownloadTaskResult resultItem = new DownloadTaskResult(
                        sourceFileUrl,
                        "", //Set em
                        0, //Set 0 to ignore the statistics
                        0, //Set 0 to ignore the statistics
                        "Can not download/copy file (" + sourceFileUrl + ") to {'" + destinationFileNames.stream().map(fileName -> FileUtil.getFilePath(fileName, destinationDirectoryPath)).collect(Collectors.joining("', '")) + "'}",
                        true
                );
                result.add(resultItem);
            }
        }

        return result;
    }
}
