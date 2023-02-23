import javax.swing.plaf.metal.OceanTheme;
import java.io.*;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class DiscussionServer {
    private static ArrayList<DiscussionHandler> discussions = new ArrayList<>();
    private static ArrayList<Account> createdAccounts = new ArrayList<>();
    private static ArrayList<Courses> courses = new ArrayList<>();
    private static Socket socket;
    // Both createdAccounts and courses are
    // shared among the entire program. Each course has its own Arraylist of Forums that is stored within itself.
    // TODO: Properly store information inside the server, and add a "refresh" button that automatically updates
    //  information in the client. File IO can be used (i think), but it must only be done serverside.
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(4302);
            while (!serverSocket.isClosed()) {
                System.out.println("Waiting for client to connect");
                socket = serverSocket.accept();
                System.out.println("A client has connected!");

                DiscussionHandler clientDiscussion;
                clientDiscussion = new DiscussionHandler(socket);
                discussions.add(clientDiscussion);
                //BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                //PrintWriter pw = new PrintWriter(socket.getOutputStream());
                //ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                //ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

                clientDiscussion.start();
            }
        } catch (SocketException s) {
            s.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Account> getCreatedAccounts() {
        return createdAccounts;
    }

    public static ArrayList<Courses> getCourses() {
        return courses;
    }

    public static ArrayList<DiscussionHandler> getDiscussions() {
        return discussions;
    }

    public static void setCreatedAccounts(ArrayList<Account> createdAccounts) {
        DiscussionServer.createdAccounts = createdAccounts;
    }

    public static void setCourses(ArrayList<Courses> courses) {
        DiscussionServer.courses = courses;
    }

    public static void writeCreatedAccounts(ObjectOutputStream oos) throws IOException {
        oos.writeObject(createdAccounts);
    }
    /*
    public static void shutdownServer() {
        boolean alive = false;
        for (DiscussionHandler discussion : DiscussionServer.getDiscussions()) {
            if (discussion.isAlive()) {
                alive = true;
                break;
            }
        }
        if (!alive) {
            System.exit(0);
        }
    } */
}
