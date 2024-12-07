import java.util.ArrayList;
import java.util.List;


public class Scanner {
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
        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch(c) {
            case '(' : addToken(TokenType.LEFT_PAREN); break;
            case ')' : addToken(TokenType.RIGHT_PAREN); break;
            case '{' : addToken(TokenType.LEFT_BRACE); break;
            case '}' : addToken(TokenType.RIGHT_BRACE); break;
            case ',' : addToken(TokenType.COMMA); break;
            case '.' : addToken(TokenType.DOT); break;
            case '-' : addToken(TokenType.MINUS); break;
            case '+' : addToken(TokenType.PLUS); break;
            case ';' : addToken(TokenType.SEMICOLON); break;
            case '*' : addToken(TokenType.STAR); break;
            // 2-character lexemes (i.e boolean operators)
            case '!' :
                addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
                break;
            case '=' :
                addToken(match('=') ? TokenType.EQUAL : TokenType.EQUAL_EQUAL);
                break;
            case '<' :
                addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
                break;
            case '>' :
                addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
                break;
            case '/' :
                if (match('/')) {
                    // the line is a comment.. ignore all characters in this line.
                    while(peek() != '\n' && !isAtEnd()) {
                        advance();
                    }
                } else {
                    // i.e. division, a/b
                    addToken(TokenType.SLASH);
                }
                break;
            // skipped lexemes
            case ' ' :
            case '\r':
            case '\t':
                break;
            // new line
            case '\n' :
                line ++;
                break;
            default:
                Lox.error(line, "Unexpected character");
                break;
        }
    }


    // basic Scanner operations
    private char advance() {
        return source.charAt(current++);
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
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
    private boolean isAtEnd() {
        return current >= source.length();
    }

    // Add tokens
    private void addToken(TokenType type) {
       addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }


}
