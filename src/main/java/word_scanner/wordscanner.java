package word_scanner;

import java.io.*;
import java.util.ArrayList;

import word_scanner.wordscanner;

public class wordscanner {
    public static ArrayList<String> strings = new ArrayList<String>();
    private String keyWord[] = {"if", "else", "while", "int", "float"};
    private char ch;

    private String userDir = System.getProperty("user.dir");

    boolean isDigits(char digit) {
        if (digit >= '0' && digit <= '9') {
            return true;
        } else
            return false;
    }

    boolean isLetter(char letter) {
        if ((letter >= 'a' && letter <= 'z') || (letter >= 'A' && letter <= 'Z'))
            return true;
        else
            return false;
    }

    boolean isKeyword(String str) {
        for (int i = 0; i < keyWord.length; i++) {
            if (keyWord[i].equals(str))
                return true;
        }
        return false;
    }

    boolean isEnd(char end) {
        if (end == ';')
            return true;
        else
            return false;
    }

    void wordanalyze(char[] chars) {
        int line = 1;
        String arr = "";
        String result;
        for (int i = 0; i < chars.length; i++) {

            ch = chars[i];
            arr = "";
            if (ch == ' ' || ch == '\t' || ch == '\r') {
            } else if (ch == '\n') {
                line++;
            } else if (isEnd(ch)) {
                result = "token is (" + "SEMI," + ch + ")";
                System.out.println(result);
                strings.add(result);
            } else if (isLetter(ch)) {
                while (isLetter(ch) || isDigits(ch)) {
                    arr += ch;
                    ch = chars[++i];
                }
                i--;
                if (isKeyword(arr)) {
                    result = "token is (" + "keyword," + arr + ")";
                    System.out.println(result);
                    strings.add(result);
                } else {
                    result = "token is (" + "id," + arr + ")";
                    System.out.println(result);
                    strings.add(result);
                }
            } else if (isDigits(ch) || (ch == '.')) {
                while (isDigits(ch)) {
                    arr = arr + ch;
                    ch = chars[++i];
                }
                result = "token is (" + "const," + arr + ")";
                System.out.println(result);
                strings.add(result);
            } else {
                if (ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '>' || ch == '<' || ch == '=' || ch == '!') {
                    result = "token is (" + "op," + ch + ")";
                    System.out.println(result);
                    strings.add(result);
                }

            }
        }
    }

    void write(ArrayList<String> strings) {
        String path = userDir + "/src/main/resources/result/Token.txt";
        File file = new File(path);
        //���û���ļ��ʹ���
        if (!file.isFile()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String l : strings) {
            try {
                writer.write(l + "\r\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            writer.close();
            System.out.println("ɨ��ɹ�");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void scanner() throws IOException {

        File file = new File(userDir + "/src/main/resources/srcinput.txt");
        FileReader reader = new FileReader(file);
        int length = (int) file.length();
        char buf[] = new char[length + 1];
        reader.read(buf);
        reader.close();
        wordscanner l = new wordscanner();
        l.wordanalyze(buf);
        l.write(strings);
    }
}
