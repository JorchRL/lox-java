import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Scanner {

    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and", TokenType.AND);
        keywords.put("class", TokenType.CLASS);
        keywords.put("else", TokenType.ELSE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("for", TokenType.FOR);
        keywords.put("fun", TokenType.FUN);
        keywords.put("if", TokenType.IF);
        keywords.put("nil", TokenType.NIL);
        keywords.put("or", TokenType.OR);
        keywords.put("print", TokenType.PRINT);
        keywords.put("return", TokenType.RETURN);
        keywords.put("super", TokenType.SUPER);
        keywords.put("this", TokenType.THIS);
        keywords.put("true", TokenType.TRUE);
        keywords.put("var", TokenType.VAR);
        keywords.put("while", TokenType.WHILE);
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
            case '!' : addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG); break;
            case '=' : addToken(match('=') ? TokenType.EQUAL : TokenType.EQUAL_EQUAL); break;
            case '<' : addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS); break;
            case '>' : addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER); break;
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
                    Lox.error(line, "Unexpected character");
                }
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
        addToken(TokenType.STRING, value);
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
        addToken(TokenType.NUMBER,
                Double.parseDouble(source.substring(start, current)));
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);

        // the idetifier is either a keyword or a symbol name
        if (type == null) type = TokenType.IDENTIFIER;

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
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }


}
