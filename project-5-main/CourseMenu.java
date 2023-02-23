import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
public class CourseMenu extends JFrame {
    private static final String currentLogIn = "Currently logged in as: ";
    private static final String farewell = "Thank you for choosing our Discussion Board, goodbye.";
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
    private static final String loggedInMenu = "1. Manage my account\n2. Select the course\n3. Log Out";
    private static final String manageAccountMenu = "1. Edit account\n2. Delete account";
    private static final String newUsernamePrompt = "Please type your new username";
    private static final String newPasswordPrompt = "Please type your new password";
    private static final String accountEditedSuccessfullyPrompt = "Congratulations! Your account information has been changed successfully!";
    private static final String accountDeletedConfirmation = "Your account has been successfully deleted.";
    private static final String goBack = "-1: Go Back";
    private static final String selectCourse = "Select a Course:";
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
    private static final String title = "Discussion Board";
    private static final Object OBJECT = new Object();
    private static Account loggedInAccount;
    private static Student loggedInStudent;
    private static Teacher loggedInTeacher;
    private JFrame frame;
    private ArrayList<Courses> courseList;
    String[] courseNames;
    JButton createButton; // adds a new Forum
    JButton selectButton; //Fill the background with the current pen color.
    JButton editButton; // Allows a user to edit or delete a course.
    JButton backButton; // a button to erase the line
    JComboBox<String> courseMenu; // Drop Down Menu for Courses
    boolean quit = false;
    private static ObjectOutputStream oos;
    private static ObjectInputStream ois;

    /* set up functions of buttons */
    public CourseMenu(ArrayList<Courses> courseList,ObjectOutputStream oos,ObjectInputStream ois, Account account) {
        if (account.isTeacher()) {
            loggedInTeacher = new Teacher(account.getUsername(), account.getPassword());
        } else {
            loggedInStudent = new Student(account.getUsername(), account.getPassword());
        }
        loggedInAccount = account;
        this.oos = oos;
        this.ois = ois;
        //      System.out.println(loggedInAccount.getUsername());
        this.courseList = courseList;
        /* set up JFrame */
        frame = new JFrame("Course Menu");
        Container content = frame.getContentPane();
        content.setLayout(null);
        courseNames = new String[courseList.size()];
        if (courseNames.length == 0) {
            courseNames = new String[1];
            courseNames[0] = "No Courses Yet";
        } else {
            for (int i = 0; i < courseList.size(); i++) {
                courseNames[i] = courseList.get(i).getTitle();
            }
        }
        courseMenu = new JComboBox<>(courseNames);
        courseMenu.addActionListener(actionListener);
        courseMenu.setBounds(200, 80, 200, 40);

        createButton = new JButton("Create a Course");
        createButton.addActionListener(actionListener);
        createButton.setBounds(200, 130, 200, 40);

        selectButton = new JButton("View Forums");
        selectButton.addActionListener(actionListener);
        selectButton.setBounds(200, 180, 200, 40);

        editButton = new JButton("Edit Course");
        editButton.addActionListener(actionListener);
        editButton.setBounds(200, 230, 200, 40);

        backButton = new JButton("Go back");
        backButton.addActionListener(actionListener);
        backButton.setBounds(200, 280, 200, 40);

        JLabel label = new JLabel("", JLabel.CENTER);
        label.setText(selectCourse);
        label.setFont(new Font("Serif", Font.BOLD, 20));
        label.setOpaque(true);
        label.setBounds(100, 40, 400, 40);


        content.add(label);
        content.add(courseMenu);
        content.add(createButton);
        content.add(selectButton);
        content.add(editButton);
        content.add(backButton);

        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }


    ActionListener actionListener = new ActionListener() {
        @Override

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == createButton) {
                if (loggedInStudent != null) {
                    JOptionPane.showMessageDialog(null, errorStudent, "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    String newCourseName = JOptionPane.showInputDialog(null, enterNewCourse, "Enter Course",
                            JOptionPane.INFORMATION_MESSAGE);
                    //Temporary until a proper storage of Courses is implemented
                    try {
                        oos.writeObject("createCourse");
                        oos.flush();
                        Courses newCourse = new Courses(newCourseName);
                        courseList.add(0, newCourse);
                        oos.writeObject(courseList);
                        oos.flush();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        DiscussionClient client = new DiscussionClient();
                        client.setCrashed(true);
                        client.run();
                    }

                    JOptionPane.showMessageDialog(null, "Course Created", "Success!",
                            JOptionPane.INFORMATION_MESSAGE);
                    new CourseMenu(Courses.getCourses(), oos, ois, loggedInAccount);
                    frame.dispose();
                }
            }
            if (e.getSource() == selectButton) {
                if (courseList.size() == 0) {
                    JOptionPane.showMessageDialog(null,
                            "There are no courses yet! Add a course first.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    int selectedIndex = courseMenu.getSelectedIndex();
                    Courses currentCourse = courseList.get(selectedIndex);
                    try {
                        oos.writeObject("select");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        DiscussionClient client = new DiscussionClient();
                        client.setCrashed(true);
                        client.run();
                    }
                    try {
                        oos.writeObject(currentCourse);
                        oos.flush();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        DiscussionClient client = new DiscussionClient();
                        client.setCrashed(true);
                        client.run();
                    }
                    //New forum GUI with selected Course
                    new ForumsMenu(currentCourse, loggedInAccount, oos, ois);
                    frame.dispose();
                }
            }
            if (e.getSource() == editButton) {
                if (loggedInStudent != null) {
                    JOptionPane.showMessageDialog(null, errorStudent, "Error",
                            JOptionPane.ERROR_MESSAGE);
                } else if (courseList.size() == 0) {
                    JOptionPane.showMessageDialog(null,
                            "There are no courses yet! Add a course first.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    int selectedIndex = courseMenu.getSelectedIndex();
                    Object[] options = {"Edit Course", "Delete Course", "Cancel"};
                    int choice = JOptionPane.showOptionDialog(null, "What would you like to do with this course?",
                            "Edit Course", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,
                            options[2]);
                    if (choice == JOptionPane.YES_OPTION) {

                        String newTitle = JOptionPane.showInputDialog(null, "Enter new Course Title:",
                                "Edit Course", JOptionPane.INFORMATION_MESSAGE);
                        Courses changingCourse = courseList.get(selectedIndex);
                        changingCourse.setTitle(newTitle);
                        courseList.set(selectedIndex, changingCourse);
                        try {
                            oos.writeObject("editCourse");
                            oos.flush();
                            oos.writeObject(courseList);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            DiscussionClient client = new DiscussionClient();
                            client.setCrashed(true);
                            client.run();
                        }
                        new CourseMenu(courseList, oos, ois, loggedInAccount);
                        frame.setVisible(false);
                        frame.dispose();
                    } else if (choice == JOptionPane.NO_OPTION) {
                        courseList.remove(selectedIndex);
                        try {
                            oos.writeObject("deleteCourse");
                            oos.flush();
                            oos.writeObject(courseList);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            DiscussionClient client = new DiscussionClient();
                            client.setCrashed(true);
                            client.run();
                        }
                        new CourseMenu(courseList, oos, ois, loggedInAccount);
                        frame.setVisible(false);
                        frame.dispose();
                    }
                }

            }
            if (e.getSource() == backButton) {
                try {
                    oos.writeObject("back");
                    oos.flush();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    DiscussionClient client = new DiscussionClient();
                    client.setCrashed(true);
                    client.run();
                }
                new loggedInMenu(loggedInAccount, loggedInAccount.getUsername(), oos, ois);
                frame.setVisible(false);
                frame.dispose();
            }
        }
    };
}
