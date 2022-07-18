package com.yiban.javaBase.dev.antlr.parser.code;

import com.yiban.javaBase.dev.antlr.parser.spark_g4.SqlBaseLexer;
import com.yiban.javaBase.dev.antlr.parser.spark_g4.SqlBaseParser;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.Map;
import java.util.Set;
public class SparkSqlUtil {
    public static Map<String, Set<String>> getDataBaseTablenameAndOper(String sql){
        SqlBaseLexer lexer = new SqlBaseLexer(new ANTLRNoCaseStringStream(sql));

        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        SqlBaseParser parser = new SqlBaseParser(tokenStream);
        ParseTreeWalker walker = new ParseTreeWalker();
        MySqlBaseBaseListener mySqlBaseBaseListener = new MySqlBaseBaseListener();

        walker.walk(mySqlBaseBaseListener, parser.statement());

        return mySqlBaseBaseListener.getDataBaseTablenameAndOper();
    }

    public static Map<String, Set<String>> getDataBaseTablenameAndOperWithSubQuery(String sql){
        SqlBaseLexer lexer = new SqlBaseLexer(new ANTLRNoCaseStringStream(sql));

        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        SqlBaseParser parser = new SqlBaseParser(tokenStream);
        ParseTreeWalker walker = new ParseTreeWalker();
        MySqlBaseBaseListener mySqlBaseBaseListener = new MySqlBaseBaseListener();

        walker.walk(mySqlBaseBaseListener, parser.statement());

        return mySqlBaseBaseListener.getDataBaseTablenameAndOper();
    }

    public static void main(String[] args) {
        String sql = "select a.*,b.* from table1 as a,(select * from table2 where id=2) as b where a.id=b.id and a.age=18 and a.id=1 and a.name=\"bb\";";
//        Map<String, Set<String>> map = getDataBaseTablenameAndOper(sql);
//        for (Map.Entry<String, Set<String>> entry :
//                map.entrySet()) {
//            System.out.println("entry.getKey() = " + entry.getKey() + " -> " + entry.getValue().stream().findFirst().get());
//        }
        SqlBaseLexer lexer = new SqlBaseLexer(new ANTLRNoCaseStringStream(sql));
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        SqlBaseParser parser = new SqlBaseParser(tokenStream);
        SqlBaseParser.QuerySpecificationContext ctx = parser.querySpecification();
        System.out.println("ctx = " + ctx.where.getText());
        System.out.println("ctx.getRuleIndex() = " + ctx.where.getChildCount());
        System.out.println("-----------------");
        for (int i = 0; i < ctx.where.getChildCount(); i++) {
            ParseTree tree = ctx.where.getChild(i);
            recursion(tree);
        }

    }
    private static void recursion(ParseTree tree){
        for (int i = 0; i < tree.getChildCount(); i++) {
            ParseTree temp = tree.getChild(i);
            if (tree.getChildCount() == 0 || tree.getChildCount() == 1) {
                System.out.println("tree.getText() = " + tree.getText());
            }else {
                recursion(temp);
            }
        }
    }
}
