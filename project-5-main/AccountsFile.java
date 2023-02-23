import java.io.*;
import java.util.ArrayList;
/*Saves the created accounts in a text file named AccountsFile.txt
 returns the original arraylist of created accounts from AccountsFile.txt
 This class isn't supposed to print anything to the console, try-catch messages are just for debugging*/

public class AccountsFile {
    public static File f;

    /*The constructor creates a new file if one doesn't already exist*/
    public AccountsFile() {
        try {
            f = new File("AccountsFile.txt");
            if (!f.exists()) {
                f.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*Updates AccountsFile.txt to the arraylist in parameter
     * prints Account parameters in each line
     * String "----" between each separate account*/
    public void updateFile(ArrayList<Account> Accounts) {

        FileWriter fr = null;
        try {
            fr = new FileWriter(f, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter pw = new PrintWriter(fr);
        for (Account i : Accounts) {
            pw.println("----");
            pw.println(i.getUsername());
            pw.println(i.getPassword());
            pw.println(i.isTeacher());
            pw.println(i.isStudent());
        }
        pw.close();
    }

    /*returns the arraylist that was most recently saved*/
    public ArrayList<Account> retrieveCreatedAccounts() {
        ArrayList<Account> a = new ArrayList<>();
        try {
            FileReader reader = new FileReader(f);
            BufferedReader bfr = new BufferedReader(reader);
            while (true) {
                if (bfr.readLine() == null) {
                    break;
                }
                String storedName = bfr.readLine();
                String storedPass = bfr.readLine();
                boolean storedTeacher = Boolean.parseBoolean(bfr.readLine());
                boolean storedStudent = Boolean.parseBoolean(bfr.readLine());
                a.add(new Account(storedName, storedPass, storedTeacher, storedStudent));
            }
            bfr.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException n) {
            System.out.println("Uninitialised array or file");
        }
        return a;
    }
}
