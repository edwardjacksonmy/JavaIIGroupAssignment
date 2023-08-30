import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.text.ParseException;
import java.util.Date;

public class TaskDatabase {
    //Set constant
    //Use mysql connector, connect 3306 localhost to todo_db table
    private static final String DB_URL = "jdbc:mysql://localhost:3306/todo_db";
    //Set MySQL Database username
    private static final String DB_USER = "root";
    //Set MySQL Database password
    private static final String DB_PASSWORD = "";

    //Connect to the database, launch connection with the information stored in the constant
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static ArrayList<Task> getAllTasks() {
        //Show array list
        ArrayList<Task> tasks = new ArrayList<>();
        //Establish connection, search data from database, fetch value from table named "tasks"
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM tasks")) {

            while (rs.next()) {
                //Get id, title, description, start date, due date, date created, date edited from database
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                String startDateStr = rs.getString("start_date");
                String dueDateStr = rs.getString("due_date");
                String personInCharge = rs.getString("person_in_charge");
                Date dateCreated = rs.getDate("date_created"); // Get Date Created
                Date dateEdited = rs.getDate("date_edited");   // Get Date Edited

                //Requires constructor to run
                Task task = new Task(title, description, startDateStr, dueDateStr, personInCharge);
                task.setId(id);
                task.setDateCreated(rs.getDate("date_created"));
                task.setDateEdited(rs.getDate("date_edited"));
                tasks.add(task);
            }
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public static int getNextTaskId() {
        //Set variable 1st, otherwise everytime new id added will turn to 0.
        int nextId = 1;


        //Establish connection, search data from database, go to "tasks" table, select id according to ascending order.
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id FROM tasks ORDER BY id")) {
            //Find ID from top to bottom, if there is a gap between two numbers, then stop finding. Otherwise, keep finding the gap.
            while (rs.next()) {
                if (rs.getInt("id") != nextId) {
                    break;
                }
                nextId++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //Display id in 1 position after the smallest value
        return nextId;
    }

    public static void addTask(Task task) {
        //Launch connection, then use SQL query to add data into "tasks" table, use ? to undefine default value
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO tasks (id, title, description, start_date, due_date, person_in_charge, date_created, date_edited) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {

            //Find possible next ID value
            int nextId = getNextTaskId();

            //Print value out of the table
            pstmt.setInt(1, nextId);
            pstmt.setString(2, task.getTitle());
            pstmt.setString(3, task.getDescription());
            pstmt.setString(4, new SimpleDateFormat("yyyy-MM-dd").format(task.getStartDate()));
            pstmt.setString(5, new SimpleDateFormat("yyyy-MM-dd").format(task.getDueDate()));
            pstmt.setString(6, task.getPersonInCharge());
            pstmt.setDate(7, new java.sql.Date(task.getDateCreated().getTime())); // Convert java.util.Date to java.sql.Date
            pstmt.setDate(8, new java.sql.Date(new Date().getTime())); // Current date for dateEdited
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateTask(Task task) {
        //Launch connection, then use SQL query to update selected data different than original value into "tasks" table, next using ? to replace old value with new one in selected column.
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("UPDATE tasks SET title = ?, description = ?, start_date = ?, due_date = ?, person_in_charge = ?, date_edited = ? WHERE id = ?")) {

            //Print value out of the table
            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            pstmt.setString(3, new SimpleDateFormat("yyyy-MM-dd").format(task.getStartDate()));
            pstmt.setString(4, new SimpleDateFormat("yyyy-MM-dd").format(task.getDueDate()));
            pstmt.setString(5, task.getPersonInCharge());
            pstmt.setString(6, new SimpleDateFormat("yyyy-MM-dd").format(new Date())); // Current date
            pstmt.setInt(7, task.getId());
            //Update specified column with new value
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void deleteTask(int taskId) {
        //Establish connection, then use SQL query to delete selected data from "tasks" table, next using ? to select the id to be deleted
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM tasks WHERE id = ?")) {

            //Update the table with selected id removal
            pstmt.setInt(1, taskId);
            pstmt.executeUpdate();

            // After deleting the task, update the IDs of remaining tasks using the stored procedure
            callUpdateTaskIDsProcedure(conn);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void callUpdateTaskIDsProcedure(Connection conn) {
        //Call UpdateTaskIDs reset id sequence without gap and in consecutive order.
        try (CallableStatement cstmt = conn.prepareCall("{CALL UpdateTaskIDs()}")) {
            //Execute the instruction stored in UpdateTaskIDs (Please refer to delimiter below)
            cstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
    DELIMITER //

    CREATE PROCEDURE UpdateTaskIDs()
    BEGIN
    //Set default value as 0, add 1 for next id, reset all auto incremental id back to 1 and start from 1 again
    SET @counter = 0;
    UPDATE tasks SET tasks.id = @counter := @counter + 1 ORDER BY id;
    ALTER TABLE tasks AUTO_INCREMENT = 1;
    END //
w
    DELIMITER ;
    */

}