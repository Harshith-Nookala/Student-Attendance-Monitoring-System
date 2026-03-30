package attendance;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StudentProfileFrame extends JFrame {

    private JFrame parentFrame;
    private String studentUsername;

    private JTextField nameField, emailField, phoneField, dobField,
                       guardianField, guardianPhoneField;
    private JTextArea  addressArea;
    private JComboBox<String> deptBox, yearBox, sectionBox, genderBox;
    private JTextField rollNoField, admYearField;

    private static final Color ACCENT = new Color(155, 89, 182);

    public StudentProfileFrame(JFrame parent, String username) {
        this.parentFrame     = parent;
        this.studentUsername = username;

        setTitle("My Profile — " + username);
        setSize(740, 820);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildForm(),   BorderLayout.CENTER);

        loadProfileFromDB();

        if (parentFrame != null) parentFrame.setVisible(false);
        setVisible(true);
    }

    // ── HEADER ───────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(ACCENT);
        h.setPreferredSize(new Dimension(0, 100));

        JButton backBtn = new JButton("← Back");
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backBtn.setBackground(new Color(142, 68, 173));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setBorderPainted(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.setPreferredSize(new Dimension(100, 34));
        backBtn.addActionListener(e -> dispose());

        JPanel bp = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 14));
        bp.setOpaque(false);
        bp.add(backBtn);

        JPanel centre = new JPanel();
        centre.setOpaque(false);
        centre.setLayout(new BoxLayout(centre, BoxLayout.Y_AXIS));
        centre.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));

        JLabel icon = new JLabel("👤");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        String roll = resolveRoll(studentUsername);
        JLabel rollLbl = new JLabel(roll.toUpperCase());
        rollLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        rollLbl.setForeground(Color.WHITE);
        rollLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLbl = new JLabel("Student Profile");
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subLbl.setForeground(new Color(210, 180, 230));
        subLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        centre.add(icon);
        centre.add(Box.createRigidArea(new Dimension(0, 5)));
        centre.add(rollLbl);
        centre.add(Box.createRigidArea(new Dimension(0, 3)));
        centre.add(subLbl);

        h.add(bp,     BorderLayout.WEST);
        h.add(centre, BorderLayout.CENTER);
        return h;
    }

    // ── FORM ─────────────────────────────────────────────────────
    private JScrollPane buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(22, 40, 22, 40));

        GridBagConstraints g = new GridBagConstraints();
        g.fill    = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        g.gridx   = 0;
        g.insets  = new Insets(6, 0, 6, 0);
        int row   = 0;

        // Notice
        g.gridy = row++;
        form.add(buildNotice(), g);

        // ── Personal ─────────────────────────────────────────────
        g.gridy = row++; g.insets = new Insets(14,0,6,0);
        form.add(secHdr("Personal Information", ACCENT), g);
        g.insets = new Insets(2,0,6,0);
        g.gridy = row++; form.add(new JSeparator(), g);
        g.insets = new Insets(6,0,6,0);

        g.gridy = row++; form.add(lbl("Full Name"), g);
        g.gridy = row++;
        nameField = viewField("");
        form.add(nameField, g);

        g.gridy = row++;
        form.add(twoLabel("Email Address", "Phone Number"), g);
        g.gridy = row++;
        emailField = viewField("");
        phoneField = viewField("");
        form.add(twoField(emailField, phoneField), g);

        g.gridy = row++;
        form.add(twoLabel("Date of Birth", "Gender"), g);
        g.gridy = row++;
        dobField  = viewField("");
        genderBox = new JComboBox<>(new String[]{"Male","Female","Other"});
        genderBox.setFont(new Font("Segoe UI",Font.PLAIN,14));
        genderBox.setBackground(new Color(245,247,249));
        genderBox.setEnabled(false);
        form.add(twoField(dobField, genderBox), g);

        // ── Academic ─────────────────────────────────────────────
        g.gridy = row++; g.insets = new Insets(14,0,6,0);
        form.add(secHdr("Academic Information", ACCENT), g);
        g.insets = new Insets(2,0,6,0);
        g.gridy = row++; form.add(new JSeparator(), g);
        g.insets = new Insets(6,0,6,0);

        g.gridy = row++;
        form.add(twoLabel("Roll Number", "Admission Year"), g);
        g.gridy = row++;
        rollNoField  = readOnlyField(resolveRoll(studentUsername).toUpperCase());
        admYearField = readOnlyField("");
        form.add(twoField(rollNoField, admYearField), g);

        g.gridy = row++;
        form.add(threeLabel("Department", "Year", "Section"), g);
        g.gridy = row++;
        deptBox    = new JComboBox<>(new String[]{"CSE","ECE","MECH","CIVIL","EEE","IT"});
        yearBox    = new JComboBox<>(new String[]{"1","2","3","4"});
        sectionBox = new JComboBox<>(new String[]{"A","B","C","D"});
        for (JComboBox<?> cb : new JComboBox[]{deptBox, yearBox, sectionBox}) {
            cb.setFont(new Font("Segoe UI",Font.PLAIN,14));
            cb.setBackground(new Color(245,247,249));
            cb.setEnabled(false);
        }
        form.add(threeField(deptBox, yearBox, sectionBox), g);

        // ── Guardian ─────────────────────────────────────────────
        g.gridy = row++; g.insets = new Insets(14,0,6,0);
        form.add(secHdr("Guardian Details", ACCENT), g);
        g.insets = new Insets(2,0,6,0);
        g.gridy = row++; form.add(new JSeparator(), g);
        g.insets = new Insets(6,0,6,0);

        g.gridy = row++;
        form.add(twoLabel("Guardian Name", "Guardian Phone"), g);
        g.gridy = row++;
        guardianField      = viewField("");
        guardianPhoneField = viewField("");
        form.add(twoField(guardianField, guardianPhoneField), g);

        g.gridy = row++; form.add(lbl("Address"), g);
        g.gridy = row++;
        addressArea = new JTextArea(3, 20);
        addressArea.setFont(new Font("Segoe UI",Font.PLAIN,14));
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        addressArea.setEditable(false);
        addressArea.setBackground(new Color(245,247,249));
        addressArea.setBorder(new CompoundBorder(
            new LineBorder(new Color(180,190,200),1,true),
            BorderFactory.createEmptyBorder(8,10,8,10)));
        JScrollPane addrScroll = new JScrollPane(addressArea);
        addrScroll.setPreferredSize(new Dimension(0,80));
        addrScroll.setBorder(BorderFactory.createEmptyBorder());
        form.add(addrScroll, g);

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    // ── Load from DB ─────────────────────────────────────────────
    private void loadProfileFromDB() {
        String roll = resolveRoll(studentUsername).toUpperCase();
        String sql  = "SELECT * FROM students WHERE roll_no = ? OR username = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, roll);
            ps.setString(2, studentUsername.toLowerCase());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                nameField.setText(rs.getString("name")           != null ? rs.getString("name")           : "");
                emailField.setText(rs.getString("email")         != null ? rs.getString("email")          : "");
                phoneField.setText(rs.getString("phone")         != null ? rs.getString("phone")          : "");
                dobField.setText(rs.getString("dob")             != null ? rs.getString("dob")            : "");
                guardianField.setText(rs.getString("guardian_name")  != null ? rs.getString("guardian_name")  : "");
                guardianPhoneField.setText(rs.getString("guardian_phone") != null ? rs.getString("guardian_phone") : "");
                addressArea.setText(rs.getString("address")      != null ? rs.getString("address")        : "");
                rollNoField.setText(rs.getString("roll_no")      != null ? rs.getString("roll_no")        : "");
                admYearField.setText(rs.getString("year")        != null ? "20" + rs.getString("year")    : "");

                if (rs.getString("gender") != null)
                    genderBox.setSelectedItem(rs.getString("gender"));
                if (rs.getString("department") != null)
                    deptBox.setSelectedItem(rs.getString("department"));
                if (rs.getString("year") != null)
                    yearBox.setSelectedItem(rs.getString("year"));
                if (rs.getString("section") != null)
                    sectionBox.setSelectedItem(rs.getString("section"));
            }
        } catch (Exception e) {
            System.out.println("Profile load error: " + e.getMessage());
        }
    }

    // ── Helpers ──────────────────────────────────────────────────
    private static String resolveRoll(String username) {
        if (username.matches(".*\\d{3,}.*")) return username;
        switch (username.toLowerCase()) {
            case "rahul":    return "21A91A0501";
            case "sneha":    return "21A91A0504";
            default: return username.toUpperCase();
        }
    }

    private JPanel buildNotice() {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setBackground(new Color(245, 240, 255));
        p.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(188,143,212),1,true),
            BorderFactory.createEmptyBorder(10,14,10,14)));
        JLabel icon = new JLabel("🔒");
        icon.setFont(new Font("Segoe UI Emoji",Font.PLAIN,18));
        JLabel msg = new JLabel("<html><b>View-only profile.</b> Contact your administrator to update details.</html>");
        msg.setFont(new Font("Segoe UI",Font.PLAIN,13));
        msg.setForeground(new Color(80,40,120));
        p.add(icon, BorderLayout.WEST);
        p.add(msg,  BorderLayout.CENTER);
        return p;
    }

    private static JLabel secHdr(String text, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI",Font.BOLD,15));
        l.setForeground(color);
        return l;
    }
    private static JLabel lbl(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI",Font.BOLD,13));
        l.setForeground(new Color(52,73,94));
        return l;
    }
    private static JTextField viewField(String val) {
        JTextField f = new JTextField(val);
        f.setFont(new Font("Segoe UI",Font.PLAIN,14));
        f.setPreferredSize(new Dimension(0,42));
        f.setEditable(false);
        f.setBackground(new Color(245,247,249));
        f.setBorder(new CompoundBorder(
            new LineBorder(new Color(180,190,200),1,true),
            BorderFactory.createEmptyBorder(6,12,6,12)));
        return f;
    }
    private static JTextField readOnlyField(String val) {
        JTextField f = new JTextField(val);
        f.setFont(new Font("Segoe UI",Font.PLAIN,14));
        f.setPreferredSize(new Dimension(0,42));
        f.setEditable(false);
        f.setBackground(new Color(230,232,235));
        f.setBorder(new CompoundBorder(
            new LineBorder(new Color(190,195,200),1,true),
            BorderFactory.createEmptyBorder(6,12,6,12)));
        return f;
    }
    private static JPanel twoLabel(String l1, String l2) {
        JPanel p = new JPanel(new GridLayout(1,2,14,0));
        p.setOpaque(false);
        p.add(lbl(l1)); p.add(lbl(l2));
        return p;
    }
    private static JPanel twoField(JComponent f1, JComponent f2) {
        JPanel p = new JPanel(new GridLayout(1,2,14,0));
        p.setOpaque(false);
        f1.setPreferredSize(new Dimension(0,42));
        f2.setPreferredSize(new Dimension(0,42));
        p.add(f1); p.add(f2);
        return p;
    }
    private static JPanel threeLabel(String l1, String l2, String l3) {
        JPanel p = new JPanel(new GridLayout(1,3,10,0));
        p.setOpaque(false);
        p.add(lbl(l1)); p.add(lbl(l2)); p.add(lbl(l3));
        return p;
    }
    private static JPanel threeField(JComponent f1, JComponent f2, JComponent f3) {
        JPanel p = new JPanel(new GridLayout(1,3,10,0));
        p.setOpaque(false);
        p.add(f1); p.add(f2); p.add(f3);
        return p;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (parentFrame != null) parentFrame.setVisible(true);
    }
}