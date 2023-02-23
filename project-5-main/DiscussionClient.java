import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

// Note: Allow parallel run in IntelliJ to simulate a LAN port connecting multiple threads at once
public class DiscussionClient implements Runnable {
    private static final String welcomeMessage = "Welcome to the Discussion Board!";
    private static final String actionsMenu = "Please choose the option:\n1. Log in\n2. Register\n3. Quit";
    private static final String createUsernamePrompt = "Create the username:";
    private static final String createPasswordPrompt = "Create the password:";
    private static final String accountCreatedConfirmation = "Congratulations! Your account has been created.";
    private static final String studentOrTeacher = "Select one of the following:\n1. Teacher\n2. Student";
    private static final String usernamePrompt = "Please enter the username:";
    private static final String passwordPrompt = "Please enter the password:";
    private static final String logInError = "Error. Cannot log in. Please ensure your username and password is correct, " +
            "and you selected the right role.";
    private static final String currentLogIn = "Currently logged in as: ";
    private static final String loggedInMenu = "1. Manage my account\n2. Select the course\n3. Log Out";
    private static final String manageAccountMenu = "1. Edit account\n2. Delete account";
    private static final String newUsernamePrompt = "Please type your new username";
    private static final String newPasswordPrompt = "Please type your new password";
    private static final String accountEditedSuccessfullyPrompt = "Congratulations! Your account information has been changed successfully!";
    private static final String accountDeletedConfirmation = "Your account has been successfully deleted.";
    private static final String goBack = "-1: Go Back";
    private static final String selectCourse = "Select a Course:\n0: Create a new Course";
    private static final String selectForum = "Course: %s\nSelect a Forum:\n0: Create a new Forum\n-2: Edit a forum\n-3: Delete a forum";
    private static final String selectPosts = "\nDiscussion Forum: %s\n\n%s\n\nChoose a post to view or create a new " +
            "post:\n0: New Post";
    private static final String enterNewCourse = "Please write the title of this new course";
    private static final String enterNewForumTitle = "Please write the title of this new forum";
    private static final String editForum = "Enter title of forum to be edited:";
    private static final String deleteForum = "Enter title of forum to be deleted:";
    private static final String enterNewForumPrompt = "Please enter the prompt for this new forum";
    private static final String enterPostTitle = "Enter the title of the post:";
    private static final String enterPostContent = "Write the contents of your post here:";
    private static final String replyDecision = "0: Write a reply\n1: Edit this post\n2: Delete this post\n3: Upvote this post";
    private static final String enterReply = "Write your reply:";
    private static final String chooseFileImport = "Enter 1 to enter manually, 2 to use file import";
    private static final String errorStudent = "Only teachers can do that!";
    private static final String editFailure = "You can only edit your own posts";
    private static final String generalError = "An error as occurred, logging out and returning to the main menu...";
    private static final String farewell = "Thank you for choosing our Discussion Board, goodbye.";
    private static final String title = "Discussion Board";
    // Sets initial parameters
    private static ObjectOutputStream oos;
    private static ObjectInputStream ois;
    private static Account loggedInAccount = null;
    private static Teacher loggedInTeacher = null;
    private static Student loggedInStudent = null;

    String username = "";
    String password = "";
    String newUsername = "";
    String newPassword = "";
    boolean isTeacher = false;
    boolean isStudent = false;
    boolean quit = false;
    boolean loggedIn = false;
    boolean crashed = false;
    public static void main(String[] args) {
        DiscussionClient client = new DiscussionClient();
        client.run();
    }

    public void run() {
        try {
            // Connects to the server, then starts run() in client.
            Socket socket = new Socket("localhost", 4302);
            //BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //PrintWriter pw = new PrintWriter(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
            if (crashed) {
                JOptionPane.showMessageDialog(null, generalError,
                        "Discussion Board", JOptionPane.ERROR_MESSAGE);
            }
            crashed = false;
            Account.setCreatedAccounts((ArrayList<Account>) ois.readObject());
            if (ois.readBoolean()) {
                Menu menu = new Menu(oos, ois);
                // TODO: Write a way to store information to the server after changes are made.
                SwingUtilities.invokeLater(menu);
                // Supposed to close the server if all clients are closed, but never works right now due to the
                // server always checking for a new connection
                // TODO: Implement a way to run this code after this thread closes.
                /*boolean alive = false;
                for (DiscussionHandler discussion : DiscussionServer.getDiscussions()) {
                    if (discussion.isAlive()) {
                        alive = true;
                        break;
                    }
                }
                if (!alive) {
                    //socket.close();
                }*/
            }
        } catch (ConnectException ce) {
            JOptionPane.showMessageDialog(null, "Cannot connect to the server, Please try again.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            ce.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            DiscussionClient client = new DiscussionClient();
            client.setCrashed(true);
            client.run();
        } catch (Exception e) {
            e.printStackTrace();
            DiscussionClient client = new DiscussionClient();
            client.setCrashed(true);
            client.run();
        }
    }
    public static void setLoggedInStudent(Student loggedInStudent) {
        DiscussionClient.loggedInStudent = loggedInStudent;
        loggedInAccount = loggedInStudent;
    }

    public static void setLoggedInTeacher(Teacher loggedInTeacher) {
        DiscussionClient.loggedInTeacher = loggedInTeacher;
        loggedInAccount = loggedInTeacher;
    }

    public static Account getLoggedInAccount() {
        return loggedInAccount;
    }

    public static Student getLoggedInStudent() {
        return loggedInStudent;
    }

    public static Teacher getLoggedInTeacher() {
        return loggedInTeacher;
    }

    public static void logOut() {
        loggedInStudent = null;
        loggedInTeacher = null;
        loggedInAccount = null;
    }
    public void setCrashed(boolean crashed) {
        this.crashed = crashed;
    }

    public static ObjectInputStream getOis() {
        return ois;
    }

    public static ObjectOutputStream getOos() {
        return oos;
    }
}
