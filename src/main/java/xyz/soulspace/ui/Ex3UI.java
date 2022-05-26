package xyz.soulspace.ui;

import xyz.soulspace.grammar.GrammarTable;
import xyz.soulspace.grammar.LR1Set;
import xyz.soulspace.grammar.LRParser;
import xyz.soulspace.lexer.Lexer;
import xyz.soulspace.semantic.GrammarTree;
import xyz.soulspace.semantic.Rule;
import xyz.soulspace.symbols.SymbolTable;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Ex3UI {
    private JPanel panel1;
    private JButton grammarInputButton;
    private JButton codeInputButton;
    private JTextArea grammarArea;
    private JTextArea codeArea;
    private JTextArea threeAddressArea;
    private JTextArea symbolTableArea;
    private JTextArea infoArea;
    private JButton outputButton;
    private String grammarFileName;
    private String codeFileName;
    private Lexer lexer;

    public Ex3UI() throws IOException {
        this.lexer = new Lexer();

        grammarInputButton.addActionListener(e -> {
            showFileOpenDialog(panel1, grammarArea);
            infoArea.append("Open grammar file: " + grammarFileName + '\n');
        });
        codeInputButton.addActionListener(e -> {
            showCodeFile(panel1, codeArea);
            infoArea.append("Open code file: " + codeFileName + '\n');
            lexer.setSrcBuffer(codeArea.getText());
            System.out.println(lexer.getSrcBuffer());
        });
        outputButton.addActionListener(e -> {
            if (lexer.getSrcBuffer().equals("")) {
                infoArea.append("Please input the code by enter the button\n");
            }
            GrammarTable grammarTable = new GrammarTable(grammarFileName);
            try {
                symbolTableArea.setText("");
                lexer.setSrcBuffer(codeArea.getText());
                grammarTable.cookGrammar();
                codeArea.append("\n");
                LR1Set lr1Set = new LR1Set(grammarTable.getGrammars(), grammarTable.getFirstSet(), grammarTable.getFollowSet());
                lr1Set.genItemGroups();
                GrammarTree grammarTree = new GrammarTree();
                lexer.parse();
                LRParser lp = new LRParser(lr1Set);
                System.out.println(lexer.getTokensToString());
                boolean result = lp.parse(lexer.getTokenList(), grammarTree);
                if (result) {
                    infoArea.append("Parse over and all right! No error!");
                    threeAddressArea.append(Rule.threeAddressToString());
                    symbolTableArea.append(SymbolTable.printSymbolTableToString());
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        threeAddressArea.setEditable(false);
        symbolTableArea.setEditable(false);
        infoArea.setEditable(false);
    }

    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("Ex3UI");
        frame.setContentPane(new Ex3UI().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(1000, 600);
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
