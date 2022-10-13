public class WordAnalysis {
//    private String SYM;
//    private String ID;
//    private Integer NUM;
    private final char[] alphabet= {';',':','=','+','-','/','#','<','>','{','}','(',')','0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
    private final String[] keywords = {"const","var","procedure","begin","end","odd","if","then","call","while","do","read","write"};
    private String text;
    private int pointer;
    private int length;

    public WordAnalysis(){}

    public WordAnalysis(String text){
        this.text = text;
        length = text.length();
        pointer = 0;//下一个要取的下标
    }

    public WordInfo GETSYM(){
//        读前驱空格
        char ch ;
        do {
            ch = getCh();
        } while (isSpace(ch));
//        读到结束
        if(ch == '@')
            return WordInfo.end();
//        读首字符
        StringBuilder sb = new StringBuilder();
        if(isLetter(ch)){
//      首字符为字母
            sb.append(ch);
            while(true){
                ch = getCh();
//                读入字母或数字
                if(isLetterOrNum((ch)))
                    sb.append(ch);
                else
                    break;
            }
            WordInfo res = new WordInfo();
            pointer--;
                //WordInfo res = new WordInfo();
//                res.setSYM("IDENT");
//                res.setID(sb.toString());
//                return res;

//            读入的是@
//            if(ch == '@'){
//                //WordInfo res = new WordInfo();
//                res.setSYM("IDENT");
//                res.setID(sb.toString());
//                return res;
//            }
            if(isKeyword(sb.toString())){
                res.setSYM(sb.toString().toUpperCase()+"SYM");
            }else{
                res.setSYM("IDENT");
                res.setID(sb.toString());
            }
            return res;
        }else if(isNum(ch)){
//      首字符为数字
            sb.append(ch);
            while(true){
                ch = getCh();
//                读入的是数字
                if(isNum((ch)))
                    sb.append(ch);
                else
                    break;
            }
//            入读的不是@
            if(ch != '@'){
                pointer--;
            }
            WordInfo res = new WordInfo();
            res.setSYM("NUMBER");
            res.setNUM(Integer.valueOf(sb.toString()));
            return res;


        }else if(ch == ':'){
            sb.append(ch);
            ch = getCh();
            if(ch == '='){
                WordInfo res = new WordInfo();
                res.setSYM(":=SYM");
                return res;
            }else
                return WordInfo.error(pointer);

        }else if(ch == '>' || ch == '<'){
            sb.append(ch);
            ch = getCh();
            if(ch == '='){
                WordInfo res = new WordInfo();
                res.setSYM(ch+"=SYM");
                return res;
            }else {
                pointer -- ;
                WordInfo res = new WordInfo();
                res.setSYM(ch+"SYM");
                return res;
            }
        }else if(isInAlphabet(ch)){
            sb.append(ch);
            WordInfo res = new WordInfo();
            res.setSYM(ch+"SYM");
            return res;
        }
        return WordInfo.error(pointer);
    }

    private boolean isLetter(char ch){
        return ch>='a'&&ch<='z' || ch>='A'&&ch<='Z';
    }

    private boolean isNum(char ch){
        return ch>='0'&&ch<='9';
    }

    private boolean isLetterOrNum(char ch){
        return isLetter(ch) || isNum(ch);
    }

    private boolean isSpace(char ch){
        return ch == ' '|| ch == '\t' || ch == '\n';
    }

    private boolean isSymbol(char ch){
        char[] symbols = {';',':','=','+','-','/','#','<','>','{','}','(',')'};
        for (char symbol : symbols)
            if (ch == symbol)
                return true;
        return false;
    }

    private boolean isKeyword(String keyword){
        for(String s : keywords)
            if(s.equals(keyword))
                return true;
        return false;
    }

    private boolean isInAlphabet(char ch){
        for(char c : alphabet)
            if(c == ch)
                return true;
        return false;
    }

    /**
     *
     * @return @为没有字符（到头了）
     */
    private char getCh(){
        if(pointer == length)
            return '@';
        char ch = text.charAt(pointer);
        pointer++;
        return ch;
    }

    static class WordInfo{
        private String SYM;
        private String ID;
        private Integer NUM;

        public WordInfo(){}

        public static WordInfo end(){
            WordInfo res = new WordInfo();
            res.setSYM("END");
            return res;
        }

        public static WordInfo error(int pointer){
            WordInfo res = new WordInfo();
            res.setSYM("ERROR");
            res.setNUM(pointer);
            return res;
        }

        public String getSYM() {
            return SYM;
        }

        public void setSYM(String SYM) {
            this.SYM = SYM;
        }

        public String getID() {
            return ID;
        }

        public void setID(String ID) {
            this.ID = ID;
        }

        public Integer getNUM() {
            return NUM;
        }

        public void setNUM(Integer NUM) {
            this.NUM = NUM;
        }

        @Override
        public String toString() {
            return "WordInfo{" +
                    "SYM='" + SYM + '\'' +
                    ", ID='" + ID + '\'' +
                    ", NUM=" + NUM +
                    '}';
        }
    }


}
