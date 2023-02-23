import java.io.Serializable;
import java.util.ArrayList;

public class Courses implements Serializable {
    private String title;
    private ArrayList<DiscussionForums> forums;
    private static ArrayList<Courses> courses = new ArrayList<Courses>(0);

    public Courses(String title) {
        this.title = title;
        ArrayList<DiscussionForums> forums = new ArrayList<DiscussionForums>();
        this.forums = forums;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static void createCourse(Courses course) {
        courses.add(course);
    }

    public ArrayList<DiscussionForums> getForums() {
        return forums;
    }

    public void setForums(ArrayList<DiscussionForums> forums) {
        this.forums = forums;
    }

    public boolean createForum(String title, String prompt) {
        for (DiscussionForums forum : forums) {
            if (forum.getTitle().equals(title)) {
                System.out.println("Forum already exists.");
                return false;
            }
        }
        forums.add(new DiscussionForums(title, prompt));
        System.out.println("Forum created");
        return true;
    }

    public void deleteForum(String title) {
        for (DiscussionForums forum : forums) {
            if(forum.getTitle().equals(title)) {
                forums.remove(forum);
                System.out.println("Forum deleted!");
                return;
            }
        }
        System.out.println("Forum was not found");
    }

    public void editForum(String title, String content) {
        for (DiscussionForums forum : forums) {
            if(forum.getTitle().equals(title)) {
                forum.setPrompt(content);
                System.out.println("Forum edited!");
                return;
            }
        }
        System.out.println("Forum was not found");
    }

    public static ArrayList<Courses> getCourses() {
        return courses;
    }

    public String getTitle() {
        return title;
    }

    public static void setCourses(ArrayList<Courses> courses) {
        Courses.courses = courses;
    }

    //Method to create forum by file import
    public void createForumByFile(String fileName) {
        DiscussionForums f = new DiscussionForums(fileName);
        createForum(f.getTitle(), f.getPrompt());
    }

    // Prints all Courses, starts at 1 because 0 is reserved for a new course.
    public static void printCourses() {
        for (int i = 0; i < courses.size(); i++) {
            System.out.printf("%d: %s\n", i + 1, courses.get(i).getTitle());
        }
    }

    public static void printForums(Courses course) {
        for (int i = 0; i < course.getForums().size(); i++) {
            System.out.printf("%d: %s\n", i + 1, course.getForums().get(i).getTitle());
        }
    }
}
