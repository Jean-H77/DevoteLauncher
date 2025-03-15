package org.devote;

import javax.swing.*;

public class DownloadSwingWorker extends SwingWorker<Void, Void> {

    private final CacheDownloader cacheDownloader;
    private final JButton repairButton;
    private final JButton playButton;
    private final JLabel statusLabel;

    public DownloadSwingWorker(CacheDownloader cacheDownloader, JButton repairButton, JButton playButton, JLabel statusLabel) {
        this.cacheDownloader = cacheDownloader;
        this.repairButton = repairButton;
        this.playButton = playButton;
        this.statusLabel = statusLabel;
    }

    @Override
    protected Void doInBackground() throws Exception {
        NeedsDownloadResult result = cacheDownloader.needsDownload();
        if(result.isNeedsDownload()) {
            repairButton.setEnabled(false);
            playButton.setEnabled(false);
            statusLabel.setText("Downloading update...");
            cacheDownloader.download(result.getVersion());
            repairButton.setEnabled(true);
            playButton.setEnabled(true);
            statusLabel.setText("Download complete.");
            JOptionPane.showMessageDialog(null, "Update complete.", "Update Complete", JOptionPane.INFORMATION_MESSAGE);
        } else {
            playButton.setEnabled(true);
            repairButton.setEnabled(true);
            statusLabel.setText("Ready!");
        }
        return null;
    }
}
