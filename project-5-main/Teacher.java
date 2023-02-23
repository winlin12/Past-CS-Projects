import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

//extends discussion posts?

public class Teacher extends Account implements Serializable {
    // dunno if we want a password
    //also identifier should be unique
    public Teacher (String username, String password) {
        super(username, password, true, false);
    }

    //create, edit, delete discussion forums
    //for teachers
    //creating a new arraylist of discussionposts for each new forum but it doesn't work
    public void createForum(String title, String prompt, Courses course) {
        course.createForum(title, prompt);
    }

    public void deleteForum(String title, Courses course) {
        course.deleteForum(title);
    }

    public void editForum(String title, String content, Courses course) {
        course.editForum(title, content);
    }

    public void createCourse(Courses course) {
        course.createCourse(course);
    }

    public void deletePost(int id, DiscussionForums forum) {
        forum.deletePost(id);
    }

    public void editPost(String title, String newContent, DiscussionForums forum) {
        forum.editPost(title, newContent);
    }

    // Teacher chooses a post to assign a grade to based off of post order.
    public void assignGrade(DiscussionForums forum, int index, int grade) {
        forum.getPosts().get(index).setGrade(grade);
    }

    public static Teacher retrieveAccount(String username, String password) {
        return (Teacher) Account.retrieveAccount(username, password);
    }

}
