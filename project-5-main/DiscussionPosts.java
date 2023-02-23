import java.io.*;
import java.util.ArrayList;
import java.util.Date;

public class DiscussionPosts implements Serializable {
    private int postID; //This field just allows us to avoid problems with duplicate posts,
    // Two posts with same title, content and poster will still be regarded differently.
    // A post which is edited will also retain its unique ID
    // Important for the upvote method
    private String title;
    private static int counter = 0; //used to assign a post ID
    private String content;
    private String poster;
    private DiscussionForums forum;
    private String postTimeStamp;
    //ArrayList<DiscussionPosts> posts = new ArrayList<DiscussionPosts>();
    private ArrayList<Replies> replies = new ArrayList<>();
    private int grade;
    private int votes;

    //constructor for when the post is made by a student
    public DiscussionPosts(String title, String content, String poster) {
        this.title = title;
        this.content = content;
        this.poster = poster;
        this.grade = 0;
        this.votes = 0;
        counter++; // makes sure all post IDs are different
        this.postID = counter;
        Date d1 = new Date();
        this.postTimeStamp = d1.toString();
        this.replies = new ArrayList<>();
    }


    //constructor when the post is retrieved from DataFile.txt
    public DiscussionPosts(String title, String content, String poster, int grade, int votes, int postID,
                           String timeStamp) {
        this.title = title;
        this.content = content;
        this.poster = poster;
        this.grade = grade;
        this.votes = votes;
        this.postID = postID;
        this.postTimeStamp = timeStamp;
    }

    //Constructor when the post is created via a file import
    //The first line of the file will be taken as title
    //The rest of the file will be taken as content
    public DiscussionPosts(String fileName, String poster) {
        try {
            File f = new File(fileName);
            BufferedReader bfr = new BufferedReader(new FileReader(f));
            this.title = bfr.readLine();
            String s = "";
            while (true) {
                String k = bfr.readLine();
                if (k == null) {
                    break;
                }
                s += k;
                s += "\n";
            }
            this.content = s;
            this.poster = poster;
            this.grade = 0;
            this.votes = 0;
            counter++;
            postID = counter;
            Date d1 = new Date();
            this.postTimeStamp = d1.toString();
        } catch (FileNotFoundException e) {
            System.out.println("Ensure the file exists and path is correct");
        } catch (IOException e) {

        }
    }


    public void reply(Replies reply) {
        if (replies == null) {
            replies = new ArrayList<Replies>();
        }
        replies.add(0, reply);
    }

    //I think editsPost might be better in posts to avoid problems with duplicate post titles
    public void editsPost(String newTitle, String newContent) {
        title = newTitle;
        content = newContent;
    }

    public void upVoted() {
        votes++;
    }

    public String getTitle() {
        return title;
    }

    public String getPoster() {
        return poster;
    }

    public int getPostID() {
        return postID;
    }

    public int getVotes() {
        return votes;
    }

    public String getContent() {
        return content;
    }

    public int getGrade() {
        return grade;
    }

    public static int getCounter() {
        return counter;
    }
    public String getPostTimeStamp() {
        return postTimeStamp;
    }

    public ArrayList<Replies> getReplies() {
        return replies;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public static void setCounter(int counter) {
        DiscussionPosts.counter = counter;
    }

    public String displayPost() {
        String s = String.format("Post by: %s\nCreated on: %s\n%s\nReplies: \n", poster, postTimeStamp, content);
        int i = 1;
        if (replies == null) {
            return s + "No Replies Yet";
        } else {
            for (Replies r : replies) {
                s += "Reply " + i + ":\n" + r.displayReply();
            }
        }
        return s;
    }
}
