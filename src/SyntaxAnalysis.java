import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SyntaxAnalysis {

    private WordAnalysis wordAnalysis;
    private WordAnalysis.WordInfo SYM;

    private List<List<TableItem>> tables;
    private List<Code> codes;

//〈程序〉→〈分程序>
    void program(){
        advance();
        partProgram(0, 0,null);
        if(SYM.getSYM().equals(".SYM")){
            advance();
            if(SYM.getSYM().equals("END")){
                System.out.println("分析结束");
                return;
            }
        }else{
            System.out.println("没有句号");
        }
    }

//〈分程序〉→ [<常量说明部分>][<变量说明部分>][<过程说明部分>]〈语句〉
    void partProgram(int level, int tx, TableItem p){
        ArrayList<TableItem> table = new ArrayList<>();
        tables.add(tx,table);
        int loc=0;
        if(level == 0 && tx == 0){
            genCode(Operator.JMP,0,0);
            //jmp的位置，需要回填
            loc = codes.size()-1;

        }
        //生成jmp

        constDeclaration(level, tx);

        varDeclaration(level, tx);

        procedureDeclaration(level, tx);

        int count = 0;
        for(TableItem item : table){
            if(item.kind == Kind.VARIABLE)
                count++;
        }
        genCode(Operator.INT,0,3+count);

        if(level == 0 && tx == 0)
            codes.get(loc).setA(codes.size());
        if(p!=null)
            p.adr = codes.size();
        statement(level,tx);

        //生成返回
        genCode(Operator.OPR,0,0);
    }

//<常量说明部分> → CONST<常量定义>{ ,<常量定义>}；
    void constDeclaration(int level ,int tx){
        if(SYM.getSYM().equals("CONSTSYM")){
            advance();
            constDefinition(level, tx);
            while(SYM.getSYM().equals(",SYM")){
                advance();
                constDefinition(level, tx);
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
    void varDeclaration(int level, int tx){
        if(SYM.getSYM().equals("VARSYM")){
            int dx = 3;
            advance();
            String name = identifier();
            List<TableItem> table = tables.get(tx);
            if(is_exist(name,level,tx,2)){

                System.out.println("标识符重定义！");
            }
            TableItem item = new TableItem(name, Kind.VARIABLE, level,dx++);
            table.add(item);
            while(SYM.getSYM().equals(",SYM")){
                advance();
                name = identifier();
                if(is_exist(name,level,tx,2)){
                    System.out.println("标识符重定义！");
                }
                table.add(new TableItem(name, Kind.VARIABLE, level,dx++));
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
    void procedureDeclaration(int level, int tx){
        if(!SYM.getSYM().equals("PROCEDURESYM"))
            return;
        TableItem item = procedureHead(level, tx);
        //要保证存储到末尾一个table的下一区域
        partProgram(level+1, Math.max(tx+1, tables.size()),item);

        if(SYM.getSYM().equals(";SYM")){
            advance();
            while (SYM.getSYM().equals("PROCEDURESYM")){
                procedureDeclaration(level, Math.max(tx+1, tables.size()));
            }
        }
    }

/**
    <语句> → <赋值语句>|<条件语句>|<当型循环语句>|<过程调用语句>|<读语句>|<写语句>|<复合语句>|<空>
 *
 */
    void statement(int level,int tx){
//        根据first集判断
        if(SYM.getSYM().equals("IDENT")){
//            赋值
            assignmentStatement(level,tx);
        }else if(SYM.getSYM().equals("IFSYM")){
//            条件语句
            conditionStatement(level, tx);
        }else if(SYM.getSYM().equals("WHILESYM")){
//            当循环
            whileStatement(level, tx);
        }else if(SYM.getSYM().equals("CALLSYM")){
//            过程调用
            callProcedure(level,tx);
        }else if(SYM.getSYM().equals("READSYM")){
//            读
            read(level, tx);
        }else if(SYM.getSYM().equals("WRITESYM")){
//            写
            write(level, tx);
        }else if(SYM.getSYM().equals("BEGINSYM")){
//            复合语句
            compoundStatement(level, tx);
        }else{
//            空语句
        }
    }

//<常量定义> → <标识符>=<无符号整数>
    void constDefinition(int level ,int tx){
        String name = identifier();

        if(SYM.getSYM().equals("=SYM")){
            advance();
            Integer value = unsignedInteger();
            if(is_exist(name,level,tx,2)){
                System.out.println("标识符重定义！");
            }
            TableItem item = new TableItem(name,Kind.CONSTANT,value);
            tables.get(tx).add(item);
        }else{
            System.out.println("变量定义错误");
        }
    }

//标识符
    String identifier(){
        if(SYM.getSYM().equals("IDENT")){
            String res = SYM.getID();
            advance();
            return res;
        }else{
            System.out.println("不是标识符！");
            return null;
        }
    }

//数字
    Integer unsignedInteger(){
        if(SYM.getSYM().equals("NUMBER")){
            Integer res = SYM.getNUM();
            advance();
            return res;
        }else{
            System.out.println("不是数字");
            return null;
        }
    }

//<过程首部> → procedure<标识符>；
TableItem procedureHead(int level, int tx){
        if(SYM.getSYM().equals("PROCEDURESYM")){
            advance();
            String name = identifier();
            if(is_exist(name,level,tx,1)){
                System.out.println("标识符重定义！");
            }
            TableItem item = new TableItem(name, Kind.PROCEDURE, level);
            tables.get(tx).add(item);

            if(SYM.getSYM().equals(";SYM")){
                advance();
                return item;
            }

        }
        return null;
    }

//<赋值语句> → <标识符>:=<表达式>
    void assignmentStatement(int level, int tx){
        String name = identifier();
        if(!is_exist(name,level,tx,3)){
            System.out.println("标识符" +name+"未定义");
        }
        Integer adr = getItem(tx, level, name, Kind.VARIABLE).getAdr();
        if(SYM.getSYM().equals(":=SYM")){
            advance();
            expression(level, tx);
            genCode(Operator.STO,level,adr);
        }
    }

//<复合语句> → begin<语句>{ ；<语句>}<end>
    void compoundStatement(int level, int tx){
        if(SYM.getSYM().equals("BEGINSYM")){
            advance();
            statement(level, tx);
            while (SYM.getSYM().equals(";SYM")) {
                advance();
                statement(level, tx);
            }
            if (SYM.getSYM().equals("ENDSYM")){
                advance();
            }
        }
    }

//<条件> → <表达式><关系运算符><表达式>|ood<表达式>
//<关系运算符> → =|#|<|<=|>|>=
    void condition(int level, int tx){
        if(SYM.getSYM().equals("OODSYM")){
            advance();
            expression(level, tx);
            genCode(Operator.OPR,0,6);
        }else{
            String oprType;
            expression(level, tx);
            if(SYM.getSYM().equals("=SYM")||SYM.getSYM().equals("#SYM")||SYM.getSYM().equals("<SYM")||
               SYM.getSYM().equals("<=SYM")||SYM.getSYM().equals(">SYM")||SYM.getSYM().equals(">=SYM")){
                oprType = SYM.getSYM();
                advance();
                expression(level, tx);
                if(oprType.equals("=SYM")){
                    genCode(Operator.OPR,0,8);
                }else if(oprType.equals("#SYM")){
                    genCode(Operator.OPR,0,9);
                }else if(oprType.equals("<SYM")){
                    genCode(Operator.OPR,0,10);

                }else if(oprType.equals("<=SYM")){
                    genCode(Operator.OPR,0,13);

                }else if(oprType.equals(">SYM")){
                    genCode(Operator.OPR,0,12);
                }else if(oprType.equals(">=SYM")){
                    genCode(Operator.OPR,0,11);
                }
            }
        }
    }

//<表达式> → [+|-]<项>{<加减运算符><项>}
    void expression(int level, int tx){
        int op = 1;//0为减，1为加法
        if(SYM.getSYM().equals("+SYM")){
            advance();
        }else if(SYM.getSYM().equals("-SYM")){
            op = 0;
            advance();
        }
        if(op == 0)
            genCode(Operator.LIT,0,0);
        item(level,tx);
        if(op == 0)
            genCode(Operator.OPR,0,3);
        op = 1;
        while (SYM.getSYM().equals("+SYM")||SYM.getSYM().equals("-SYM")){
            if(SYM.getSYM().equals("-SYM"))
                op = 0;
            advance();
            item(level, tx);
            if(op == 1)
                genCode(Operator.OPR,0,2);
            else
                genCode(Operator.OPR,0,3);
        }
    }

//<项> → <因子>{<乘除运算符><因子>}
    void item(int level, int tx){
        factor(level, tx);
        while (SYM.getSYM().equals("*SYM")||SYM.getSYM().equals("/SYM")){
            int op;//1是乘法，0是除法
            if(SYM.getSYM().equals("*SYM"))
                op = 1;
            else
                op = 0;
            advance();
            factor(level, tx);
            if(op == 1)
                genCode(Operator.OPR,0,4);
            else
                genCode(Operator.OPR,0,5);
        }
    }

//<因子> → <标识符>|<无符号整数>|(<表达式>)
    void factor(int level, int tx){
        if(SYM.getSYM().equals("IDENT")){
            String name = identifier();
            if(!is_exist(name,level,tx,2)){
                System.out.println("标识符" +name+"未定义");
            }
            TableItem item = getItem(tx, level, name, Kind.VARIABLE);
            if(item!=null){
                //是变量
                Integer adr = item.getAdr();
                genCode(Operator.LOD,level,adr);
            }else{
                item = getItem(tx,level,name,Kind.CONSTANT);
                genCode(Operator.LIT,0,item.getLevel_val());
            }
        }else if(SYM.getSYM().equals("NUMBER")){
            Integer val = unsignedInteger();
            genCode(Operator.LIT,0,val);
        }else if(SYM.getSYM().equals("(SYM")){
            advance();
            expression(level, tx);
            if(SYM.getSYM().equals(")SYM"))
                advance();
        }
    }

//<加减运符> → +|-

//<乘除运算符> → *|/

//<关系运算符> → =|#|<|<=|>|>=

//<条件语句> → if<条件>then<语句>
    void conditionStatement(int level, int tx){
        if(SYM.getSYM().equals("IFSYM")){
            advance();
            condition(level,tx);
            genCode(Operator.JPC, 0, 0);
            int loc = codes.size()-1;
            if(SYM.getSYM().equals("THENSYM")){
                advance();
                statement(level,tx);
                codes.get(loc).setA(codes.size());
            }
        }
    }

//<过程调用语句> → call<标识符>
    void callProcedure(int level, int tx){
        if(SYM.getSYM().equals("CALLSYM")){
            advance();
            String name = identifier();
            if(!is_exist(name,level,tx,1)){
                System.out.println("标识符" +name+"未定义");
            }
            TableItem item = getItem(tx,level, name, Kind.PROCEDURE);
            genCode(Operator.CAL, item.level_val, item.adr);
        }
    }

//<当型循环语句> → while<条件>do<语句>
    void whileStatement(int level, int tx){
        if(SYM.getSYM().equals("WHILESYM")){
            int loc1 = codes.size()+1;
            advance();
            condition(level, tx);
            int loc2 = codes.size();
            genCode(Operator.JPC,0,0);
            if(SYM.getSYM().equals("DOSYM")){
                advance();
                statement(level, tx);
                genCode(Operator.JMP,0,loc1);
                codes.get(loc2).setA(codes.size()+1);
            }
        }
    }

//<读语句> → read(<标识符>{ ，<标识符>})
    void read(int level, int tx){
        if(SYM.getSYM().equals("READSYM")){
            advance();
            if(SYM.getSYM().equals("(SYM")){
                advance();
                String name = identifier();
                if(!is_exist(name,level,tx,3)){
                    System.out.println("标识符" +name+"未定义");
                }
                Integer adr = getItem(tx,level, name, Kind.VARIABLE).getAdr();
                genCode(Operator.OPR,0,16);
                genCode(Operator.STO,level,adr);
                while (SYM.getSYM().equals(",SYM")){
                    advance();
                    name = identifier();
                    if(!is_exist(name,level,tx,3)){
                        System.out.println("标识符" +name+"未定义");
                    }
                    adr = getItem(tx,level, name, Kind.VARIABLE).getAdr();
                    genCode(Operator.OPR,0,16);
                    genCode(Operator.STO,level,adr);
                }
                if(SYM.getSYM().equals(")SYM"))
                    advance();
            }
        }
    }

//<写语句> → write(<标识符>{，<标识符>}) , 改成expression
    void write(int level, int tx){
        if(SYM.getSYM().equals("WRITESYM")){
            advance();
            if(SYM.getSYM().equals("(SYM")){
                advance();
                expression(level, tx);
                if(SYM.getSYM().equals(")SYM")){
                    genCode(Operator.OPR,0,14);
                    genCode(Operator.OPR,0,15);
                    advance();
                }
            }
        }
    }


//读下一个词
    void advance(){
        SYM = wordAnalysis.GETSYM();
    }

    public SyntaxAnalysis(){
        tables = new ArrayList<>();
        codes = new ArrayList<>();
    }

    public SyntaxAnalysis(String fileName) throws IOException {
        wordAnalysis = new WordAnalysis(fileName);
        tables = new ArrayList<>();
        codes = new ArrayList<>();
    }


    /**
     * 判断标识符是否存在
     * @param type 0为检查所有类型，1为函数，2为变量和常量,3为变量
     * @return true为存在（不可用）
     */
    boolean is_exist(String identifier, int level, int tx, int type){
        if(type == 0) {
            List<TableItem> table = tables.get(tx);
            for (TableItem item : table) {
                if (item.name.equals(identifier))
                    return true;
            }

            for (int i = tx - 1; i >= 0; i--) {
                table = tables.get(i);
//                if (table.get(0).level_val >= level)
//                    continue;
                for (TableItem item : table) {
                    if ((item.level_val<level||item.kind == Kind.CONSTANT) && item.name.equals(identifier))
                        return true;
                }
            }
            return false;
        }else if(type == 1){
            List<TableItem> table = tables.get(tx);
            for (TableItem item : table) {
                if (item.name.equals(identifier) && item.kind == Kind.PROCEDURE)
                    return true;
            }

            for (int i = tx - 1; i >= 0; i--) {
                table = tables.get(i);
//                if (table.get(0).level_val >= level)
//                    continue;
                for (TableItem item : table) {
                    if ((item.level_val<level||item.kind == Kind.CONSTANT) && item.name.equals(identifier) && item.kind == Kind.PROCEDURE)
                        return true;
                }
            }
            return false;
        }else if(type == 2){
            List<TableItem> table = tables.get(tx);
            for (TableItem item : table) {
                if (item.name.equals(identifier) && (item.kind == Kind.VARIABLE || item.kind == Kind.CONSTANT))
                    return true;
            }

            for (int i = tx - 1; i >= 0; i--) {
                table = tables.get(i);
//                if (table.get(0).level_val >= level)
//                    continue;
                for (TableItem item : table) {
                    if ((item.level_val<level || item.kind == Kind.CONSTANT) && item.name.equals(identifier) && (item.kind == Kind.VARIABLE || item.kind == Kind.CONSTANT))
                        return true;
                }
            }
            return false;
        }else if(type == 3){
            List<TableItem> table = tables.get(tx);
            for (TableItem item : table) {
                if (item.name.equals(identifier) && item.kind == Kind.VARIABLE)
                    return true;
            }

            for (int i = tx - 1; i >= 0; i--) {
                table = tables.get(i);
//                if (table.get(0).level_val >= level)
//                    continue;
                for (TableItem item : table) {
                    if (item.level_val<level && item.name.equals(identifier) && item.kind == Kind.VARIABLE)
                        return true;
                }
            }
            return false;
        }
        return true;
    }

    TableItem getItem(int tx, int level, String name, Kind kind){
        List<TableItem> list = tables.get(tx);
        for(TableItem item: list){
            if(item.kind == kind && item.name.equals(name))
                return item;
        }

        for (int i = tx-1; i >=0 ; i--) {
            list = tables.get(i);
            for(TableItem item: list){
                if(item.kind == kind && item.name.equals(name) && (item.level_val<level || item.kind == Kind.CONSTANT))
                    return item;
            }
        }
        return null;
    }

    void genCode(Operator op, int l, int a){
        Code code = new Code(op, l, a);
        codes.add(code);
    }

    void printCodes(){
        codes.forEach(item ->{
            System.out.println(codes.indexOf(item)+1+" : "+item);
        });
    }

    public static void main(String[] args) throws IOException {
        SyntaxAnalysis syntaxAnalysis = new SyntaxAnalysis("");
        syntaxAnalysis.program();
        syntaxAnalysis.printCodes();
    }
}
