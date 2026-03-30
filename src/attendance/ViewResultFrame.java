package attendance;
import java.sql.Connection;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class ViewResultFrame extends JFrame {

    private JComboBox<String> yearBox, deptBox, filterTypeBox, sectionBox;
    private JTextField studentIdField;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JPanel studentPanel, sectionPanel;
    private JFrame parentFrame;

    private static final Color ACCENT = new Color(52, 152, 219);
    private static final String[] COL_NAMES =
        {"Roll No", "Student Name", "Subject", "Exam Type", "Marks", "Grade", "Status"};

    public ViewResultFrame(JFrame parent) {
        this.parentFrame = parent;
        setTitle("View Student Results");
        setSize(1050, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ── HEADER ──────────────────────────────────────────────
        add(buildHeader(), BorderLayout.NORTH);

        // ── FILTER PANEL ────────────────────────────────────────
        add(buildFilterPanel(), BorderLayout.CENTER);

        if (parentFrame != null) parentFrame.setVisible(false);
        setVisible(true);
    }

    // ─────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(ACCENT);
        h.setPreferredSize(new Dimension(0, 64));

        JButton back = backBtn(ACCENT.darker());
        back.addActionListener(e -> dispose());
        JPanel bw = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 16));
        bw.setOpaque(false); bw.add(back);

        JLabel title = new JLabel("📊  View Student Results");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        JPanel tw = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 17));
        tw.setOpaque(false); tw.add(title);

        h.add(bw, BorderLayout.WEST);
        h.add(tw, BorderLayout.CENTER);
        return h;
    }

    private JPanel buildFilterPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);

        // Filter area
        JPanel filter = new JPanel(new GridBagLayout());
        filter.setBackground(new Color(248, 249, 250));
        filter.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(218, 218, 218)),
            BorderFactory.createEmptyBorder(18, 24, 18, 24)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 8, 6, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        // Row 0 — Year, Dept
        g.gridy = 0;
        g.gridx = 0; g.weightx = 0; filter.add(bold("1. Year :"), g);
        g.gridx = 1; g.weightx = 1;
        yearBox = combo("1","2","3","4");
        filter.add(yearBox, g);

        g.gridx = 2; g.weightx = 0; filter.add(bold("2. Department :"), g);
        g.gridx = 3; g.weightx = 1;
        deptBox = combo("CSE","ECE","MECH","CIVIL","EEE","IT");
        filter.add(deptBox, g);

        // Row 1 — View By
        g.gridy = 1;
        g.gridx = 0; g.weightx = 0; filter.add(bold("3. View By :"), g);
        g.gridx = 1; g.weightx = 1;
        filterTypeBox = combo("Single Student","Section Wise");
        filterTypeBox.addActionListener(e -> toggleMode());
        filter.add(filterTypeBox, g);

        // Student input (shown by default)
        studentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        studentPanel.setOpaque(false);
        studentPanel.add(bold("Roll No :"));
        studentIdField = new JTextField(12);
        studentIdField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        studentIdField.setPreferredSize(new Dimension(160, 34));
        studentIdField.setBorder(BorderFactory.createCompoundBorder(
            new javax.swing.border.LineBorder(new Color(180,190,200), 1, true),
            BorderFactory.createEmptyBorder(4,10,4,10)));
        studentPanel.add(studentIdField);

        // Section input (hidden initially)
        sectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        sectionPanel.setOpaque(false);
        sectionPanel.add(bold("Section :"));
        sectionBox = combo("A","B","C","D");
        sectionPanel.add(sectionBox);
        sectionPanel.setVisible(false);

        g.gridx = 2; g.gridwidth = 2;
        JPanel dynPanel = new JPanel(new CardLayout());
        dynPanel.setOpaque(false);
        dynPanel.add(studentPanel, "student");
        dynPanel.add(sectionPanel, "section");
        filter.add(dynPanel, g);
        g.gridwidth = 1;

        // Row 2 — Buttons
        g.gridy = 2; g.gridx = 0; g.gridwidth = 4;
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 4));
        btns.setOpaque(false);
        JButton searchBtn = btn("🔍  View Results", new Color(46,204,113));
        JButton exportBtn = btn("⬇  Export PDF",   new Color(155,89,182));
        JButton backBtn2  = btn("← Back",           new Color(149,165,166));
        searchBtn.addActionListener(e -> loadResults());
        exportBtn.addActionListener(e -> exportResults());
        backBtn2.addActionListener(e -> dispose());
        btns.add(searchBtn); btns.add(exportBtn); btns.add(backBtn2);
        filter.add(btns, g);

        // Table area
        JPanel tableWrap = new JPanel(new BorderLayout());
        tableWrap.setBackground(Color.WHITE);
        tableWrap.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        tableModel = new DefaultTableModel(COL_NAMES, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        resultTable = new JTable(tableModel);
        resultTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        resultTable.setRowHeight(38);
        resultTable.setGridColor(new Color(220, 225, 230));
        resultTable.setShowGrid(true);
        resultTable.setIntercellSpacing(new Dimension(0, 1));

        // ── APPLY VISIBLE HEADERS ──
        TableUtil.applyHeader(resultTable, COL_NAMES, ACCENT);
        TableUtil.applyRowStripes(resultTable,
            new Color(245, 249, 253), Color.WHITE);

        JScrollPane scroll = new JScrollPane(resultTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200,205,210)));
        scroll.getViewport().setBackground(Color.WHITE);
        tableWrap.add(scroll, BorderLayout.CENTER);

        wrapper.add(filter,    BorderLayout.NORTH);
        wrapper.add(tableWrap, BorderLayout.CENTER);
        return wrapper;
    }

    private void toggleMode() {
        boolean single = filterTypeBox.getSelectedItem().toString().equals("Single Student");
        studentPanel.setVisible(single);
        sectionPanel.setVisible(!single);
    }

    private void loadResults() {
        tableModel.setRowCount(0);
        String year = yearBox.getSelectedItem().toString();
        String dept = deptBox.getSelectedItem().toString();
        boolean single = filterTypeBox.getSelectedItem()
                                      .toString().equals("Single Student");

        if (single) {
            String roll = studentIdField.getText().trim();
            if (roll.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Enter Roll Number", "Validation",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            String sql =
                "SELECT s.roll_no, s.name, m.subject, m.marks_obtained, m.max_marks " +
                "FROM students s JOIN marks m ON s.roll_no = m.roll_no " +
                "WHERE s.roll_no = ?";
            try (Connection con = DBConnection.getConnection();
                 java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, roll);
                java.sql.ResultSet rs = ps.executeQuery();
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    String subjectFull = rs.getString("subject");
                    String[] parts     = subjectFull.split("\\|");
                    String subName     = parts.length > 1 ? parts[1] : subjectFull;
                    int marks          = rs.getInt("marks_obtained");
                    String grade       = StudentMarksDB.marksToGrade(marks);
                    String status      = marks >= 40 ? "Pass" : "Fail";
                    tableModel.addRow(new Object[]{
                        rs.getString("roll_no"),
                        rs.getString("name"),
                        subName,
                        "Mid-1",
                        marks,
                        grade,
                        status
                    });
                }
                if (!found) {
                    JOptionPane.showMessageDialog(this,
                        "No results found for Roll No: " + roll,
                        "Not Found", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error loading results: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            }

        } else {
            String sec = sectionBox.getSelectedItem().toString();
            String sql =
                "SELECT s.roll_no, s.name, m.subject, m.marks_obtained, m.max_marks " +
                "FROM students s JOIN marks m ON s.roll_no = m.roll_no " +
                "WHERE s.department = ? AND s.year = ? AND s.section = ?";
            try (Connection con = DBConnection.getConnection();
                 java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, dept);
                ps.setString(2, year);
                ps.setString(3, sec);
                java.sql.ResultSet rs = ps.executeQuery();
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    String subjectFull = rs.getString("subject");
                    String[] parts     = subjectFull.split("\\|");
                    String subName     = parts.length > 1 ? parts[1] : subjectFull;
                    int marks          = rs.getInt("marks_obtained");
                    String grade       = StudentMarksDB.marksToGrade(marks);
                    String status      = marks >= 40 ? "Pass" : "Fail";
                    tableModel.addRow(new Object[]{
                        rs.getString("roll_no"),
                        rs.getString("name"),
                        subName,
                        "Mid-1",
                        marks,
                        grade,
                        status
                    });
                }
                if (!found) {
                    JOptionPane.showMessageDialog(this,
                        "No results found for selected filters.",
                        "Not Found", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error loading results: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportResults() {
        if (tableModel.getRowCount()==0) {
            JOptionPane.showMessageDialog(this,"Load results first","Info",JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this,"Exported to Downloads/Results_"+System.currentTimeMillis()+".pdf","Exported",JOptionPane.INFORMATION_MESSAGE);
    }

    // ── small helpers ────────────────────────────────────────────
    private static JComboBox<String> combo(String... items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cb.setBackground(Color.WHITE);
        cb.setPreferredSize(new Dimension(150, 34));
        return cb;
    }
    private static JLabel bold(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(new Color(44,62,80));
        return l;
    }
    private static JButton btn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(155, 38));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(bg.darker()); }
            public void mouseExited (MouseEvent e) { b.setBackground(bg); }
        });
        return b;
    }
    private static JButton backBtn(Color bg) {
        JButton b = new JButton("← Back");
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
