import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class loggedInMenu  extends JFrame {
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
    public static final Object OBJECT = new Object();
    private Account account;

    private static ObjectOutputStream oos;
    private static ObjectInputStream ois;

    JFrame frame;


    JButton manageButton; // a button to clear the line
    JButton selectButton; //Fill the background with the current pen color.
    JButton logoutButton; // a button to erase the line

    boolean quit = false;
    Teacher loggedInTeacher = null;
    Student loggedInStudent = null;

    String username = "";
    String password = "";
    boolean isTeacher = false;
    boolean isStudent = false;
    boolean loggedIn = false;
    Scanner scanner = new Scanner(System.in);




    ActionListener actionListener = new ActionListener() {
        @Override

        public void actionPerformed(ActionEvent e) {

            if (e.getSource() == manageButton) {
                try {
                    oos.writeObject("manage");
                    oos.flush();

   //                 System.out.println(manageAccountMenu);
                    Object[] deleteOrEdit = {"Edit", "Delete"};
                    int manageAccountOption = JOptionPane.showOptionDialog(null, "Select one of the following:", "Project 4", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, deleteOrEdit, deleteOrEdit[1]);


                    if (manageAccountOption == JOptionPane.YES_OPTION ) {
                        oos.writeObject("edit");
                        oos.flush();

                        String newUsername = JOptionPane.showInputDialog(null, newUsernamePrompt, "Edit account",
                                JOptionPane.INFORMATION_MESSAGE);

                        String newPassword = JOptionPane.showInputDialog(null, newPasswordPrompt, "Edit account",
                                JOptionPane.INFORMATION_MESSAGE);
                        boolean changeFunction = false;
                        JOptionPane.showMessageDialog(null, accountEditedSuccessfullyPrompt,
                                "Create account", JOptionPane.INFORMATION_MESSAGE);
                        oos.writeObject(newUsername+"\f"+newPassword);
                        oos.flush();
                        account = (Account) ois.readObject();
    //                    System.out.println(account.getUsername());


                    }
                    if (manageAccountOption == JOptionPane.NO_OPTION) {
                        oos.writeObject("delete");
                        oos.flush();
                        frame.dispose();
                        loggedIn = false;
                        loggedInStudent = null;
                        loggedInTeacher = null;
                        SwingUtilities.invokeLater(new Menu(oos, ois));
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    DiscussionClient client = new DiscussionClient();
                    client.setCrashed(true);
                    client.run();
                }

            }
            // Look at catch, that should be the catch for every try block in this program.
            if (e.getSource() == selectButton) {
                try {
                    frame.dispose();
                    oos.writeObject("select");
                    oos.flush();
                    Courses.setCourses((ArrayList<Courses>) ois.readObject());
   //                 System.out.println("11111111");
                    new CourseMenu(Courses.getCourses(), oos, ois, account);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    DiscussionClient client = new DiscussionClient();
                    client.setCrashed(true);
                    client.run();
                }
            }
            if (e.getSource() == logoutButton) {
                quit = true;

                JOptionPane.showMessageDialog(null, farewell, "Discussion Board", JOptionPane.INFORMATION_MESSAGE);
                //System.exit(0);
                try {
                    oos.writeObject("Logout");
                    oos.flush();
                    //System.out.println(ois.readObject());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    DiscussionClient client = new DiscussionClient();
                    client.setCrashed(true);
                    client.run();
                }
                loggedIn = false;
                loggedInStudent = null;
                loggedInTeacher = null;
                frame.setVisible(false);
                frame.dispose();
                SwingUtilities.invokeLater(new Menu(oos, ois));


            }






        }
    };

    public loggedInMenu(Account account, String name, ObjectOutputStream oos, ObjectInputStream ois) {
        this.oos = oos;
        this.ois = ois;
        /* set up JFrame */
        frame = new JFrame("Logged In Menu");
        Container content = frame.getContentPane();
        content.setLayout(null);
        this.account = account;



        //Clear, Fill, Erase, Random
        manageButton = new JButton("Manage My account");
        manageButton.setBounds(200, 100, 200, 40);
        manageButton.addActionListener(actionListener);

        selectButton = new JButton("Select the course");
        selectButton.addActionListener(actionListener);
        selectButton.setBounds(200, 150, 200, 40);

        logoutButton = new JButton("Log Out");
        logoutButton.addActionListener(actionListener);
        logoutButton.setBounds(200, 200, 200, 40);

        JLabel label = new JLabel("", JLabel.CENTER);
        label.setText(currentLogIn + name);
        label.setFont(new Font("Serif", Font.BOLD, 20));
        label.setOpaque(true);
        label.setBounds(100, 40, 400, 40);


        content.add(label);
        content.add(manageButton);
        content.add(selectButton);
        content.add(logoutButton);


        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);



    }

}