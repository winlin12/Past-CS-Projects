import java.io.Serializable;
import java.util.Date;
import java.util.*;


public class Replies implements Serializable {
    String content;
    String timeStamp;
    String commenter;

    //Constructor sets the timeStamp to the date and time at which the reply was posted
    //Create a new reply and check if you like the format
    public Replies(String content, String commenter) {
        this.content = content;
        Date d1 = new Date();
        this.timeStamp = d1.toString();
        this.commenter = commenter;
    }
    // Constructor for when the reply is retrieved from Datafile
    public Replies(String content, String timeStamp, String commenter) {
        this.content = content;
        this.timeStamp = timeStamp;
        this.commenter = commenter;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCommenter() {
        return commenter;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String displayReply(){
        return String.format("posted by: %s\ncreated on: %s\n%s", commenter, timeStamp,
                content);
    }

    public String getTime() {
        return timeStamp;
    }

    public void setTime(String time) {
        this.timeStamp = time;
    }

    public int compareTo(Replies reply) {
        return getTime().compareTo(reply.getTime());
    }
}
