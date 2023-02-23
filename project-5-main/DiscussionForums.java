import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class DiscussionForums implements Serializable {
    private String title;
    private String prompt;
    ArrayList<DiscussionPosts> posts = new ArrayList<>();
    //   private Courses course;
    private String forumTimeStamp;
    //   private ArrayList<DiscussionForums> forums = new ArrayList<DiscussionForums>();

    public DiscussionForums(String title, String prompt) {
        this.title = title;
        this.prompt = prompt;
        this.posts = new ArrayList<>();
        Date d1 = new Date();
        this.forumTimeStamp = d1.toString();
    }

    //Constructor when forum is retrieved from datafile
    public DiscussionForums(String title, String prompt, String forumTimeStamp) {
        this.title = title;
        this.prompt = prompt;
        this.forumTimeStamp = forumTimeStamp;
    }

    //Constructor if the forum is created via file login
    //first line is made the title, rest is read as the prompt
    public DiscussionForums(String fileName) {
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
            this.prompt = s;
            Date d1 = new Date();
            this.forumTimeStamp = d1.toString();
        } catch (FileNotFoundException e) {
            System.out.println("Ensure the file exists and path is correct.");
        } catch (Exception e) {
            System.out.println("An error has occured while reading this file.");
        }
    }


    public String getForumTimeStamp() {
        return forumTimeStamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public void createPost(String title, String content, String poster) {
        posts.add(0, new DiscussionPosts(title, content, poster));
    }

    public void fileCreatePost(String fileName, String poster) {
        posts.add(0, new DiscussionPosts(fileName, poster));
    }

    public void createPost(String title, String content, String poster, int grade, int votes, int postID,
                           String timeStamp) {
        posts.add(new DiscussionPosts(title, content, poster, grade, votes, postID, timeStamp));
    }

    public boolean deletePost(int id) {
        for (DiscussionPosts post : posts) {
            if (post.getPostID() == id) {
                posts.remove(post);
                return true;
            }
        }
        return false;
    }

    public boolean editPost(String title, String newContent) {
        for (int i = 0; i < posts.size(); i++) {
            if (posts.get(i).getTitle().equals(title)) {
                String poster = posts.get(i).getPoster();
                int votes = posts.get(i).getVotes();
                int grade = posts.get(i).getGrade();
                int postID = posts.get(i).getPostID();
                String timeStamp = posts.get(i).getPostTimeStamp();
                posts.set(i, new DiscussionPosts(title, newContent, poster, grade, votes, postID, timeStamp));
                return true;
            }
        }
        return false;
    }

    public ArrayList<DiscussionPosts> getPosts() {
        return posts;
    }

    //Create post method for creating post through file import
    public void createPostByFile(String fileName, String poster) {
        posts.add(0, new DiscussionPosts(fileName, poster));
    }
    public void printPosts(DiscussionForums forum) {
        for (int i = 0; i < posts.size(); i++) {
            System.out.printf("%d: %s\n", i + 1, forum.getPosts().get(i).getTitle());
        }
    }
    public ArrayList<DiscussionPosts> sortByLikes () {
        ArrayList<DiscussionPosts> posts = getPosts();
        ArrayList<Integer> likes = new ArrayList<>();
        for (DiscussionPosts post : posts) {
            likes.add(post.getVotes());
        }
        Collections.sort(likes, Collections.reverseOrder());
        /* Should swap each element in posts based on if the likes are equal to it.
         * Looks through each element in likes, compares it to each element in j, if votes are equal, swap elements in
         * posts.*/
        for (int i = 0; i < posts.size(); i++) {
            for (int j = 0; j < posts.size(); j++) {
                if (likes.get(i) == posts.get(j).getVotes()) {
                    DiscussionPosts temp = posts.get(i);
                    posts.set(i, posts.get(j));
                    posts.set(j, temp);
                }
            }
        }
        return posts;
    }
    public ArrayList<DiscussionPosts> sortByStudent(String studentUsername) {
        ArrayList<DiscussionPosts> allPosts = getPosts();
        ArrayList<DiscussionPosts> studentPosts = new ArrayList<>();
        for (int i = 0; i < allPosts.size(); i++) {
            if (getPosts().get(i).getPoster().equals(studentUsername)) {
                studentPosts.add(getPosts().get(i));
            }
        }
        return studentPosts;
    }

}
