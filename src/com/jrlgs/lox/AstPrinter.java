package com.jrlgs.lox;


public class AstPrinter implements Expr.Visitor<String> {

    public static void main(String[] args) {
        Expr.Unary left = new Expr.Unary(
                        new Token(TokenType.MINUS, "-", null, 1),
                        new Expr.Literal(32));
        Token tok = new Token(TokenType.STAR, "*", null, 1);
        Expr.Grouping right = new Expr.Grouping( new Expr.Literal(53) );

        Expr.Binary testExpr = new Expr.Binary(left, tok, right);

        System.out.println(new AstPrinter().print(testExpr));

    }

    // the new functionality to "add" to "Expr" classes...
    public String print(Expr expr) {
        // Each Expr implements an "accept" method that will
        // call this.visit<NameExpr>()
        return expr.accept(this);
    }


    // And here we define those visit<NameExpr>() methods.
    // Each Expr class passes itself so that this visitor can
    // access its methods and (maybe) data....

    @Override
    public String visitBinaryExpr(Expr.Binary binary) {
        return parenthesize(binary.operator.lexeme, binary.left, binary.right);
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping grouping) {
        return parenthesize("group", grouping.expression);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal literal) {
        // How do we print a literal expression like <"hello"> or <class>
        if (literal.value == null) return "nil";
        return literal.value.toString();
    }

    @Override
    public String visitUnaryExpr(Expr.Unary unary) {
        return parenthesize(unary.operator.lexeme, unary.right);
    }

    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(name);
        for (Expr expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");
        return builder.toString();
    }
}
