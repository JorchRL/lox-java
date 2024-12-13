package com.jrlgs.lox;

import java.util.List;

import static com.jrlgs.lox.TokenType.*;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    // Ask the Parser to parse its list of tokens.
    public Expr parse() {
        try {
            return expression();
        } catch(ParseError error) {
            return null;
        }
    }

    /* Precedence rules (top to bottom)

    Expression  -> Ternary;
    Ternary     -> Equality "?" Equality ":" Equality ;
    Equality    -> Comparison ( ("!=" | "==") Comparison )* ;
    Comparision -> Term ( ("<" | ">" | ">=" | "<=") Term )* ;
    Term        -> Factor ( ( "+" | "-" ) Factor )*;
    Factor      -> Unary ( ( "/" | "*" ) Unary )*;
    Unary       -> ( "!" | "-" ) Unary | Primary;
    Primary     -> NUMBER | STRING | "true" | "false" | "nil" | "(" Expression ")" ;
     */
    private Expr expression() {
        // Expression  -> Equality ;
        return ternary();
    }

    private Expr ternary () {
        // Ternary -> Equality "?" Equality ":" Equality ;
        Expr expr = equality();
        if (peek().type == QUESTION) {
            advance();
            Token condOp = previous();
            Expr mid = equality();
            advance();
            Token elseOp = previous();
            if (elseOp.type != COLON) {
                throw error(elseOp, "Expected ':' ternary operator");
            }
            Expr right = equality();
            expr = new Expr.Ternary(expr, condOp, mid, elseOp, right);
        }
        return expr;
    }

    private Expr equality() {
        // Equality    -> Comparison ( ("!=" | "==") Comparison )* ;
        Expr expr = comparison();
        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr comparison() {
        // Comparision -> Term ( ("<" | ">" | ">=" | "<=") Term )* ;
        Expr expr = term();
        while (match(LESS, GREATER, LESS_EQUAL, GREATER_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr term() {

        // Term -> Factor ( ( "+" | "-" ) Factor )*;
        Expr expr = factor();
        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr factor() {
        // Factor -> Unary ( ( "/" | "*" ) Unary )*;
        Expr expr = unary();
        while (match(SLASH, STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr unary() {
        // Unary -> ( "!" | "-" ) Unary | Primary;
        if (match(BANG, MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }
        return primary();
    }

    private Expr primary() {
        // Primary -> NUMBER | STRING | "true" | "false" | "nil" | "(" Expression ")" ;
        if (match(TRUE)) return new Expr.Literal(true);
        if (match(FALSE)) return new Expr.Literal(false);
        if (match(NIL)) return new Expr.Literal(null);

        if (match(NUMBER, STRING)) return new Expr.Literal(previous().literal);

        //case of (expression)
        if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }
        // if the last possible case does not match, then we have a token
        // that cannot be the beginning of an expresssion. Thus an error.
        throw error(peek(), "Expect an expression.");
    }

    private void consume(TokenType type, String message) {
        // check if the next token to be parsed is of the expected type.
        if (check(type)) advance();
        // if not, then panic
        throw error(peek(), message);
    }

    private ParseError error(Token token, String message) {
        Lox.error(token, message);
        return new ParseError();
    }

    // UTILS
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    // advance Token pointer
    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    // check if next token is of TokenType type
    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    // look at next token in queue
    private Token peek() {
        return tokens.get(current);
    }


    // look at prev token in queue
    private Token previous() {
        return tokens.get(current - 1);
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private static class ParseError extends RuntimeException {
    }

    private void synchronize() {
        // advance past the erroneous token
        advance();
        // while there are still tokens (not at EOF)
        // check the next token for a "Statement boundary"
        while (!isAtEnd()) {
            // one of those is the ending ;
            if (peek().type == SEMICOLON) return;
            // Or any token that implies the start of a new statement
            switch (peek().type) {
                case FUN:
                case CLASS:
                case VAR:
                case WHILE:
                case FOR:
                case IF:
                case PRINT:
                case RETURN:
                    return;
            }
            advance();
        }
    }
}
