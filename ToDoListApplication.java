import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.text.ParseException;
import java.util.Date;


public class ToDoListApplication extends JFrame {
    private ArrayList<Task> tasks;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField titleField, descriptionField, startDateField, dueDateField, personInChargeField;
    private JButton addButton, updateButton, deleteButton, overdueButton;
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    public ToDoListApplication() {
        tasks = TaskDatabase.getAllTasks();
        initComponents();
        updateTableData();

        // Add a MouseListener to the table
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) { // Check if it's a right-click
                    table.clearSelection(); // Clear the selection
                    addButton.setEnabled(true);

                    // Clear the input fields
                    titleField.setText("");
                    descriptionField.setText("");
                    startDateField.setText("");
                    dueDateField.setText("");
                    personInChargeField.setText("");
                } else {
                    handleTableClick();
                }
            }
        });
    }

    private void handleTableClick() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow >= 0) {
            int taskId = (int) table.getValueAt(selectedRow, 0);
            Task selectedTask = getTaskById(taskId);

            if (selectedTask != null) {
                titleField.setText(selectedTask.getTitle());
                descriptionField.setText(selectedTask.getDescription());
                startDateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(selectedTask.getStartDate()));
                dueDateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(selectedTask.getDueDate()));
                personInChargeField.setText(selectedTask.getPersonInCharge());

                addButton.setEnabled(false);
            }
        } else {
            addButton.setEnabled(true);

            // Clear the input fields
            titleField.setText("");
            descriptionField.setText("");
            startDateField.setText("");
            dueDateField.setText("");
            personInChargeField.setText("");
        }
    }

    // Implement a method to retrieve a task by ID
    private Task getTaskById(int taskId) {
        for (Task task : tasks) {
            if (task.getId() == taskId) {
                return task;
            }
        }
        return null; // Task with the specified ID not found
    }

    private void initComponents() {
        setTitle("To-Do List Application");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set up table model and JTable
        String[] columnNames = {"ID", "Title", "Description", "Start Date", "Due Date", "PIC", "Date created", "Date edited"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);

        // Make the table non-editable
        table.setDefaultEditor(Object.class, null);

        // Set up input fields
        titleField = new JTextField(87);
        descriptionField = new JTextField(87);
        startDateField = new JTextField(87);
        dueDateField = new JTextField(87);
        personInChargeField = new JTextField(87);

        // Set up buttons
        addButton = new JButton("Add Task");
        updateButton = new JButton("Update Task");
        deleteButton = new JButton("Delete Task");
        overdueButton = new JButton("Check Task");

        // Set up button actions
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTask();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTask();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteTask();
            }
        });

        overdueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayOverdueTasks();
            }
        });

        // Set up panel for input fields and buttons
        JPanel inputPanel = new JPanel(new GridLayout(7, 2));
        inputPanel.add(new JLabel("Title:"));
        inputPanel.add(titleField);
        inputPanel.add(new JLabel("Description:"));
        inputPanel.add(descriptionField);
        inputPanel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        inputPanel.add(startDateField);
        inputPanel.add(new JLabel("Due Date (YYYY-MM-DD):"));
        inputPanel.add(dueDateField);
        inputPanel.add(new JLabel("Person In Charge (PIC):"));
        inputPanel.add(personInChargeField);
        inputPanel.add(addButton);
        inputPanel.add(updateButton);
        inputPanel.add(deleteButton);
        inputPanel.add(overdueButton);

        // Set up main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        //mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.WEST);
        mainPanel.add(new JScrollPane(table), BorderLayout.NORTH);
        //Use TableHeaderRenderer, implement sort method in the column header
        // Add main panel to the frame
        add(mainPanel);
    }

    private void updateTableData() {
        tableModel.setRowCount(0); // Clear previous data
        tasks.clear(); // Clear existing tasks

        tasks.addAll(TaskDatabase.getAllTasks());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (Task task : tasks) {
            Object[] row = {task.getId(), task.getTitle(), task.getDescription(), dateFormat.format(task.getStartDate()), dateFormat.format(task.getDueDate()), task.getPersonInCharge(), dateFormat.format(task.getDateCreated()),  // Date Created
                    dateFormat.format(task.getDateEdited())};
            tableModel.addRow(row);
        }
    }

    private void displayOverdueTasks() {
        Date currentDate = new Date();
        int overdueCount = 0;
        StringBuilder overdueIds = new StringBuilder();

        for (Task task : tasks) {
            if (task.getDueDate().before(currentDate)) {
                if (overdueCount > 0) {
                    overdueIds.append(", ");
                }
                overdueIds.append(task.getId());
                overdueCount++;
            }
        }

        if (overdueCount > 0) {
            String message = String.format(
                    "You have %d task(s) over the due date. They are: %s",
                    overdueCount,
                    overdueIds.toString()
            );
            JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Well done! No tasks are overdue.", "Congratulations", JOptionPane.INFORMATION_MESSAGE);
        }
    }


    private void addTask() {
        String title = titleField.getText().trim();
        String description = descriptionField.getText().trim();
        String startDate = startDateField.getText().trim();
        String dueDate = dueDateField.getText().trim();
        String personInCharge = personInChargeField.getText().trim();

        if (!title.isEmpty() && !description.isEmpty() && !startDate.isEmpty() && !dueDate.isEmpty() && !personInCharge.isEmpty()) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
                dateFormat.setLenient(false); // Enforce strict date parsing

                Date parsedStartDate = dateFormat.parse(startDate);
                Date parsedDueDate = dateFormat.parse(dueDate);
                Date dateWithoutTime = dateFormat.parse(dateFormat.format(new Date()));

                if (parsedStartDate.before(dateWithoutTime)) {
                    JOptionPane.showMessageDialog(this, "Start date cannot be earlier than the Date Created.", "Error", JOptionPane.ERROR_MESSAGE);
                    return; // Don't proceed with adding the task
                }

                if (parsedStartDate.after(parsedDueDate)) {
                    JOptionPane.showMessageDialog(this, "Start date must not be later than due date.", "Error", JOptionPane.ERROR_MESSAGE);
                    return; // Don't proceed with adding the task
                }

                //Requires constructor to run
                Task task = new Task(title, description, startDate, dueDate, personInCharge);
                TaskDatabase.addTask(task);

                tasks.add(task);
                updateTableData();

                titleField.setText("");
                descriptionField.setText("");
                startDateField.setText("");
                dueDateField.setText("");
                personInChargeField.setText("");

                JOptionPane.showMessageDialog(this, "Task added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format (YYYY-MM-DD) or invalid date values.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTask() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow >= 0) {
            int confirmationResult = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to update this task?",
                    "Confirm Update",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmationResult == JOptionPane.YES_OPTION) {
                int taskId = (int) table.getValueAt(selectedRow, 0);
                String title = titleField.getText().trim();
                String description = descriptionField.getText().trim();
                String startDate = startDateField.getText().trim();
                String dueDate = dueDateField.getText().trim();
                String personInCharge = personInChargeField.getText().trim();

                if (!title.isEmpty() && !description.isEmpty() && !startDate.isEmpty() && !dueDate.isEmpty() && !personInCharge.isEmpty()) {
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
                        dateFormat.setLenient(false); // Enforce strict date parsing

                        Date parsedStartDate = dateFormat.parse(startDate);
                        Date parsedDueDate = dateFormat.parse(dueDate);
                        Date dateWithoutTime = dateFormat.parse(dateFormat.format(new Date()));

                        if (parsedStartDate.before(dateWithoutTime)) {
                            JOptionPane.showMessageDialog(this, "Start date cannot be earlier than the Date Created.", "Error", JOptionPane.ERROR_MESSAGE);
                            return; // Don't proceed with adding the task
                        }

                        if (parsedStartDate.after(parsedDueDate)) {
                            JOptionPane.showMessageDialog(this, "Start date must not be later than due date.", "Error", JOptionPane.ERROR_MESSAGE);
                            return; // Don't proceed with updating the task
                        }

                        //Requires constructor to run
                        Task task = new Task(title, description, startDate, dueDate, personInCharge);
                        task.setId(taskId);
                        TaskDatabase.updateTask(task);

                        tasks.set(selectedRow, task);
                        updateTableData();

                        titleField.setText("");
                        descriptionField.setText("");
                        startDateField.setText("");
                        dueDateField.setText("");
                        personInChargeField.setText("");

                        JOptionPane.showMessageDialog(this, "Task successfully updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        addButton.setEnabled(true);
                    } catch (ParseException ex) {
                        JOptionPane.showMessageDialog(this, "Invalid date format (YYYY-MM-DD) or data values.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a task to update.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void deleteTask() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow >= 0) {
            int confirmationResult = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete this task?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmationResult == JOptionPane.YES_OPTION) {
                int taskId = (int) table.getValueAt(selectedRow, 0);
                TaskDatabase.deleteTask(taskId);

                tasks.remove(selectedRow);
                updateTableData();

                titleField.setText("");
                descriptionField.setText("");
                startDateField.setText("");
                dueDateField.setText("");
                personInChargeField.setText("");

                JOptionPane.showMessageDialog(this, "Task successfully deleted!", "Success", JOptionPane.INFORMATION_MESSAGE);
                addButton.setEnabled(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a task to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ToDoListApplication app = new ToDoListApplication();
            app.setVisible(true);
        });
    }
}
