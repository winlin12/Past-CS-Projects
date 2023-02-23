# project-5

Submissions: 
report by Aruzhan

vocareum code by Ameya 

presentation video by Winston


# how to compile and run project

The program can be compiled and ran by first running the DiscussionServer class, and then running the DiscussionClient class, which will display the first GUI and prompt the user for further actions. 
You can run muitiple threads of DiscussionClient by allowing parallel run in IntelliJ.

# detailed description of each class 
Account.java
This class contains some functions that allow users to create, edit, and delete their accounts. There is also the function that makes the user log in. 
The fields:
private static ArrayList<Account> createdAccounts - the ArrayList of created accounts
private String username - username
private String password - password
private boolean isTeacher - checking if the user's status is teacher
private boolean isStudent - checking if the user's status is student
ArrayList<Integer> upVotedPosts - the arraylist to keep track of the post ID for the post the student has upvoted. It's an integer list instead of a post list to avoid problems with duplicate posts, which makes the code for Accounfiles simpler. ArrayList for teachers is going to be empty and won't be called.


AccountsFile.java
This is the class that saves the created accounts into a text file “AccountFile.txt” and returns the original ArrayList of created accounts from the text file.

Courses.java
This is the class that creates and prints Courses also create, edit, delete, and print the forum that is in the courses.
The fields:
private String title - the title of courses
private ArrayList<DiscussionForums> forums - the ArrayList to store the discussion forums. The discussion forums are created, edited, and deleted through the use of this ArrayList.
private static ArrayList<Courses> courses  - initialize the capacity of ArrayList of courses to 0. As same as the ArrayList of discussion forums this ArrayList is used to store the courses. 

DiscussionForums.java
This is the class that creates the prompts and title for the forum. It also creates, edits, deletes and prints the posts that are in the forum. DiscussionForums objects are called by Courses.
The fields:
private String title - the title of the forum

private String prompt - the contents of the forum
ArrayList<DiscussionPosts> posts = new ArrayList<>() - initializing the ArrayList to store the discussion post. The discussion posts are created, edited, and deleted through the use of this ArrayList.
private String forumTimeStamp - determine the date and time of the forum created

DiscusisonPosts.java
This class creates the title and content of the posts. Also, the date and time of the post, username of the user who made the post, grade of the post, replies of the post, and the votes from the user for the posts are determined within this class. DiscussionPosts objects are called by DiscussionForums. 
The fields:
private int postID - this field allows to avoid problems with the duplicate post, two posts with the same title, content and poster will still be regarded differently. A post that is edited will also retain its unique ID. Important for the upvote method.
private String title - the title of the post
private static int counter - already initialized and equal to zero, used to assign a post ID
private String content - the content of the post
private String poster - the name of the user who posted the post
private String postTimeStamp - the date and time of the post created
private ArrayList<Replies> replies - ArrayList that stores the data of replies
private int grade - the grade given by the teacher
private int votes - votes

FileStorage.java
This class stores the pieces of information about courses, discussion forums, discussion posts and replies storing them in the files.

Replies.java 
This class creates the Replies objects with fields content, timeStamp, and commenter, and the methods associated with it, such as displayReply. Replies objects are called by DiscussionPosts. 
The fields:
String content - the content of replies
String timeStamp - the date and time of the replies posted
String commenter - the name of the user who posts the replies

Student.java
The student class extends the Account class with additional information specific to students, which includes the ability to upvote posts (comments to forums) once. 
The fields:
private String username - username
private String password - password

Teacher.java
The teacher class also extends the Account class with additional methods for teachers, including their ability to create, edit, and delete forums. Teachers can also sort the discussion replies by the number of upvotes, view the posts made by a specific student, and assign grades to posts. 

Menu: This is the class responsible for the first GUI that the user sees upon connecting to the client. This page displays the buttons for the options for logging in, creating an account, and quitting. As is with all of the classes that have to do with displaying GUIs, if an error occurs, it is caught and a new client is called. Also, all updates, if they do not throw any errors, will be updated through the server by object output stream and object input stream. 

LoggedInMenu: This class is called from the Menu class if the user logs in successfully. It will display the options to edit and delete the current logged in account and select the course, and log out. This class calls the 

CourseMenu: This class is called from the LoggedInMenu, and shows the options to create, edit, delete the course and select forums , and go back to the logged in menu. 

DiscussionClient: this class implements Runnable, which allows its instances to be executed by a thread. In its run function, it attempts to connect to the socket, creates a new ois and oos, and displays the first Menu. 

DiscussionHandler: this class handles the process of writing and retrieving information with AccountsFile and FileStorage methods, depending on what the String postDecision shows. 

DiscussionServer: the DiscussionServer sets up a socket and waits for a client to connect. It also has methods for edit created accounts. 
  
ForumsMenu: This class is called from the CourseMenu class, and displays the options to create, edit, delete the forums, move to post menu, go back to forum menu and (Teacher side: give grade to student, Student side: check the grade)

PostsMenu: this class displays  the option to create a reply, sort the replies by number of votes given by users, display the date and time of post created and then go back to the forum menu. 

RepliesMenu: The RepliesMenu is the last GUI class and it shows the option to create a reply, vote once for each user, go back to the post menu, display the  title, content, and date and time of the replies. With all classes that have to do with GUI, it shows appropriate error messages.


  
 
 
