package attendance;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FacultyAddMarksFrame extends JFrame {

    private JFrame parentFrame;
    private AuthHelper.FacultyInfo facultyInfo;
    private JComboBox<String> yearBox, deptBox, sectionBox, subjectBox;
    private JTextField rollNoField, studentNameField, marksField;
    private JButton saveBtn, clearBtn, backBtn;

    public FacultyAddMarksFrame(JFrame parent, AuthHelper.FacultyInfo info) {
        this.parentFrame = parent;
        this.facultyInfo = info;
        
        setTitle("Add Student Marks - Faculty");
        setSize(650, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(236, 240, 241));
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(26, 188, 156));
        headerPanel.setPreferredSize(new Dimension(650, 70));
        headerPanel.setLayout(new BorderLayout());
        
        JButton backButton = new JButton("← Back");
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(22, 160, 133));
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setPreferredSize(new Dimension(100, 35));
        backButton.addActionListener(e -> {
            dispose();
            if (parentFrame != null) parentFrame.setVisible(true);
        });
        
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 18));
        backPanel.setOpaque(false);
        backPanel.add(backButton);
        
        JLabel headerLabel = new JLabel("📝 Add Student Marks");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(Color.WHITE);
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false);
        titlePanel.add(headerLabel);
        
        headerPanel.add(backPanel, BorderLayout.WEST);
        headerPanel.add(titlePanel, BorderLayout.CENTER);
        
        // Form
        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0;
        gbc.weightx = 1;
        
        int row = 0;
        
        // Year
        gbc.gridy = row++;
        formPanel.add(createLabel("1. Select Year *"), gbc);
        gbc.gridy = row++;
        yearBox = new JComboBox<>(new String[]{"1", "2", "3", "4"});
        styleComboBox(yearBox);
        formPanel.add(yearBox, gbc);
        
        // Department
        gbc.gridy = row++;
        formPanel.add(createLabel("2. Select Department *"), gbc);
        gbc.gridy = row++;
        deptBox = new JComboBox<>(new String[]{"CSE", "ECE", "MECH", "CIVIL", "EEE", "IT"});
        styleComboBox(deptBox);
        if (facultyInfo != null) {
            deptBox.setSelectedItem(facultyInfo.department);
            deptBox.setEnabled(false);
        }
        formPanel.add(deptBox, gbc);
        
        // Section
        gbc.gridy = row++;
        formPanel.add(createLabel("3. Select Section *"), gbc);
        gbc.gridy = row++;
        sectionBox = new JComboBox<>(new String[]{"A", "B", "C", "D"});
        styleComboBox(sectionBox);
        formPanel.add(sectionBox, gbc);
        
        // Subject
        gbc.gridy = row++;
        formPanel.add(createLabel("4. Select Subject *"), gbc);
        gbc.gridy = row++;
        if (facultyInfo != null && facultyInfo.subjects != null) {
            subjectBox = new JComboBox<>(facultyInfo.subjects);
        } else {
            subjectBox = new JComboBox<>(new String[]{"Data Structures", "Algorithms", "DBMS"});
        }
        styleComboBox(subjectBox);
        formPanel.add(subjectBox, gbc);
        
        // Roll Number
        gbc.gridy = row++;
        formPanel.add(createLabel("5. Student Roll Number *"), gbc);
        gbc.gridy = row++;
        rollNoField = createTextField();
        formPanel.add(rollNoField, gbc);
        
        // Student Name
        gbc.gridy = row++;
        formPanel.add(createLabel("6. Student Name *"), gbc);
        gbc.gridy = row++;
        studentNameField = createTextField();
        formPanel.add(studentNameField, gbc);
        
        // Marks
        gbc.gridy = row++;
        formPanel.add(createLabel("7. Marks Obtained (0-100) *"), gbc);
        gbc.gridy = row++;
        marksField = createTextField();
        formPanel.add(marksField, gbc);
        
        // Buttons
        gbc.gridy = row;
        gbc.insets = new Insets(30, 0, 10, 0);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        
        saveBtn = createButton("Save Marks", new Color(46, 204, 113));
        clearBtn = createButton("Clear", new Color(52, 152, 219));
        backBtn = createButton("Cancel", new Color(149, 165, 166));
        
        buttonPanel.add(saveBtn);
        buttonPanel.add(clearBtn);
        buttonPanel.add(backBtn);
        
        formPanel.add(buttonPanel, gbc);
        
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        saveBtn.addActionListener(e -> saveMarks());
        clearBtn.addActionListener(e -> clearFields());
        backBtn.addActionListener(e -> {
            dispose();
            if (parentFrame != null) parentFrame.setVisible(true);
        });
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(mainPanel);
        
        if (parentFrame != null) parentFrame.setVisible(false);
        setVisible(true);
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(52, 73, 94));
        return label;
    }
    
    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(0, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }
    
    private void styleComboBox(JComboBox<String> box) {
        box.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        box.setPreferredSize(new Dimension(0, 40));
        box.setBackground(Color.WHITE);
    }
    
    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(130, 40));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private void clearFields() {
        rollNoField.setText("");
        studentNameField.setText("");
        marksField.setText("");
        yearBox.setSelectedIndex(0);
        sectionBox.setSelectedIndex(0);
        subjectBox.setSelectedIndex(0);
    }
    
    private void saveMarks() {
        if (rollNoField.getText().trim().isEmpty() ||
            studentNameField.getText().trim().isEmpty() ||
            marksField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int marks = Integer.parseInt(marksField.getText().trim());
            if (marks < 0 || marks > 100) {
                JOptionPane.showMessageDialog(this,
                    "Marks should be between 0 and 100",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String rollNo   = rollNoField.getText().trim().toUpperCase();
            String subject  = subjectBox.getSelectedItem().toString();
            int    semester = Integer.parseInt(
            		yearBox.getSelectedItem().toString());

            // Check if marks already exist for this student+subject+semester
            String checkSql = "SELECT id FROM marks WHERE roll_no=? " +
                              "AND subject LIKE ? AND semester=?";
            String insertSql = "INSERT INTO marks " +
                               "(roll_no, subject, semester, marks_obtained, max_marks) " +
                               "VALUES (?, ?, ?, ?, 100)";
            String updateSql = "UPDATE marks SET marks_obtained=? " +
                               "WHERE roll_no=? AND subject LIKE ? AND semester=?";

            try (java.sql.Connection con = DBConnection.getConnection()) {

                // Check if record exists
                java.sql.PreparedStatement checkPs =
                    con.prepareStatement(checkSql);
                checkPs.setString(1, rollNo);
                checkPs.setString(2, "%" + subject + "%");
                checkPs.setInt(3, semester);
                java.sql.ResultSet rs = checkPs.executeQuery();

                if (rs.next()) {
                    // Update existing
                    java.sql.PreparedStatement updatePs =
                        con.prepareStatement(updateSql);
                    updatePs.setInt(1, marks);
                    updatePs.setString(2, rollNo);
                    updatePs.setString(3, "%" + subject + "%");
                    updatePs.setInt(4, semester);
                    updatePs.executeUpdate();
                } else {
                    // Insert new
                    java.sql.PreparedStatement insertPs =
                        con.prepareStatement(insertSql);
                    insertPs.setString(1, rollNo);
                    insertPs.setString(2, subject);
                    insertPs.setInt(3, semester);
                    insertPs.setInt(4, marks);
                    insertPs.executeUpdate();
                }

                JOptionPane.showMessageDialog(this,
                    "Marks saved to database!\n\n" +
                    "Roll No : " + rollNo + "\n" +
                    "Student : " + studentNameField.getText() + "\n" +
                    "Subject : " + subject + "\n" +
                    "Semester: " + semester + "\n" +
                    "Marks   : " + marks + " / 100",
                    "Saved Successfully",
                    JOptionPane.INFORMATION_MESSAGE);

                clearFields();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Please enter valid marks", "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    @Override
    public void dispose() {
        super.dispose();
        if (parentFrame != null) parentFrame.setVisible(true);
    }
}
