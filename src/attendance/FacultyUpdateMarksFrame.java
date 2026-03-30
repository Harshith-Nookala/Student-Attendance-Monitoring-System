package attendance;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FacultyUpdateMarksFrame extends JFrame {

    private JFrame parentFrame;
    private AuthHelper.FacultyInfo facultyInfo;
    
    private JComboBox<String> yearBox, deptBox, sectionBox, subjectBox, examBox;
    private JTextField rollNoField, studentNameField, oldMarksField, newMarksField;
    private JButton searchBtn, updateBtn, clearBtn, backBtn;
    private JPanel resultPanel;
    private JLabel statusLabel;

    private static final Color ACCENT = new Color(241, 196, 15);

    public FacultyUpdateMarksFrame(JFrame parent, AuthHelper.FacultyInfo info) {
        this.parentFrame = parent;
        this.facultyInfo = info;
        
        setTitle("Update Student Marks - Faculty");
        setSize(700, 850);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(236, 240, 241));
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(ACCENT);
        headerPanel.setPreferredSize(new Dimension(700, 70));
        headerPanel.setLayout(new BorderLayout());
        
        JButton backButton = new JButton("← Back");
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(212, 172, 13));
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
        
        JLabel headerLabel = new JLabel("✏️ Update Student Marks");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(Color.WHITE);
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false);
        titlePanel.add(headerLabel);
        
        headerPanel.add(backPanel, BorderLayout.WEST);
        headerPanel.add(titlePanel, BorderLayout.CENTER);
        
        // Search section
        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setLayout(new GridBagLayout());
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                "1. Search Student Record",
                0,
                0,
                new Font("Segoe UI", Font.BOLD, 15),
                new Color(52, 73, 94)
            ),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0;
        gbc.weightx = 1;
        
        int row = 0;
        
        // Year
        gbc.gridy = row++;
        searchPanel.add(createLabel("Year *"), gbc);
        gbc.gridy = row++;
        yearBox = new JComboBox<>(new String[]{"1", "2", "3", "4"});
        styleComboBox(yearBox);
        searchPanel.add(yearBox, gbc);
        
        // Department
        gbc.gridy = row++;
        searchPanel.add(createLabel("Department *"), gbc);
        gbc.gridy = row++;
        deptBox = new JComboBox<>(new String[]{"CSE", "ECE", "MECH", "CIVIL", "EEE", "IT"});
        styleComboBox(deptBox);
        if (facultyInfo != null) {
            deptBox.setSelectedItem(facultyInfo.department);
            deptBox.setEnabled(false);
        }
        searchPanel.add(deptBox, gbc);
        
        // Section
        gbc.gridy = row++;
        searchPanel.add(createLabel("Section *"), gbc);
        gbc.gridy = row++;
        sectionBox = new JComboBox<>(new String[]{"A", "B", "C", "D"});
        styleComboBox(sectionBox);
        searchPanel.add(sectionBox, gbc);
        
        // Subject
        gbc.gridy = row++;
        searchPanel.add(createLabel("Subject *"), gbc);
        gbc.gridy = row++;
        if (facultyInfo != null && facultyInfo.subjects != null) {
            subjectBox = new JComboBox<>(facultyInfo.subjects);
        } else {
            subjectBox = new JComboBox<>(new String[]{"Data Structures", "Algorithms", "DBMS"});
        }
        styleComboBox(subjectBox);
        searchPanel.add(subjectBox, gbc);
        
        // Exam Type
        gbc.gridy = row++;
        searchPanel.add(createLabel("Exam Type *"), gbc);
        gbc.gridy = row++;
        examBox = new JComboBox<>(new String[]{"Mid-1", "Mid-2", "Lab Internal", "Lab External", "Final Exam"});
        styleComboBox(examBox);
        searchPanel.add(examBox, gbc);
        
        // Roll Number
        gbc.gridy = row++;
        searchPanel.add(createLabel("Student Roll Number *"), gbc);
        gbc.gridy = row++;
        rollNoField = createTextField();
        rollNoField.setToolTipText("Enter student roll number");
        searchPanel.add(rollNoField, gbc);
        
        // Student Name
        gbc.gridy = row++;
        searchPanel.add(createLabel("Student Name *"), gbc);
        gbc.gridy = row++;
        studentNameField = createTextField();
        studentNameField.setToolTipText("Enter student full name");
        searchPanel.add(studentNameField, gbc);
        
        // Search button
        gbc.gridy = row;
        gbc.insets = new Insets(20, 0, 10, 0);
        searchBtn = createButton("🔍 Search Record", new Color(52, 152, 219));
        searchPanel.add(searchBtn, gbc);
        
        // Update panel
        resultPanel = new JPanel();
        resultPanel.setBackground(Color.WHITE);
        resultPanel.setLayout(new GridBagLayout());
        resultPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                "2. Update Marks",
                0,
                0,
                new Font("Segoe UI", Font.BOLD, 15),
                new Color(52, 73, 94)
            ),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        resultPanel.setVisible(false);
        
        gbc.insets = new Insets(10, 0, 10, 0);
        row = 0;
        
        // Current Marks
        gbc.gridy = row++;
        resultPanel.add(createLabel("Current Marks"), gbc);
        gbc.gridy = row++;
        oldMarksField = createTextField();
        oldMarksField.setEditable(false);
        oldMarksField.setBackground(new Color(236, 240, 241));
        resultPanel.add(oldMarksField, gbc);
        
        // New Marks
        gbc.gridy = row++;
        resultPanel.add(createLabel("New Marks (0-100) *"), gbc);
        gbc.gridy = row++;
        newMarksField = createTextField();
        newMarksField.setToolTipText("Enter new marks between 0 and 100");
        resultPanel.add(newMarksField, gbc);
        
        // Status label
        gbc.gridy = row++;
        gbc.insets = new Insets(15, 0, 15, 0);
        statusLabel = new JLabel("");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        resultPanel.add(statusLabel, gbc);
        
        // Update and Back buttons
        gbc.gridy = row;
        gbc.insets = new Insets(10, 0, 10, 0);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        
        updateBtn = createButton("💾 Update Marks", new Color(46, 204, 113));
        clearBtn = createButton("Clear", new Color(149, 165, 166));
        backBtn = createButton("Cancel", new Color(231, 76, 60));
        
        buttonPanel.add(updateBtn);
        buttonPanel.add(clearBtn);
        buttonPanel.add(backBtn);
        resultPanel.add(buttonPanel, gbc);
        
        // Main content panel with scroll
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        contentPanel.add(searchPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(resultPanel);
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Add action listeners
        searchBtn.addActionListener(e -> searchRecord());
        updateBtn.addActionListener(e -> updateRecord());
        clearBtn.addActionListener(e -> clearUpdateFields());
        backBtn.addActionListener(e -> {
            resultPanel.setVisible(false);
            clearUpdateFields();
        });
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(mainPanel);
        
        if (parentFrame != null) {
            parentFrame.setVisible(false);
        }
        
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
        button.setPreferredSize(new Dimension(160, 40));
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
    
    private void clearUpdateFields() {
        oldMarksField.setText("");
        newMarksField.setText("");
        statusLabel.setText("");
    }
    
    private void clearAllFields() {
        rollNoField.setText("");
        studentNameField.setText("");
        oldMarksField.setText("");
        newMarksField.setText("");
        statusLabel.setText("");
        yearBox.setSelectedIndex(0);
        sectionBox.setSelectedIndex(0);
        subjectBox.setSelectedIndex(0);
        examBox.setSelectedIndex(0);
    }
    
    private void searchRecord() {
        String rollNo = rollNoField.getText().trim();
        String studentName = studentNameField.getText().trim();
        
        if (rollNo.isEmpty()) {
            showError("Please enter Student Roll Number");
            return;
        }
        
        if (studentName.isEmpty()) {
            showError("Please enter Student Name");
            return;
        }
        
        // Simulate database search
        oldMarksField.setText("85");
        resultPanel.setVisible(true);
        statusLabel.setText("✓ Record found successfully!");
        statusLabel.setForeground(new Color(46, 204, 113));
        
        revalidate();
        repaint();
    }
    
    private void updateRecord() {
        String newMarks = newMarksField.getText().trim();
        
        if (newMarks.isEmpty()) {
            showError("Please enter new marks");
            return;
        }
        
        try {
            int marks = Integer.parseInt(newMarks);
            if (marks < 0 || marks > 100) {
                showError("Marks should be between 0 and 100");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Please enter valid marks (numbers only)");
            return;
        }
        
        String grade = calculateGrade(Integer.parseInt(newMarks));
        
        JOptionPane.showMessageDialog(this,
            "Marks updated successfully!\n\n" +
            "Roll Number: " + rollNoField.getText() + "\n" +
            "Student Name: " + studentNameField.getText() + "\n" +
            "Department: " + deptBox.getSelectedItem() + "\n" +
            "Year: " + yearBox.getSelectedItem() + " | Section: " + sectionBox.getSelectedItem() + "\n" +
            "Subject: " + subjectBox.getSelectedItem() + "\n" +
            "Exam: " + examBox.getSelectedItem() + "\n" +
            "Old Marks: " + oldMarksField.getText() + "\n" +
            "New Marks: " + newMarks + "\n" +
            "Grade: " + grade,
            "Success",
            JOptionPane.INFORMATION_MESSAGE);
        
        resultPanel.setVisible(false);
        clearAllFields();
    }
    
    private String calculateGrade(int marks) {
        if (marks >= 90) return "A+";
        else if (marks >= 80) return "A";
        else if (marks >= 70) return "B+";
        else if (marks >= 60) return "B";
        else if (marks >= 50) return "C";
        else if (marks >= 40) return "D";
        else return "F";
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
            message,
            "Validation Error",
            JOptionPane.ERROR_MESSAGE);
    }
    
    @Override
    public void dispose() {
        super.dispose();
        if (parentFrame != null) {
            parentFrame.setVisible(true);
        }
    }
}