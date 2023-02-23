import java.io.Serializable;
import java.util.ArrayList;
//extends discussion posts?

public class Student extends Account implements Serializable {
    private String username;
    private String password;

    // dunno if we want a password
    //also identifier should be unique
    public Student(String username, String password) {
        super(username, password, false, true);
    }

    public static Student retrieveAccount(String username, String password) {
        return (Student) Account.retrieveAccount(username, password);
    }
}
