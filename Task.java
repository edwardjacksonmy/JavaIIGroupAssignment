import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.LocalDate;

public class Task {
    private int id;
    private String title;
    private String description;
    private Date startDate;
    private Date dueDate;

    public Task(String title, String description, String startDate, String dueDate) throws ParseException {
        this.title = title;
        this.description = description;
        this.startDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
        this.dueDate = new SimpleDateFormat("yyyy-MM-dd").parse(dueDate);
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

    // Inside the Task class
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



}
