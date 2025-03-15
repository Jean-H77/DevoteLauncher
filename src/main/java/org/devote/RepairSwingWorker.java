package org.devote;

import javax.swing.*;

public class RepairSwingWorker extends SwingWorker<Void, Void> {
    private final CacheDownloader cacheDownloader;
    private final JButton repairButton;
    private final JButton playButton;
    private final JLabel statusLabel;

    public RepairSwingWorker(CacheDownloader cacheDownloader, JButton repairButton, JButton playButton, JLabel statusLabel) {
        this.cacheDownloader = cacheDownloader;
        this.repairButton = repairButton;
        this.playButton = playButton;
        this.statusLabel = statusLabel;
    }

    @Override
    protected Void doInBackground() {
        repairButton.setEnabled(false);
        playButton.setEnabled(false);
        statusLabel.setText("Repairing...");
        cacheDownloader.repair();
        repairButton.setEnabled(true);
        statusLabel.setText("Repairing complete.");
        playButton.setEnabled(true);
        JOptionPane.showMessageDialog(null, "Repair complete.", "Repair Complete", JOptionPane.INFORMATION_MESSAGE);
        return null;
    }
}
