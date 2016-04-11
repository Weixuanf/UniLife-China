package RenrenAnalyze;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class ReadAccounts {
	String[] email = new String[400];
	String[] psw = new String[400];
    /**
     * 以行为单位读取txt文件，并将所有用户名密码存入数组
     */
    public void exportAccounts(String filePath) {
        File file = new File(filePath);
        BufferedReader reader = null;
        try {
            //System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号：读取的每行内容
                //System.out.println("line " + line + ": " + tempString);
                String[] split = tempString.split("----");
                //System.out.println("username:"+split[0]+"   password:"+split[1]);
                email[line-1]=split[0];
                psw[line-1]=split[1];
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }
    public static void main(String[] args) {
    	ReadAccounts reader = new ReadAccounts();
    	reader.exportAccounts("D:/OneDrive/JAVA workspace/spider1/renrenAccounts100.txt");
    	System.out.println(Arrays.toString(reader.email));
    	System.out.println(Arrays.toString(reader.psw));
    	System.out.println(reader.email[99]);
    	System.out.println(reader.psw[99]);
    }
}
