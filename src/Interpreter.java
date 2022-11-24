import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

public class Interpreter {

    private final int stackSize = 100;
    private final List<Code> codes;
//    指令寄存器
    private Code iRegister;
//    程序地址寄存器
    private int pc;
//    栈顶寄存器
    private int topR;
//    基地址寄存器
    private int baseR;

    int[] stack;

    void work(){
        do{
            //取指令
            iRegister = codes.get(pc);
            pc++;
            switch (iRegister.op){
                case INT :
                    topR += iRegister.a;
                    break;
                case JMP:
                    pc = iRegister.a - 1;
                    break;
                case JPC:
                    //1为真，0为假
                    if (stack[topR] != 1)
                        pc = iRegister.a - 1;
                    break;
                case LIT:
                    topR++;
                    stack[topR] = iRegister.a;

                    break;
                case CAL:
                    //返回地址
                    stack[topR+1] = pc;
                    pc = iRegister.a - 1;
                    //DL
                    stack[topR+2] = baseR;
                    //SL 还不会
                    stack[topR+3] = baseR;
                    baseR = topR+1;
                    break;
                case STO:
                    int address = base(iRegister.l, stack[baseR + 2]);
                    stack[address + iRegister.a] = stack[topR];
                    topR--;
                    break;
                case LOD:
                    address = base(iRegister.l, stack[baseR + 2]);
                    topR++;
                    stack[topR] = stack[address + iRegister.a];
                    break;
                case OPR:
                    switch (iRegister.a){
                        case 0://返回
                            pc = stack[baseR];
                            topR = baseR-1;
                            baseR = stack[baseR+1];
                            break;
                        case 2://加法
                            int a = stack[topR--];
                            int b = stack[topR];
                            stack[topR] = a+b;
                            break;
                        case 4://乘法
                            a = stack[topR--];
                            b = stack[topR];
                            stack[topR] = a*b;
                            break;
                        case 9://是否不等
                            a = stack[topR--];
                            b = stack[topR];
                            stack[topR] = a!=b?1:0;
                            break;
                        case 14://栈顶值输出至屏幕
                            System.out.print("栈顶值："+stack[topR]);
                            break;
                        case 15:
                            System.out.println();
                            break;
                        case 16:
                            System.out.print("输入值：");
                            Scanner sc = new Scanner(System.in);
                            int input = sc.nextInt();
                            topR++;
                            stack[topR] = input;

                            break;
                    }
                    break;
            }
        }while (pc != 0);
    }

    /**
     *
     * @param l 层差
     * @param address 调用base函数的静态链内容
     * @return
     */
    int base(int l, int address){
        while (l!=0){
            address = stack[address+2];
            l--;
        }

        return address;
    }

    public Interpreter(List<Code> codes){
        pc = 0;
        topR = -1;
        this.codes = codes;
        stack = new int[stackSize];
    }

    public static void main(String[] args) throws IOException {
        SyntaxAnalysis syntaxAnalysis = new SyntaxAnalysis("");
        syntaxAnalysis.program();
        Interpreter interpreter = new Interpreter(syntaxAnalysis.getCodes());
        System.out.println("Interpreter start...");
        interpreter.work();
        System.out.println("Interpreter end.");

    }
}
