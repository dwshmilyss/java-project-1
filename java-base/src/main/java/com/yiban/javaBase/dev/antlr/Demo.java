package com.yiban.javaBase.dev.antlr;

import antlr.DslLexer;
import antlr.DslParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.IOException;

/**
 * @auther WEI.DUAN
 * @date 2021/6/6
 * @website http://blog.csdn.net/dwshmilyss
 * 1. 生成g4文件，在resource里面
 * 2. idea安装antlr插件
 * 3. 右键g4文件，点击configure antlr，配置output目录和语言：Java
 * 4. 右键g4文件，生成Java代码
 * 5.
 */
public class Demo {
    public static void main(String[] args) throws IOException {
        printParserTree();
    }

    /**
     * 打印解析树
     */
    public static void printParserTree() {
//        String sql= "Select 'abc' as a, `hahah` as c  From a aS table;";
        String sql= "select `id`, `version`, `name` from contact_group a;";
        ANTLRInputStream input = new ANTLRInputStream(sql);  //将输入转成antlr的input流
        DslLexer lexer = new DslLexer(input);  //词法分析
        CommonTokenStream tokens = new CommonTokenStream(lexer);  //转成token流
        DslParser parser = new DslParser(tokens); // 语法分析
//        DslParser.StaContext tree = parser.sta();  //获取某一个规则树，这里获取的是最外层的规则，也可以通过sql()获取sql规则树......
        DslParser.SqlContext tree = parser.sql();
        TerminalNode terminalNode = tree.SELECT();
        for (ParseTree child : tree.children) {
            System.out.println("child.getText() = " + child.getText());
        }
//        System.out.println("tree.toStringTree() = " + tree.children.size());
//        System.out.println("terminalNode.getText() = " + terminalNode);
//        System.out.println(tree.toStringTree(parser)); //打印规则数
//        System.out.println("tree.getRuleContext().depth() = " + tree.getRuleContext().getAltNumber());
//        System.out.println("tree.getText() = " + tree.getText());
    }
}