import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.net.*;

public class DiscussionHandler extends Thread {
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
    private boolean quit = false;
    private final Socket socket;

    public DiscussionHandler(Socket socket) {
        this.socket = socket;
    }

    public static final Object OBJECT = new Object();

    @Override
    public void run() {
        try {
            FileStorage f1 = new FileStorage();
            AccountsFile f2 = new AccountsFile();
            Account.setCreatedAccounts(f2.retrieveCreatedAccounts());
            BufferedReader br = new BufferedReader(new FileReader("Datafile.txt"));
            if (br.readLine() == null) {
                f1.encode(Courses.getCourses());
            }
            Courses.setCourses(f1.retrieve());
            //BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //PrintWriter pw = new PrintWriter(socket.getOutputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            oos.writeObject(Account.getCreatedAccounts());
            oos.flush();
            oos.writeBoolean(true);
            oos.flush();
            while (!quit) {
                Object o = ois.readObject();
                if (o.equals("Register")) {
                    Account.setCreatedAccounts(f2.retrieveCreatedAccounts());
                    for (Account i : Account.getCreatedAccounts()) {
                        System.out.println(i.getUsername());
                    }
                    String createdAccount = (String) ois.readObject();
                    String username = createdAccount.substring(0, createdAccount.indexOf('\f'));
                    String password = createdAccount.substring(createdAccount.indexOf('\f') +1 , createdAccount.length());
                    boolean a = ois.readBoolean();
                    boolean created = Account.createAccount(new Account(username, password, a, !a));
                    oos.writeBoolean(created);
                    oos.flush();
                    /*for (Account i : Account.getCreatedAccounts()) {
                        System.out.println(i.getUsername());
                    }*/

                    f2.updateFile(Account.getCreatedAccounts());
                    /*for (Account i : Account.getCreatedAccounts()) {
                        System.out.println(i.getUsername());
                    }*/
                    //System.out.printf("%s", DiscussionServer.getCreatedAccounts().get(0).getUsername());
                } else if (o.equals("Login")) {
                    System.out.println(1);
                    Account.setCreatedAccounts(f2.retrieveCreatedAccounts());
                    String login = (String) ois.readObject();
                    System.out.println(login);
                    String username = login.substring(0, login.indexOf('\f'));
                    String password = login.substring(login.indexOf('\f') + 1, login.length());
                    boolean loggedIn = Account.logIn(username, password);
                    oos.writeBoolean(loggedIn);
                    oos.flush();
                    Account account = Account.retrieveAccount(username, password);
                    if (account == null) {
                        oos.writeBoolean(false);
                        oos.flush();
                    } else {
                        oos.writeBoolean(account.isStudent());
                        oos.flush();
                    }
                    /* I didn't do this part, this is Ameya's part. If you need me
                    to walk through it, I can, but bear with me as I don't know all of it
                     */
                    if (loggedIn) {
                        if (ois.readObject().equals("LoggedIn")) {
                            while (true) {
                                Object loggedInDecision = ois.readObject();
                                if (loggedInDecision.equals("Logout")) {
                                    break;
                                } else if (loggedInDecision.equals("manage")) {
                                    Object manageDecision = ois.readObject();
                                    if (manageDecision.equals("edit")) {
                                        Account.setCreatedAccounts(f2.retrieveCreatedAccounts());
                                        Object o4 = ois.readObject();
                                        String a = (String)  o4;
                                        String newUsername = a.substring(0, a.indexOf('\f'));
                                        System.out.println(newUsername);
                                        String newPassword = a.substring(a.indexOf('\f')+1, a.length());
                                        System.out.println(newPassword);
                                        account.editAccount(account, newUsername, newPassword);
                                        f2.updateFile(Account.getCreatedAccounts());
                                        oos.writeObject(account);
                                        oos.flush();


                                    } else {
                                        Account.setCreatedAccounts(f2.retrieveCreatedAccounts());
                                        account.deleteAccount(account);
                                        f2.updateFile(Account.getCreatedAccounts());
                                        break;

                                    }
                                } else if(loggedInDecision.equals("select")) {
                                    Courses.setCourses(f1.retrieve());
                                    oos.writeObject(Courses.getCourses());
                                    oos.flush();
                                    while (true) {
                                        Object courseDecision = ois.readObject();
                                        if (courseDecision.equals("createCourse") || courseDecision.equals("editCourse")
                                                || courseDecision.equals("deleteCourse")){
                                            Courses.setCourses((ArrayList<Courses>) ois.readObject());
                                            f1.encode(Courses.getCourses());
                                        } else if (courseDecision.equals("back")) {
                                            break;
                                        } else if (courseDecision.equals("select")) {
                                            Courses currentCourse = (Courses) ois.readObject();
                                            while (true) {
                                                String forumDecision = (String) ois.readObject();
                                                if (forumDecision.equals("create")||forumDecision.equals("edit")||
                                                        forumDecision.equals("delete")) {
                                                    Courses.setCourses(f1.retrieve());

                                                    if (forumDecision.equals("create")) {
                                                        String add = (String) ois.readObject();
                                                        currentCourse.createForum(add.substring(0, add.indexOf("\f")), add.substring(add.indexOf("\f")+1, add.length()));
                                                        for (Courses i: Courses.getCourses()) {
                                                            if (i.getTitle().equals(currentCourse.getTitle())) {
                                                                Courses.getCourses().set(Courses.getCourses().indexOf(i), currentCourse);
                                                            }
                                                        }
                                                        f1.encode(Courses.getCourses());
                                                    } else if (forumDecision.equals("edit")) {
                                                        int selectedIndex = ois.readInt();
                                                        String editedForum = (String) ois.readObject();
                                                        currentCourse.getForums().get(selectedIndex).setTitle(editedForum.substring(0, editedForum.indexOf("\f")));
                                                        currentCourse.getForums().get(selectedIndex).setPrompt(editedForum.substring(editedForum.indexOf("\f")+1, editedForum.length()));
                                                        for (Courses i: Courses.getCourses()) {
                                                            if (i.getTitle().equals(currentCourse.getTitle())) {
                                                                Courses.getCourses().set(Courses.getCourses().indexOf(i), currentCourse);
                                                            }
                                                        }
                                                        f1.encode(Courses.getCourses());
                                                    } else if (forumDecision.equals("delete")){
                                                        int selectedIndex = ois.readInt();
                                                        currentCourse.getForums().remove(currentCourse.getForums().get(selectedIndex));
                                                        for (Courses i: Courses.getCourses()) {
                                                            if (i.getTitle().equals(currentCourse.getTitle())) {
                                                                Courses.getCourses().set(Courses.getCourses().indexOf(i), currentCourse);
                                                            }
                                                        }
                                                        f1.encode(Courses.getCourses());
                                                    } else {

                                                    }

                                                } else if (forumDecision.equals("back")) {
                                                    Courses.setCourses(f1.retrieve());
                                                    oos.writeObject(Courses.getCourses());
                                                    oos.flush();
                                                    break;
                                                } else if (forumDecision.equals("selectForum")) {
                                                    int forumIndex = (Integer) ois.readObject();
                                                    int courseIndex = (Integer) ois.readObject();
                                                    System.out.println("FFF" + forumIndex);
                                                    DiscussionForums currentForum = currentCourse.getForums().get(forumIndex);
                                                    String postDecision = (String) ois.readObject();
                                                    while (true) {
                                                        
                                                        if (postDecision.equals("createPost")) {
                                                            Courses.setCourses(f1.retrieve());
                                                            String title = (String) ois.readObject();
                                                            String content = (String) ois.readObject();
                                                            currentForum.createPost(title, content, account.getUsername());
                                                            currentCourse.getForums().set(forumIndex, currentForum);
                                                            for (Courses i : Courses.getCourses()) {
                                                                if (i.getTitle().equals(currentCourse.getTitle())) {
                                                                    Courses.getCourses().set(Courses.getCourses().indexOf(i), currentCourse);
                                                                }
                                                            }
                                                            f1.encode(Courses.getCourses());
                                                        } else if (postDecision.equals("back")) {
                                                            break;
                                                        } else if (postDecision.equals("select")) {
                                                            int postIndex = (Integer) ois.readObject();
                                                            DiscussionPosts currentPost = currentForum.getPosts().get(postIndex);
                                                            while (true) {
                                                                String replyDecision = (String) ois.readObject();
                                                                if (replyDecision.equals("create")) {
                                                                    Courses.setCourses(f1.retrieve());
                                                                    String replyTitle = (String) ois.readObject();
                                                                    String replyContent = (String) ois.readObject();
                                                                    String replyPoster= (String) ois.readObject();
                                                                    Replies newReply = new Replies(replyTitle, replyContent, replyPoster);
                                                                    currentPost.reply(newReply);
                                                                    currentForum.getPosts().set(postIndex, currentPost);
                                                                    currentCourse.getForums().set(forumIndex, currentForum);
                                                                    for (Courses i : Courses.getCourses()) {
                                                                        if (i.getTitle().equals(currentCourse.getTitle())) {
                                                                            Courses.getCourses().set(Courses.getCourses().indexOf(i), currentCourse);
                                                                        }
                                                                    }
                                                                    f1.encode(Courses.getCourses());
                                                                } else if (replyDecision.equals("back")) {
                                                                    break;
                                                                }
                                                            }
                                                        }

                                                    }
                                                }
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }

                    //DiscussionServer.writeCreatedAccounts(oos);

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Just some methods to group some code that's going to come up a lot
    public void getData(AccountsFile f2, FileStorage f1) throws IOException {
        Account.setCreatedAccounts(f2.retrieveCreatedAccounts());
        Courses.setCourses(f1.retrieve());
    }

    public void storeData(AccountsFile f2, FileStorage f1) throws IOException {
        f1.encode(Courses.getCourses());
        f2.updateFile(Account.getCreatedAccounts());
    }

    public void setQuit(boolean quit) {
        this.quit = quit;
    }
}
