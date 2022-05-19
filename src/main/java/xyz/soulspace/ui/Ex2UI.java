package xyz.soulspace.ui;

import xyz.soulspace.grammar.GrammarTable;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author :lisoulspace
 * @create :2022-05-19 11:38
 */
public class Ex2UI {
    private JPanel panel1;
    private JButton inputGrammarButton;
    private JTextField textField1;
    private JButton outputButton;
    private JTextArea textArea1;
    private JTextArea textArea2;

    private String grammarFileName;

    public Ex2UI() {
        textArea2.setEditable(false);
        inputGrammarButton.addActionListener(e -> {
            showFileOpenDialog(panel1, textArea1);
            textField1.setText(grammarFileName);
        });
        outputButton.addActionListener(e -> {

            GrammarTable grammarTable = new GrammarTable(grammarFileName);
            try {
                grammarTable.cookGrammar();
                textArea2.append("first set : \n");
                textArea2.append(grammarTable.firstSetToString());
                textArea2.append("\n\nfollow set : \n");
                textArea2.append(grammarTable.followSetToString());
                textArea2.append("\n");

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Ex2UI");
        frame.setContentPane(new Ex2UI().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(500, 500);
        frame.setVisible(true);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        panel1 = new JPanel();
    }

    private void showFileOpenDialog(Component parent, JTextArea msgTextArea) {
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setCurrentDirectory(new File("."));

        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setMultiSelectionEnabled(true);

        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("zip(*.zip, *.rar)", "zip", "rar"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("image(*.c, *.cpp, *.txt)", "c", "cpp", "txt"));

        int result = fileChooser.showOpenDialog(parent);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            StringBuilder sb = new StringBuilder();
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                String temp;
                while (true) {
                    temp = bufferedReader.readLine();
                    if (temp == null) break;
                    sb.append(temp).append('\n');
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.grammarFileName = file.getName();

            msgTextArea.append(sb.toString() + "\n");
        }
    }
}
