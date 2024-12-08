package com.jrlgs.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            // use System.out.println instead of PrintWriter.println
            PrintWriter writer = new PrintWriter(System.out);
            defineAst(writer, "Expr", Arrays.asList(
                    "Binary   : Expr left, Token operator, Expr right",
                    "Grouping : Expr expression",
                    "Literal  : Object value",
                    "Unary    : Token operator, Expr right"
            ));
        } else if (args.length == 1) {
            String outputDir = args[0];
            String baseName = "Expr";
            String path = outputDir + "/" + baseName + ".java";
            PrintWriter writer = new PrintWriter(path, "UTF-8");

            defineAst(writer, baseName, Arrays.asList(
                    "Binary   : Expr left, Token operator, Expr right",
                    "Grouping : Expr expression",
                    "Literal  : Object value",
                    "Unary    : Token operator, Expr right"
            ));
        } else {
            System.err.println("Usage: generate_ast [flags] <output directory>");
            System.exit(64);
        }

    }

    private static void defineAst(PrintWriter writer, String baseName, List<String> types) throws IOException {

        writer.println("package com.jrlgs.lox;");
        writer.println();
        writer.println("import java.util.List;");
        writer.println();
        writer.println("public abstract class " + baseName + " {");
        defineVisitor(writer, baseName, types);

        writer.println("  abstract <R> R accept (Visitor<R> visitor);");
        // AST classes
        for (String type : types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
        }


        writer.println("}");
        writer.close();
    }

    private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
        writer.println("  interface Visitor<R> {");
        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            writer.println("    R visit" + typeName + baseName + "(" + typeName +  " " + typeName.toLowerCase() + ");");
        }
        writer.println("  }");
    }

    private static void defineType(PrintWriter writer, String baseName, String className, String fieldList) throws IOException {

        writer.println("  static class " + className + " extends " + baseName + " {");
        String[] fields = fieldList.split(", ");
        for (String field : fields) {
            // private final <fieldType> <fieldName>;
            String fieldType = field.split(" ")[0];
            String fieldName = field.split(" ")[1];
            writer.println("   final " + fieldType + " " + fieldName + ";");
        }
        writer.println("    " + className + "(" + fieldList + ") {");
        for (String field : fields) {
            // this.<field> = field;
            String fieldName = field.split(" ")[1];
            writer.println("      this." + fieldName + " = " + fieldName + ";");
        }
        writer.println("    }");
        writer.println("    @Override");
        writer.println("    <R> R accept(Visitor<R> visitor) {");
        writer.println("        return visitor.visit" + className + baseName +"(this);");
        writer.println("      }");
        writer.println("  }");
    }
}
