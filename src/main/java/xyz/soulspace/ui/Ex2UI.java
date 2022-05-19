package xyz.soulspace.ui;

import xyz.soulspace.grammar.GrammarTable;
import xyz.soulspace.grammar.LR1Set;
import xyz.soulspace.grammar.LRParser;
import xyz.soulspace.lexer.Lexer;

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
    private JButton outputButton;
    private JTextArea inputArea;
    private JTextArea codeArea;
    private JTextArea actionArea;
    private JTextArea firstSetArea;
    private JTextArea followArea;
    private JTextArea gotoArea;
    private JTextArea groupsArea;
    private JTextArea infoArea;
    private JButton codeInputButton;
    private String grammarFileName;
    private String codeFileName;
    private Lexer lexer;

    public Ex2UI() throws IOException {
        firstSetArea.setEditable(false);
        followArea.setEditable(false);
        codeArea.setEditable(false);
        groupsArea.setEditable(false);
        actionArea.setEditable(false);
        gotoArea.setEditable(false);
        infoArea.setEditable(false);
        codeArea.setEditable(false);
        infoArea.setText("Information Output:\n");
        groupsArea.setText("Groups:\n");
        firstSetArea.setText("first set : \n");
        followArea.setText("follow set : \n");
        actionArea.setText("ActionTable:\n");
        gotoArea.setText("GotoTable:\n");
        lexer = new Lexer();
        inputGrammarButton.addActionListener(e -> {
            showFileOpenDialog(panel1, inputArea);
            infoArea.append("Open grammar file: " + grammarFileName + '\n');
        });
        outputButton.addActionListener(e -> {
            if (lexer.getSrcBuffer().equals("")) {
                infoArea.append("Please input the code by enter the button\n");
            }
            GrammarTable grammarTable = new GrammarTable(grammarFileName);
            try {
                grammarTable.cookGrammar();
                firstSetArea.setText("first set : \n");
                firstSetArea.append(grammarTable.firstSetToString());
                followArea.setText("follow set : \n");
                followArea.append(grammarTable.followSetToString());
                codeArea.append("\n");
                groupsArea.setText("Groups:\n");
                LR1Set lr1Set = new LR1Set(grammarTable.getGrammars(), grammarTable.getFirstSet(), grammarTable.getFollowSet());
                lr1Set.genItemGroups();
                groupsArea.append(lr1Set.itemGroupToString());
                actionArea.setText("ActionTable:\n");
                actionArea.append(lr1Set.actionTableToString());
                gotoArea.setText("GotoTable:\n");
                gotoArea.append(lr1Set.gotoTableToString());
                lexer.parse();
                LRParser lp = new LRParser(lr1Set);
                System.out.println(lexer.getTokensToString());
                boolean result = lp.parse(lexer.getTokenList());
                if (result)infoArea.append("Parse over and all right! No error!");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        codeInputButton.addActionListener(e -> {
            showCodeFile(panel1, codeArea);
            infoArea.append("Open code file: " + codeFileName + '\n');
            lexer.setSrcBuffer(codeArea.getText());
            System.out.println(lexer.getSrcBuffer());
        });
    }

    public static void main(String[] args) throws IOException{
        JFrame frame = new JFrame("Ex2UI");
        frame.setContentPane(new Ex2UI().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(1000, 800);
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

            msgTextArea.setText(sb.toString() + "\n");
        }
    }

    private void showCodeFile(Component parent, JTextArea msgTextArea) {
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

            this.codeFileName = file.getName();

            msgTextArea.setText(sb.toString() + "\n");
        }
    }
}
