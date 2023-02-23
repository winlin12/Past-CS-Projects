import java.io.*;
import java.util.ArrayList;

public class FileStorage {
    File f;
    File f2;
    BufferedReader bfr ;
    PrintWriter pw;
//We really only have to call the constructor, the encode(), and the retrieve() methods.
    //I've just split the code into different methods for my convenience

    //HOW TO EDIT: If you want to add some data to the file, make sure all the fields are printed into,
    // and read from the file in the same order.
    // If you're making changes, be careful while calling println or readline

    //Constructor creates files if they doesn't exist
    public FileStorage() {
        try {
            this.f = new File("Datafile.txt");
            if (!this.f.exists()) {
                this.f.createNewFile();
            }
            this.f2 = new File("Datafile.txt");
            if (!this.f2.exists()) {
                this.f2.createNewFile();
            }
            this.bfr = new BufferedReader(new FileReader(f));
            //this.pw = new PrintWriter(new FileOutputStream(f, false));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Prints post attributes in the file, followed by all the replies
    public void encodePost(DiscussionPosts p) {
        pw.println(p.getTitle());
        String change = p.getContent().replace("\n", "\f");
        pw.println(p.getPoster());
        pw.println(p.getGrade());
        pw.println(p.getVotes());
        pw.println(p.getPostID());
        pw.println(p.getPostTimeStamp());
        pw.println(change);
        if (p.getReplies() != null) {
            for (Replies r : p.getReplies()) {
                pw.println("\fNEW REPLY");
                pw.println(r.getContent());
                pw.println(r.getTime());
                pw.println(r.getCommenter());
            }
        }

        pw.println("\f");
    }

    //Retrieves a post from the file, adds all encoded replies to the post's replies arraylist
    public DiscussionPosts retrievePost() throws IOException {
        String title1 = bfr.readLine();
        String user = bfr.readLine();
        int grade1 = Integer.parseInt(bfr.readLine());
        int votes1 = Integer.parseInt(bfr.readLine());
        int id = Integer.parseInt(bfr.readLine());
        String timeStamp = bfr.readLine();
        String content1 = bfr.readLine();
        content1 = content1.replace("\f", "\n");
        DiscussionPosts myPost = new DiscussionPosts(title1, content1, user, grade1, votes1, id,timeStamp);
        while ((bfr.readLine().equals("\fNEW REPLY"))) {
            String c = bfr.readLine();
            String t = bfr.readLine();
            String c1 = bfr.readLine();
            myPost.reply(new Replies(c, t, c1));
        }
        return myPost;
    }

    //Prints forum attributes in the file, followed by all the replies
    public void encodeForum(DiscussionForums f) {
        pw.println(f.getTitle());
        String forumChange = f.getPrompt().replace("\n", "\f");
        pw.println(forumChange);
        pw.println(f.getForumTimeStamp());
        for (DiscussionPosts i : f.getPosts()) {
            pw.println("\fNEW POST");
            encodePost(i);
        }
        pw.println("\f");
    }

    //Retrieves a forum from the file, adds all encoded replies to the forum's posts arraylist
    public DiscussionForums retrieveForum() throws IOException {
        String forumTitle = bfr.readLine();
        String forumPrompt = bfr.readLine();
        String fts = bfr.readLine();
        String lel = forumPrompt.replace("\f", "\n");
        DiscussionForums myForum = new DiscussionForums(forumTitle, lel, fts);
        while (bfr.readLine().equals("\fNEW POST")) {
            DiscussionPosts kale = retrievePost();
            myForum.getPosts().add(kale);

        }
        return myForum;
    }

    // Same thing for course
    public void encodeCourse(Courses c) {
        pw.println(c.getTitle());
        for (DiscussionForums i : c.getForums()) {
            pw.println("\fNEW FORUM");
            encodeForum(i);
        }
        pw.println("\f");
    }

    public Courses retrieveCourse() throws IOException {
        String courseTitle = bfr.readLine();
        Courses myCourse = new Courses(courseTitle);
        while (bfr.readLine().equals("\fNEW FORUM")) {
            DiscussionForums fee = retrieveForum();
            myCourse.getForums().add(fee);
        }

        return myCourse;
    }

    //Encodes all the courses in the courses arraylist
    public void encode(ArrayList<Courses> list) throws IOException {
        pw = new PrintWriter(new FileWriter(f, false));
        pw.println(DiscussionPosts.getCounter());
        for (Courses i : list) {
            pw.println("\fNEW COURSE");
            encodeCourse(i);
        }
        pw.println("\f");
        pw.close();
    }

    //retrieves an arraylist of courses, who within them have all original forums, posts, replies
    public ArrayList<Courses> retrieve() throws IOException {
        bfr = new BufferedReader(new FileReader("Datafile.txt"));
        DiscussionPosts.setCounter(Integer.parseInt(bfr.readLine()));
        ArrayList<Courses> list = new ArrayList<>();
        while (bfr.readLine().equals("\fNEW COURSE")) {
            Courses c = retrieveCourse();
            list.add(c);
        }
        bfr.close();
        return list;

    }


}
