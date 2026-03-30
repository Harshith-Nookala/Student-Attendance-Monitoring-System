package attendance;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;

public class AddStudentFrame extends JFrame {

    private JFrame parentFrame;

    // Form fields
    private JTextField rollField, nameField, emailField, phoneField,
                       dobField, guardianNameField, guardianPhoneField, addressField;
    private JComboBox<String> deptBox, yearBox, sectionBox, genderBox, admissionYearBox;
    private JButton saveBtn, clearBtn;

    private static final Color ACCENT = new Color(46, 204, 113);

    public AddStudentFrame(JFrame parent) {
        this.parentFrame = parent;
        setTitle("Add Student");
        setSize(760, 820);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildForm(),    BorderLayout.CENTER);

        if (parentFrame != null) parentFrame.setVisible(false);
        setVisible(true);
    }

    // ── HEADER ────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(ACCENT);
        h.setPreferredSize(new Dimension(0, 64));

        JButton back = hdrBtn("← Back", ACCENT.darker());
        back.addActionListener(e -> dispose());
        JPanel bw = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 16));
        bw.setOpaque(false); bw.add(back);

        JLabel title = new JLabel("🎓  Add New Student");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        JPanel tw = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 17));
        tw.setOpaque(false); tw.add(title);

        h.add(bw, BorderLayout.WEST);
        h.add(tw, BorderLayout.CENTER);
        return h;
    }

    // ── FORM ──────────────────────────────────────────────────────
    private JScrollPane buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(24, 40, 24, 40));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(6, 0, 6, 0);
        g.gridx = 0;
        g.weightx = 1;

        int row = 0;

        // ── Section 1: Academic Details ──────────────────────────
        g.gridy = row++;
        form.add(sectionHeader("1. Academic Details", ACCENT), g);
        g.gridy = row++; form.add(divider(), g);

        // Dept + Year in one row
        g.gridy = row++;
        JPanel deptYearRow = new JPanel(new GridLayout(1, 4, 12, 0));
        deptYearRow.setOpaque(false);
        deptYearRow.add(lbl("Department *"));
        deptBox = combo("CSE","ECE","MECH","CIVIL","EEE","IT");
        deptYearRow.add(deptBox);
        deptYearRow.add(lbl("Year *"));
        yearBox = combo("1","2","3","4");
        deptYearRow.add(yearBox);
        form.add(deptYearRow, g);

        // Section + Admission Year
        g.gridy = row++;
        JPanel secAdmRow = new JPanel(new GridLayout(1, 4, 12, 0));
        secAdmRow.setOpaque(false);
        secAdmRow.add(lbl("Section *"));
        sectionBox = combo("A","B","C","D");
        secAdmRow.add(sectionBox);
        secAdmRow.add(lbl("Admission Year *"));
        admissionYearBox = combo("2021","2022","2023","2024","2025");
        secAdmRow.add(admissionYearBox);
        form.add(secAdmRow, g);

        // ── Section 2: Personal Details ──────────────────────────
        g.gridy = row++;
        g.insets = new Insets(20, 0, 6, 0);
        form.add(sectionHeader("2. Personal Details", ACCENT), g);
        g.insets = new Insets(6, 0, 6, 0);
        g.gridy = row++; form.add(divider(), g);

        // Roll Number
        g.gridy = row++;
        form.add(lbl("Roll Number *"), g);
        g.gridy = row++;
        rollField = field("e.g. 21A91A0501");
        form.add(rollField, g);

        // Full Name
        g.gridy = row++;
        form.add(lbl("Full Name *"), g);
        g.gridy = row++;
        nameField = field("Enter full name");
        form.add(nameField, g);

        // Email + Phone side by side
        g.gridy = row++;
        JPanel emailPhoneLabel = new JPanel(new GridLayout(1, 2, 14, 0));
        emailPhoneLabel.setOpaque(false);
        emailPhoneLabel.add(lbl("Email Address *"));
        emailPhoneLabel.add(lbl("Phone Number"));
        form.add(emailPhoneLabel, g);

        g.gridy = row++;
        JPanel emailPhoneFields = new JPanel(new GridLayout(1, 2, 14, 0));
        emailPhoneFields.setOpaque(false);
        emailField = field("student@college.edu");
        phoneField = field("10-digit mobile number");
        emailPhoneFields.add(emailField);
        emailPhoneFields.add(phoneField);
        form.add(emailPhoneFields, g);

        // DOB + Gender
        g.gridy = row++;
        JPanel dobGenderLabel = new JPanel(new GridLayout(1, 2, 14, 0));
        dobGenderLabel.setOpaque(false);
        dobGenderLabel.add(lbl("Date of Birth"));
        dobGenderLabel.add(lbl("Gender"));
        form.add(dobGenderLabel, g);

        g.gridy = row++;
        JPanel dobGenderFields = new JPanel(new GridLayout(1, 2, 14, 0));
        dobGenderFields.setOpaque(false);
        dobField = field("DD/MM/YYYY");
        genderBox = combo("Male","Female","Other");
        genderBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        genderBox.setBackground(Color.WHITE);
        dobGenderFields.add(dobField);
        dobGenderFields.add(genderBox);
        form.add(dobGenderFields, g);

        // ── Section 3: Guardian Details ───────────────────────────
        g.gridy = row++;
        g.insets = new Insets(20, 0, 6, 0);
        form.add(sectionHeader("3. Guardian Details", ACCENT), g);
        g.insets = new Insets(6, 0, 6, 0);
        g.gridy = row++; form.add(divider(), g);

        // Guardian Name + Phone
        g.gridy = row++;
        JPanel gNamePhoneLabel = new JPanel(new GridLayout(1, 2, 14, 0));
        gNamePhoneLabel.setOpaque(false);
        gNamePhoneLabel.add(lbl("Guardian Name"));
        gNamePhoneLabel.add(lbl("Guardian Phone"));
        form.add(gNamePhoneLabel, g);

        g.gridy = row++;
        JPanel gNamePhoneFields = new JPanel(new GridLayout(1, 2, 14, 0));
        gNamePhoneFields.setOpaque(false);
        guardianNameField  = field("Enter guardian name");
        guardianPhoneField = field("10-digit mobile number");
        gNamePhoneFields.add(guardianNameField);
        gNamePhoneFields.add(guardianPhoneField);
        form.add(gNamePhoneFields, g);

        // Address
        g.gridy = row++;
        form.add(lbl("Address"), g);
        g.gridy = row++;
        addressField = field("Enter full address");
        form.add(addressField, g);

        // ── Buttons ───────────────────────────────────────────────
        g.gridy = row;
        g.insets = new Insets(28, 0, 10, 0);
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        btns.setOpaque(false);
        saveBtn  = actionBtn("💾  Save Student", ACCENT);
        clearBtn = actionBtn("✕  Clear Form",    new Color(149, 165, 166));
        btns.add(saveBtn);
        btns.add(clearBtn);
        form.add(btns, g);

        saveBtn.addActionListener(e  -> saveStudent());
        clearBtn.addActionListener(e -> clearForm());

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private void saveStudent() {
        if (rollField.getText().trim().isEmpty() || nameField.getText().trim().isEmpty()
                || emailField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill all required (*) fields.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String rollNo  = rollField.getText().trim();
        String name    = nameField.getText().trim();
        String email   = emailField.getText().trim();
        String phone   = phoneField.getText().trim();
        String dob     = dobField.getText().trim();
        String address = addressField.getText().trim();
        String guardian      = guardianNameField.getText().trim();
        String guardianPhone = guardianPhoneField.getText().trim();
        String dept    = (String) deptBox.getSelectedItem();
        String year    = (String) yearBox.getSelectedItem();
        String section = (String) sectionBox.getSelectedItem();
        String gender  = (String) genderBox.getSelectedItem();
        String username = rollNo.toLowerCase();
        String password = rollNo.toLowerCase() + "123";

        String sql = "INSERT INTO students (roll_no, name, email, phone, dob, " +
                     "address, guardian_name, guardian_phone, department, year, " +
                     "section, gender, username, password) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1,  rollNo);
            ps.setString(2,  name);
            ps.setString(3,  email);
            ps.setString(4,  phone);
            ps.setString(5,  dob);
            ps.setString(6,  address);
            ps.setString(7,  guardian);
            ps.setString(8,  guardianPhone);
            ps.setString(9,  dept);
            ps.setString(10, year);
            ps.setString(11, section);
            ps.setString(12, gender);
            ps.setString(13, username);
            ps.setString(14, password);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                "Student added successfully!\n\n"
                + "Roll No  : " + rollNo + "\n"
                + "Name     : " + name + "\n"
                + "Username : " + username + "\n"
                + "Password : " + password + "\n"
                + "Dept     : " + dept + " | Year: " + year
                + " | Section: " + section,
                "Success", JOptionPane.INFORMATION_MESSAGE);

            clearForm();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error saving student: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private void clearForm() {
        rollField.setText(""); nameField.setText(""); emailField.setText("");
        phoneField.setText(""); dobField.setText(""); addressField.setText("");
        guardianNameField.setText(""); guardianPhoneField.setText("");
        deptBox.setSelectedIndex(0); yearBox.setSelectedIndex(0);
        sectionBox.setSelectedIndex(0); genderBox.setSelectedIndex(0);
    }

    // ── small helpers ────────────────────────────────────────────
    private static JLabel sectionHeader(String text, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 15));
        l.setForeground(color);
        return l;
    }
    private static JSeparator divider() {
        JSeparator s = new JSeparator();
        s.setForeground(new Color(220, 220, 220));
        return s;
    }
    private static JLabel lbl(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(new Color(52, 73, 94));
        return l;
    }
    private static JTextField field(String placeholder) {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBackground(new Color(248, 249, 250));
        f.setPreferredSize(new Dimension(0, 42));
        f.setBorder(new CompoundBorder(
            new LineBorder(new Color(180, 190, 200), 1, true),
            new EmptyBorder(6, 12, 6, 12)));
        f.setToolTipText(placeholder);
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                f.setBackground(Color.WHITE);
                f.setBorder(new CompoundBorder(
                    new LineBorder(new Color(46, 204, 113), 2, true),
                    new EmptyBorder(6, 12, 6, 12)));
            }
            public void focusLost(FocusEvent e) {
                f.setBackground(new Color(248, 249, 250));
                f.setBorder(new CompoundBorder(
                    new LineBorder(new Color(180, 190, 200), 1, true),
                    new EmptyBorder(6, 12, 6, 12)));
            }
        });
        return f;
    }
    private static JComboBox<String> combo(String... items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cb.setBackground(Color.WHITE);
        cb.setPreferredSize(new Dimension(0, 42));
        return cb;
    }
    private static JButton actionBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(180, 42));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(bg.darker()); }
            public void mouseExited (MouseEvent e) { b.setBackground(bg); }
        });
        return b;
    }
    private static JButton hdrBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(110, 34));
        return b;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (parentFrame != null) parentFrame.setVisible(true);
    }
}
