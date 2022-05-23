package xyz.soulspace.ui;

import xyz.soulspace.lexer.Lexer;
import xyz.soulspace.utils.FileOperation;

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
 * @create :2022-05-19 10:30
 */
public class Ex1UI {
    private JPanel panel1;
    private JButton inputFileButton;
    private JTextArea textArea1;
    private JButton outputFileButton;
    private JTextArea textArea2;
    private JTextField textField1;

    public Ex1UI() {
        textArea2.setEditable(false);
        inputFileButton.addActionListener(e -> showFileOpenDialog(panel1, textArea1));
        outputFileButton.addActionListener(e -> {
            textArea2.setText("");
            try {
                Lexer lexer = new Lexer(textArea1.getText());
                lexer.parse2();
                textArea2.append(lexer.getTokensToString());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Ex1UI");
        frame.setContentPane(new Ex1UI().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // 窗口居中
        frame.pack();
        frame.setSize(500, 500);
        frame.setVisible(true);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        panel1 = new JPanel();
    }

    private static void showFileOpenDialog(Component parent, JTextArea msgTextArea) {
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

            msgTextArea.append(sb.toString() + "\n");
        }
    }
}
