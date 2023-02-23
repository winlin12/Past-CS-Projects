import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


public class ForumsMenu extends JFrame {

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
    private JFrame frame;

    private static ObjectOutputStream oos;
    private static ObjectInputStream ois;

    JButton createForumButton; // adds a new Forum
    JButton selectForumButton; //Fill the background with the current pen color.
    JButton editForumButton; // Allows a user to edit or delete a course.
    JButton backButton; // a button to erase the line
    JButton gradeButton; // Button allows teachers to grade or students to view grades.

    JComboBox<String> forums;
    private Account account;

    private Courses course;
    private String[] forumNames;
    private DiscussionForums currentForum;

    ActionListener actionListener = new ActionListener() {
        @Override

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == createForumButton) {
                if (account.isStudent()) {
                    JOptionPane.showMessageDialog(null, errorStudent, "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    String forumTitle = JOptionPane.showInputDialog(null, enterNewForumTitle,
                            "Create Forum", JOptionPane.INFORMATION_MESSAGE);
                    String forumContent = JOptionPane.showInputDialog(null, enterNewForumPrompt,
                            "Create Forum", JOptionPane.INFORMATION_MESSAGE);
                    if (course.createForum(forumTitle, forumContent)) {
                        JOptionPane.showMessageDialog(null, "Forum created", "Create Forum",
                                JOptionPane.INFORMATION_MESSAGE);
                        frame.dispose();
                        try {
                            oos.writeObject("create");
                            oos.flush();
                            System.out.println(course.getForums().size());
                            oos.writeObject(forumTitle + "\f" + forumContent);
                            oos.flush();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            DiscussionClient client = new DiscussionClient();
                            client.setCrashed(true);
                            client.run();
                        }
                        new ForumsMenu(course, account, oos, ois);
                    } else {
                        JOptionPane.showMessageDialog(null, "This forum already exists",
                                "Create Forum", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } if (e.getSource() == editForumButton) {
                if (account.isStudent()) {
                    JOptionPane.showMessageDialog(null, errorStudent, "Error",
                            JOptionPane.ERROR_MESSAGE);
                } else if (course.getForums().size() == 0) {
                    JOptionPane.showMessageDialog(null,
                            "There are no forums in this course. Add a forum first.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    int selectedIndex = forums.getSelectedIndex();
                    Object[] options = {"Edit Forum", "Delete Forum", "Cancel"};
                    int choice = JOptionPane.showOptionDialog(null, "What would you like to do with this Forum?",
                            "Edit Forum", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,
                            options[2]);
                    if (choice == JOptionPane.YES_OPTION) {

                        String newTitle = JOptionPane.showInputDialog(null, "Enter new Forum Title:",
                                "Edit Forum", JOptionPane.INFORMATION_MESSAGE);
                        String newContent = JOptionPane.showInputDialog(null, "Enter new Forum Prompt:",
                                "Edit Forum", JOptionPane.INFORMATION_MESSAGE);
                        DiscussionForums changingForum = course.getForums().get(selectedIndex);
                        changingForum.setTitle(newTitle);
                        changingForum.setPrompt(newContent);
                        try {
                            oos.writeObject("edit");
                            oos.flush();
                            oos.writeObject(selectedIndex);
                            oos.flush();
                            oos.writeObject(newTitle + "\f" + newContent);
                            oos.flush();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            DiscussionClient client = new DiscussionClient();
                            client.setCrashed(true);
                            client.run();
                        }
                        new ForumsMenu(course, account, oos, ois);
                        frame.setVisible(false);
                        frame.dispose();
                    } else if (choice == JOptionPane.NO_OPTION) {
                        course.getForums().remove(selectedIndex);
                        try {
                            oos.writeObject("delete");
                            oos.flush();
                            oos.writeObject(selectedIndex);
                            oos.flush();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            DiscussionClient client = new DiscussionClient();
                            client.setCrashed(true);
                            client.run();
                        }
                        new ForumsMenu(course, account, oos, ois);
                        frame.setVisible(false);
                        frame.dispose();
                    }
                }
            }
            if (e.getSource() == gradeButton) {
                int choice;
                boolean isStudent = DiscussionClient.getLoggedInAccount().isStudent();
                DiscussionForums selectedForum = course.getForums().get(forums.getSelectedIndex());
                if (isStudent) {
                    ArrayList<DiscussionPosts> studentPosts =
                            selectedForum.sortByStudent(DiscussionClient.getLoggedInAccount().getUsername());
                    StringBuilder grade = new StringBuilder();
                    for (int i = 0; i < studentPosts.size(); i++) {
                        grade.append("Post: ").append(studentPosts.get(i).getTitle());
                        grade.append("| Grade: ").append(studentPosts.get(i).getGrade()).append("\n");
                    }
                    JOptionPane.showMessageDialog(null, grade,
                            "Your Grades", JOptionPane.PLAIN_MESSAGE);
                } else {
                    try {
                        ArrayList<String> students = new ArrayList<>();
                        for (int i = 0; i < Account.getCreatedAccounts().size(); i++) {
                            if (Account.getCreatedAccounts().get(i).isStudent()) {
                                students.add(Account.getCreatedAccounts().get(i).getUsername());
                            }
                        }
                        if (students.size() == 0) {
                            throw new NullPointerException();
                        }
                        String selectedName = (String) JOptionPane.showInputDialog(null, "Choose a student",
                                "Grade Manager", JOptionPane.INFORMATION_MESSAGE, null, students.toArray(),
                                students.get(0));
                        Account selectedStudent = Account.retrieveAccount(selectedName);
                        ArrayList<DiscussionPosts> studentPosts =
                                selectedForum.sortByStudent(selectedStudent.getUsername());
                        if (studentPosts.isEmpty()) {
                            throw new NullPointerException();
                        }
                        String[] postNames = new String[studentPosts.size()];
                        for (int i = 0; i < studentPosts.size(); i++) {
                            postNames[i] = studentPosts.get(i).getTitle();
                        }
                        String postName = ((String) JOptionPane.showInputDialog(null, "Choose a post",
                                "Grade Manager", JOptionPane.INFORMATION_MESSAGE, null, postNames,
                                postNames[0]));
                        DiscussionPosts selectedPost = null;
                        for (int i = 0; i < studentPosts.size(); i++) {
                            if (studentPosts.get(i).getTitle().equals(postName)) {
                                selectedPost = studentPosts.get(i);
                                break;
                            }
                        }
                        try {
                            int grade = Integer.parseInt(JOptionPane.showInputDialog(null, "Assign a grade",
                                    "Grade Manager", JOptionPane.INFORMATION_MESSAGE));
                            selectedPost.setGrade(grade);
                        } catch (NumberFormatException ne) {
                            JOptionPane.showMessageDialog(null, "Please enter a number",
                                    "Grade Manager", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (NullPointerException ne) {
                        JOptionPane.showMessageDialog(null, "There are no Posts for that Student.",
                                "Grade Manager", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            if (e.getSource() == selectForumButton) {
                int selectedIndex = forums.getSelectedIndex();
                try {
                    oos.writeObject("selectForum");
                    oos.flush();
                    oos.writeObject(selectedIndex);
                    oos.flush();
                    int forumIndex = course.getForums().indexOf(forums);
                    oos.writeObject(forumIndex);
                    oos.flush();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    DiscussionClient client = new DiscussionClient();
                    client.setCrashed(true);
                    client.run();
                }
                new PostsMenu(course.getForums().get(forums.getSelectedIndex()), course, oos, ois, account);
                frame.dispose();
            }
            if (e.getSource() == backButton) {
                try {
                    frame.setVisible(false);
                    frame.dispose();
                    oos.writeObject("back");
                    oos.flush();
                    Courses.setCourses((ArrayList<Courses>) ois.readObject());
                    new CourseMenu(Courses.getCourses(), oos , ois, account);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    DiscussionClient client = new DiscussionClient();
                    client.setCrashed(true);
                    client.run();
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                    DiscussionClient client = new DiscussionClient();
                    client.setCrashed(true);
                    client.run();
                }
            }
        }
    };

    public ForumsMenu(Courses course, Account account, ObjectOutputStream oos, ObjectInputStream ois) {
        this.oos = oos;
        this.ois = ois;
        this.account = account;
        this.course = course;
        /* set up JFrame */
        frame = new JFrame(course.getTitle());
        Container content = frame.getContentPane();
        content.setLayout(null);
        String[] forumNames = new String[course.getForums().size()];
        if (forumNames.length == 0) {
            forumNames = new String[1];
            forumNames[0] = "No Forums in this Course";
        } else {
            for (int i = 0; i < forumNames.length; i++) {
                forumNames[i] = course.getForums().get(i).getTitle();
            }
        }
        forums = new JComboBox<>(forumNames);
        forums.setBounds(200, 70, 200, 40);

        if (DiscussionClient.getLoggedInAccount().isStudent()) {
            gradeButton = new JButton("View Grades");
        } else if (DiscussionClient.getLoggedInAccount().isTeacher()) {
            gradeButton = new JButton("Grade a Student");
        }
        gradeButton.addActionListener(actionListener);
        gradeButton.setBounds(200, 270, 200, 40);

        createForumButton = new JButton("Create a Forum");
        createForumButton.addActionListener(actionListener);
        createForumButton.setBounds(200, 120, 200, 40);

        selectForumButton = new JButton("View Posts");
        selectForumButton.addActionListener(actionListener);
        selectForumButton.setBounds(200, 170, 200, 40);

        editForumButton = new JButton("Edit a Forum");
        editForumButton.addActionListener(actionListener);
        editForumButton.setBounds(200, 220, 200, 40);

        backButton = new JButton("Go back");
        backButton.addActionListener(actionListener);
        backButton.setBounds(200, 320, 200, 40);



        JLabel label = new JLabel("", JLabel.CENTER);
        label.setText("Select a forum");
        label.setFont(new Font("Serif", Font.BOLD, 20));
        label.setOpaque(true);
        label.setBounds(100, 40, 400, 40);

        content.add(label);
        content.add(forums);
        content.add(createForumButton);
        content.add(selectForumButton);
        content.add(editForumButton);
        content.add(gradeButton);
        content.add(backButton);

        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);



    }

}
