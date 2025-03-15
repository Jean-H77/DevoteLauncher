package org.devote;

import mdlaf.MaterialLookAndFeel;
import mdlaf.themes.MaterialOceanicTheme;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Paths;

public class Launcher {

    private final JButton playButton = new JButton("Play");
    private final JButton repairButton = new JButton("Repair");
    private final JButton closeButton = new JButton("Close");
    private final JLabel statusLabel = new JLabel("Checking for updates...");

    private final CacheDownloader cacheDownloader = new CacheDownloader();

    static {
        try {
            UIManager.setLookAndFeel(new MaterialLookAndFeel(new MaterialOceanicTheme()));
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        buildUI();
        new DownloadSwingWorker(
                cacheDownloader,
                repairButton,
                playButton,
                statusLabel
        ).execute();
    }

    private void buildUI() {
        JFrame jFrame = new JFrame();
        jFrame.setLayout(new MigLayout("fill", "[center]", "[top][center][center]"));

        JLabel header = new JLabel("Devote RSPS");
        header.setFont(new Font("Arial", Font.BOLD, 20));
        jFrame.add(header, "cell 0 0, align center");

        JLabel iconLabel = new JLabel("Icon goes here");
        iconLabel.setPreferredSize(new Dimension(123, 123));
        jFrame.add(iconLabel, "cell 0 1, align center");

        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(Color.YELLOW);
        jFrame.add(statusLabel, "cell 0 2, align center");

        JPanel buttonPanel = new JPanel(new MigLayout("fill", "[grow][grow][grow]", "[]"));

        playButton.setPreferredSize(new Dimension(100, 50));
        playButton.setEnabled(false);
        playButton.addActionListener(e -> {
            playButton.setEnabled(false);
            try {
                String currentDir = new File(Launcher.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
                File jreDir = new File(currentDir, "jre");
                String javaExec;
                if (jreDir.exists() && jreDir.isDirectory()) {
                    javaExec = Paths.get(jreDir.getAbsolutePath(), "bin", "java").toString();
                } else {
                    javaExec = "java";
                }
                String clientPath = cacheDownloader.getClient();
                if(clientPath == null) {
                    return;
                }
                ProcessBuilder pb = new ProcessBuilder(javaExec, "-jar", clientPath);
                pb.directory(new File(clientPath).getParentFile());
                pb.start();
                System.exit(0);
            } catch (IOException | URISyntaxException ex) {
                ex.printStackTrace();
            }
        });
        buttonPanel.add(playButton);

        repairButton.setPreferredSize(new Dimension(100, 50));
        repairButton.setEnabled(false);
        repairButton.addActionListener(e -> {
            repairButton.setEnabled(false);
            new RepairSwingWorker(
                    cacheDownloader,
                    repairButton,
                    playButton,
                    statusLabel
            ).execute();
        });
        buttonPanel.add(repairButton);

        closeButton.setPreferredSize(new Dimension(100, 50));
        closeButton.addActionListener(a -> System.exit(0));
        buttonPanel.add(closeButton);

        jFrame.add(buttonPanel, "cell 0 3");

        jFrame.setSize(300, 250);
        jFrame.setResizable(false);
        jFrame.setUndecorated(true);
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
    }
}