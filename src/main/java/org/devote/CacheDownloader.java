package org.devote;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.OptionalDouble;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class CacheDownloader {

    public static String CACHE_NAME = ".devote";
    private static final String CACHE_DIR = System.getProperty("user.home") + File.separator + CACHE_NAME + File.separator;

    public void download(double version) {
        downloadCache(version);
    }

    public NeedsDownloadResult needsDownload() {
        OptionalDouble currentCacheVersionOptional = getCurrentCacheVersion();
        OptionalDouble liveCacheVersionOptional = getLiveCacheVersion();

        if(!liveCacheVersionOptional.isPresent()) {
            throw new IllegalStateException("No live cache version found");
        }

        double liveCacheVersion = liveCacheVersionOptional.getAsDouble();
        if(!currentCacheVersionOptional.isPresent() || liveCacheVersion > currentCacheVersionOptional.getAsDouble()) {
            downloadCache(liveCacheVersion);
            return new NeedsDownloadResult(true, liveCacheVersion);
        }

        return new NeedsDownloadResult(false, -1);
    }

    public void repair() {
        OptionalDouble liveCacheVersionOptional = getLiveCacheVersion();
        if(!liveCacheVersionOptional.isPresent()) {
            throw new IllegalStateException("No live cache version found");
        }
        downloadCache(liveCacheVersionOptional.getAsDouble());
    }

    public String getClient() {
        File client = new File(CACHE_DIR, "DevoteRSPS.jar");
        if(!client.exists()) {
            repair();
            return null;
        }
        return client.getAbsolutePath();
    }

    public OptionalDouble getCurrentCacheVersion() {
        if(!hasCacheDirectory()) {
            return OptionalDouble.empty();
        }

        File cacheVersionFile = new File(CACHE_DIR, "version.txt");
        if(!cacheVersionFile.exists()) {
            return OptionalDouble.empty();
        }

        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(cacheVersionFile))) {
            String line = bufferedReader.readLine();
            return OptionalDouble.of(Double.parseDouble(line));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveCacheVersion(double version) {
        File file = new File(CACHE_DIR, "version.txt");
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(String.valueOf(version));
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the file using FileWriter: " + e.getMessage());
        }
    }

    public boolean hasCacheDirectory() {
        File cacheDir = new File(CACHE_DIR);
        return cacheDir.exists();
    }

    private void downloadCache(double version) {
        String cacheUrl = "https://cdn.devoteps.com/cache.zip";
        try {
            HttpURLConnection connection = (HttpURLConnection ) new URL(cacheUrl).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                System.out.println("Downloading");
                try (InputStream inputStream = connection.getInputStream()) {

                    File cacheDir = new File(CACHE_DIR);
                    cacheDir.delete();
                    cacheDir.mkdirs();

                    ZipInputStream zis = new ZipInputStream(inputStream);
                    ZipEntry entry;
                    while ((entry = zis.getNextEntry()) != null)
                    {
                        File outputFile = new File(cacheDir, entry.getName());
                        if (entry.isDirectory()) {
                            outputFile.mkdirs();
                        } else {
                            outputFile.getParentFile().mkdirs();

                            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                                byte[] buffer = new byte[1024];
                                int length;
                                while ((length = zis.read(buffer)) > 0) {
                                    fos.write(buffer, 0, length);
                                }
                            }
                        }
                        zis.closeEntry();
                    }

                    saveCacheVersion(version);
                }
            } else {
                System.err.println("Failed to fetch version: HTTP " + connection.getResponseCode());
            }

        } catch (IOException e) {
            System.err.println("Error fetching or downloading: " + e.getMessage());
        }
    }

    public OptionalDouble getLiveCacheVersion() {
        String versionUrl = "https://cdn.devoteps.com/version.txt";

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(versionUrl).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (InputStream inputStream = connection.getInputStream()) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                    }
                    String content = byteArrayOutputStream.toString(StandardCharsets.UTF_8.name());
                    return OptionalDouble.of(Double.parseDouble(content));
                }
            } else {
                System.err.println("Failed to fetch version: HTTP " + connection.getResponseCode());
            }
        } catch (IOException  | NumberFormatException e) {
            System.err.println("Error fetching or parsing version: " + e.getMessage());
        }

        return OptionalDouble.empty();
    }
}