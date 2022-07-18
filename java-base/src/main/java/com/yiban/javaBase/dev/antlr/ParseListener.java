package com.yiban.javaBase.dev.antlr;

import antlr.DslBaseListener;
import antlr.DslLexer;
import antlr.DslParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;
import java.util.List;

/**
 * @auther WEI.DUAN
 * @date 2021/6/6
 * @website http://blog.csdn.net/dwshmilyss
 *
 * load语法实现
 * 功能解说:
 * load的语法： load json.'F:\\tmp\\user' as temp; 通过类似的语法，实现spark加载文件夹的数据，然后将数据注册成一张表。这里的json可以替换为spark支持的文件格式。
 *
 * 实现思路:
 * 如 load json.'F:\\tmp\\user' as temp 这样一个sql,对应了我们自定义规则的sql规则里面的load分支。
 * load-->LOAD，json-->format，'F:\\tmp\\user' -->path， as-->as，temp--> tableName。
 * 我们可以通过覆写Listener的enterSql()方法，来获取到sql规则里面，与之相关联的其他元素，
 * 获取到各个元素的内容，通过spark来根据不同的内容加载不同的数据。
 */
public class ParseListener  extends DslBaseListener {
    @Override
    public void enterSql(DslParser.SqlContext ctx) {
        String keyword = ctx.children.get(0).getText();  //获取sql规则的第一个元素，为select或者load
        if("select".equalsIgnoreCase(keyword)){
            execSelect(ctx);   //第一个元素为selece的时候执行select
        }else if("load".equalsIgnoreCase(keyword)){
            execLoad(ctx);  //第一个元素为load的时候执行load
        }

    }
    public void execLoad(DslParser.SqlContext ctx){
        List<ParseTree> children = ctx.children;   //获取该规则树的所有子节点
        String format = "";
        String path = "";
        String tableName = "";
        for (ParseTree c :children) {
            if(c instanceof DslParser.FormatContext){
                format = c.getText();
            }else if(c instanceof DslParser.PathContext){
                path = c.getText().substring(1,c.getText().length()-1);
            }else if(c instanceof DslParser.TableNameContext){
                tableName = c.getText();
            }
        }
        System.out.println(format);
        System.out.println(path);
        System.out.println(tableName);
        // spark load实现，省略
    }

    public void execSelect(DslParser.SqlContext ctx){

    }

    public static void main(String[] args) throws IOException {
        String len = "load json.`F:\\tmp\\user` as temp;";
        ANTLRInputStream input = new ANTLRInputStream(len);
        DslLexer lexer = new DslLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        DslParser parser = new DslParser(tokens);
        DslParser.SqlContext tree = parser.sql();
        ParseListener listener = new ParseListener();
        ParseTreeWalker.DEFAULT.walk(listener,tree);  //规则树遍历
    }
}