import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class PostsMenu extends JFrame {
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
    private Courses course;

    private Account account;

    static ObjectOutputStream oos;
    static ObjectInputStream ois;

    JButton createReplyButton; // adds a new reply
    JButton sortByVotes; //Allows teachers to sort by votes
    JButton editForumButton; // Allows a user to edit or delete a course.
    JButton backButton; // a button to erase the line
    JButton view; //Views a post in more detail.
    ArrayList<JButton> viewButtons = new ArrayList<>(); // All view buttons

    private DiscussionForums forums;

    ActionListener actionListener = new ActionListener() {
        @Override
        //ADD THE BUTTONS FOR EACH STUDENT NAME AND UPVOTES
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == createReplyButton) {
                String DiscussionTitle = JOptionPane.showInputDialog(null, enterPostTitle,
                        "Enter reply title", JOptionPane.INFORMATION_MESSAGE);
                String DiscussionContent = JOptionPane.showInputDialog(null, enterPostContent,
                        "Enter reply content", JOptionPane.INFORMATION_MESSAGE);
                forums.createPost(DiscussionTitle, DiscussionContent, DiscussionClient.getLoggedInAccount().getUsername());
                JOptionPane.showMessageDialog(null, "Post created", "Create Post",
                        JOptionPane.INFORMATION_MESSAGE);
                new PostsMenu(forums, course, oos, ois, account);
                frame.dispose();
                try {
                    oos.writeObject("createPost");
                    oos.flush();
                    oos.writeObject(DiscussionTitle);
                    oos.flush();
                    oos.writeObject(DiscussionContent);
                    oos.flush();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            if (e.getSource() == sortByVotes) {
                forums.sortByLikes();
                new PostsMenu(forums, course, oos, ois, account);
                frame.dispose();
            }
            if (e.getSource() == backButton) {

                new ForumsMenu(course, account, oos, ois);
                frame.dispose();
                try {
                    oos.writeObject("back");
                    oos.flush();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            for (int i = 0; i < viewButtons.size(); i++) {
                if (e.getSource() == viewButtons.get(i)) {
                    new RepliesMenu(forums.getPosts().get(i), forums, course, oos, ois);
                    try {
                        oos.writeObject("select");
                        oos.flush();
                        oos.writeObject(i);
                        oos.flush();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

            }
        }
    };

    public JPanel formatPosts(DiscussionPosts currentPost) {
        JPanel response = new JPanel(new BorderLayout());
        JLabel studentName = new JLabel("Posted By: " + currentPost.getPoster() + " | Likes: " +
                currentPost.getVotes());
        JTextArea title= new JTextArea(currentPost.getTitle());
        //ADD THE REPLIES
        JLabel date = new JLabel(currentPost.getPostTimeStamp());
        view = new JButton("View");
        viewButtons.add(view);
        //JLabel votes = new JLabel(String.valueOf(currentPost.getVotes()));
        response.add(studentName, BorderLayout.NORTH);
        response.add(title, BorderLayout.CENTER);
        response.add(date, BorderLayout.SOUTH);
        response.add(view, BorderLayout.EAST);
        //response.add(votes, BorderLayout.SOUTH);

        view.addActionListener(actionListener);
        return response;
    }


    public PostsMenu(DiscussionForums currentForum, Courses currentCourse, ObjectOutputStream oos,
                     ObjectInputStream ois, Account account) {
        this.oos = oos;
        this.ois = ois;
        this.account = account;
        /* set up JFrame */
        frame = new JFrame();
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());
        JPanel responses = new JPanel();
        JScrollPane scrollPane = new JScrollPane(responses);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        JPanel heading = new JPanel();
        content.setLayout(new BorderLayout());
        JPanel footer = new JPanel();
        content.setLayout(new BorderLayout());
        forums = currentForum;
        course = currentCourse;

        for (int i = 0; i < currentForum.getPosts().size(); i++) {
            DiscussionPosts currentPost = currentForum.getPosts().get(i);
            responses.add(formatPosts(currentPost));
        }

        content.add(new JScrollPane(responses), BorderLayout.CENTER);
        content.add(heading, BorderLayout.NORTH);
        content.add(footer, BorderLayout.SOUTH);
        footer.add(new JTextField(currentForum.getForumTimeStamp()), BorderLayout.CENTER);


        createReplyButton = new JButton("Create a Reply");
        createReplyButton.addActionListener(actionListener);
        footer.add(createReplyButton, BorderLayout.SOUTH);
        //how to check for teacher
        heading.add(new JLabel("Forum: " + currentForum.getTitle() + "\n"), BorderLayout.NORTH);
        heading.add(new JLabel("Prompt: " + currentForum.getPrompt()), BorderLayout.NORTH);
        sortByVotes = new JButton("Sort by Votes");
        sortByVotes.addActionListener(actionListener);
        footer.add(sortByVotes, BorderLayout.SOUTH);

        backButton = new JButton("Go back");
        backButton.addActionListener(actionListener);
        footer.add(backButton, BorderLayout.SOUTH);

        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

}
