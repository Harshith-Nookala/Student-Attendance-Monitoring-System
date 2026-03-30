package attendance;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class StudentResultFrame extends JFrame {

    private JFrame parentFrame;
    private String studentUsername;

    private static final Color ACCENT  = new Color(52, 152, 219);
    private static final Color HDR_BG  = new Color(52, 73, 94);
    private static final Color ROW_ODD = new Color(245, 247, 250);

    public StudentResultFrame(JFrame parent, String username) {
        this.parentFrame     = parent;
        this.studentUsername = username;
        setTitle("Academic Results");
        setSize(1050, 780);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(),   BorderLayout.CENTER);

        if (parentFrame != null) parentFrame.setVisible(false);
        setVisible(true);
    }

    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(ACCENT);
        h.setPreferredSize(new Dimension(0, 60));

        JButton back = new JButton("← Back");
        back.setFont(new Font("Segoe UI", Font.BOLD, 14));
        back.setBackground(ACCENT.darker());
        back.setForeground(Color.WHITE);
        back.setFocusPainted(false);
        back.setBorderPainted(false);
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        back.setPreferredSize(new Dimension(110, 34));
        back.addActionListener(e -> dispose());

        JPanel bw = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 14));
        bw.setOpaque(false);
        bw.add(back);

        JLabel t = new JLabel("Academic Results");
        t.setFont(new Font("Segoe UI", Font.BOLD, 22));
        t.setForeground(Color.WHITE);
        JPanel tw = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 14));
        tw.setOpaque(false);
        tw.add(t);

        h.add(bw, BorderLayout.WEST);
        h.add(tw, BorderLayout.CENTER);
        return h;
    }

    private JScrollPane buildBody() {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(new Color(248, 249, 250));
        body.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        String roll = resolveRoll(studentUsername);

        // Load marks from DB grouped by semester
        Map<Integer, List<Object[]>> semesterMap = loadMarksFromDB(roll);

        if (semesterMap.isEmpty()) {
            JLabel noData = new JLabel("No results found for: " + roll);
            noData.setFont(new Font("Segoe UI", Font.BOLD, 16));
            noData.setForeground(new Color(192, 57, 43));
            noData.setAlignmentX(Component.LEFT_ALIGNMENT);
            body.add(noData);
        }

        String[] semNames = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII"};
        String[] displayCols = {"S.No", "Course Code", "Course Name",
                                "Marks", "Grade", "Grade Points", "Max Marks", "Result"};

        int totalPassed = 0, totalFailed = 0;
        double totalPoints = 0;
        int totalCredits   = 0;
        int semCount       = 0;

        for (Map.Entry<Integer, List<Object[]>> entry :
                new TreeMap<>(semesterMap).entrySet()) {

            int semNo = entry.getKey();
            List<Object[]> rows = entry.getValue();
            semCount++;

            // Semester header
            String semLabel = (semNo < semNames.length ? semNames[semNo] : semNo+"") + " Semester";
            JLabel semHdr = new JLabel(semLabel);
            semHdr.setFont(new Font("Segoe UI", Font.BOLD, 15));
            semHdr.setForeground(new Color(44, 62, 80));
            semHdr.setAlignmentX(Component.LEFT_ALIGNMENT);
            body.add(semHdr);
            body.add(Box.createRigidArea(new Dimension(0, 6)));

            // Build table data
            DefaultTableModel mdl = new DefaultTableModel(displayCols, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };

            int sno = 1;
            int semPassed = 0, semFailed = 0;
            double semPoints = 0;

            for (Object[] r : rows) {
                String subjectFull = r[0].toString();
                String[] parts     = subjectFull.split("\\|");
                String subCode     = parts.length > 0 ? parts[0] : subjectFull;
                String subName     = parts.length > 1 ? parts[1] : subjectFull;
                int    marks       = Integer.parseInt(r[1].toString());
                int    maxMarks    = Integer.parseInt(r[2].toString());
                String grade       = StudentMarksDB.marksToGrade(marks);
                double points      = StudentMarksDB.gradeToPoint(grade);
                String result      = marks >= 40 ? "P" : "F";

                if (marks >= 40) semPassed++; else semFailed++;
                semPoints += points;
                totalPoints += points;
                totalCredits += 3; // default credits

                mdl.addRow(new Object[]{
                    sno++, subCode, subName,
                    marks, grade, points, maxMarks, result
                });
            }

            totalPassed += semPassed;
            totalFailed += semFailed;

            JTable tbl = new JTable(mdl);
            tbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            tbl.setRowHeight(34);
            tbl.setShowGrid(true);
            tbl.setGridColor(new Color(200, 200, 200));

            // Header
            JTableHeader jth = tbl.getTableHeader();
            jth.setPreferredSize(new Dimension(0, 46));
            jth.setReorderingAllowed(false);

            for (int ci = 0; ci < displayCols.length; ci++) {
                final String lbl   = displayCols[ci];
                final int colIdx   = ci;
                tbl.getColumnModel().getColumn(ci).setHeaderRenderer(
                    (t2, v, sel, foc, rr, cc) -> {
                        JPanel cell = new JPanel(new GridBagLayout());
                        cell.setOpaque(true);
                        cell.setBackground(HDR_BG);
                        cell.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 0, 2,
                                colIdx < displayCols.length-1 ? 1 : 0,
                                new Color(90, 110, 130)),
                            BorderFactory.createEmptyBorder(4, 4, 4, 4)));
                        JLabel label = new JLabel(lbl, SwingConstants.CENTER);
                        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                        label.setForeground(Color.WHITE);
                        cell.add(label);
                        return cell;
                    }
                );
            }

            int[] widths = {40, 100, 300, 70, 70, 90, 80, 60};
            for (int ci = 0; ci < widths.length; ci++)
                tbl.getColumnModel().getColumn(ci).setPreferredWidth(widths[ci]);

            // Cell renderer
            tbl.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                public Component getTableCellRendererComponent(JTable t2, Object v,
                        boolean sel, boolean foc, int row, int col) {
                    super.getTableCellRendererComponent(t2, v, sel, foc, row, col);
                    setHorizontalAlignment(col == 2 ? LEFT : CENTER);
                    setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
                    if (!sel) {
                        setBackground(row % 2 == 0 ? ROW_ODD : Color.WHITE);
                        setForeground(new Color(33, 37, 41));
                        setFont(new Font("Segoe UI", Font.PLAIN, 13));
                        if (col == 4 && v != null) {
                            String gr = v.toString();
                            if (gr.equals("A+") || gr.equals("O")) {
                                setForeground(new Color(39, 174, 96));
                                setFont(getFont().deriveFont(Font.BOLD));
                            } else if (gr.equals("A") || gr.equals("B+")) {
                                setForeground(new Color(52, 152, 219));
                                setFont(getFont().deriveFont(Font.BOLD));
                            } else if (gr.equals("F")) {
                                setForeground(new Color(192, 57, 43));
                                setFont(getFont().deriveFont(Font.BOLD));
                            }
                        }
                        if (col == 7 && v != null) {
                            if (v.toString().equals("P")) {
                                setForeground(new Color(39, 174, 96));
                                setFont(getFont().deriveFont(Font.BOLD));
                            } else {
                                setForeground(new Color(192, 57, 43));
                                setFont(getFont().deriveFont(Font.BOLD));
                            }
                        }
                    }
                    return this;
                }
            });

            JScrollPane sp = new JScrollPane(tbl);
            sp.setAlignmentX(Component.LEFT_ALIGNMENT);
            sp.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
            int rowH = rows.size() * 34 + 46 + 4;
            sp.setPreferredSize(new Dimension(1010, rowH));
            sp.setMaximumSize(new Dimension(Integer.MAX_VALUE, rowH));
            sp.getViewport().setBackground(Color.WHITE);
            body.add(sp);

            // Summary bar
            double sgpa = rows.isEmpty() ? 0 : semPoints / rows.size();
            JPanel summaryBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 6));
            summaryBar.setOpaque(false);
            summaryBar.setAlignmentX(Component.LEFT_ALIGNMENT);
            JLabel sumLbl = new JLabel(semLabel + " Summary   " +
                "Passed:" + semPassed + ", Failed:" + semFailed +
                "  Result:" + (semFailed == 0 ? "Pass" : "Fail") +
                "  SGPA:" + String.format("%.2f", sgpa));
            sumLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
            sumLbl.setForeground(new Color(44, 62, 80));
            summaryBar.add(sumLbl);
            body.add(summaryBar);
            body.add(Box.createRigidArea(new Dimension(0, 18)));
        }

        // Overall summary
        double cgpa = (totalPassed + totalFailed) > 0
            ? totalPoints / (totalPassed + totalFailed) : 0;

        JPanel overall = new JPanel(new GridBagLayout());
        overall.setBackground(new Color(240, 245, 250));
        overall.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)));
        overall.setAlignmentX(Component.CENTER_ALIGNMENT);
        overall.setMaximumSize(new Dimension(280, 160));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(2, 10, 2, 10);
        gc.fill   = GridBagConstraints.HORIZONTAL;

        gc.gridy = 0; gc.gridx = 0; gc.gridwidth = 2;
        JLabel ovHdr = new JLabel("Overall Summary", SwingConstants.CENTER);
        ovHdr.setFont(new Font("Segoe UI", Font.BOLD, 14));
        ovHdr.setForeground(new Color(44, 62, 80));
        overall.add(ovHdr, gc);
        gc.gridwidth = 1;

        String[][] overallData = {
            {"Passed",  String.valueOf(totalPassed)},
            {"Failed",  String.valueOf(totalFailed)},
            {"CGPA",    String.format("%.2f", cgpa)},
            {"Result",  totalFailed == 0 ? "Pass" : "Fail"}
        };

        for (String[] row : overallData) {
            gc.gridy++;
            gc.gridx = 0;
            JLabel k = new JLabel(row[0]);
            k.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            overall.add(k, gc);
            gc.gridx = 1;
            JLabel v2 = new JLabel(row[1], SwingConstants.CENTER);
            v2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            v2.setForeground(row[0].equals("Result")
                ? (row[1].equals("Pass")
                    ? new Color(39, 174, 96)
                    : new Color(192, 57, 43))
                : new Color(44, 62, 80));
            overall.add(v2, gc);
        }

        JPanel overallWrap = new JPanel(new FlowLayout(FlowLayout.CENTER));
        overallWrap.setOpaque(false);
        overallWrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        overallWrap.add(overall);
        body.add(overallWrap);

        JScrollPane outer = new JScrollPane(body);
        outer.setBorder(BorderFactory.createEmptyBorder());
        outer.getVerticalScrollBar().setUnitIncrement(16);
        return outer;
    }

    // ── Load marks from DB ────────────────────────────────────────
    private Map<Integer, List<Object[]>> loadMarksFromDB(String roll) {
        Map<Integer, List<Object[]>> map = new TreeMap<>();
        String sql = "SELECT subject, marks_obtained, max_marks, semester " +
                     "FROM marks WHERE roll_no = ? ORDER BY semester, subject";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, roll);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int sem = rs.getInt("semester");
                map.computeIfAbsent(sem, k -> new ArrayList<>())
                   .add(new Object[]{
                       rs.getString("subject"),
                       rs.getInt("marks_obtained"),
                       rs.getInt("max_marks")
                   });
            }
        } catch (Exception e) {
            System.out.println("Results load error: " + e.getMessage());
        }
        return map;
    }

    private static String resolveRoll(String username) {
        if (username.matches(".*\\d{3,}.*")) return username.toUpperCase();
        switch (username.toLowerCase()) {
            case "rahul": return "21A91A0501";
            case "sneha": return "21A91A0504";
            default:      return username.toUpperCase();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (parentFrame != null) parentFrame.setVisible(true);
    }
}