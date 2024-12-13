package com.jrlgs.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jrlgs.lox.TokenType.*;

public class Scanner {

    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and", AND);
        keywords.put("class", CLASS);
        keywords.put("else", ELSE);
        keywords.put("false", FALSE);
        keywords.put("for", FOR);
        keywords.put("fun", FUN);
        keywords.put("if", IF);
        keywords.put("nil", NIL);
        keywords.put("or", OR);
        keywords.put("print", PRINT);
        keywords.put("return", RETURN);
        keywords.put("super", SUPER);
        keywords.put("this", THIS);
        keywords.put("true", TRUE);
        keywords.put("var", VAR);
        keywords.put("while", WHILE);
    }

    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    private int start = 0;
    private int current = 0;
    private int line = 1;

    public Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch(c) {
            case '(' : addToken(LEFT_PAREN); break;
            case ')' : addToken(RIGHT_PAREN); break;
            case '{' : addToken(LEFT_BRACE); break;
            case '}' : addToken(RIGHT_BRACE); break;
            case ',' : addToken(COMMA); break;
            case '.' : addToken(DOT); break;
            case '-' : addToken(MINUS); break;
            case '+' : addToken(PLUS); break;
            case ';' : addToken(SEMICOLON); break;
            case '*' : addToken(STAR); break;
            case '?' : addToken(QUESTION); break;
            case ':' : addToken(COLON); break;
            // 2-character lexemes (i.e boolean operators)
            case '!' : addToken(match('=') ? BANG_EQUAL : BANG); break;
            case '=' : addToken(match('=') ? EQUAL_EQUAL : EQUAL); break;
            case '<' : addToken(match('=') ? LESS_EQUAL : LESS); break;
            case '>' : addToken(match('=') ? GREATER_EQUAL : GREATER); break;
            case '/' :
                if (match('/')) {
                    // the line is a comment.. ignore all characters in this line.
                    while(peek() != '\n' && !isAtEnd()) {
                        advance();
                    }
                } else {
                    // i.e. division, a/b
                    addToken(SLASH);
                }
                break;
            // skipped lexemes
            case ' ' :
            case '\r':
            case '\t':
                break;
            // new line
            case '\n' : line ++; break;
            // strings
            case '"' : string(); break;
            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                   identifier();
                }
                else {
                    Lox.error(line, "Unexpected character: " + "'" + c + "'");
                }
                break;
        }
    }


    // basic com.lox.Lox.Scanner operations
    private char advance() {
        return source.charAt(current++);
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    // matching operations

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()) {
            Lox.error(line,"Unterminated String.");
            return;
        }
        // we only reach here on the closing "
        advance();
        // cut up from the left pointer to the right pointer excluding the " "
        String value = source.substring(start + 1, current - 1);
        // and create a STRING token with its contents as value.
        addToken(STRING, value);
    }

    private void number(){
        // check if the next chars are digits
        while (isDigit(peek())) advance();

        // if we peek a . we need to peek one extra character
        if (peek() == '.' && isDigit(peekNext())){
            advance(); //consume .
            while(isDigit(peek())) advance();
        }

        // once we peek the next non-digit, we can end the lexing
        addToken(NUMBER,
                Double.parseDouble(source.substring(start, current)));
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);

        // the idetifier is either a keyword or a symbol name
        if (type == null) type = IDENTIFIER;

        addToken(type);
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current)!= expected) return false;
        // Only consume the character if it is the one we expect.
        // e.g. for ! we would expect !=  if so, consume, and so.
        current++;

        return true;
    }

    // utilities

    private boolean isDigit(char c) {
        return c >= '0' && c <+ '9';
    }

    private boolean isAlpha(char c ) {
        return (c >= 'a' && c <= 'z' ||
                c >= 'A' && c <= 'Z' ||
                c == '_');
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    // Add tokens
    private void addToken(TokenType type) {
       addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String lexemeText = source.substring(start, current);
        tokens.add(new Token(type, lexemeText, literal, line));
    }


}
