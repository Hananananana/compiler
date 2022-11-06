import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SyntaxAnalysis {

    private WordAnalysis wordAnalysis;
    private WordAnalysis.WordInfo SYM;

    private List<List<TableItem>> tables;

//〈程序〉→〈分程序>
    void program(){
        advance();
        partProgram(0);
        if(SYM.getSYM().equals(".SYM")){
            advance();
            System.out.println("分析完了");
        }else{
            System.out.println("没有句号");
        }
    }

//〈分程序〉→ [<常量说明部分>][<变量说明部分>][<过程说明部分>]〈语句〉
    void partProgram(int level){
        ArrayList<TableItem> table = new ArrayList<>();
        tables.add(table);

        constDeclaration(level);

        varDeclaration(level);

        procedureDeclaration(level);

        statement();
    }

//<常量说明部分> → CONST<常量定义>{ ,<常量定义>}；
    void constDeclaration(int level){
        if(SYM.getSYM().equals("CONSTSYM")){
            advance();
            constDefinition();
            while(SYM.getSYM().equals(",SYM")){
                advance();
                constDefinition();
            }
            if(SYM.getSYM().equals(";SYM")){
                advance();
            }else{
                System.out.println("const语句没有分号");
            }
        }else{
            System.out.println("没有常量定义部分！");
        }
    }

//<变量说明部分> → VAR<标识符>{ ,<标识符>}；
    void varDeclaration(int level){
        if(SYM.getSYM().equals("VARSYM")){
            advance();
            identifier();
            while(SYM.getSYM().equals(",SYM")){
                advance();
                identifier();
            }
            if(SYM.getSYM().equals(";SYM")){
                advance();
            }else{
                System.out.println("var语句没有分号！");
            }
        }else{
            System.out.println("没有变量定义部分！");
        }
    }

//<过程说明部分> → <过程首部><分程度>；{<过程说明部分>}
    void procedureDeclaration(int level){
        procedureHead();
        partProgram(level+1);
        if(SYM.getSYM().equals(";SYM")){
            advance();
//            这里的循环部分不会写
            while (SYM.getSYM().equals("PROCEDURESYM")){
                procedureDeclaration(level+1);
            }
        }
    }

/**
    <语句> → <赋值语句>|<条件语句>|<当型循环语句>|<过程调用语句>|<读语句>|<写语句>|<复合语句>|<空>
 *
 */
    void statement(){
//        根据first集判断
        if(SYM.getSYM().equals("IDENT")){
//            赋值
            assignmentStatement();
        }else if(SYM.getSYM().equals("IFSYM")){
//            条件语句
            conditionStatement();
        }else if(SYM.getSYM().equals("WHILESYM")){
//            当循环
            whileStatement();
        }else if(SYM.getSYM().equals("CALLSYM")){
//            过程调用
            callProcedure();
        }else if(SYM.getSYM().equals("READSYM")){
//            读
            read();
        }else if(SYM.getSYM().equals("WRITESYM")){
//            写
            write();
        }else if(SYM.getSYM().equals("BEGINSYM")){
//            复合语句
            compoundStatement();
        }else{
//            空语句
        }
    }

//<常量定义> → <标识符>=<无符号整数>
    void constDefinition(){
        identifier();

        if(SYM.getSYM().equals("=SYM")){
            advance();
            unsignedInteger();
        }else{
            System.out.println("变量定义错误");
        }

    }

//标识符
    void identifier(){
        if(SYM.getSYM().equals("IDENT")){
            advance();
        }else{
            System.out.println("不是标识符！");
        }
    }

//数字
    void unsignedInteger(){
        if(SYM.getSYM().equals("NUMBER")){
            System.out.println(SYM);
            advance();
        }else{
            System.out.println("不是数字");

        }
    }

//<过程首部> → procedure<标识符>；
    void procedureHead(){
        if(SYM.getSYM().equals("PROCEDURESYM")){
            advance();
            identifier();
        }
    }

//<赋值语句> → <标识符>:=<表达式>
    void assignmentStatement(){
        identifier();
        if(SYM.getSYM().equals(":=SYM")){
            advance();
            expression();
        }
    }

//<复合语句> → begin<语句>{ ；<语句>}<end>
    void compoundStatement(){
        if(SYM.getSYM().equals("BEGINSYM")){
            statement();
            while (SYM.getSYM().equals(";SYM")) {
                advance();
                statement();
            }
            if (SYM.getSYM().equals("ENDSYM")){
                advance();
            }
        }
    }

//<条件> → <表达式><关系运算符><表达式>|ood<表达式>
//<关系运算符> → =|#|<|<=|>|>=
    void condition(){
        if(SYM.getSYM().equals("OODSYM")){
            advance();
            expression();
        }else{
            expression();
            if(SYM.getSYM().equals("=SYM")||SYM.getSYM().equals("#SYM")||SYM.getSYM().equals("<SYM")||
               SYM.getSYM().equals("<=SYM")||SYM.getSYM().equals(">SYM")||SYM.getSYM().equals(">=SYM")){
                advance();
            }
            expression();
        }
    }

//<表达式> → [+|-]<项>{<加减运算符><项>}
    void expression(){
        if(SYM.getSYM().equals("+SYM")||SYM.getSYM().equals("-SYM")){
            advance();
        }
        item();
//        循环不会写
        while (SYM.getSYM().equals("+SYM")||SYM.getSYM().equals("-SYM")){
                advance();
                item();
        }
    }

//<项> → <因子>{<乘除运算符><因子>}
    void item(){
        factor();
        while (SYM.getSYM().equals("*SYM")||SYM.getSYM().equals("/SYM")){
            advance();
            factor();
        }
    }

//<因子> → <标识符>|<无符号整数>|(<表达式>)
    void factor(){
        if(SYM.getSYM().equals("IDENT")){
            identifier();
        }else if(SYM.getSYM().equals("NUMBER")){
            unsignedInteger();
        }else if(SYM.getSYM().equals("(SYM")){
            advance();
            expression();
            if(SYM.getSYM().equals(")SYM"))
                advance();
        }
    }

//<加减运符> → +|-

//<乘除运算符> → *|/

//<关系运算符> → =|#|<|<=|>|>=

//<条件语句> → if<条件>then<语句>
    void conditionStatement(){
        if(SYM.getSYM().equals("IFSYM")){
            advance();
            condition();
            if(SYM.getSYM().equals("THENSYM")){
                advance();
                statement();
            }
        }
    }

//<过程调用语句> → call<标识符>
    void callProcedure(){
        if(SYM.getSYM().equals("CALLSYM")){
            advance();
            identifier();
        }
    }

//<当型循环语句> → while<条件>do<语句>
    void whileStatement(){
        if(SYM.getSYM().equals("WHILESYM")){
            advance();
            condition();
            if(SYM.getSYM().equals("DOSYM")){
                advance();
                statement();
            }
        }
    }

//<读语句> → read(<标识符>{ ，<标识符>})
    void read(){
        if(SYM.getSYM().equals("READSYM")){
            advance();
            if(SYM.getSYM().equals("(SYM")){
                identifier();
                while (SYM.getSYM().equals(",SYM")){
                    advance();
                    identifier();
                }
                if(SYM.getSYM().equals(")SYM"))
                    advance();
            }
        }
    }

//<写语句> → write(<标识符>{，<标识符>})
    void write(){
        if(SYM.getSYM().equals("WRITESYM")){
            advance();
            if(SYM.getSYM().equals("(SYM")){
                identifier();
                while (SYM.getSYM().equals(",SYM")){
                    advance();
                    identifier();
                }
                if(SYM.getSYM().equals(")SYM"))
                    advance();
            }
        }
    }


//读下一个词
    void advance(){
        SYM = wordAnalysis.GETSYM();
    }

    public SyntaxAnalysis(){}

    public SyntaxAnalysis(String fileName) throws IOException {
        wordAnalysis = new WordAnalysis(fileName);
        tables = new ArrayList<>();
    }

    public static void main(String[] args) throws IOException {
        SyntaxAnalysis syntaxAnalysis = new SyntaxAnalysis("");
        syntaxAnalysis.program();
    }
}
