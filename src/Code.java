public class Code {
    Operator op;
    int l;
    int a;

    public Code() {
    }

    public Operator getOp() {
        return op;
    }

    public void setOp(Operator op) {
        this.op = op;
    }

    public int getL() {
        return l;
    }

    public void setL(int l) {
        this.l = l;
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public Code(Operator op, int l, int a) {
        this.op = op;
        this.l = l;
        this.a = a;
    }

    @Override
    public String toString() {
        return "Code{" +
                "op=" + op +
                ", l=" + l +
                ", a=" + a +
                '}';
    }
}

enum Operator{
    LIT, LOD, STO, CAL, INT, JMP, JPC, OPR
}
