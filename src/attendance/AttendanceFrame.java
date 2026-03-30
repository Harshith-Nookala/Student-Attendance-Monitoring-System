package attendance;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.util.*;

public class AttendanceFrame extends JFrame {

    private JFrame parentFrame;
    private String studentUsername;

    private static final Color HDR_BG  = new Color(44, 62, 80);
    private static final Color ROW_ODD = new Color(248, 248, 248);
    private static final Color TOT_BG  = new Color(204, 204, 204);

    public AttendanceFrame(JFrame parent, String username) {
        this.parentFrame     = parent;
        this.studentUsername = username;
        setTitle("Attendance Report");
        setSize(900, 760);
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
        h.setBackground(new Color(230, 126, 34));
        h.setPreferredSize(new Dimension(0, 56));

        JButton back = new JButton("← Back");
        back.setFont(new Font("Segoe UI", Font.BOLD, 14));
        back.setBackground(new Color(200, 100, 10));
        back.setForeground(Color.WHITE);
        back.setFocusPainted(false);
        back.setBorderPainted(false);
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        back.setPreferredSize(new Dimension(110, 34));
        back.addActionListener(e -> dispose());

        JPanel bw = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 12));
        bw.setOpaque(false);
        bw.add(back);

        JLabel t = new JLabel("ATTENDANCE REPORT");
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
        body.setBackground(Color.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        body.add(buildInfoBlock());
        body.add(Box.createRigidArea(new Dimension(0, 18)));
        body.add(buildAttendanceTable());

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    private JPanel buildInfoBlock() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        GridBagConstraints g = new GridBagConstraints();
        g.anchor = GridBagConstraints.CENTER;
        g.gridx  = 0;
        g.insets = new Insets(2, 0, 2, 0);

        g.gridy = 0;
        JLabel title = new JLabel("ATTENDANCE REPORT", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(33, 37, 41));
        p.add(title, g);

        String roll      = resolveRoll(studentUsername);
        String studName  = getStudentName(roll);

        String[][] info = {
            {"Roll No",      roll},
            {"Student Name", studName},
            {"Course",       "B.Tech"},
            {"Branch",       "CSE"},
            {"Semester",     "IV Semester"},
        };

        for (String[] row : info) {
            g.gridy++;
            JPanel line = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
            line.setOpaque(false);
            JLabel key = new JLabel(row[0] + " : ");
            key.setFont(new Font("Segoe UI", Font.BOLD, 14));
            key.setForeground(new Color(33, 37, 41));
            JLabel val = new JLabel(row[1]);
            val.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            val.setForeground(new Color(33, 37, 41));
            line.add(key);
            line.add(val);
            p.add(line, g);
        }
        return p;
    }

    private JPanel buildAttendanceTable() {
        // Load attendance from database
        Object[][] RAW = loadAttendanceFromDB(resolveRoll(studentUsername));

        int totalHeld = 0, totalAttended = 0;
        for (Object[] r : RAW) {
            totalHeld     += (int) r[1];
            totalAttended += (int) r[2];
        }

        String[] cols = {"Sl.No.", "Subject", "Held", "Attend", "%"};
        int n = RAW.length;
        Object[][] rows = new Object[n + 1][5];

        for (int i = 0; i < n; i++) {
            int held = (int) RAW[i][1];
            int att  = (int) RAW[i][2];
            double pct = held > 0 ? (double) att / held * 100 : 0.0;
            rows[i][0] = i + 1;
            rows[i][1] = RAW[i][0];
            rows[i][2] = held;
            rows[i][3] = att;
            rows[i][4] = held > 0 ? String.format("%.2f", pct) : "-";
        }

        // TOTAL row
        rows[n][0] = "";
        rows[n][1] = "TOTAL";
        rows[n][2] = totalHeld;
        rows[n][3] = totalAttended;
        rows[n][4] = totalHeld > 0
            ? String.format("%.2f", (double) totalAttended / totalHeld * 100)
            : "0.00";

        DefaultTableModel model = new DefaultTableModel(rows, cols) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(36);
        table.setShowGrid(true);
        table.setGridColor(new Color(180, 180, 180));

        JTableHeader jth = table.getTableHeader();
        jth.setPreferredSize(new Dimension(0, 44));
        jth.setReorderingAllowed(false);

        for (int ci = 0; ci < cols.length; ci++) {
            final int colIdx = ci;
            final String lbl = cols[ci];
            table.getColumnModel().getColumn(ci).setHeaderRenderer(
                (t2, v, sel, foc, r, c) -> {
                    JPanel cell = new JPanel(new GridBagLayout());
                    cell.setOpaque(true);
                    cell.setBackground(HDR_BG);
                    cell.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2,
                            colIdx < cols.length - 1 ? 1 : 0,
                            new Color(80, 100, 120)),
                        BorderFactory.createEmptyBorder(4, 6, 4, 6)));
                    JLabel lb = new JLabel(lbl, SwingConstants.CENTER);
                    lb.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    lb.setForeground(Color.WHITE);
                    cell.add(lb);
                    return cell;
                }
            );
        }

        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);

        final int totalRow = n;
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t2, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t2, v, sel, foc, row, col);
                setHorizontalAlignment(col == 1 ? LEFT : CENTER);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

                if (!sel) {
                    if (row == totalRow) {
                        setBackground(TOT_BG);
                        setFont(new Font("Segoe UI", Font.BOLD, 14));
                        setForeground(new Color(33, 37, 41));
                    } else {
                        setBackground(row % 2 == 0 ? Color.WHITE : ROW_ODD);
                        setForeground(new Color(33, 37, 41));
                        setFont(new Font("Segoe UI", Font.PLAIN, 14));

                        if (col == 4 && v != null && !v.toString().equals("-")) {
                            try {
                                double pct = Double.parseDouble(v.toString());
                                if (pct < 75) {
                                    setForeground(new Color(192, 57, 43));
                                    setFont(new Font("Segoe UI", Font.BOLD, 14));
                                } else {
                                    setForeground(new Color(39, 174, 96));
                                }
                            } catch (NumberFormatException ignored) {}
                        }
                        if ((col == 2 || col == 3) && "0".equals(String.valueOf(v))) {
                            setForeground(Color.GRAY);
                        }
                    }
                }
                return this;
            }
        });

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(Color.WHITE);
        wrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(new Color(175, 175, 175)));
        sp.getViewport().setBackground(Color.WHITE);
        int h2 = 44 + (n + 1) * 36 + 4;
        sp.setPreferredSize(new Dimension(840, h2));
        sp.setMaximumSize(new Dimension(Integer.MAX_VALUE, h2));
        wrap.add(sp, BorderLayout.CENTER);

        JLabel note = new JLabel(
            "  ⚠  Students must maintain minimum 75% attendance per subject.");
        note.setFont(new Font("Segoe UI", Font.BOLD, 13));
        note.setForeground(new Color(192, 57, 43));
        note.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        note.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel outer = new JPanel();
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setBackground(Color.WHITE);
        outer.setAlignmentX(Component.LEFT_ALIGNMENT);
        outer.add(wrap);
        outer.add(note);
        return outer;
    }

    // ── Load attendance from MySQL ────────────────────────────────
    private Object[][] loadAttendanceFromDB(String rollNo) {
        String sql = "SELECT subject, " +
                     "COUNT(*) AS held, " +
                     "SUM(CASE WHEN status='Present' THEN 1 ELSE 0 END) AS attended " +
                     "FROM attendance " +
                     "WHERE roll_no = ? AND semester = 4 " +
                     "GROUP BY subject";

        java.util.List<Object[]> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, rollNo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getString("subject"),
                    rs.getInt("held"),
                    rs.getInt("attended")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading attendance: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        // If no data in DB, return empty array
        if (list.isEmpty()) {
            return new Object[][]{{"No attendance data found", 0, 0}};
        }

        return list.toArray(new Object[0][]);
    }

    // ── Get student name from DB ──────────────────────────────────
    private String getStudentName(String rollNo) {
        String sql = "SELECT name FROM students WHERE roll_no = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, rollNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("name").toUpperCase();
        } catch (Exception e) {
            System.out.println("Error getting student name: " + e.getMessage());
        }
        return "UNKNOWN";
    }

    private static String resolveRoll(String username) {
        if (username.matches(".*\\d{4}.*")) return username;
        switch (username.toLowerCase()) {
            case "rahul":    return "21A91A0501";
            case "sneha":    return "21A91A0504";
            case "student1": return "21A91A0505";
            default: return username.toUpperCase();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (parentFrame != null) parentFrame.setVisible(true);
    }
}