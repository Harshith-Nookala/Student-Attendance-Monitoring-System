package attendance;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AddMarksFrame extends JFrame {

    private JTextField htField, studentNameField, subjectField, marksField;
    private JComboBox<String> examBox, deptBox, yearBox, sectionBox;
    private JButton saveBtn, clearBtn, backBtn;
    private JFrame parentFrame;

    public AddMarksFrame(JFrame parent) {
        this.parentFrame = parent;
        setTitle("Add Student Marks");
        setSize(600, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(236, 240, 241));
        
        // Header with back button
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(46, 204, 113));
        headerPanel.setPreferredSize(new Dimension(600, 70));
        headerPanel.setLayout(new BorderLayout());
        
        JButton backButton = new JButton("← Back");
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(39, 174, 96));
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
        
        // Form panel with scroll
        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0;
        gbc.weightx = 1;
        
        // Hall Ticket Number
        gbc.gridy = 0;
        formPanel.add(createLabel("Hall Ticket Number *"), gbc);
        gbc.gridy = 1;
        htField = createTextField();
        htField.setToolTipText("Enter student hall ticket number");
        formPanel.add(htField, gbc);
        
        // Student Name
        gbc.gridy = 2;
        formPanel.add(createLabel("Student Name *"), gbc);
        gbc.gridy = 3;
        studentNameField = createTextField();
        studentNameField.setToolTipText("Enter student full name");
        formPanel.add(studentNameField, gbc);
        
        // Department
        gbc.gridy = 4;
        formPanel.add(createLabel("Department *"), gbc);
        gbc.gridy = 5;
        deptBox = new JComboBox<>(new String[]{"CSE", "ECE", "MECH", "CIVIL", "EEE", "IT"});
        deptBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        deptBox.setPreferredSize(new Dimension(0, 40));
        deptBox.setBackground(Color.WHITE);
        formPanel.add(deptBox, gbc);
        
        // Year and Section panel
        JPanel yearSectionPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        yearSectionPanel.setOpaque(false);
        
        JPanel yearPanel = new JPanel();
        yearPanel.setLayout(new BoxLayout(yearPanel, BoxLayout.Y_AXIS));
        yearPanel.setOpaque(false);
        yearPanel.add(createLabel("Year *"));
        yearBox = new JComboBox<>(new String[]{"1", "2", "3", "4"});
        yearBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        yearBox.setPreferredSize(new Dimension(0, 40));
        yearBox.setBackground(Color.WHITE);
        yearPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        yearPanel.add(yearBox);
        
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setOpaque(false);
        sectionPanel.add(createLabel("Section *"));
        sectionBox = new JComboBox<>(new String[]{"A", "B", "C", "D"});
        sectionBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sectionBox.setPreferredSize(new Dimension(0, 40));
        sectionBox.setBackground(Color.WHITE);
        sectionPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sectionPanel.add(sectionBox);
        
        yearSectionPanel.add(yearPanel);
        yearSectionPanel.add(sectionPanel);
        
        gbc.gridy = 6;
        formPanel.add(yearSectionPanel, gbc);
        
        // Subject Name
        gbc.gridy = 7;
        formPanel.add(createLabel("Subject Name *"), gbc);
        gbc.gridy = 8;
        subjectField = createTextField();
        subjectField.setToolTipText("Enter subject name");
        formPanel.add(subjectField, gbc);
        
        // Exam Type
        gbc.gridy = 9;
        formPanel.add(createLabel("Exam Type *"), gbc);
        gbc.gridy = 10;
        examBox = new JComboBox<>(new String[]{"Mid-1", "Mid-2", "Lab Internal", "Lab External", "Final Exam"});
        examBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        examBox.setPreferredSize(new Dimension(0, 40));
        examBox.setBackground(Color.WHITE);
        formPanel.add(examBox, gbc);
        
        // Marks Obtained
        gbc.gridy = 11;
        formPanel.add(createLabel("Marks Obtained *"), gbc);
        gbc.gridy = 12;
        marksField = createTextField();
        marksField.setToolTipText("Enter marks (0-100)");
        formPanel.add(marksField, gbc);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 30));
        buttonPanel.setOpaque(false);
        
        saveBtn = createButton("Save Marks", new Color(46, 204, 113));
        clearBtn = createButton("Clear All", new Color(52, 152, 219));
        backBtn = createButton("Cancel", new Color(149, 165, 166));
        
        buttonPanel.add(saveBtn);
        buttonPanel.add(clearBtn);
        buttonPanel.add(backBtn);
        
        gbc.gridy = 13;
        formPanel.add(buttonPanel, gbc);
        
        // Scroll pane for form
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Add action listeners
        saveBtn.addActionListener(e -> saveMarks());
        clearBtn.addActionListener(e -> clearFields());
        backBtn.addActionListener(e -> {
            dispose();
            if (parentFrame != null) parentFrame.setVisible(true);
        });
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(mainPanel);
        
        // Hide parent frame
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
        htField.setText("");
        studentNameField.setText("");
        subjectField.setText("");
        marksField.setText("");
        examBox.setSelectedIndex(0);
        deptBox.setSelectedIndex(0);
        yearBox.setSelectedIndex(0);
        sectionBox.setSelectedIndex(0);
    }
    
    private void saveMarks() {
        // Validation
        if (htField.getText().trim().isEmpty()) {
            showError("Please enter Hall Ticket Number");
            return;
        }
        
        if (studentNameField.getText().trim().isEmpty()) {
            showError("Please enter Student Name");
            return;
        }
        
        if (subjectField.getText().trim().isEmpty()) {
            showError("Please enter Subject Name");
            return;
        }
        
        if (marksField.getText().trim().isEmpty()) {
            showError("Please enter Marks");
            return;
        }
        
        try {
            int marks = Integer.parseInt(marksField.getText().trim());
            if (marks < 0 || marks > 100) {
                showError("Marks should be between 0 and 100");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Please enter valid marks (numbers only)");
            return;
        }
        
        // Success message
        String grade = calculateGrade(Integer.parseInt(marksField.getText().trim()));
        
        JOptionPane.showMessageDialog(this,
            "Marks added successfully!\n\n" +
            "Hall Ticket: " + htField.getText() + "\n" +
            "Student Name: " + studentNameField.getText() + "\n" +
            "Department: " + deptBox.getSelectedItem() + "\n" +
            "Year: " + yearBox.getSelectedItem() + ", Section: " + sectionBox.getSelectedItem() + "\n" +
            "Subject: " + subjectField.getText() + "\n" +
            "Exam: " + examBox.getSelectedItem() + "\n" +
            "Marks: " + marksField.getText() + "\n" +
            "Grade: " + grade,
            "Success",
            JOptionPane.INFORMATION_MESSAGE);
        
        clearFields();
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
