public class Test {
    public static void main(String[] args) {
        String text = "const! a=10;\n" +
                "var b,c;\n" +
                "procedure p;\n" +
                "begin\n" +
                "  c:=b+a\n" +
                "end;;!\n";
        WordAnalysis analysis = new WordAnalysis(text);
        WordAnalysis.WordInfo wordInfo = analysis.GETSYM();
        while(!wordInfo.getSYM().equals("END")){
            if(wordInfo.getSYM().equals("ERROR")){
                System.out.println("ERROR : in char at " + wordInfo.getNUM());
                break;
            }
            System.out.println(wordInfo);
            wordInfo = analysis.GETSYM();
        }
    }
}
