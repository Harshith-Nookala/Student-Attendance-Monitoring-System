package attendance;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class FacultyProfileFrame extends JFrame {

    private JFrame parentFrame;
    private AuthHelper.FacultyInfo facultyInfo;
    private String facultyUsername;

    private JTextField nameField, emailField, phoneField, qualField, empIdField;
    private JComboBox<String> deptBox, designBox;
    private JTextArea addressArea;

    private JButton updateBtn, saveBtn, cancelBtn;
    private String origName, origEmail, origPhone, origQual, origAddr;

    private static final Color ACCENT = new Color(142, 68, 173);

    public FacultyProfileFrame(JFrame parent, AuthHelper.FacultyInfo info) {
        this.parentFrame     = parent;
        this.facultyInfo     = info;
        this.facultyUsername = getFacultyUsername(info);

        setTitle("My Profile — " + (info != null ? info.name : "Faculty"));
        setSize(700, 740);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildForm(),   BorderLayout.CENTER);

        loadProfileFromDB();

        if (parentFrame != null) parentFrame.setVisible(false);
        setVisible(true);
    }

    private String getFacultyUsername(AuthHelper.FacultyInfo info) {
        if (info == null) return "";
        String sql = "SELECT username FROM faculty WHERE name = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, info.name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("username");
        } catch (Exception e) {
            System.out.println("Username fetch error: " + e.getMessage());
        }
        return "";
    }

    private void loadProfileFromDB() {
        if (facultyUsername == null || facultyUsername.isEmpty()) return;
        String sql = "SELECT * FROM faculty WHERE username = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, facultyUsername);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                nameField.setText(rs.getString("name")          != null ? rs.getString("name")          : "");
                emailField.setText(rs.getString("email")        != null ? rs.getString("email")         : facultyUsername + "@college.edu");
                phoneField.setText(rs.getString("phone")        != null ? rs.getString("phone")         : "+91 9000000000");
                qualField.setText(rs.getString("qualification") != null ? rs.getString("qualification") : "Ph.D.");
                addressArea.setText(rs.getString("address")     != null ? rs.getString("address")       : "Hyderabad, Telangana - 500001");
                empIdField.setText(rs.getString("employee_id")  != null ? rs.getString("employee_id")   : "FAC-" + rs.getString("department") + "-001");
                if (rs.getString("department") != null)
                    deptBox.setSelectedItem(rs.getString("department"));
                if (rs.getString("designation") != null)
                    designBox.setSelectedItem(rs.getString("designation"));
            }
        } catch (Exception e) {
            System.out.println("Profile load error: " + e.getMessage());
        }
    }

    private void saveProfileToDB() {
        if (facultyUsername == null || facultyUsername.isEmpty()) return;
        String sql = "UPDATE faculty SET name=?, email=?, phone=?, " +
                     "qualification=?, address=?, designation=? WHERE username=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nameField.getText().trim());
            ps.setString(2, emailField.getText().trim());
            ps.setString(3, phoneField.getText().trim());
            ps.setString(4, qualField.getText().trim());
            ps.setString(5, addressArea.getText().trim());
            ps.setString(6, designBox.getSelectedItem().toString());
            ps.setString(7, facultyUsername);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,
                "Profile updated successfully!",
                "Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error saving profile: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(ACCENT);
        h.setPreferredSize(new Dimension(0, 100));

        JButton back = hdrBtn("← Back", new Color(125, 60, 152));
        back.addActionListener(e -> dispose());
        JPanel bw = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 14));
        bw.setOpaque(false);
        bw.add(back);

        JPanel centre = new JPanel();
        centre.setOpaque(false);
        centre.setLayout(new BoxLayout(centre, BoxLayout.Y_AXIS));
        centre.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));

        JLabel icon = new JLabel("👨‍🏫");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        String dispName = facultyInfo != null ? facultyInfo.name : "Faculty";
        JLabel nameL = new JLabel(dispName);
        nameL.setFont(new Font("Segoe UI", Font.BOLD, 20));
        nameL.setForeground(Color.WHITE);
        nameL.setAlignmentX(Component.CENTER_ALIGNMENT);

        String deptStr = facultyInfo != null ? facultyInfo.department + " Dept" : "Faculty";
        JLabel deptL = new JLabel(deptStr);
        deptL.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        deptL.setForeground(new Color(210, 180, 230));
        deptL.setAlignmentX(Component.CENTER_ALIGNMENT);

        centre.add(icon);
        centre.add(Box.createRigidArea(new Dimension(0, 4)));
        centre.add(nameL);
        centre.add(Box.createRigidArea(new Dimension(0, 3)));
        centre.add(deptL);

        h.add(bw,     BorderLayout.WEST);
        h.add(centre, BorderLayout.CENTER);
        return h;
    }

    private JScrollPane buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(22, 44, 22, 44));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1; g.gridx = 0;
        g.insets = new Insets(6, 0, 6, 0);
        int row = 0;

        g.gridy = row++; form.add(buildNotice(), g);

        // ── Personal ─────────────────────────────────────────────
        g.gridy = row++; g.insets = new Insets(14, 0, 6, 0);
        form.add(secHdr("Personal Information", ACCENT), g);
        g.insets = new Insets(2, 0, 6, 0);
        g.gridy = row++; form.add(new JSeparator(), g);
        g.insets = new Insets(6, 0, 6, 0);

        g.gridy = row++;
        form.add(twoLbl("Full Name", "Email Address"), g);
        g.gridy = row++;
        nameField  = ef("");
        emailField = ef("");
        form.add(twoFld(nameField, emailField), g);

        g.gridy = row++;
        form.add(twoLbl("Phone Number", "Qualification"), g);
        g.gridy = row++;
        phoneField = ef("");
        qualField  = ef("");
        form.add(twoFld(phoneField, qualField), g);

        // ── Academic ─────────────────────────────────────────────
        g.gridy = row++; g.insets = new Insets(14, 0, 6, 0);
        form.add(secHdr("Academic Information", ACCENT), g);
        g.insets = new Insets(2, 0, 6, 0);
        g.gridy = row++; form.add(new JSeparator(), g);
        g.insets = new Insets(6, 0, 6, 0);

        g.gridy = row++;
        form.add(twoLbl("Employee ID", "Department"), g);
        g.gridy = row++;
        empIdField = rf("");
        deptBox = new JComboBox<>(new String[]{"CSE","ECE","MECH","CIVIL","EEE","IT"});
        deptBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        deptBox.setBackground(Color.WHITE);
        deptBox.setEnabled(false);
        form.add(twoFld(empIdField, deptBox), g);

        g.gridy = row++;
        form.add(lblFor("Designation"), g);
        g.gridy = row++;
        designBox = new JComboBox<>(new String[]{
            "Professor", "Associate Professor",
            "Assistant Professor", "Lecturer"});
        designBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        designBox.setBackground(Color.WHITE);
        designBox.setEnabled(false);
        JPanel dw = new JPanel(new GridLayout(1, 2, 14, 0));
        dw.setOpaque(false);
        dw.add(designBox);
        dw.add(new JLabel());
        form.add(dw, g);

        g.gridy = row++;
        form.add(lblFor("Address"), g);
        g.gridy = row++;
        addressArea = new JTextArea(3, 20);
        addressArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        addressArea.setEditable(false);
        addressArea.setBackground(new Color(245, 247, 249));
        addressArea.setBorder(new CompoundBorder(
            new LineBorder(new Color(180, 190, 200), 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        JScrollPane addrSp = new JScrollPane(addressArea);
        addrSp.setPreferredSize(new Dimension(0, 80));
        addrSp.setBorder(BorderFactory.createEmptyBorder());
        form.add(addrSp, g);

        // Buttons
        g.gridy = row; g.insets = new Insets(22, 0, 10, 0);
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        btns.setOpaque(false);
        updateBtn = abtn("✏  Update Profile", ACCENT);
        saveBtn   = abtn("💾  Save Changes",   new Color(46, 204, 113));
        cancelBtn = abtn("✕  Cancel",          new Color(149, 165, 166));
        saveBtn.setVisible(false);
        cancelBtn.setVisible(false);
        btns.add(updateBtn);
        btns.add(saveBtn);
        btns.add(cancelBtn);
        form.add(btns, g);

        updateBtn.addActionListener(e -> enterEdit());
        saveBtn.addActionListener(e -> {
            saveProfileToDB();
            setFieldsEditable(false);
            updateBtn.setVisible(true);
            saveBtn.setVisible(false);
            cancelBtn.setVisible(false);
        });
        cancelBtn.addActionListener(e -> cancel());

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private void enterEdit() {
        origName  = nameField.getText();
        origEmail = emailField.getText();
        origPhone = phoneField.getText();
        origQual  = qualField.getText();
        origAddr  = addressArea.getText();
        setFieldsEditable(true);
        updateBtn.setVisible(false);
        saveBtn.setVisible(true);
        cancelBtn.setVisible(true);
        nameField.requestFocus();
    }

    private void cancel() {
        nameField.setText(origName);
        emailField.setText(origEmail);
        phoneField.setText(origPhone);
        qualField.setText(origQual);
        addressArea.setText(origAddr);
        setFieldsEditable(false);
        updateBtn.setVisible(true);
        saveBtn.setVisible(false);
        cancelBtn.setVisible(false);
    }

    private void setFieldsEditable(boolean e) {
        Color bg = e ? Color.WHITE : new Color(245, 247, 249);
        Color bd = e ? ACCENT : new Color(180, 190, 200);
        int bw = e ? 2 : 1;
        for (JTextField f : new JTextField[]{nameField, emailField, phoneField, qualField}) {
            f.setEditable(e);
            f.setBackground(bg);
            f.setBorder(new CompoundBorder(
                new LineBorder(bd, bw, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        }
        addressArea.setEditable(e);
        addressArea.setBackground(bg);
        addressArea.setBorder(new CompoundBorder(
            new LineBorder(bd, bw, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        designBox.setEnabled(e);
    }

    private JPanel buildNotice() {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setBackground(new Color(245, 240, 255));
        p.setBorder(new CompoundBorder(
            new LineBorder(new Color(188, 143, 212), 1, true),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        JLabel icon = new JLabel("🔒");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        JLabel msg = new JLabel("<html><b>View-only mode.</b> Click <i>Update Profile</i> to enable editing.</html>");
        msg.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        msg.setForeground(new Color(80, 40, 120));
        p.add(icon, BorderLayout.WEST);
        p.add(msg,  BorderLayout.CENTER);
        return p;
    }

    // ── Helpers ──────────────────────────────────────────────────
    private static JLabel secHdr(String t, Color c) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.BOLD, 15));
        l.setForeground(c);
        return l;
    }
    private static JLabel lblFor(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(new Color(52, 73, 94));
        return l;
    }
    private static JPanel twoLbl(String a, String b) {
        JPanel p = new JPanel(new GridLayout(1, 2, 14, 0));
        p.setOpaque(false);
        p.add(lblFor(a));
        p.add(lblFor(b));
        return p;
    }
    private static JPanel twoFld(JComponent a, JComponent b) {
        JPanel p = new JPanel(new GridLayout(1, 2, 14, 0));
        p.setOpaque(false);
        a.setPreferredSize(new Dimension(0, 42));
        b.setPreferredSize(new Dimension(0, 42));
        p.add(a);
        p.add(b);
        return p;
    }
    private static JTextField ef(String v) {
        JTextField f = new JTextField(v);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setEditable(false);
        f.setBackground(new Color(245, 247, 249));
        f.setBorder(new CompoundBorder(
            new LineBorder(new Color(180, 190, 200), 1, true),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        return f;
    }
    private static JTextField rf(String v) {
        JTextField f = new JTextField(v);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setEditable(false);
        f.setBackground(new Color(230, 232, 235));
        f.setBorder(new CompoundBorder(
            new LineBorder(new Color(190, 195, 200), 1, true),
            BorderFactory.createEmptyBorder(7, 12, 7, 12)));
        return f;
    }
    private static JButton abtn(String t, Color bg) {
        JButton b = new JButton(t);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(180, 42));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(bg.darker()); }
            public void mouseExited (MouseEvent e) { b.setBackground(bg); }
        });
        return b;
    }
    private static JButton hdrBtn(String t, Color bg) {
        JButton b = new JButton(t);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
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