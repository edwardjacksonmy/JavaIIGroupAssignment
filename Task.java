import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.LocalDate;

public class Task {
    //Set variable
    private int id;
    private String title;
    private String description;
    private Date startDate;
    private Date dueDate;
    private String personInCharge;
    private Date dateCreated;
    private Date dateEdited;


    private static final String DATE_FORMAT = "yyyy-MM-dd";

    //Set constructor, set parameter inside constructor
    public Task(String title, String description, String startDate, String dueDate, String personInCharge) throws ParseException {
        this.title = title;
        this.description = description;
        this.startDate = new SimpleDateFormat(DATE_FORMAT).parse(startDate);
        this.dueDate = new SimpleDateFormat(DATE_FORMAT).parse(dueDate);
        this.personInCharge = personInCharge;
        this.dateCreated = new Date(); // Set current date and time as dateCreated
        this.dateEdited = new Date(); // Set current date and time as dateEdited
    }

    // Implement the nextId as a static variable inside the Task class
    private static int nextId = 1;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static int getNextId() {
        return nextId++;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getPersonInCharge() {
        return personInCharge;
    }

    public void setPersonInCharge(String personInCharge) {
        this.personInCharge = personInCharge;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateEdited() {
        return dateEdited;
    }

    public void setDateEdited(Date dateEdited) {
        this.dateEdited = dateEdited;
    }
}
