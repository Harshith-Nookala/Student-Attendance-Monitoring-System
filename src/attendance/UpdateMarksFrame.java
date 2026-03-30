package attendance;
import java.sql.Connection;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

/**
 * UpdateMarksFrame
 * - Search student by roll number
 * - Double-click any row to edit that subject's marks via a dialog
 * - After editing, Grade / Grade Points / Status update in the table
 * - "View Result Calculation" button opens ResultCalculationFrame
 *   which shows the full CGPA breakdown live
 */
public class UpdateMarksFrame extends JFrame {

    private final JFrame parent;
    private final AuthHelper.FacultyInfo facultyInfo;

    private JTextField rollField;
    private JPanel studentInfoPanel;
    private JLabel studentNameLbl, studentRollLbl, studentDeptLbl;

    private JTable marksTable;
    private DefaultTableModel marksModel;
    private JPanel actionPanel;          // Save + View Result buttons
    private JButton viewResultBtn;

    private String currentRoll = null;
    private ResultCalculationFrame resultFrame = null; // live reference

    private static final Color ACCENT    = new Color(231, 76, 60);
    private static final Color HEADER_BG = new Color(44, 62, 80);

    private static final String[] COLS = {
        "Subject Code", "Subject Name", "Max Marks",
        "Marks Obtained", "Grade", "Grade Points", "Status"
    };

    // ─────────────────────────────────────────────────────────────
    public UpdateMarksFrame(JFrame parent, AuthHelper.FacultyInfo info) {
        this.parent      = parent;
        this.facultyInfo = info;

        setTitle("Update Student Marks");
        setSize(980, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { goBack(); }
        });

        add(buildHeader(),      BorderLayout.NORTH);
        add(buildMainContent(), BorderLayout.CENTER);
        setVisible(true);
    }

    // ── HEADER ───────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(ACCENT);
        h.setPreferredSize(new Dimension(0, 72));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);
        left.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 0));

        JLabel title = new JLabel("Update Student Marks");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel(facultyInfo != null
            ? facultyInfo.department + " Department" : "Faculty");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(255, 200, 190));

        left.add(title); left.add(sub);

        JButton backBtn = makeBtn("<- Back", new Color(180, 50, 30), new Dimension(110, 34));
        backBtn.addActionListener(e -> goBack());

        JPanel rp = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 18));
        rp.setOpaque(false); rp.add(backBtn);

        h.add(left, BorderLayout.WEST);
        h.add(rp,   BorderLayout.EAST);
        return h;
    }

    // ── MAIN CONTENT ─────────────────────────────────────────────
    private JPanel buildMainContent() {
        JPanel main = new JPanel(new BorderLayout(0, 0));
        main.setBackground(new Color(245, 246, 248));

        main.add(buildSearchBar(),     BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 8));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(12, 16, 0, 16));

        studentInfoPanel = buildStudentInfoPanel();
        studentInfoPanel.setVisible(false);

        JPanel tableWrapper = new JPanel(new BorderLayout(0, 6));
        tableWrapper.setOpaque(false);
        tableWrapper.add(buildHintBar(),    BorderLayout.NORTH);
        tableWrapper.add(buildMarksTable(), BorderLayout.CENTER);

        center.add(studentInfoPanel, BorderLayout.NORTH);
        center.add(tableWrapper,     BorderLayout.CENTER);

        actionPanel = buildActionPanel();
        actionPanel.setVisible(false);

        main.add(center,      BorderLayout.CENTER);
        main.add(actionPanel, BorderLayout.SOUTH);
        return main;
    }

    // ── Search bar ───────────────────────────────────────────────
    private JPanel buildSearchBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 12));
        bar.setBackground(Color.WHITE);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

        JLabel lbl = new JLabel("Enter Roll Number:");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(HEADER_BG);

        rollField = new JTextField(18);
        rollField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rollField.setPreferredSize(new Dimension(200, 34));
        rollField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(180, 190, 200), 1, true),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        rollField.addActionListener(e -> searchStudent());

        JButton searchBtn = makeBtn("Search", ACCENT, new Dimension(100, 34));
        searchBtn.addActionListener(e -> searchStudent());

        JButton clearBtn = makeBtn("Clear", new Color(149, 165, 166), new Dimension(80, 34));
        clearBtn.addActionListener(e -> clearAll());

        JLabel hint = new JLabel("  Try: 21A91A0501 ... 21A91A0505,  20A95A0101,  20A95A0102");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        hint.setForeground(new Color(150, 150, 160));

        bar.add(lbl); bar.add(rollField);
        bar.add(searchBtn); bar.add(clearBtn); bar.add(hint);
        return bar;
    }

    private JPanel buildHintBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        bar.setBackground(new Color(255, 255, 210));
        bar.setBorder(BorderFactory.createLineBorder(new Color(220, 210, 140)));
        JLabel lbl = new JLabel(
            "  Double-click any row to edit that subject's marks.");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(100, 80, 10));
        bar.add(lbl);
        return bar;
    }

    // ── Student info strip ───────────────────────────────────────
    private JPanel buildStudentInfoPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 28, 10));
        p.setBackground(new Color(240, 244, 250));
        p.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(180, 200, 230), 1, true),
            BorderFactory.createEmptyBorder(2, 12, 2, 12)));

        studentNameLbl = boldLbl("Name: --");
        studentRollLbl = boldLbl("Roll: --");
        studentDeptLbl = boldLbl("Dept: --");

        p.add(studentNameLbl); p.add(vSep());
        p.add(studentRollLbl); p.add(vSep());
        p.add(studentDeptLbl);
        return p;
    }

    // ── Marks table ───────────────────────────────────────────────
    private JScrollPane buildMarksTable() {
        marksModel = new DefaultTableModel(COLS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        marksTable = new JTable(marksModel);
        marksTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        marksTable.setRowHeight(42);
        marksTable.setGridColor(new Color(220, 225, 230));
        marksTable.setShowGrid(true);
        marksTable.setSelectionBackground(new Color(255, 200, 180));
        marksTable.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Header
        JTableHeader header = marksTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(HEADER_BG);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 42));
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean s, boolean f, int r, int c) {
                super.getTableCellRendererComponent(t, v, s, f, r, c);
                setBackground(HEADER_BG); setForeground(Color.WHITE);
                setFont(new Font("Segoe UI", Font.BOLD, 13));
                setHorizontalAlignment(c == 1 ? LEFT : CENTER);
                setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
                return this;
            }
        });

        int[] widths = {100, 200, 85, 120, 75, 105, 80};
        for (int i = 0; i < widths.length; i++)
            marksTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Renderer
        marksTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setHorizontalAlignment(col == 1 ? LEFT : CENTER);
                setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
                if (!sel) {
                    Color base = row % 2 == 0 ? new Color(255, 251, 251) : Color.WHITE;
                    if (col == 3) {
                        setBackground(new Color(255, 255, 210));
                        setFont(new Font("Segoe UI", Font.BOLD, 14));
                        setForeground(new Color(33, 37, 41));
                    } else if (col == 4) {
                        setBackground(base);
                        setFont(new Font("Segoe UI", Font.BOLD, 14));
                        setForeground(gradeColor(v == null ? "" : v.toString()));
                    } else if (col == 5) {
                        setBackground(new Color(235, 245, 255));
                        setFont(new Font("Segoe UI", Font.BOLD, 14));
                        setForeground(new Color(41, 128, 185));
                    } else if (col == 6) {
                        setBackground(base);
                        String st = v == null ? "" : v.toString();
                        setForeground(st.equals("Pass")
                            ? new Color(39, 174, 96) : new Color(192, 57, 43));
                        setFont(new Font("Segoe UI", Font.BOLD, 13));
                    } else {
                        setBackground(base);
                        setForeground(new Color(33, 37, 41));
                        setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    }
                }
                return this;
            }
        });

        // Double-click opens edit dialog
        marksTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = marksTable.rowAtPoint(e.getPoint());
                    if (row >= 0) openEditDialog(row);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(marksTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    // ── Edit dialog ───────────────────────────────────────────────
    private void openEditDialog(int row) {
        String subCode    = marksModel.getValueAt(row, 0).toString();
        String subName    = marksModel.getValueAt(row, 1).toString();
        int    curMarks   = Integer.parseInt(marksModel.getValueAt(row, 3).toString());
        String curGrade   = marksModel.getValueAt(row, 4).toString();

        JDialog dlg = new JDialog(this, "Edit Marks  |  " + subCode, true);
        dlg.setSize(420, 270);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        // Title bar
        JPanel titleBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 12));
        titleBar.setBackground(ACCENT);
        JLabel titleLbl = new JLabel(subCode + "  —  " + subName);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLbl.setForeground(Color.WHITE);
        titleBar.add(titleLbl);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        gc.anchor = GridBagConstraints.WEST;

        gc.gridx=0; gc.gridy=0;
        form.add(boldLbl("Current Marks:"), gc);
        gc.gridx=1;
        JLabel curLbl = new JLabel(curMarks + "   Grade: " + curGrade);
        curLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        curLbl.setForeground(gradeColor(curGrade));
        form.add(curLbl, gc);

        gc.gridx=0; gc.gridy=1;
        form.add(boldLbl("New Marks (0–100):"), gc);
        gc.gridx=1;
        JTextField field = new JTextField(String.valueOf(curMarks), 8);
        field.setFont(new Font("Segoe UI", Font.BOLD, 16));
        field.setPreferredSize(new Dimension(120, 36));
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(ACCENT, 2, true),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        field.selectAll();
        form.add(field, gc);

        // Live preview
        gc.gridx=0; gc.gridy=2; gc.gridwidth=2;
        JLabel preview = new JLabel("  Grade: " + curGrade + "   Points: "
            + StudentMarksDB.gradeToPoint(curGrade));
        preview.setFont(new Font("Segoe UI", Font.BOLD, 13));
        preview.setForeground(gradeColor(curGrade));
        form.add(preview, gc);

        field.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            void upd() {
                try {
                    int m = Integer.parseInt(field.getText().trim());
                    if (m >= 0 && m <= 100) {
                        String g = StudentMarksDB.marksToGrade(m);
                        double p = StudentMarksDB.gradeToPoint(g);
                        preview.setText("  Grade: " + g + "   Points: " + p);
                        preview.setForeground(gradeColor(g));
                    } else {
                        preview.setText("  Out of range (0–100)");
                        preview.setForeground(Color.RED);
                    }
                } catch (NumberFormatException ex) {
                    preview.setText("  Enter a number");
                    preview.setForeground(Color.GRAY);
                }
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { upd(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { upd(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { upd(); }
        });

        // Buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        btnRow.setBackground(new Color(248, 249, 250));
        btnRow.setBorder(BorderFactory.createMatteBorder(1,0,0,0,new Color(220,220,220)));

        JButton cancelBtn = makeBtn("Cancel", new Color(149,165,166), new Dimension(90, 34));
        cancelBtn.addActionListener(e -> dlg.dispose());

        JButton updateBtn = makeBtn("Update", new Color(39,174,96), new Dimension(100, 34));
        updateBtn.addActionListener(e -> {
            int newM;
            try {
                newM = Integer.parseInt(field.getText().trim());
                if (newM < 0 || newM > 100) {
                    JOptionPane.showMessageDialog(dlg,
                        "Marks must be between 0 and 100.", "Invalid", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dlg,
                    "Please enter a valid number.", "Invalid", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // ── Directly update all 4 columns in the table ────────
            String newGrade  = StudentMarksDB.marksToGrade(newM);
            double newPts    = StudentMarksDB.gradeToPoint(newGrade);
            String newStatus = newM >= 40 ? "Pass" : "Fail";

            marksModel.setValueAt(newM,      row, 3);
            marksModel.setValueAt(newGrade,  row, 4);
            marksModel.setValueAt(newPts,    row, 5);
            marksModel.setValueAt(newStatus, row, 6);

            marksTable.repaint();

            // If ResultCalculationFrame is open, refresh it live
            if (resultFrame != null && resultFrame.isVisible()) {
                resultFrame.loadAndRender();
            }

            dlg.dispose();
        });

        field.addActionListener(e -> updateBtn.doClick());
        btnRow.add(cancelBtn); btnRow.add(updateBtn);

        dlg.add(titleBar, BorderLayout.NORTH);
        dlg.add(form,     BorderLayout.CENTER);
        dlg.add(btnRow,   BorderLayout.SOUTH);
        dlg.setVisible(true);
        SwingUtilities.invokeLater(field::requestFocusInWindow);
    }

    // ── Action panel (Save + View Result) ────────────────────────
    private JPanel buildActionPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(248, 249, 250));
        p.setBorder(BorderFactory.createMatteBorder(1,0,0,0,new Color(220,220,220)));

        // Left: info text
        JLabel info = new JLabel(
            "   After editing marks, click 'Save Changes' then 'View Result Calculation'");
        info.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        info.setForeground(new Color(120, 130, 140));

        // Right: buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 10));
        btnRow.setOpaque(false);

        JButton cancelBtn = makeBtn("Cancel", new Color(149,165,166), new Dimension(100, 36));
        cancelBtn.addActionListener(e -> clearAll());

        JButton saveBtn = makeBtn("Save Changes", new Color(39,174,96), new Dimension(140, 36));
        saveBtn.addActionListener(e -> saveChanges());

        viewResultBtn = makeBtn("View Result Calculation", new Color(52,152,219),
            new Dimension(200, 36));
        viewResultBtn.addActionListener(e -> openResultFrame());

        btnRow.add(cancelBtn);
        btnRow.add(saveBtn);
        btnRow.add(viewResultBtn);

        p.add(info,   BorderLayout.WEST);
        p.add(btnRow, BorderLayout.EAST);
        return p;
    }

    // ── Search ────────────────────────────────────────────────────
    private void searchStudent() {
        String roll = rollField.getText().trim().toUpperCase();
        if (roll.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a roll number.",
                "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check student exists in DB
        String checkSql = "SELECT name, department FROM students WHERE roll_no = ?";
        try (Connection con = DBConnection.getConnection();
             java.sql.PreparedStatement ps = con.prepareStatement(checkSql)) {
            ps.setString(1, roll);
            java.sql.ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                JOptionPane.showMessageDialog(this,
                    "No student found with Roll Number: " + roll,
                    "Not Found", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            currentRoll = roll;
            studentNameLbl.setText("Name: " + rs.getString("name"));
            studentRollLbl.setText("Roll: " + roll);
            studentDeptLbl.setText("Dept: " + rs.getString("department"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error searching student: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        loadMarksIntoTable();
        studentInfoPanel.setVisible(true);
        actionPanel.setVisible(true);
        revalidate(); repaint();
    }

    private void loadMarksIntoTable() {
        marksModel.setRowCount(0);
        String sql = "SELECT subject, marks_obtained, max_marks " +
                     "FROM marks WHERE roll_no = ?";
        try (Connection con = DBConnection.getConnection();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, currentRoll);
            java.sql.ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String subjectFull = rs.getString("subject");
                String[] parts     = subjectFull.split("\\|");
                String subCode     = parts.length > 0 ? parts[0] : subjectFull;
                String subName     = parts.length > 1 ? parts[1] : subjectFull;
                int    marks       = rs.getInt("marks_obtained");
                int    maxMarks    = rs.getInt("max_marks");
                String grade       = StudentMarksDB.marksToGrade(marks);
                double points      = StudentMarksDB.gradeToPoint(grade);
                String status      = marks >= 40 ? "Pass" : "Fail";
                marksModel.addRow(new Object[]{
                    subCode, subName, maxMarks, marks, grade, points, status
                });
            }
            if (marksModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this,
                    "No marks found for: " + currentRoll +
                    "\nPlease add marks first.",
                    "No Data", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading marks: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Save changes to shared DB ─────────────────────────────────
    private void saveChanges() {
        if (currentRoll == null) return;

        String sql = "UPDATE marks SET marks_obtained = ? " +
                     "WHERE roll_no = ? AND subject LIKE ?";

        try (Connection con = DBConnection.getConnection();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {

            double total = 0;
            int rows = marksModel.getRowCount();

            for (int i = 0; i < rows; i++) {
                String subCode = marksModel.getValueAt(i, 0).toString();
                int    marks   = Integer.parseInt(
                                 marksModel.getValueAt(i, 3).toString());
                double points  = Double.parseDouble(
                                 marksModel.getValueAt(i, 5).toString());
                total += points;

                ps.setInt(1, marks);
                ps.setString(2, currentRoll);
                ps.setString(3, subCode + "%");
                ps.addBatch();
            }
            ps.executeBatch();

            double cgpa = total / rows;
            JOptionPane.showMessageDialog(this,
                "Marks saved successfully!\n\n" +
                "Student  : " + studentNameLbl.getText() + "\n" +
                "New CGPA : " + String.format("%.2f", cgpa) + " / 10.00\n\n" +
                "Click 'View Result Calculation' to see full breakdown.",
                "Saved", JOptionPane.INFORMATION_MESSAGE);

            if (resultFrame != null && resultFrame.isVisible()) {
                resultFrame.loadAndRender();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error saving marks: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Open ResultCalculationFrame ───────────────────────────────
    private void openResultFrame() {
        if (currentRoll == null) return;

        // Save first silently before opening
        int rows = marksModel.getRowCount();
        Object[][] updated = new Object[rows][];
        for (int i = 0; i < rows; i++) {
            updated[i] = new Object[]{
                marksModel.getValueAt(i,0), marksModel.getValueAt(i,1),
                marksModel.getValueAt(i,2), marksModel.getValueAt(i,3),
                marksModel.getValueAt(i,4), marksModel.getValueAt(i,5),
                marksModel.getValueAt(i,6)
            };
        }
        StudentMarksDB.saveMarks(currentRoll, updated);

        if (resultFrame != null && resultFrame.isVisible()) {
            resultFrame.loadAndRender();
            resultFrame.toFront();
        } else {
            resultFrame = new ResultCalculationFrame(this, currentRoll);
        }
    }

    private void clearAll() {
        currentRoll  = null;
        resultFrame  = null;
        rollField.setText("");
        marksModel.setRowCount(0);
        studentInfoPanel.setVisible(false);
        actionPanel.setVisible(false);
        revalidate(); repaint();
    }

    private void goBack() {
        if (marksTable.getRowCount() > 0) {
            int c = JOptionPane.showConfirmDialog(this,
                "Unsaved changes will be lost. Go back?",
                "Confirm", JOptionPane.YES_NO_OPTION);
            if (c != JOptionPane.YES_OPTION) return;
        }
        parent.setVisible(true);
        dispose();
    }

    // ── Helpers ───────────────────────────────────────────────────
    private static Color gradeColor(String g) {
        switch (g) {
            case "A+": case "A":  return new Color(27,153,78);
            case "B+": case "B":  return new Color(41,128,185);
            case "B-": case "C+": return new Color(142,68,173);
            case "C":  case "D":  return new Color(211,84,0);
            default:              return new Color(192,57,43);
        }
    }

    private static JLabel boldLbl(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(HEADER_BG);
        return l;
    }

    private static JSeparator vSep() {
        JSeparator s = new JSeparator(JSeparator.VERTICAL);
        s.setPreferredSize(new Dimension(1, 20));
        s.setForeground(new Color(180, 200, 220));
        return s;
    }

    private static JButton makeBtn(String text, Color bg, Dimension size) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(size);
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(bg.darker()); }
            public void mouseExited (MouseEvent e) { b.setBackground(bg); }
        });
        return b;
    }
}
