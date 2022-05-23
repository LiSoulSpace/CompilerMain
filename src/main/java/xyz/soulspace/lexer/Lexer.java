package xyz.soulspace.lexer;

import xyz.soulspace.symbols.SymbolTable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * @author :lisoulspace
 * @create :2022-05-10 21:28
 */
public class Lexer {
    char CHAR_BLANK = ' ', CHAR_END = '\n';
    private String srcBuffer = "";
    int srcLength = 0;
    String current = "";
    SymbolTable symbolTable = null;
    int cursor = 0;

    List<Token> tokenList;

    public Lexer() throws IOException {
        this("");
    }

    public Lexer(File f) throws IOException {
        this(f, null, null);
    }

    public Lexer(String buffer) throws IOException {
        this(null, buffer, null);
    }

    public Lexer(File f, String buffer, SymbolTable st) throws IOException {
        if (f == null) {
            this.srcBuffer = buffer;
        } else {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
            String temp;
            StringBuilder sb = new StringBuilder();
            while (true) {
                temp = bufferedReader.readLine();
                if (temp == null) break;
                if (temp.length() == 0) {
                    sb.append(CHAR_END);
                    continue;
                }
                sb.append(temp);
                if (!temp.substring(temp.length() - 1).equals(";")) {
                    sb.append(CHAR_END);
                }
            }
            srcBuffer = sb.toString();
            bufferedReader.close();
        }
        srcLength = srcBuffer.length();
        tokenList = new ArrayList<>();
        symbolTable = st;
    }

    public boolean parse() {
        tokenList.clear();
        DFA dfa = new DFA();
        boolean result = true;
        while (cursor < srcLength) {
            Token newToken = dfa.parseToken();
            switch (newToken.getTag()) {
                case Tag.ERROR -> result = false;
                case Tag.HLPCHR -> {
                }
                default -> {
                    tokenList.add(newToken);
                }
            }
        }
        return result;
    }

    public boolean parse2() {
        tokenList.clear();
        DFA dfa = new DFA();
        boolean result = true;
        while (cursor < srcLength) {
            Token newToken = dfa.parseToken();
            switch (newToken.getTag()) {
                case Tag.ERROR -> {
                    result = false;
                    tokenList.add(newToken);
                }
                case Tag.HLPCHR -> {
                }
                default -> {
                    tokenList.add(newToken);
                }
            }
        }
        return result;
    }

    public List<Token> getTokenList() {
        return tokenList;
    }

    public void setSrcBuffer(String srcBuffer) {
        this.srcBuffer = srcBuffer;
        srcLength = srcBuffer.length();
        cursor = 0;
        System.out.println(cursor + " " + srcLength);
    }

    public String getSrcBuffer() {
        return srcBuffer;
    }

    /**
     * 词法分析自动机
     */
    public class DFA {
        enum State {
            start, numInteger, numFloat, word, charF
        }

        public Token parseToken() {
            State curState = State.start;
            StringBuilder sb = new StringBuilder();
            boolean isEscape = false;
            while (true) {
                if (cursor == srcLength) {
                    return new Token(Tag.ERROR);
                }
                char head = srcBuffer.charAt(cursor);
                switch (curState) {
                    case start:
                        if (Character.isDigit(head)) {
                            sb.append(head);
                            curState = State.numInteger;
                        } else if (Character.isLetter(head) || head == '_') {
                            sb.append(head);
                            curState = State.word;
                        } else if (head == '\'') {
                            sb.append('\'');
                            curState = State.charF;
                        } else {
                            String temp = "";
                            if (cursor < srcLength - 1) {
                                temp = srcBuffer.substring(cursor, cursor + 2);
                            } else temp = srcBuffer.substring(cursor, cursor + 1);
                            Token token = containsOperator(temp);
                            if (!Objects.equals(token.getTag(), Tag.ERROR)) {
                                Operator operator = (Operator) token;
                                cursor += operator.width();
                                return operator;
                            } else if (Character.isWhitespace(head)) {
                                cursor++;
                                return new Token(Tag.HLPCHR);
                            } else {
                                return new Error(Tag.ERROR, "Wrong symbol recognition");
                            }
                        }
                        break;
                    case numInteger:
                        if (head == '.') {
                            sb.append('.');
                            curState = State.numFloat;
                        } else if (Character.isWhitespace(head)) {
                            return new IntNumber(Integer.parseInt(sb.toString()));
                        } else if (!Objects.equals(containsOperator(srcBuffer.substring(cursor, cursor + 1)).getTag(), Tag.ERROR)) {
                            return new IntNumber(Integer.parseInt(sb.toString()));
                        } else if (Character.isDigit(head)) {
                            sb.append(head);
                        } else {
                            return new Error(Tag.ERROR, "Wrong int recognition");
                        }
                        break;
                    case numFloat:
                        if (head == '.') {
                            cursor++;
                            return new Error(Tag.ERROR, "extra decimal point");
                        } else if (Character.isWhitespace(head)) {
                            return new FloatNumber(Float.parseFloat(sb.toString()));
                        } else if (!Objects.equals(containsOperator(srcBuffer.substring(cursor, cursor + 1)).getTag(), Tag.ERROR)) {
                            return new FloatNumber(Float.parseFloat(sb.toString()));
                        } else if (Character.isDigit(head)) {
                            sb.append(head);
                        } else {
                            return new Error(Tag.ERROR, "Wrong float recognition!");
                        }
                        break;
                    case word:
                        if (Character.isWhitespace(head)) {
                            Token token = Keyword.isKeyword(sb.toString());
                            if (!Objects.equals(token.getTag(), Tag.ERROR)) {
                                return token;
                            }
                            token = Type.isType(sb.toString());
                            if (!Objects.equals(token.getTag(), Tag.ERROR)) {
                                return token;
                            }
                            return new Id(sb.toString());
                        }
                        if (!Objects.equals(containsOperator(srcBuffer.substring(cursor, cursor + 1)).getTag(), Tag.ERROR)) {
                            Token token = Keyword.isKeyword(sb.toString());
                            if (!Objects.equals(token.getTag(), Tag.ERROR)) {
                                return token;
                            }
                            return new Id(sb.toString());
                        }
                        if (Character.isDigit(head) || Character.isLetter(head) || head == '_') {
                            sb.append(head);
                        } else {
                            return new Error(Tag.ERROR, "Wrong word recognition!");
                        }
                        break;
                    case charF:
                        if (isWhiteChar(head)) {
                            return new Error(Tag.ERROR, "No right char!");
                        } else if (head == '\'') {
                            if (sb.length() == 1) {
                                return new Error(Tag.ERROR, "Null character!");
                            } else if (sb.length() == 2) {
                                sb.append('\'');
                                cursor++;
                                return new CharConstant(sb.toString());
                            }
                        } else if (head == '\\') {
                            isEscape = true;
                            sb.append('\\');
                        } else if (Character.isLetter(head)) {
                            if (isEscape && sb.length() == 2) {
                                sb.append(head);
                            } else if (isEscape) {
                                return new Token(Tag.ERROR);
                            } else if (sb.length() == 1) {
                                sb.append(head);
                            } else return new Error(Tag.ERROR, "Error with char!");
                        }
                        break;
                }
                cursor++;
            }
        }

        public Token containsOperator(String src) {
            if (src.length() > 2) {
                src = src.substring(0, 1);
            }
            return Operator.isOperator(src);
        }

        public boolean isWhiteChar(char ch) {
            return Character.isWhitespace(ch) && ch != ' ';
        }
    }

    public String getTokensToString() {
        StringBuilder sb = new StringBuilder();
        for (Token token : tokenList) {
            String s = "";
            if (token.getTag().equals("op")) {
                if (((Operator) token).isDelimiter()) {
                    s = "delimiter";
                } else {
                    s = token.getTag();
                }
            } else s = token.getTag();
            sb.append(s).append(" ").append(token.getValue()).append('\n');
        }
        return sb.toString();
    }
}
