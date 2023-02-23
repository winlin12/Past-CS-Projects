import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class RepliesMenu extends JFrame {
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

    static ObjectOutputStream oos;
    static ObjectInputStream ois;

    JButton createReplyButton; // adds a new reply
    JButton sortByVotes; //Allows teachers to sort by votes
    JButton editForumButton; // Allows a user to edit or delete a course.
    JButton backButton; // a button to erase the line
    JButton vote; //Votes a post

    private DiscussionForums forums;
    private DiscussionPosts post;
    ActionListener actionListener = new ActionListener() {
        @Override
        //ADD THE BUTTONS FOR EACH STUDENT NAME AND UPVOTES
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == createReplyButton) {
                String DiscussionTitle = JOptionPane.showInputDialog(null, enterPostTitle,
                        "Enter reply title", JOptionPane.INFORMATION_MESSAGE);
                String DiscussionContent = JOptionPane.showInputDialog(null, enterPostContent,
                        "Enter reply content", JOptionPane.INFORMATION_MESSAGE);
                post.reply(new Replies(DiscussionTitle, DiscussionContent,
                        DiscussionClient.getLoggedInAccount().getUsername()));
                JOptionPane.showMessageDialog(null, "Post created", "Create Post",
                        JOptionPane.INFORMATION_MESSAGE);
                new RepliesMenu(post, forums, course, oos, ois);
                frame.dispose();
                try {
                    oos.writeObject("create");
                    oos.flush();
                    oos.writeObject(DiscussionTitle);
                    oos.flush();
                    oos.writeObject(DiscussionContent);
                    oos.flush();
                    oos.writeObject(DiscussionClient.getLoggedInAccount().getUsername());
                    oos.flush();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            if (e.getSource() == backButton) {
                try {
                    oos.writeObject("back");
                    oos.flush();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                frame.dispose();
            }

            if (e.getSource() == vote) {
                if (DiscussionClient.getLoggedInAccount().upVote(post)) {
                    JOptionPane.showMessageDialog(null, "Post Upvoted!",
                            "Upvote Post", JOptionPane.PLAIN_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "You have already voted on this post.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    };

    public JPanel formatReplies(Replies currentReply) {
        JPanel response = new JPanel(new BorderLayout());
        JLabel studentName = new JLabel("Posted By: " + currentReply.getCommenter());
        JTextArea reply = new JTextArea(currentReply.getContent());
        JLabel date = new JLabel(currentReply.getTimeStamp());
        //vote = new JButton("Upvote");
        //voteButtons.add(vote);
        //JLabel votes = new JLabel(String.valueOf(currentPost.getVotes()));
        response.add(studentName, BorderLayout.NORTH);
        response.add(reply, BorderLayout.CENTER);
        response.add(date, BorderLayout.SOUTH);
        //response.add(vote, BorderLayout.EAST);
        //response.add(votes, BorderLayout.SOUTH);

        //vote.addActionListener(actionListener);
        return response;
    }


    public RepliesMenu(DiscussionPosts currentPost, DiscussionForums currentForum, Courses currentCourse,
                       ObjectOutputStream oos, ObjectInputStream ois) {
        this.oos = oos;
        this.ois = ois;
        /* set up JFrame */
        frame = new JFrame();
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());
        JPanel responses = new JPanel();
        JScrollPane scrollPane = new JScrollPane(responses);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        post = currentPost;

        for (int i = 0; i < post.getReplies().size(); i++) {
            Replies currentReply = post.getReplies().get(i);
            responses.add(formatReplies(currentReply));
        }

        forums = currentForum;
        course = currentCourse;
        post = currentPost;

        createReplyButton = new JButton("Create a Reply");
        createReplyButton.addActionListener(actionListener);
        Dimension size = createReplyButton.getPreferredSize();
        createReplyButton.setBounds(210, 200, size.width, size.height);

        JPanel heading = new JPanel();
        JPanel footing = new JPanel();
        content.setLayout(new BorderLayout());
        //how to check for teacher
        heading.add(new JTextField(post.getTitle()), BorderLayout.NORTH);
        heading.add(new JTextField(post.getContent()), BorderLayout.CENTER);
        heading.add(new JTextField(post.getPostTimeStamp()), BorderLayout.CENTER);

        backButton = new JButton("Go back");
        backButton.addActionListener(actionListener);
        backButton.setBounds(300, 200, size.width, size.height);

        vote = new JButton("Vote");
        vote.addActionListener(actionListener);

        footing.add(vote);
        footing.add(createReplyButton);
        footing.add(backButton);
        content.add(new JScrollPane(responses), BorderLayout.CENTER);
        content.add(heading, BorderLayout.NORTH);
        content.add(footing, BorderLayout.SOUTH);

        frame.setSize(400, 267);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

}
