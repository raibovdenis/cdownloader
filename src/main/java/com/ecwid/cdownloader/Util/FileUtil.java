package com.ecwid.cdownloader.Util;

import com.ecwid.cdownloader.Throttle.Throttle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtil {
    private static final int BUFFER_SIZE = 4096;

    /**
     * Get file path
     *
     * @param fileName
     * @param directoryPath
     * @return
     */
    public static String getFilePath(String fileName, String directoryPath) {
        return directoryPath + File.separator + fileName;
    }

    /**
     * Read file to string list
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static List<String> readFileToStringList(String filePath) throws IOException {
        if (StringUtil.isNullOrEmpty(filePath)) {
            throw new IllegalArgumentException("File path can not be empty");
        }

        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            return stream
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .collect(Collectors.toList())
                    ;
        } catch (Exception e) {
            throw new IOException("Can not read file " + filePath, e);
        }
    }

    /**
     * Create directory
     *
     * @param directoryPath
     * @return
     * @throws IllegalArgumentException
     * @throws IOException
     */
    public static File createDirectory(String directoryPath) throws IOException {
        if (StringUtil.isNullOrEmpty(directoryPath)) {
            throw new IllegalArgumentException("Directory path can not be empty");
        }

        File file = new File(directoryPath);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new IOException("Unable to create directory " + directoryPath);
            }
        }

        return file;
    }

    /**
     * Copy file
     *
     * @param sourceFile
     * @param destinationFile
     * @return
     * @throws IOException
     */
    public static File copyFile(File sourceFile, File destinationFile) throws IOException {
        try {
            return Files.copy(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING).toFile();
        } catch (Exception e) {
            throw new IOException("Unable to copy from " + sourceFile.getCanonicalPath() + " to " + destinationFile.getCanonicalPath(), e);
        }
    }

    /**
     * Copy file
     *
     * @param sourceFilePath
     * @param destinationFilePath
     * @return
     * @throws IOException
     */
    public static File copyFile(String sourceFilePath, String destinationFilePath) throws IOException {
        if (StringUtil.isNullOrEmpty(sourceFilePath)) {
            throw new IllegalArgumentException("Source file path can not be empty");
        }

        if (StringUtil.isNullOrEmpty(destinationFilePath)) {
            throw new IllegalArgumentException("Destination file path can not be empty");
        }

        File sourceFile = new File(sourceFilePath);
        File destinationFile = new File(destinationFilePath);

        return copyFile(sourceFile, destinationFile);
    }

    /**
     * Check http file exists
     *
     * @param fileUrl
     * @return
     */
    public static boolean httpFileExists(String fileUrl) {
        try {
            /** Set for redirects */
            HttpURLConnection.setFollowRedirects(false);

            /** Open connection */
            HttpURLConnection connection = (HttpURLConnection) new URL(fileUrl).openConnection();

            /** Set method */
            connection.setRequestMethod("HEAD");

            /** Set browser headers */
            setBrowserHeaders(connection);

            return (connection.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Set browser headers
     * <p>
     * Avoid problem "Server returned HTTP response code: 403"
     *
     * @param connection
     */
    private static void setBrowserHeaders(HttpURLConnection connection) {
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
    }

    /**
     * Download file
     *
     * @param sourceFileUrl
     * @param destinationFileName
     * @param destinationDirectoryPath
     * @return
     * @throws IOException
     */
    public static File downloadFile(String sourceFileUrl, String destinationFileName, String destinationDirectoryPath, Throttle throttle) throws IOException {
        if (StringUtil.isNullOrEmpty(sourceFileUrl)) {
            throw new IllegalArgumentException("Source file url can not be empty");
        }

        if (StringUtil.isNullOrEmpty(destinationFileName)) {
            throw new IllegalArgumentException("Destination file name can not be empty");
        }

        if (StringUtil.isNullOrEmpty(destinationDirectoryPath)) {
            throw new IllegalArgumentException("Destination directory path can not be empty");
        }

        int readBytes = 0;
        long fileSize = 0;
        byte[] buffer = new byte[BUFFER_SIZE];

        /** Create directory */
        File directoryFile = createDirectory(destinationDirectoryPath);

        /** Get destination file path*/
        String destinationFilePath = getFilePath(destinationFileName, directoryFile.getCanonicalPath());

        /** Create url  */
        URL url = new URL(sourceFileUrl);

        /** Open connection */
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        /** Set browser headers */
        setBrowserHeaders(connection);

        /** Check http file exists */
        if (!httpFileExists(sourceFileUrl)) {
            throw new IOException("File " + sourceFileUrl + " does not exists");
        }

        /** Create destination file */
        File destinationFile = new File(destinationFilePath);

        /** Hack - avoid bug with create file with incorrect chars - throw Exception */
        destinationFile.toPath();

        /** Download file */
        try (
                InputStream inputStream = connection.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(destinationFile, false)
        ) {
            while ((readBytes = inputStream.read(buffer)) != -1) {
                fileSize += readBytes;

                /** Throttle acquire */
                throttle.acquire(readBytes);

                /** Write tu buffer */
                outputStream.write(buffer, 0, readBytes);
            }

            return destinationFile;
        } catch (Exception e) {
            throw new IOException("Error on download file from " + sourceFileUrl + " to " + destinationFilePath + ". " + e.getMessage(), e);
        }
    }
}
