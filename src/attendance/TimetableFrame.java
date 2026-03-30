package attendance;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class TimetableFrame extends JFrame {

    private JFrame parentFrame;
    private String username;
    private String dept    = "CSE";
    private String year    = "2";
    private String section = "A";

    public TimetableFrame(JFrame parent, String username) {
        this.parentFrame = parent;
        this.username    = username;

        loadStudentInfo();

        setTitle("Time Table");
        setSize(1200, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTable(),  BorderLayout.CENTER);

        if (parentFrame != null) parentFrame.setVisible(false);
        setVisible(true);
    }

    // ── Load student dept/year/section from DB ────────────────────
    private void loadStudentInfo() {
        String roll = resolveRoll(username);
        String sql  = "SELECT department, year, section FROM students " +
                      "WHERE roll_no = ? OR username = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, roll);
            ps.setString(2, username.toLowerCase());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                dept    = rs.getString("department") != null ? rs.getString("department") : "CSE";
                year    = rs.getString("year")       != null ? rs.getString("year")       : "2";
                section = rs.getString("section")    != null ? rs.getString("section")    : "A";
            }
        } catch (Exception e) {
            System.out.println("Timetable info error: " + e.getMessage());
        }
    }

    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(new Color(52, 152, 219));
        h.setPreferredSize(new Dimension(0, 56));

        JButton back = new JButton("← Back");
        back.setFont(new Font("Segoe UI", Font.BOLD, 14));
        back.setBackground(new Color(41, 128, 185));
        back.setForeground(Color.WHITE);
        back.setFocusPainted(false);
        back.setBorderPainted(false);
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        back.setPreferredSize(new Dimension(110, 34));
        back.addActionListener(e -> dispose());

        JPanel bw = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 12));
        bw.setOpaque(false);
        bw.add(back);

        String semLabel = getSemesterLabel();
        JLabel t = new JLabel("TIME TABLE  —  " + semLabel);
        t.setFont(new Font("Segoe UI", Font.BOLD, 21));
        t.setForeground(Color.WHITE);
        JPanel tw = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 13));
        tw.setOpaque(false);
        tw.add(t);

        // Info chips
        JPanel chips = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 14));
        chips.setOpaque(false);
        String[][] chipData = {
            {dept,           "#1ABC9C"},
            {"Year " + year, "#9B59B6"},
            {"Sec " + section, "#E67E22"}
        };
        for (String[] cd : chipData) {
            JLabel c2 = new JLabel("  " + cd[0] + "  ");
            c2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            c2.setForeground(Color.WHITE);
            c2.setOpaque(true);
            c2.setBackground(Color.decode(cd[1]));
            chips.add(c2);
        }

        h.add(bw,    BorderLayout.WEST);
        h.add(tw,    BorderLayout.CENTER);
        h.add(chips, BorderLayout.EAST);
        return h;
    }

    private String getSemesterLabel() {
        switch (year) {
            case "1": return "I / II Semester";
            case "2": return "III / IV Semester";
            case "3": return "V / VI Semester";
            case "4": return "VII / VIII Semester";
            default:  return "Current Semester";
        }
    }

    private JScrollPane buildTable() {
        String[] cols = {
            "Day",
            "Period 1\n09:30–10:20",
            "Period 2\n10:20–11:10",
            "Period 3\n11:10–12:00",
            "12:00–01:00\n(LUNCH)",
            "Period 4\n01:00–01:50",
            "Period 5\n01:50–02:40",
            "Period 6\n02:40–03:30",
            "Period 7\n03:30–04:20"
        };

        Object[][] data = getTimetableData();

        DefaultTableModel model = new DefaultTableModel(data, cols) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(model);
        table.setRowHeight(46);
        table.setShowGrid(true);
        table.setGridColor(new Color(175, 175, 175));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        table.getColumnModel().getColumn(0).setPreferredWidth(55);
        table.getColumnModel().getColumn(4).setPreferredWidth(85);
        for (int i = 1; i <= 3; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(110);
        for (int i = 5; i <= 8; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(110);

        JTableHeader jth = table.getTableHeader();
        jth.setPreferredSize(new Dimension(0, 54));
        jth.setReorderingAllowed(false);

        for (int ci = 0; ci < cols.length; ci++) {
            final String lbl    = cols[ci].replace("\n", "<br>");
            final int   colIdx  = ci;
            table.getColumnModel().getColumn(ci).setHeaderRenderer(
                (t2, v, sel, foc, r, c) -> {
                    JPanel cell = new JPanel(new GridBagLayout());
                    cell.setOpaque(true);
                    boolean special = (colIdx == 4);
                    cell.setBackground(special
                        ? new Color(80, 80, 80)
                        : new Color(44, 62, 80));
                    cell.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 3,
                            colIdx < cols.length-1 ? 1 : 0,
                            new Color(52, 152, 219)),
                        BorderFactory.createEmptyBorder(3, 3, 3, 3)));
                    JLabel lb = new JLabel(
                        "<html><center>" + lbl + "</center></html>",
                        SwingConstants.CENTER);
                    lb.setFont(new Font("Segoe UI", Font.BOLD, special ? 10 : 11));
                    lb.setForeground(special
                        ? new Color(200, 200, 200) : Color.WHITE);
                    cell.add(lb);
                    return cell;
                }
            );
        }

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t2, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t2, v, sel, foc, row, col);
                setHorizontalAlignment(CENTER);
                setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
                String val = v == null ? "" : v.toString();
                if (!sel) {
                    if (col == 0) {
                        setBackground(new Color(230, 230, 230));
                        setFont(new Font("Segoe UI", Font.BOLD, 13));
                        setForeground(new Color(33, 37, 41));
                    } else if (col == 4) {
                        setBackground(new Color(240, 240, 240));
                        setForeground(Color.GRAY);
                        setFont(new Font("Segoe UI", Font.ITALIC, 12));
                    } else if (val.equals("–") || val.isEmpty()) {
                        setBackground(new Color(248, 248, 248));
                        setForeground(new Color(180, 180, 180));
                        setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    } else {
                        setBackground(subjectColor(val));
                        setForeground(new Color(33, 37, 41));
                        setFont(new Font("Segoe UI", Font.BOLD, 13));
                    }
                }
                return this;
            }

            private Color subjectColor(String s) {
                int hash = Math.abs(s.hashCode()) % 8;
                Color[] colors = {
                    new Color(198, 235, 198),
                    new Color(198, 224, 240),
                    new Color(255, 235, 180),
                    new Color(255, 215, 215),
                    new Color(225, 215, 255),
                    new Color(255, 245, 180),
                    new Color(255, 210, 225),
                    new Color(215, 225, 255)
                };
                return colors[hash];
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    // ── Generate timetable based on dept + year + section ────────
    private Object[][] getTimetableData() {
        if (dept.equals("CSE") && year.equals("2") && section.equals("A")) {
            return new Object[][]{
                {"Mon","DS","DS","OS",       "–","OS",   "Math",  "AI",   "SE"},
                {"Tue","AI","Math","DS",     "–","SE",   "OS",    "Math", "DS"},
                {"Wed","OS","AI","Math",     "–","DS",   "SE",    "OS",   "AI"},
                {"Thu","Math","SE","AI",     "–","AI",   "DS",    "SE",   "Math"},
                {"Fri","SE","DS","OS",       "–","Math", "AI",    "DS",   "OS"},
                {"Sat","–", "–", "–",        "–","–",    "–",     "–",    "–"}
            };
        } else if (dept.equals("CSE") && year.equals("2") && section.equals("B")) {
            return new Object[][]{
                {"Mon","CN","CN","DBMS",     "–","DBMS", "SE",    "OS",   "Math"},
                {"Tue","OS","DBMS","CN",     "–","SE",   "Math",  "CN",   "DBMS"},
                {"Wed","DBMS","OS","Math",   "–","CN",   "SE",    "DBMS", "OS"},
                {"Thu","Math","SE","OS",     "–","OS",   "CN",    "Math", "SE"},
                {"Fri","SE","Math","DBMS",   "–","OS",   "DBMS",  "SE",   "CN"},
                {"Sat","–", "–", "–",        "–","–",    "–",     "–",    "–"}
            };
        } else if (dept.equals("CSE") && year.equals("2") && section.equals("C")) {
            return new Object[][]{
                {"Mon","TOC","TOC","OOP",    "–","OOP",  "DBMS",  "Math", "SE"},
                {"Tue","DBMS","OOP","TOC",   "–","Math", "SE",    "OOP",  "TOC"},
                {"Wed","OOP","Math","DBMS",  "–","TOC",  "SE",    "DBMS", "OOP"},
                {"Thu","Math","SE","TOC",    "–","DBMS", "OOP",   "Math", "SE"},
                {"Fri","SE","DBMS","OOP",    "–","Math", "TOC",   "SE",   "DBMS"},
                {"Sat","–", "–", "–",        "–","–",    "–",     "–",    "–"}
            };
        } else if (dept.equals("CSE") && year.equals("2") && section.equals("D")) {
            return new Object[][]{
                {"Mon","AI","AI","ML",       "–","ML",   "DS",    "Math", "Web"},
                {"Tue","ML","DS","AI",       "–","Web",  "Math",  "AI",   "ML"},
                {"Wed","DS","Web","Math",    "–","AI",   "ML",    "DS",   "Web"},
                {"Thu","Math","ML","DS",     "–","DS",   "AI",    "Math", "ML"},
                {"Fri","Web","DS","AI",      "–","Math", "ML",    "Web",  "DS"},
                {"Sat","–", "–", "–",        "–","–",    "–",     "–",    "–"}
            };
        } else if (dept.equals("CSE") && year.equals("3")) {
            return new Object[][]{
                {"Mon","CN","CN","OOP",      "–","OOP",  "DBMS",  "SE",   "TOC"},
                {"Tue","DBMS","OOP","CN",    "–","TOC",  "SE",    "OOP",  "DBMS"},
                {"Wed","OOP","DBMS","SE",    "–","CN",   "TOC",   "DBMS", "OOP"},
                {"Thu","TOC","SE","CN",      "–","DBMS", "OOP",   "SE",   "CN"},
                {"Fri","SE","CN","DBMS",     "–","OOP",  "DBMS",  "CN",   "TOC"},
                {"Sat","–", "–", "–",        "–","–",    "–",     "–",    "–"}
            };
        } else if (dept.equals("CSE") && year.equals("4")) {
            return new Object[][]{
                {"Mon","Cloud","Cloud","ML", "–","ML",   "Big Data","SE",  "Project"},
                {"Tue","ML","Big Data","Cloud","–","SE", "Project","Cloud","ML"},
                {"Wed","Big Data","SE","ML", "–","Cloud","Project","ML",   "Big Data"},
                {"Thu","Project","ML","Big Data","–","ML","Cloud","Big Data","SE"},
                {"Fri","SE","Cloud","Project","–","Big Data","ML","SE",   "Cloud"},
                {"Sat","–", "–", "–",         "–","–",   "–",     "–",    "–"}
            };
        } else if (dept.equals("ECE") && year.equals("2")) {
            return new Object[][]{
                {"Mon","DE","DE","Signals",  "–","Signals","EM",  "Math", "Control"},
                {"Tue","Signals","EM","DE",  "–","Control","Math","DE",   "Signals"},
                {"Wed","EM","Control","Math","–","DE",   "Signals","EM",  "Control"},
                {"Thu","Math","DE","EM",     "–","EM",   "Control","Math","DE"},
                {"Fri","Control","Signals","EM","–","Math","DE","Control","Signals"},
                {"Sat","–", "–", "–",         "–","–",    "–",    "–",    "–"}
            };
        } else if (dept.equals("ECE") && year.equals("3")) {
            return new Object[][]{
                {"Mon","VLSI","VLSI","Comm", "–","Comm", "DSP",   "Math", "Micro"},
                {"Tue","DSP","Comm","VLSI",  "–","Micro","Math",  "VLSI", "DSP"},
                {"Wed","Comm","Micro","Math","–","VLSI", "DSP",   "Comm", "Micro"},
                {"Thu","Math","DSP","Comm",  "–","DSP",  "VLSI",  "Math", "Comm"},
                {"Fri","Micro","VLSI","DSP", "–","Math", "Comm",  "Micro","VLSI"},
                {"Sat","–", "–", "–",        "–","–",    "–",     "–",    "–"}
            };
        } else if (dept.equals("ECE") && year.equals("4")) {
            return new Object[][]{
                {"Mon","IoT","IoT","Embedded","–","Embedded","RF", "Project","SE"},
                {"Tue","Embedded","RF","IoT","–","Project","SE",   "IoT",   "Embedded"},
                {"Wed","RF","Project","SE",  "–","IoT",   "Embedded","RF", "Project"},
                {"Thu","Project","IoT","RF", "–","RF",    "SE",    "Embedded","IoT"},
                {"Fri","SE","Embedded","Project","–","SE", "IoT",  "RF",    "Embedded"},
                {"Sat","–", "–", "–",          "–","–",   "–",    "–",     "–"}
            };
        } else if (dept.equals("MECH")) {
            return new Object[][]{
                {"Mon","Thermo","Thermo","FM","–","FM",   "MD",    "Math", "Mfg"},
                {"Tue","FM","MD","Thermo",   "–","Mfg",  "Math",  "Thermo","FM"},
                {"Wed","MD","Mfg","Math",    "–","Thermo","FM",   "MD",   "Mfg"},
                {"Thu","Math","Thermo","MD", "–","MD",   "Mfg",   "Math", "Thermo"},
                {"Fri","Mfg","FM","MD",      "–","Math", "Thermo","Mfg",  "FM"},
                {"Sat","–", "–", "–",        "–","–",    "–",     "–",    "–"}
            };
        } else if (dept.equals("IT")) {
            return new Object[][]{
                {"Mon","Web","Web","Cloud",  "–","Cloud", "DB",    "Network","SE"},
                {"Tue","Cloud","DB","Web",   "–","SE",   "Network","Web",   "Cloud"},
                {"Wed","DB","Network","SE",  "–","Web",  "Cloud", "DB",    "Network"},
                {"Thu","Network","Cloud","DB","–","DB",  "SE",    "Network","Web"},
                {"Fri","SE","Web","Network", "–","Network","DB",  "SE",    "Cloud"},
                {"Sat","–", "–", "–",         "–","–",   "–",    "–",     "–"}
            };
        } else {
            // Default timetable
            return new Object[][]{
                {"Mon","Sub1","Sub2","Sub3", "–","Sub4", "Sub5",  "Sub6", "Sub7"},
                {"Tue","Sub2","Sub3","Sub1", "–","Sub5", "Sub6",  "Sub7", "Sub1"},
                {"Wed","Sub3","Sub1","Sub2", "–","Sub6", "Sub7",  "Sub1", "Sub2"},
                {"Thu","Sub4","Sub5","Sub6", "–","Sub1", "Sub2",  "Sub3", "Sub4"},
                {"Fri","Sub5","Sub6","Sub7", "–","Sub2", "Sub3",  "Sub4", "Sub5"},
                {"Sat","–",   "–",   "–",    "–","–",    "–",     "–",    "–"}
            };
        }
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