import javax.security.auth.login.AccountException;
import javax.security.auth.login.AccountNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;

public class Account implements Serializable {
    private static ArrayList<Account> createdAccounts = new ArrayList<>();
    private String username;
    private String password;
    private boolean isTeacher;
    private boolean isStudent;
    ArrayList<Integer> upVotedPosts = new ArrayList<>();
    //An arraylist to keep track of the
    // postID for the post the student has upvoted
    // It's an integer list instead of a post list to avoid problems with duplicate posts and making the code
    // for Accounfiles simpler
    //Arraylists for teachers are just going to be empty and won't be called

    public void setUpVotedPosts(ArrayList<Integer> idArray) {
        this.upVotedPosts = idArray;
    }

    public Account(String username, String password, boolean isTeacher, boolean isStudent) {
        this.username = username;
        this.password = password;
        this.isStudent = isStudent;
        this.isTeacher = isTeacher;
    }

    public ArrayList<Integer> getUpVotedPosts() {
        return upVotedPosts;
    }

    /*
        Tries to create an account. If unsuccessful, returns false, if successful, returns true.
        Will not create an account if one already exists with that username.
        Will not create an account with illegal arguments, such as being both a teacher or student.
        I made the method to be static as changing the created accounts arraylist should be an ongoing process throughout
         the program, not based off of a specific account.
        */
    public static boolean createAccount(Account account) {
        //createdAccounts = DiscussionServer.getCreatedAccounts();
        try {
            if (account == null) {
                throw new NullPointerException("No name specified.");
            }
            Account retrievedAccount = Account.retrieveAccount(account.getUsername());
            if (retrievedAccount != null) {
                if (retrievedAccount.getUsername().equals(account.getUsername())) {
                    throw new AccountException("An account with this username already exists!");
                }
            }
            if (account.isTeacher() == account.isStudent()) {
                throw new IllegalArgumentException("Please choose Teacher or Student");
            }
        } catch (Exception e) {
            System.out.printf("%s\n", e);
            return false;
        }
        createdAccounts.add(account);
        return true;
    }

    /*
    Tries to edit an account. If unsuccessful, returns false, if successful, returns true.
    changeFunction is true if the user wishes to change an account to a teacher or a student (normally this requires
    administrative action, but I'll let them change it whenever for now).
    If the user only wants to change a username, call the function with the user's current password.
     */
    public boolean editAccount(Account account, String newUsername, String newPassword) {
        //createdAccounts = DiscussionServer.getCreatedAccounts();
        for (Account a : createdAccounts) {
            if (a.getUsername().equals(account.getUsername())) {
                account.setUsername(newUsername);
                account.setPassword(newPassword);
                createdAccounts.set(createdAccounts.indexOf(a), account);
                return true;
            }
        }
        System.out.println("No account with that username exists.");
        return false;
    }

    /*
    Tries to delete an account. If unsuccessful, returns false, if successful, returns true
     */
    public boolean deleteAccount(Account account) {
        //createdAccounts = DiscussionServer.getCreatedAccounts();
        for (Account a : createdAccounts) {
            if (a.getUsername().equals(account.getUsername())) {
                createdAccounts.remove(a);
                return true;
            }
        }
        System.out.println("No account with that username exists.");
        return false;
    }

    // Changes whether an account is a teacher or a student
    public void changeStatus() {
        isTeacher = !isTeacher;
        isStudent = !isStudent;
    }

    // Checks if a user logging in has entered the same username and password as one that has signed up for the
    // website previously. If true, allow the user to use the program, if false, force the user to try to log in the
    // program again, sign up, or quit the program.
    public static boolean logIn(String username, String password) {
        return retrieveAccount(username, password) != null;
    }

    // Finds an account from the Arraylist from a passed username and password
    public static Account retrieveAccount(String username, String password) {
        for (int i = 0; i < createdAccounts.size(); i++) {
            if (createdAccounts.get(i).getUsername().equals(username) && createdAccounts.get(i).getPassword().equals(password)) {
                return createdAccounts.get(i);
            }
        }
        return null;
    }

    // Finds an account from the Arraylist from a passed username
    public static Account retrieveAccount(String username) {
        for (int i = 0; i < createdAccounts.size(); i++) {
            if (createdAccounts.get(i).getUsername().equals(username)) {
                return createdAccounts.get(i);
            }
        }
        return null;
    }

    public boolean upVote(DiscussionPosts post) {
        for (int p : upVotedPosts) {
            if (p == post.getPostID()) {
                System.out.println("You've already upvoted this post.");
                return false;
            }
        }
        post.upVoted();
        upVotedPosts.add(post.getPostID());
        System.out.println("Post Upvoted");
        return true;
    }

    public void createPost(DiscussionForums forum, String title, String content) {
        forum.createPost(title, content, username);
    }

    public void reply(DiscussionPosts posts, Replies reply) {
        posts.reply(reply);
    }

    // Standard get and set methods
    public static ArrayList<Account> getCreatedAccounts() {
        return createdAccounts;
    }

    public static void setCreatedAccounts(ArrayList<Account> createdAccounts) {
        Account.createdAccounts = createdAccounts;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isStudent() {
        return isStudent;
    }

    public boolean isTeacher() {
        return isTeacher;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
