package Scan;


import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.*;

class SignatureDatabase {
    private List<String> signatures;

    public SignatureDatabase() {
        this.signatures = new ArrayList<>();
    }

    public void addSignature(String signature) {
        signatures.add(signature);
    }

    public void removeSignature(String signature) {
        signatures.remove(signature);
    }

    public boolean containsSignature(String signature) {
        return signatures.contains(signature);
    }

    public List<String> getSignatures() {
        return signatures;
    }
}

class FileScanner {
    private SignatureDatabase database;

    public FileScanner(SignatureDatabase database) {
        this.database = database;
    }

    public boolean scanFile(File file) {
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                for (String signature : database.getSignatures()) {
                    if (line.contains(signature)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error scanning file: " + e.getMessage());
        }
        return false;
    }
}

class MalwareRemover {
    public boolean removeFile(File file) {
        return file.delete();
    }
}

class MalwareScanner {
    private FileScanner fileScanner;
    private MalwareRemover malwareRemover;

    public MalwareScanner(FileScanner fileScanner, MalwareRemover malwareRemover) {
        this.fileScanner = fileScanner;
        this.malwareRemover = malwareRemover;
    }

    public void scanAndRemoveMalware(File directory, JTextArea logArea) {
        if (directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                if (file.isFile()) {
                    if (fileScanner.scanFile(file)) {
                        logArea.append("Malware detected in file: " + file.getName() + "\n");
                        if (malwareRemover.removeFile(file)) {
                            logArea.append("Malware removed: " + file.getName() + "\n");
                        } else {
                            logArea.append("Failed to remove malware: " + file.getName() + "\n");
                        }
                    }
                } else if (file.isDirectory()) {
                    scanAndRemoveMalware(file, logArea);
                }
            }
        } else {
            logArea.append("Not a directory: " + directory.getName() + "\n");
        }
    }
}

public class Antivirus extends JFrame {
	private static final long serialVersionUID = 1L;
	private SignatureDatabase database;
    private FileScanner fileScanner;
    private MalwareRemover malwareRemover;
    private MalwareScanner malwareScanner;

    private JTextField directoryField;
    private JTextArea logArea;
    private JButton scanButton;
    private JButton browseButton;

    public Antivirus() {
        database = new SignatureDatabase();
        database.addSignature("djtconme");
        database.addSignature("abc123");

        fileScanner = new FileScanner(database);
        malwareRemover = new MalwareRemover();
        malwareScanner = new MalwareScanner(fileScanner, malwareRemover);

        initComponents();
    }

    private void initComponents() {
        setTitle("Malware Scanner");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        directoryField = new JTextField(20);
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        scanButton = new JButton("Scan and Remove");
        browseButton = new JButton("Browse");

        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int option = fileChooser.showOpenDialog(Antivirus.this);
                if (option == JFileChooser.APPROVE_OPTION) {
                    File selectedDirectory = fileChooser.getSelectedFile();
                    directoryField.setText(selectedDirectory.getAbsolutePath());
                }
            }
        });

        scanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String directoryPath = directoryField.getText();
                File directory = new File(directoryPath);
                if (directory.exists() && directory.isDirectory()) {
                    logArea.setText("Scanning directory: " + directoryPath + "\n");
                    malwareScanner.scanAndRemoveMalware(directory, logArea);
                } else {
                    logArea.setText("Invalid directory: " + directoryPath + "\n");
                }
            }
        });

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Directory:"), gbc);

        gbc.gridx = 1;
        panel.add(directoryField, gbc);

        gbc.gridx = 2;
        panel.add(browseButton, gbc);

        gbc.gridx = 3;
        panel.add(scanButton, gbc);

        getContentPane().add(panel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Antivirus().setVisible(true);
            }
        });
    }
}
