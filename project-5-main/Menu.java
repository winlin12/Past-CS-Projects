import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Menu extends JComponent implements Runnable {
    private Image image; // the canvas
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
    private static boolean started = false;
    private static ObjectOutputStream oos;
    private static ObjectInputStream ois;

    JFrame frame;


    JButton loginButton; // a button to clear the line
    JButton registerButton; //Fill the background with the current pen color.
    JButton quitButton; // a button to erase the line
    Teacher loggedInTeacher = null;
    Student loggedInStudent = null;

    String username = "";
    String password = "";
    boolean isTeacher = false;
    boolean isStudent = false;
    boolean quit = false;
    boolean loggedIn = false;
    Scanner scanner = new Scanner(System.in);
    public static final Object OBJECT = new Object();


    ActionListener actionListener = new ActionListener() {
        @Override

        public void actionPerformed(ActionEvent e) {
            // TODO: Write a way to store information (such as accounts) in the server. This most likely will be done
            //  with files.
            try {
                if (e.getSource() == loginButton) {
                    oos.writeObject("Login");
                    Object[] studentOrTeacherOption = {"Teacher", "Student"};
                    int role = JOptionPane.showOptionDialog(null, "Select one of the following:", "Project 4", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, studentOrTeacherOption, studentOrTeacherOption[1]);

                    username = JOptionPane.showInputDialog(null, usernamePrompt, "Login account",
                            JOptionPane.INFORMATION_MESSAGE);
                    password = JOptionPane.showInputDialog(null, passwordPrompt, "Login account",
                            JOptionPane.INFORMATION_MESSAGE);
                    oos.writeObject(username + "\f" + password);
                    oos.flush();
                    System.out.println(1);
                    loggedIn = ois.readBoolean();
                    System.out.println(2);
                    boolean student = ois.readBoolean();
                    System.out.println(3);
                    if (loggedIn) {
                        try {
                            if (role == JOptionPane.YES_OPTION) {
                                loggedIn = true;
                                if (student) {
                                    throw new ClassCastException();
                                }
                                loggedInTeacher = new Teacher(username, password);
                                DiscussionClient.setLoggedInTeacher(loggedInTeacher);
                            }
                            if (role == JOptionPane.NO_OPTION) {
                                loggedIn = true;
                                if (!student) {
                                    throw new ClassCastException();
                                }
                                loggedInStudent = new Student(username, password);
                                DiscussionClient.setLoggedInStudent(loggedInStudent);
                            }
                        } catch (ClassCastException | NullPointerException c) {
                            loggedIn = false;
                            JOptionPane.showMessageDialog(null, logInError, "Discussion Board", JOptionPane.INFORMATION_MESSAGE);

                        }
                    } else {
                        JOptionPane.showMessageDialog(null, logInError, "Discussion Board", JOptionPane.INFORMATION_MESSAGE);

                    }


                }
                if (e.getSource() == registerButton) {
                    oos.writeObject("Register");
                    username = JOptionPane.showInputDialog(null, createUsernamePrompt, "Create account",
                            JOptionPane.INFORMATION_MESSAGE);

                    password = JOptionPane.showInputDialog(null, createPasswordPrompt,
                            "Create account", JOptionPane.QUESTION_MESSAGE);
                    oos.writeObject(username + "\f" + password);
                    Object[] studentOrTeacherOption = {"Teacher", "Student"};
                    int role = JOptionPane.showOptionDialog(null, "Select one of the following:", "Project 4", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, studentOrTeacherOption, studentOrTeacherOption[1]);

                    if (role == JOptionPane.YES_OPTION) {
                        boolean created;
                        synchronized (OBJECT) {
                            Teacher tempTeacher = new Teacher(username, password);
                            oos.writeBoolean(true);
                            oos.flush();
                            created = ois.readBoolean();
                        }
                        if (created) {
                            System.out.println(accountCreatedConfirmation);
                            JOptionPane.showMessageDialog(null, accountCreatedConfirmation,
                                    "Create account", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(null, "This username is taken",
                                    "Create account", JOptionPane.ERROR_MESSAGE);
                        }

                    }
                    if (role == JOptionPane.NO_OPTION) {
                        boolean created;
                        synchronized (OBJECT) {
                            Student tempStudent = new Student(username, password);
                            oos.writeBoolean(false);
                            oos.flush();
                            created = ois.readBoolean();
                        }
                        if (created) {
                            System.out.println(accountCreatedConfirmation);
                            JOptionPane.showMessageDialog(null, accountCreatedConfirmation,
                                    "Create account", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(null, "This username is taken",
                                    "Create account", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    synchronized (OBJECT) {
                        //f2.updateFile(Account.getCreatedAccounts());
                    }
                }
                if (e.getSource() == quitButton) {
                    quit = true;
                    JOptionPane.showMessageDialog(null, farewell, "Discussion Board", JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0);
                } else if (e.getSource() == loginButton || e.getSource() == registerButton) {
                    if (loggedIn) {
                        Account account;
                        if (loggedInStudent != null) {
                            account = loggedInStudent;
                        } else {
                            account = loggedInTeacher;
                        }
                        synchronized (OBJECT) {
                            System.out.print(currentLogIn);
                            System.out.printf("%s\n", account.getUsername());
                            System.out.println(loggedInMenu);
                            oos.writeObject("LoggedIn");
                            oos.flush();

                            new loggedInMenu(account, account.getUsername(), oos, ois);
                            frame.setVisible(false);
                            frame.dispose();


                        }
                    }
                }
            } catch (Exception d) {
                d.printStackTrace();
                synchronized (OBJECT) {
                    DiscussionClient client = new DiscussionClient();
                    client.setCrashed(true);
                    client.run();
                }
            }
        }
    };

    /* set up functions of buttons */
    public Menu(ObjectOutputStream oos, ObjectInputStream ois) {
        this.oos = oos;
        this.ois = ois;
    }

    public void run() {
        /* set up JFrame */
        frame = new JFrame("Menu");
        Container content = frame.getContentPane();
        content.setLayout(null);

//Clear, Fill, Erase, Random
        loginButton = new JButton("Login");
        loginButton.setBounds(250, 100, 100, 40);
        loginButton.addActionListener(actionListener);

        registerButton = new JButton("Register");
        registerButton.addActionListener(actionListener);
        registerButton.setBounds(250, 150, 100, 40);

        quitButton = new JButton("Quit");
        quitButton.addActionListener(actionListener);
        quitButton.setBounds(250, 200, 100, 40);

        JLabel label = new JLabel("", JLabel.CENTER);
        label.setText(welcomeMessage);
        label.setFont(new Font("Serif", Font.BOLD, 20));
        label.setOpaque(true);
        label.setBounds(100, 40, 400, 40);

        content.add(label);
        content.add(loginButton);
        content.add(registerButton);
        content.add(quitButton);


        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }

    /*public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Menu());
    }*/
}