package com.jrlgs.lox;


public class AstPrinter implements Expr.Visitor<String>{
    // the new functionality to "add" to "Expr" classes...
    public String print(Expr expr){
        // Each Expr implements an "accept" method that will
        // call this.visit<NameExpr>()
        return expr.accept(this);
    }


    // And here we define those visit<NameExpr>() methods.
    // Each Expr class passes itself so that this visitor can
    // access its methods and (maybe) data....

    @Override
    public String visitBinaryExpr(Expr.Binary binary) {
        return (binary.left.accept(this) + binary.operator.toString() + binary.right.accept(this));
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping grouping) {
        return "";
    }

    @Override
    public String visitLiteralExpr(Expr.Literal literal) {
        // How do we print a literal expression like <"hello"> or <class>
        if (literal.value == null) return "nil";
        return literal.value.toString();
    }

    @Override
    public String visitUnaryExpr(Expr.Unary unary) {
        return "";
    }
}
