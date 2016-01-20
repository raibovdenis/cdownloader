package com.ecwid.cdownloader;

/**
 * Download file result
 */
public class DownloadTaskResult {
    private final boolean isException;
    private final long fileSize;
    private final long downloadTime;
    private final String filePath;
    private final String fileUrl;
    private final String message;

    /**
     * Constructor
     *
     * @param fileUrl
     * @param filePath
     * @param fileSize
     * @param downloadTime
     * @param message
     */
    public DownloadTaskResult(String fileUrl, String filePath, long fileSize, long downloadTime, String message, boolean isException) {
        this.fileUrl = fileUrl;
        this.fileSize = fileSize;
        this.filePath = filePath;
        this.downloadTime = downloadTime;
        this.message = message;
        this.isException = isException;
    }

    /**
     * Get is exception
     *
     * @return
     */
    public boolean getIsException() {
        return isException;
    }

    /**
     * Get file url
     *
     * @return
     */
    public String getFileUrl() {
        return fileUrl;
    }

    /**
     * Get message
     *
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get file path
     *
     * @return
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Get download time
     *
     * @return
     */
    public long getDownloadTime() {
        return downloadTime;
    }

    /**
     * Get file size
     *
     * @return
     */
    public long getFileSize() {
        return fileSize;
    }

    @Override
    public String toString() {
        return getMessage();
    }
}
