package attendance;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

/**
 * ResultCalculationFrame
 * Shows the full academic result for a student:
 *   - Subject-wise marks, grade, grade points
 *   - CGPA calculation breakdown
 *   - Overall result (Pass / Fail / Distinction)
 * Refreshes automatically whenever called after UpdateMarksFrame saves.
 */
public class ResultCalculationFrame extends JFrame {

    private final JFrame parent;
    private final String roll;

    // Table
    private JTable resultTable;
    private DefaultTableModel resultModel;

    // Summary cards
    private JLabel totalMarksLbl, totalMaxLbl, percentageLbl;
    private JLabel cgpaLbl, resultLbl, classLbl;

    // CGPA strip
    private JPanel gpStripPanel;

    private static final Color ACCENT    = new Color(52, 152, 219);
    private static final Color HEADER_BG = new Color(44, 62, 80);
    private static final Color PASS_CLR  = new Color(39, 174, 96);
    private static final Color FAIL_CLR  = new Color(192, 57, 43);

    private static final String[] COLS = {
        "#", "Subject Code", "Subject Name",
        "Max Marks", "Marks Obtained", "Grade", "Grade Points", "Status"
    };

    public ResultCalculationFrame(JFrame parent, String roll) {
        this.parent = parent;
        this.roll   = roll.toUpperCase();

        setTitle("Result Calculation - " + roll + " | " +
                 StudentMarksDB.STUDENT_NAMES.getOrDefault(roll, "Student"));
        setSize(1050, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                parent.setVisible(true);
            }
        });

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildBody(),    BorderLayout.CENTER);

        loadAndRender();
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

        String name = StudentMarksDB.STUDENT_NAMES.getOrDefault(roll, "Student");
        String dept = StudentMarksDB.STUDENT_DEPT.getOrDefault(roll, "");

        JLabel title = new JLabel("Result Calculation  |  " + name + "  (" + roll + ")");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel(dept + " Department");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(180, 220, 255));

        left.add(title); left.add(sub);

        JButton backBtn = makeBtn("<- Back", new Color(30, 100, 160), new Dimension(110, 34));
        backBtn.addActionListener(e -> { parent.setVisible(true); dispose(); });
        JPanel rp = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 18));
        rp.setOpaque(false); rp.add(backBtn);

        h.add(left, BorderLayout.WEST);
        h.add(rp,   BorderLayout.EAST);
        return h;
    }

    // ── BODY ─────────────────────────────────────────────────────
    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setBackground(new Color(245, 246, 248));
        body.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        body.add(buildResultTable(), BorderLayout.CENTER);
        body.add(buildBottomSection(), BorderLayout.SOUTH);
        return body;
    }

    // ── Result Table ─────────────────────────────────────────────
    private JScrollPane buildResultTable() {
        resultModel = new DefaultTableModel(COLS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        resultTable = new JTable(resultModel);
        resultTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        resultTable.setRowHeight(44);
        resultTable.setGridColor(new Color(220, 225, 230));
        resultTable.setShowGrid(true);
        resultTable.setSelectionBackground(new Color(180, 220, 255));

        // Header
        JTableHeader header = resultTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(HEADER_BG);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 44));
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean s, boolean f, int r, int c) {
                super.getTableCellRendererComponent(t, v, s, f, r, c);
                setBackground(HEADER_BG); setForeground(Color.WHITE);
                setFont(new Font("Segoe UI", Font.BOLD, 13));
                setHorizontalAlignment(c == 2 ? LEFT : CENTER);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return this;
            }
        });

        // Column widths
        int[] widths = {40, 110, 210, 90, 120, 75, 105, 80};
        for (int i = 0; i < widths.length; i++)
            resultTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Renderer
        resultTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setHorizontalAlignment(col == 2 ? LEFT : CENTER);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

                if (!sel) {
                    Color base = row % 2 == 0 ? new Color(245, 250, 255) : Color.WHITE;
                    if (col == 4) {
                        // Marks obtained
                        setBackground(new Color(255, 255, 220));
                        setFont(new Font("Segoe UI", Font.BOLD, 14));
                        setForeground(new Color(33, 37, 41));
                    } else if (col == 5) {
                        // Grade
                        setBackground(base);
                        setFont(new Font("Segoe UI", Font.BOLD, 15));
                        setForeground(gradeColor(v == null ? "" : v.toString()));
                    } else if (col == 6) {
                        // Grade points
                        setBackground(new Color(235, 245, 255));
                        setFont(new Font("Segoe UI", Font.BOLD, 14));
                        setForeground(new Color(41, 128, 185));
                    } else if (col == 7) {
                        // Status
                        setBackground(base);
                        String st = v == null ? "" : v.toString();
                        setForeground(st.equals("Pass") ? PASS_CLR : FAIL_CLR);
                        setFont(new Font("Segoe UI", Font.BOLD, 13));
                    } else {
                        setBackground(base);
                        setForeground(new Color(33, 37, 41));
                        setFont(col == 0
                            ? new Font("Segoe UI", Font.BOLD, 12)
                            : new Font("Segoe UI", Font.PLAIN, 14));
                    }
                }
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(resultTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 220)));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    // ── Bottom Section: GP strip + summary cards ─────────────────
    private JPanel buildBottomSection() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        // Grade Points calculation strip (dark)
        gpStripPanel = new JPanel(new BorderLayout());
        gpStripPanel.setBackground(new Color(44, 62, 80));
        gpStripPanel.setBorder(new LineBorder(new Color(30, 45, 60), 1, true));
        panel.add(gpStripPanel, BorderLayout.CENTER);

        // Summary cards row
        panel.add(buildSummaryCards(), BorderLayout.SOUTH);
        return panel;
    }

    // ── Grade Points strip ────────────────────────────────────────
    private void buildGpStrip(Object[][] rows) {
        gpStripPanel.removeAll();

        // Title row
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setBackground(new Color(44, 62, 80));
        titleRow.setBorder(BorderFactory.createEmptyBorder(8, 16, 6, 16));

        JLabel titleLbl = new JLabel(
            "CGPA  =  Sum of all Grade Points  /  Total number of subjects");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLbl.setForeground(new Color(160, 200, 240));
        titleRow.add(titleLbl, BorderLayout.WEST);

        // Badges row
        JPanel badgeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        badgeRow.setBackground(new Color(52, 73, 94));

        double total = 0;
        for (Object[] r : rows) {
            String code  = r[1].toString();
            String grade = r[5].toString();
            double pts   = Double.parseDouble(r[6].toString());
            total += pts;

            JPanel badge = new JPanel(new BorderLayout(0, 2));
            badge.setBackground(new Color(63, 84, 106));
            badge.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(80, 110, 140), 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
            badge.setPreferredSize(new Dimension(95, 65));

            JLabel codeLbl = new JLabel(code, SwingConstants.CENTER);
            codeLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            codeLbl.setForeground(new Color(160, 195, 230));

            JLabel gradeLbl = new JLabel(grade, SwingConstants.CENTER);
            gradeLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
            gradeLbl.setForeground(gradeColorLight(grade));

            JLabel ptsLbl = new JLabel(String.format("%.1f pts", pts), SwingConstants.CENTER);
            ptsLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            ptsLbl.setForeground(new Color(46, 204, 113));

            badge.add(codeLbl,  BorderLayout.NORTH);
            badge.add(gradeLbl, BorderLayout.CENTER);
            badge.add(ptsLbl,   BorderLayout.SOUTH);
            badgeRow.add(badge);
        }

        // Formula box
        double cgpa = total / rows.length;

        JPanel formulaBox = new JPanel();
        formulaBox.setLayout(new BoxLayout(formulaBox, BoxLayout.Y_AXIS));
        formulaBox.setBackground(new Color(52, 73, 94));
        formulaBox.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 0));

        JLabel eq1 = new JLabel(String.format("( %.1f )  /  %d", total, rows.length));
        eq1.setFont(new Font("Segoe UI", Font.BOLD, 14));
        eq1.setForeground(new Color(255, 220, 100));

        JLabel eq2 = new JLabel(String.format("=  %.2f  / 10.00", cgpa));
        eq2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        eq2.setForeground(cgpaColor(cgpa));

        formulaBox.add(Box.createVerticalGlue());
        formulaBox.add(eq1);
        formulaBox.add(Box.createRigidArea(new Dimension(0, 4)));
        formulaBox.add(eq2);
        formulaBox.add(Box.createVerticalGlue());
        badgeRow.add(formulaBox);

        gpStripPanel.add(titleRow, BorderLayout.NORTH);
        gpStripPanel.add(badgeRow, BorderLayout.CENTER);
        gpStripPanel.revalidate();
        gpStripPanel.repaint();
    }

    // ── Summary cards ─────────────────────────────────────────────
    private JPanel buildSummaryCards() {
        JPanel row = new JPanel(new GridLayout(1, 5, 12, 0));
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(0, 90));

        totalMarksLbl = cardValueLbl("--");
        totalMaxLbl   = cardValueLbl("--");
        percentageLbl = cardValueLbl("--%");
        cgpaLbl       = cardValueLbl("--");
        resultLbl     = cardValueLbl("--");
        classLbl      = cardValueLbl("--");

        row.add(summaryCard("Total Marks",    totalMarksLbl, new Color(52,152,219)));
        row.add(summaryCard("Percentage",     percentageLbl, new Color(155,89,182)));
        row.add(summaryCard("CGPA",           cgpaLbl,       new Color(39,174,96)));
        row.add(summaryCard("Overall Result", resultLbl,     new Color(231,76,60)));
        row.add(summaryCard("Class",          classLbl,      new Color(230,126,34)));
        return row;
    }

    private JPanel summaryCard(String title, JLabel valueLbl, Color color) {
        JPanel c = new JPanel(new BorderLayout(0, 4));
        c.setBackground(Color.WHITE);
        c.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220,220,220), 1, true),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)));

        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLbl.setForeground(new Color(100, 110, 120));

        JPanel colorBar = new JPanel();
        colorBar.setBackground(color);
        colorBar.setPreferredSize(new Dimension(0, 4));

        c.add(colorBar,  BorderLayout.NORTH);
        c.add(titleLbl,  BorderLayout.CENTER);
        c.add(valueLbl,  BorderLayout.SOUTH);
        return c;
    }

    private JLabel cardValueLbl(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(new Font("Segoe UI", Font.BOLD, 20));
        l.setForeground(HEADER_BG);
        return l;
    }

    // ── Load data and render everything ──────────────────────────
    public void loadAndRender() {
        Object[][] rows = StudentMarksDB.getMarks(roll);
        if (rows == null) return;

        // Recompute everything fresh from marks
        int totalObtained = 0, totalMax = 0;
        boolean anyFail = false;

        resultModel.setRowCount(0);
        Object[][] fresh = new Object[rows.length][8];

        for (int i = 0; i < rows.length; i++) {
            int    marks  = Integer.parseInt(rows[i][3].toString());
            String grade  = StudentMarksDB.marksToGrade(marks);
            double pts    = StudentMarksDB.gradeToPoint(grade);
            String status = marks >= 40 ? "Pass" : "Fail";

            if (status.equals("Fail")) anyFail = true;
            totalObtained += marks;
            totalMax      += Integer.parseInt(rows[i][2].toString());

            fresh[i] = new Object[]{
                (i + 1), rows[i][0], rows[i][1],
                rows[i][2], marks, grade, pts, status
            };
            resultModel.addRow(fresh[i]);
        }

        // GP strip
        buildGpStrip(fresh);

        // Summary cards
        double cgpa       = StudentMarksDB.computeCgpa(fresh);
        double percentage = (totalObtained * 100.0) / totalMax;
        String overallResult = anyFail ? "FAIL" : "PASS";
        String cls = getClass(cgpa, anyFail);

        totalMarksLbl.setText(totalObtained + " / " + totalMax);
        totalMarksLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));

        percentageLbl.setText(String.format("%.1f%%", percentage));
        percentageLbl.setForeground(percentage >= 75 ? PASS_CLR : FAIL_CLR);

        cgpaLbl.setText(String.format("%.2f", cgpa));
        cgpaLbl.setForeground(cgpaColor(cgpa));

        resultLbl.setText(overallResult);
        resultLbl.setForeground(anyFail ? FAIL_CLR : PASS_CLR);

        classLbl.setText(cls);
        classLbl.setForeground(classColor(cls));
        classLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));

        revalidate(); repaint();
    }

    // ── Grade/class helpers ───────────────────────────────────────
    private static String getClass(double cgpa, boolean anyFail) {
        if (anyFail)    return "Fail";
        if (cgpa >= 9.0) return "Distinction";
        if (cgpa >= 7.5) return "First Class";
        if (cgpa >= 6.0) return "Second Class";
        if (cgpa >= 5.0) return "Pass Class";
        return "Fail";
    }

    private static Color gradeColor(String g) {
        switch (g) {
            case "A+": case "A":  return new Color(27,153,78);
            case "B+": case "B":  return new Color(41,128,185);
            case "B-": case "C+": return new Color(142,68,173);
            case "C":  case "D":  return new Color(211,84,0);
            default:              return new Color(192,57,43);
        }
    }

    private static Color gradeColorLight(String g) {
        switch (g) {
            case "A+": case "A":  return new Color(80,230,130);
            case "B+": case "B":  return new Color(100,180,255);
            case "B-": case "C+": return new Color(200,150,255);
            case "C":  case "D":  return new Color(255,160,60);
            default:              return new Color(255,100,100);
        }
    }

    private static Color cgpaColor(double c) {
        if (c >= 9.0) return new Color(46,204,113);
        if (c >= 7.5) return new Color(52,152,219);
        if (c >= 6.0) return new Color(241,196,15);
        return new Color(231,76,60);
    }

    private static Color classColor(String cls) {
        switch (cls) {
            case "Distinction":   return new Color(46,204,113);
            case "First Class":   return new Color(52,152,219);
            case "Second Class":  return new Color(142,68,173);
            case "Pass Class":    return new Color(211,84,0);
            default:              return new Color(192,57,43);
        }
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
