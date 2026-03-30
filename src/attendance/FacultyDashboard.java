package attendance;
import java.sql.Connection;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class FacultyDashboard extends JFrame {

    private String facultyUsername;
    private AuthHelper.FacultyInfo facultyInfo;

    // Student search panel components
    private JComboBox<String> deptBox, yearBox, sectionBox;
    private JTextField rollSearchField;
    private JTable studentTable;
    private DefaultTableModel studentTableModel;

    private static final Color ACCENT = new Color(142, 68, 173);
    private static final String[] COLS = {"Roll Number","Student Name","Dept","Year","Sec","CGPA","Status"};

    // Sample student data keyed by dept+year+section


    public FacultyDashboard(String username) {
        this.facultyUsername = username;
        this.facultyInfo = AuthHelper.getFacultyInfo(username);

        setTitle("Faculty Dashboard — " + (facultyInfo!=null ? facultyInfo.name : username));
        setSize(1180, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(),   BorderLayout.CENTER);
        setVisible(true);
    }

    // ── HEADER ────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(ACCENT);
        h.setPreferredSize(new Dimension(0, 88));

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        info.setBorder(BorderFactory.createEmptyBorder(14, 28, 14, 0));

        JLabel name = new JLabel(facultyInfo!=null ? facultyInfo.name : facultyUsername);
        name.setFont(new Font("Segoe UI", Font.BOLD, 24));
        name.setForeground(Color.WHITE);

        JLabel deptLbl = new JLabel(facultyInfo!=null
            ? facultyInfo.department + " Dept  •  " + String.join(", ", facultyInfo.subjects)
            : "Faculty");
        deptLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        deptLbl.setForeground(new Color(200, 180, 220));

        info.add(name); info.add(deptLbl);

        JButton logout = hdrBtn("Logout", new Color(192, 57, 43));
        logout.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this,"Logout?","Confirm",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
                new LoginFrame(); dispose();
            }
        });
        JPanel lp = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 28));
        lp.setOpaque(false); lp.add(logout);

        h.add(info, BorderLayout.WEST);
        h.add(lp,   BorderLayout.EAST);
        return h;
    }

    // ── BODY: left action cards + right student panel ──────────────
    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout(14, 0));
        body.setBackground(new Color(245, 246, 248));
        body.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        // Left cards column — 5 rows now (was 4)
        JPanel cards = new JPanel(new GridLayout(5, 1, 0, 12));
        cards.setOpaque(false);
        cards.setPreferredSize(new Dimension(310, 0));

        cards.add(card("\uD83D\uDCDD", "Add Marks",    "Enter student marks",        new Color(26,188,156),
            e -> { setVisible(false); new FacultyAddMarksFrame(this, facultyInfo); }));
        cards.add(card("\uD83D\uDCCA", "View Results", "Browse results table",       new Color(52,152,219),
            e -> { setVisible(false); new ViewResultFrame(this); }));
        cards.add(card("\uD83D\uDC68", "My Profile",   "Update personal details",    new Color(230,126,34),
            e -> { setVisible(false); new FacultyProfileFrame(this, facultyInfo); }));
        cards.add(card("\uD83D\uDCDA", "My Subjects",  "View assigned subjects",     new Color(155,89,182),
            e -> showSubjects()));

        // ── NEW: Update Marks card ────────────────────────────────
        cards.add(card("\u270F", "Update Marks", "Edit existing student marks",      new Color(231,76,60),
            e -> { setVisible(false); new UpdateMarksFrame(this, facultyInfo); }));

        // Right — student search panel
        JPanel right = buildStudentPanel();

        body.add(cards, BorderLayout.WEST);
        body.add(right, BorderLayout.CENTER);
        return body;
    }

    // ── STUDENT SEARCH PANEL ──────────────────────────────────────
    private JPanel buildStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        // Filter bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        filterBar.setBackground(new Color(248, 249, 250));
        filterBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)));

        JLabel titleLbl = new JLabel("Student Records");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titleLbl.setForeground(new Color(44, 62, 80));
        filterBar.add(titleLbl);

        JSeparator vs = new JSeparator(JSeparator.VERTICAL);
        vs.setPreferredSize(new Dimension(1, 24));
        vs.setForeground(new Color(200, 200, 200));
        filterBar.add(vs);

        filterBar.add(boldLbl("Dept:"));
        deptBox = filterCombo("CSE","ECE","MECH","CIVIL","EEE","IT");
        if (facultyInfo != null) deptBox.setSelectedItem(facultyInfo.department);
        filterBar.add(deptBox);

        filterBar.add(boldLbl("Year:"));
        yearBox = filterCombo("All","1","2","3","4");
        filterBar.add(yearBox);

        filterBar.add(boldLbl("Section:"));
        sectionBox = filterCombo("All","A","B","C","D");
        filterBar.add(sectionBox);

        JSeparator vs2 = new JSeparator(JSeparator.VERTICAL);
        vs2.setPreferredSize(new Dimension(1, 24));
        vs2.setForeground(new Color(200, 200, 200));
        filterBar.add(vs2);

        filterBar.add(boldLbl("Roll No:"));
        rollSearchField = new JTextField(12);
        rollSearchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rollSearchField.setPreferredSize(new Dimension(140, 30));
        rollSearchField.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            new javax.swing.border.LineBorder(new Color(180,190,200),1,true),
            BorderFactory.createEmptyBorder(3,8,3,8)));
        filterBar.add(rollSearchField);

        JButton searchBtn = actionBtn("🔍 Search", ACCENT);
        JButton clearBtn  = actionBtn("Clear",     new Color(149,165,166));
        filterBar.add(searchBtn);
        filterBar.add(clearBtn);

        // Table
        studentTableModel = new DefaultTableModel(COLS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        studentTable = new JTable(studentTableModel);
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        studentTable.setRowHeight(40);
        studentTable.setGridColor(new Color(220, 225, 230));
        studentTable.setShowGrid(true);
        studentTable.setSelectionBackground(new Color(187, 143, 206));
        studentTable.setSelectionForeground(Color.WHITE);

        TableUtil.applyHeader(studentTable, COLS, ACCENT);

        studentTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t,v,sel,foc,row,col);
                setHorizontalAlignment(col==0||col==1 ? LEFT : CENTER);
                setBorder(BorderFactory.createEmptyBorder(0,8,0,8));
                if (!sel) setBackground(row%2==0 ? new Color(250,248,255) : Color.WHITE);
                if (col==6 && !sel) {
                    setForeground(new Color(39,174,96));
                    setFont(getFont().deriveFont(Font.BOLD));
                } else if (!sel) {
                    setForeground(new Color(33,37,41));
                    setFont(new Font("Segoe UI",Font.PLAIN,14));
                }
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(studentTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        // Stats strip
        JPanel statsStrip = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 8));
        statsStrip.setBackground(new Color(248, 249, 250));
        statsStrip.setBorder(BorderFactory.createMatteBorder(1,0,0,0,new Color(220,220,220)));
        JLabel statsLbl = new JLabel("  Total: 0 students");
        statsLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statsLbl.setForeground(new Color(52,73,94));
        statsStrip.add(statsLbl);

        searchBtn.addActionListener(e -> {
            filterStudents();
            statsLbl.setText("  Total: " + studentTableModel.getRowCount() + " students found");
        });
        clearBtn.addActionListener(e -> {
            rollSearchField.setText("");
            deptBox.setSelectedIndex(0);
            yearBox.setSelectedIndex(0);
            sectionBox.setSelectedIndex(0);
            studentTableModel.setRowCount(0);
            statsLbl.setText("  Total: 0 students");
        });

        loadAllStudents();
        statsLbl.setText("  Total: " + studentTableModel.getRowCount() + " students");

        panel.add(filterBar,  BorderLayout.NORTH);
        panel.add(scroll,     BorderLayout.CENTER);
        panel.add(statsStrip, BorderLayout.SOUTH);
        return panel;
    }

    private void loadAllStudents() {
        studentTableModel.setRowCount(0);
        String dept = facultyInfo != null ? facultyInfo.department : "CSE";
        String sql = "SELECT roll_no, name, department, year, section FROM students " +
                     "WHERE department = ?";
        try (Connection con = DBConnection.getConnection();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dept);
            java.sql.ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String roll = rs.getString("roll_no");
                double cgpa = getStudentCGPA(roll);
                studentTableModel.addRow(new Object[]{
                    roll,
                    rs.getString("name"),
                    rs.getString("department"),
                    rs.getString("year"),
                    rs.getString("section"),
                    cgpa > 0 ? String.format("%.2f", cgpa) : "N/A",
                    "Active"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading students: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterStudents() {
        studentTableModel.setRowCount(0);
        String dept    = deptBox.getSelectedItem().toString();
        String year    = yearBox.getSelectedItem().toString();
        String section = sectionBox.getSelectedItem().toString();
        String roll    = rollSearchField.getText().trim();

        StringBuilder sql = new StringBuilder(
            "SELECT roll_no, name, department, year, section " +
            "FROM students WHERE department = ?");

        java.util.List<String> params = new java.util.ArrayList<>();
        params.add(dept);

        if (!year.equals("All")) {
            sql.append(" AND year = ?");
            params.add(year);
        }
        if (!section.equals("All")) {
            sql.append(" AND section = ?");
            params.add(section);
        }
        if (!roll.isEmpty()) {
            sql.append(" AND (roll_no LIKE ? OR name LIKE ?)");
            params.add("%" + roll + "%");
            params.add("%" + roll + "%");
        }

        try (Connection con = DBConnection.getConnection();
             java.sql.PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++)
                ps.setString(i + 1, params.get(i));
            java.sql.ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String rollNo = rs.getString("roll_no");
                double cgpa   = getStudentCGPA(rollNo);
                studentTableModel.addRow(new Object[]{
                    rollNo,
                    rs.getString("name"),
                    rs.getString("department"),
                    rs.getString("year"),
                    rs.getString("section"),
                    cgpa > 0 ? String.format("%.1f", cgpa) : "N/A",
                    "Active"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error filtering students: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private double getStudentCGPA(String rollNo) {
        String sql = "SELECT marks_obtained FROM marks WHERE roll_no = ?";
        try (Connection con = DBConnection.getConnection();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, rollNo);
            java.sql.ResultSet rs = ps.executeQuery();
            double totalPoints = 0;
            int    count       = 0;
            while (rs.next()) {
                int marks = rs.getInt("marks_obtained");
                double points;
                if      (marks >= 90) points = 10.0;
                else if (marks >= 80) points = 9.0;
                else if (marks >= 70) points = 8.0;
                else if (marks >= 60) points = 7.0;
                else if (marks >= 50) points = 6.0;
                else if (marks >= 40) points = 5.0;
                else                  points = 0.0;
                totalPoints += points;
                count++;
            }
            if (count == 0) return 0.0;
            return Math.round((totalPoints / count) * 100.0) / 100.0;
        } catch (Exception e) {
            System.out.println("CGPA error: " + e.getMessage());
        }
        return 0.0;
    }
    // ── Card widget ───────────────────────────────────────────────
    private JPanel card(String emoji, String label, String desc, Color color, ActionListener action) {
        JPanel c = new JPanel(new BorderLayout(12, 0)) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2=(Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0,0,getWidth(),getHeight(),16,16);
            }
        };
        c.setBackground(Color.WHITE);
        c.setBorder(BorderFactory.createEmptyBorder(18,18,18,18));
        c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        c.setOpaque(false);

        JLabel iconLbl = new JLabel(emoji);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        iconLbl.setForeground(color);

        JPanel txt = new JPanel();
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
        txt.setOpaque(false);
        JLabel tl = new JLabel(label);
        tl.setFont(new Font("Segoe UI",Font.BOLD,16));
        tl.setForeground(new Color(33,37,41));
        JLabel dl = new JLabel(desc);
        dl.setFont(new Font("Segoe UI",Font.PLAIN,12));
        dl.setForeground(Color.GRAY);
        txt.add(tl);
        txt.add(Box.createRigidArea(new Dimension(0,3)));
        txt.add(dl);

        JLabel arrow = new JLabel("→");
        arrow.setFont(new Font("Segoe UI",Font.BOLD,20));
        arrow.setForeground(color);

        c.add(iconLbl, BorderLayout.WEST);
        c.add(txt,     BorderLayout.CENTER);
        c.add(arrow,   BorderLayout.EAST);

        c.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { c.setBackground(new Color(248,249,250)); c.repaint(); }
            public void mouseExited (MouseEvent e) { c.setBackground(Color.WHITE); c.repaint(); }
            public void mouseClicked(MouseEvent e) { action.actionPerformed(null); }
        });
        return c;
    }

    // ── helpers ──────────────────────────────────────────────────
    private static JComboBox<String> filterCombo(String... items) {
        JComboBox<String> cb=new JComboBox<>(items);
        cb.setFont(new Font("Segoe UI",Font.PLAIN,13));
        cb.setBackground(Color.WHITE);
        cb.setPreferredSize(new Dimension(85,30));
        return cb;
    }
    private static JLabel boldLbl(String t) {
        JLabel l=new JLabel(t);
        l.setFont(new Font("Segoe UI",Font.BOLD,13));
        l.setForeground(new Color(52,73,94));
        return l;
    }
    private static JButton actionBtn(String text, Color bg) {
        JButton b=new JButton(text);
        b.setFont(new Font("Segoe UI",Font.BOLD,13));
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(95,30));
        b.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e){b.setBackground(bg.darker());}
            public void mouseExited (MouseEvent e){b.setBackground(bg);}
        });
        return b;
    }
    private static JButton hdrBtn(String text, Color bg) {
        JButton b=new JButton(text);
        b.setFont(new Font("Segoe UI",Font.BOLD,14));
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(100,34));
        b.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e){b.setBackground(bg.darker());}
            public void mouseExited (MouseEvent e){b.setBackground(bg);}
        });
        return b;
    }
    private void showSubjects() {
        if (facultyInfo==null||facultyInfo.subjects==null) return;
        JOptionPane.showMessageDialog(this,
            "Assigned Subjects:\n\n• "+String.join("\n• ",facultyInfo.subjects),
            "My Subjects", JOptionPane.INFORMATION_MESSAGE);
    }
}
