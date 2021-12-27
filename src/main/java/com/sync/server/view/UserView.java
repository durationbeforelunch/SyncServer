package com.sync.server.view;

import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.io.File;

@Component
public class UserView {

    private JFrame mainFrame;
    private JPanel jPanel;
    private JLabel chosenPath;
    private JLabel discoveryStatus;
    private JTextArea logTextArea;

    @Getter
    private JFileChooser fileChooser;
    @Getter
    private JButton startServerButton;
    @Getter
    private JButton folderChooserButton;

    // Инициализация UI
    public void init() {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        initFrame();
        initPanel();
        initButton();
        initLabels();
        initLogTextArea();
        initFileChooser();

        mainFrame.setVisible(true);

    }

    private void initFrame() {

        short size = 500;

        mainFrame = new JFrame("Server Sync App");
        mainFrame.setSize(size, size);
        mainFrame.setResizable(false);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }

    private void initPanel() {
        jPanel = new JPanel(null);
        jPanel.setBackground(Color.WHITE);

        mainFrame.add(jPanel);
    }

    private void initButton() {


        startServerButton = new JButton("Включить обнаружение");
        folderChooserButton = new JButton("Выбор директории");
        startServerButton.setBounds(50, 320, 200, 100);
        folderChooserButton.setBounds(250, 320, 200, 100);

        jPanel.add(folderChooserButton);
        jPanel.add(startServerButton);

    }

    private void initLogTextArea() {

        logTextArea = new JTextArea();
        logTextArea.setEditable(false);
        logTextArea.setFont(new Font(logTextArea.getFont().getName(), Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(logTextArea);
        scrollPane.setBounds(0, 30, 485, 275);

        jPanel.add(scrollPane);

    }

    private void initFileChooser() {

        fileChooser = new JFileChooser();

        fileChooser.setCurrentDirectory(new File("."));
        fileChooser.setDialogTitle("Выберите папку для синхронизации");
        // Ограничиваем выбор только папок
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        fileChooser.setAcceptAllFileFilterUsed(false);

    }

    private void initLabels() {

        chosenPath = new JLabel("Выберите директорию");
        discoveryStatus = new JLabel("Обнаружение : Выключено");

        chosenPath.setBounds(1, 1, 500, 20);
        discoveryStatus.setBounds(340, 440, 150, 20);

        jPanel.add(chosenPath);
        jPanel.add(discoveryStatus);

    }

    public void setDiscoveryStatus(boolean status) {
        if (status) {
            discoveryStatus.setText("Обнаружение : Включено");
        } else {
            discoveryStatus.setText("Обнаружение : Выключено");
        }
    }

    public void setChosenPath(String path) {
        chosenPath.setText(path);
    }

    public void appendLog(String text) {
        logTextArea.append(String.format("%s%n", text));
    }

}