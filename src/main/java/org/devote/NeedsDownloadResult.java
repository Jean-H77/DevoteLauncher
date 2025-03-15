package org.devote;

public class NeedsDownloadResult {

    private final boolean needsDownload;
    private final double version;

    public NeedsDownloadResult(boolean needsDownload, double version) {
        this.needsDownload = needsDownload;
        this.version = version;
    }

    public boolean isNeedsDownload() {
        return needsDownload;
    }

    public double getVersion() {
        return version;
    }
}
