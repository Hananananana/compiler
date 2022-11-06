import java.io.*;

public class FileTest {
    public static void main(String[] args) throws IOException {
        String root = System.getProperty("user.dir");
        String FileName="code.txt";
        String filePath = root+File.separator+"src"+ File.separator+FileName;

        FileReader fileReader = new FileReader(filePath);
        BufferedReader in  = new BufferedReader(fileReader);
        System.out.println((char)(in.read()));
        System.out.println((char)(in.read()));
        System.out.println(in.read());

    }
}
