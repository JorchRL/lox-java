package com.jrlgs.lox;

import java.util.List;

import static com.jrlgs.lox.TokenType.*;
import static java.time.temporal.TemporalAdjusters.previous;

public class Parser {
    private final List<Token> tokens;
    private final int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }
    /* Precedence rules (top to bottom)

    Expression  -> Equality ;
    Equality    -> Comparison ( ("!=" | "==") Comparison )* ;
    Comparision -> Term ( ("<" | ">" | ">=" | "<=") Term )* ;
    Term        -> Factor ( ( "+" | "-" ) Factor )*;
    Factor      -> Unary ( ( "/" | "*" ) Unary )*;
    Unary       -> ( "!" | "-" ) Unary | Primary;
    Primary     -> NUMBER | STRING | "true" | "false" | "nil" | "(" Expression ")" ;
     */
    private Expr expression() {
        // Expression  -> Equality ;
        return equality();
    }

    private Expr equality() {
        // Equality    -> Comparison ( ("!=" | "==") Comparison )* ;
        Expr expr = comparison();
        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
           Expr right =  comparison();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Token previous() {
        return null;
    }

    private boolean match(TokenType tokenType, TokenType tokenType1) {
        return false;
    }

    private Expr comparison() {
        // Comparision -> Term ( ("<" | ">" | ">=" | "<=") Term )* ;
    }
}
