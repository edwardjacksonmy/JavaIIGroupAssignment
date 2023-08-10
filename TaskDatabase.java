import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.text.ParseException;

public class TaskDatabase {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/todo_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasks = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM tasks")) {


            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                String startDateStr = rs.getString("start_date");
                String dueDateStr = rs.getString("due_date");

                Task task = new Task(title, description, startDateStr, dueDateStr);
                task.setId(id);
                tasks.add(task);


            }



        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }

        return tasks;
    }

    public static int getNextTaskId() {
        int nextId = 1;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id FROM tasks ORDER BY id")) {

            while (rs.next()) {
                if (rs.getInt("id") != nextId) {
                    break;
                }
                nextId++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return nextId;
    }
    public static void addTask(Task task) {

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO tasks (id, title, description, start_date, due_date) VALUES (?, ?, ?, ?, ?)")) {

            int nextId = getNextTaskId();

            pstmt.setInt(1, nextId);
            pstmt.setString(2, task.getTitle());
            pstmt.setString(3, task.getDescription());
            pstmt.setString(4, new SimpleDateFormat("yyyy-MM-dd").format(task.getStartDate()));
            pstmt.setString(5, new SimpleDateFormat("yyyy-MM-dd").format(task.getDueDate()));

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateTask(Task task) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("UPDATE tasks SET title = ?, description = ?, start_date = ?, due_date = ? WHERE id = ?")) {

            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            pstmt.setString(3, new SimpleDateFormat("yyyy-MM-dd").format(task.getStartDate()));
            pstmt.setString(4, new SimpleDateFormat("yyyy-MM-dd").format(task.getDueDate()));
            pstmt.setInt(5, task.getId());


            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteTask(int taskId) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM tasks WHERE id = ?")) {

            pstmt.setInt(1, taskId);
            pstmt.executeUpdate();

            // After deleting the task, update the IDs of remaining tasks using the stored procedure
            callUpdateTaskIDsProcedure(conn);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void callUpdateTaskIDsProcedure(Connection conn) {
        try (CallableStatement cstmt = conn.prepareCall("{CALL UpdateTaskIDs()}")) {
            cstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }





}
