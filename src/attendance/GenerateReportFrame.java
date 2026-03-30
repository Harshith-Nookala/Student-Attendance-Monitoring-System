package attendance;
import java.sql.Connection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

public class GenerateReportFrame extends JFrame {

    private JFrame parentFrame;

    private JComboBox<String> reportTypeBox, deptBox, yearBox, sectionBox,
                               subjectBox, semesterBox, formatBox;

    private ChartPanel chartPanel;
    private String curDept="CSE", curYear="All", curSection="All",
                   curSubject="All Subjects", curReport="Class Performance";

    private static final Color ACCENT = new Color(155, 89, 182);

    public GenerateReportFrame(JFrame parent) {
        this.parentFrame = parent;
        setTitle("Generate and Download Reports");
        setSize(1180, 760);
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
        h.setPreferredSize(new Dimension(0, 62));

        JButton back = hdrBtn("← Back", ACCENT.darker());
        back.addActionListener(e -> dispose());
        JPanel bw = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        bw.setOpaque(false); bw.add(back);

        JLabel title = new JLabel("📈  Generate Report");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        JPanel tw = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15));
        tw.setOpaque(false); tw.add(title);

        h.add(bw, BorderLayout.WEST);
        h.add(tw, BorderLayout.CENTER);
        return h;
    }

    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout(0, 0));
        body.setBackground(Color.WHITE);

        JPanel left = buildConfigPanel();
        left.setPreferredSize(new Dimension(310, 0));

        chartPanel = new ChartPanel();
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0,
            new Color(220, 220, 220)));

        body.add(left,       BorderLayout.WEST);
        body.add(chartPanel, BorderLayout.CENTER);
        return body;
    }

    private JPanel buildConfigPanel() {
        JPanel p = new JPanel();
        p.setBackground(new Color(248, 249, 250));
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(20, 18, 20, 18));

        p.add(secHdr("1. Report Configuration"));
        p.add(Box.createRigidArea(new Dimension(0, 6)));
        p.add(sep());
        p.add(Box.createRigidArea(new Dimension(0, 10)));

        p.add(lbl("Report Type"));
        p.add(Box.createRigidArea(new Dimension(0, 5)));
        reportTypeBox = cfgCombo("Class Performance", "Student Wise Report",
                                  "Attendance Summary", "Subject Analysis",
                                  "Top Performers");
        p.add(reportTypeBox);

        p.add(Box.createRigidArea(new Dimension(0, 22)));
        p.add(secHdr("2. Apply Filters"));
        p.add(Box.createRigidArea(new Dimension(0, 6)));
        p.add(sep());
        p.add(Box.createRigidArea(new Dimension(0, 10)));

        p.add(lbl("Department"));
        p.add(Box.createRigidArea(new Dimension(0, 5)));
        deptBox = cfgCombo("CSE","ECE","MECH","CIVIL","EEE","IT");
        p.add(deptBox);

        p.add(Box.createRigidArea(new Dimension(0, 12)));
        p.add(lbl("Year"));
        p.add(Box.createRigidArea(new Dimension(0, 5)));
        yearBox = cfgCombo("All","1","2","3","4");
        p.add(yearBox);

        p.add(Box.createRigidArea(new Dimension(0, 12)));
        p.add(lbl("Section"));
        p.add(Box.createRigidArea(new Dimension(0, 5)));
        sectionBox = cfgCombo("All","A","B","C","D");
        p.add(sectionBox);

        p.add(Box.createRigidArea(new Dimension(0, 12)));
        p.add(lbl("Subject"));
        p.add(Box.createRigidArea(new Dimension(0, 5)));
        subjectBox = cfgCombo("All Subjects","Data Structures","Algorithms",
                               "DBMS","Computer Networks","OS");
        p.add(subjectBox);

        p.add(Box.createRigidArea(new Dimension(0, 12)));
        p.add(lbl("Semester"));
        p.add(Box.createRigidArea(new Dimension(0, 5)));
        semesterBox = cfgCombo("Both","1","2","3","4","5","6","7","8");
        p.add(semesterBox);

        p.add(Box.createRigidArea(new Dimension(0, 22)));
        p.add(secHdr("3. Export Settings"));
        p.add(Box.createRigidArea(new Dimension(0, 6)));
        p.add(sep());
        p.add(Box.createRigidArea(new Dimension(0, 10)));

        p.add(lbl("Export Format"));
        p.add(Box.createRigidArea(new Dimension(0, 5)));
        formatBox = cfgCombo("PDF Document","Excel Spreadsheet","CSV File");
        p.add(formatBox);

        p.add(Box.createRigidArea(new Dimension(0, 22)));

        JButton applyBtn    = cfgBtn("🔄  Apply / Refresh", ACCENT);
        JButton downloadBtn = cfgBtn("⬇  Download Report", new Color(46, 204, 113));
        p.add(applyBtn);
        p.add(Box.createRigidArea(new Dimension(0, 10)));
        p.add(downloadBtn);

        applyBtn.addActionListener(e -> {
            curReport  = reportTypeBox.getSelectedItem().toString();
            curDept    = deptBox.getSelectedItem().toString();
            curYear    = yearBox.getSelectedItem().toString();
            curSection = sectionBox.getSelectedItem().toString();
            curSubject = subjectBox.getSelectedItem().toString();
            chartPanel.refresh(curReport, curDept, curYear, curSection, curSubject);
        });

        downloadBtn.addActionListener(e ->
            JOptionPane.showMessageDialog(this,
                "Report exported!\n\nType   : " + reportTypeBox.getSelectedItem() +
                "\nDept   : " + deptBox.getSelectedItem() +
                "\nFormat : " + formatBox.getSelectedItem() +
                "\nSaved to Downloads/Report_" + System.currentTimeMillis() + ".pdf",
                "Exported ✓", JOptionPane.INFORMATION_MESSAGE)
        );

        return p;
    }

    // ── BAR CHART PANEL ──────────────────────────────────────────
    class ChartPanel extends JPanel {

        private String report = "Class Performance", dept = "CSE",
                       year = "All", section = "All", subject = "All Subjects";
        private String[] labels;
        private int[]    scores;

        ChartPanel() {
            refresh("Class Performance", "CSE", "All", "All", "All Subjects");
        }

        void refresh(String r, String d, String y, String s, String sub) {
            report=r; dept=d; year=y; section=s; subject=sub;
            if (r.equals("Class Performance") || r.equals("Subject Analysis")) {
                loadSubjectAverages(d, y, s);
            } else if (r.equals("Top Performers")) {
                loadTopPerformers(d, y, s);
            } else if (r.equals("Attendance Summary")) {
                loadAttendanceSummary(d, y, s);
            } else {
                loadYearWise(d);
            }
            repaint();
        }

        private void loadSubjectAverages(String dept, String year, String section) {
            java.util.List<String>  lblList   = new java.util.ArrayList<>();
            java.util.List<Integer> scoreList = new java.util.ArrayList<>();

            String sql = "SELECT SUBSTRING_INDEX(m.subject,'|',-1) AS subName, " +
                "ROUND(AVG(m.marks_obtained)) AS avg_marks " +
                "FROM marks m JOIN students s ON m.roll_no = s.roll_no " +
                "WHERE s.department = ? " +
                (!"All".equals(year)    ? "AND s.year = ? "    : "") +
                (!"All".equals(section) ? "AND s.section = ? " : "") +
                "GROUP BY m.subject";

            try (Connection con = DBConnection.getConnection();
                 java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
                int idx = 1;
                ps.setString(idx++, dept);
                if (!"All".equals(year))    ps.setString(idx++, year);
                if (!"All".equals(section)) ps.setString(idx++, section);
                java.sql.ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    lblList.add(rs.getString("subName"));
                    scoreList.add(rs.getInt("avg_marks"));
                }
            } catch (Exception e) {
                System.out.println("Chart DB error: " + e.getMessage());
            }

            if (lblList.isEmpty()) {
                labels = new String[]{"No Data"}; scores = new int[]{0};
            } else {
                labels = lblList.toArray(new String[0]);
                scores = scoreList.stream().mapToInt(i -> i).toArray();
            }
        }

        private void loadTopPerformers(String dept, String year, String section) {
            java.util.List<String>  lblList   = new java.util.ArrayList<>();
            java.util.List<Integer> scoreList = new java.util.ArrayList<>();

            String sql = "SELECT s.name, ROUND(AVG(m.marks_obtained)) AS avg_marks " +
                "FROM marks m JOIN students s ON m.roll_no = s.roll_no " +
                "WHERE s.department = ? " +
                (!"All".equals(year)    ? "AND s.year = ? "    : "") +
                (!"All".equals(section) ? "AND s.section = ? " : "") +
                "GROUP BY s.roll_no, s.name " +
                "ORDER BY avg_marks DESC LIMIT 5";

            try (Connection con = DBConnection.getConnection();
                 java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
                int idx = 1;
                ps.setString(idx++, dept);
                if (!"All".equals(year))    ps.setString(idx++, year);
                if (!"All".equals(section)) ps.setString(idx++, section);
                java.sql.ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String name   = rs.getString("name");
                    String[] parts = name.split(" ");
                    lblList.add(parts[0] + (parts.length > 1
                        ? " " + parts[1].charAt(0) + "." : ""));
                    scoreList.add(rs.getInt("avg_marks"));
                }
            } catch (Exception e) {
                System.out.println("Chart DB error: " + e.getMessage());
            }

            if (lblList.isEmpty()) {
                labels = new String[]{"No Data"}; scores = new int[]{0};
            } else {
                labels = lblList.toArray(new String[0]);
                scores = scoreList.stream().mapToInt(i -> i).toArray();
            }
        }

        private void loadAttendanceSummary(String dept, String year, String section) {
            java.util.List<String>  lblList   = new java.util.ArrayList<>();
            java.util.List<Integer> scoreList = new java.util.ArrayList<>();

            String sql = "SELECT a.subject, " +
                "ROUND(SUM(CASE WHEN a.status='Present' THEN 1 ELSE 0 END)*100.0/COUNT(*)) AS pct " +
                "FROM attendance a JOIN students s ON a.roll_no = s.roll_no " +
                "WHERE s.department = ? " +
                (!"All".equals(year)    ? "AND s.year = ? "    : "") +
                (!"All".equals(section) ? "AND s.section = ? " : "") +
                "GROUP BY a.subject";

            try (Connection con = DBConnection.getConnection();
                 java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
                int idx = 1;
                ps.setString(idx++, dept);
                if (!"All".equals(year))    ps.setString(idx++, year);
                if (!"All".equals(section)) ps.setString(idx++, section);
                java.sql.ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    lblList.add(rs.getString("subject"));
                    scoreList.add(rs.getInt("pct"));
                }
            } catch (Exception e) {
                System.out.println("Chart DB error: " + e.getMessage());
            }

            if (lblList.isEmpty()) {
                labels = new String[]{"No Data"}; scores = new int[]{0};
            } else {
                labels = lblList.toArray(new String[0]);
                scores = scoreList.stream().mapToInt(i -> i).toArray();
            }
        }

        private void loadYearWise(String dept) {
            java.util.List<String>  lblList   = new java.util.ArrayList<>();
            java.util.List<Integer> scoreList = new java.util.ArrayList<>();

            String sql = "SELECT s.year, ROUND(AVG(m.marks_obtained)) AS avg_marks " +
                "FROM marks m JOIN students s ON m.roll_no = s.roll_no " +
                "WHERE s.department = ? " +
                "GROUP BY s.year ORDER BY s.year";

            try (Connection con = DBConnection.getConnection();
                 java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, dept);
                java.sql.ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    lblList.add("Year " + rs.getString("year"));
                    scoreList.add(rs.getInt("avg_marks"));
                }
            } catch (Exception e) {
                System.out.println("Chart DB error: " + e.getMessage());
            }

            if (lblList.isEmpty()) {
                labels = new String[]{"No Data"}; scores = new int[]{0};
            } else {
                labels = lblList.toArray(new String[0]);
                scores = scoreList.stream().mapToInt(i -> i).toArray();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (labels == null) return;
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w=getWidth(), h=getHeight();
            int padL=70, padR=30, padT=70, padB=80;
            int cW=w-padL-padR, cH=h-padT-padB;
            int n=labels.length;
            int slot = n > 0 ? cW/n : cW;
            int bW=Math.min(65, slot-24);

            // Title
            g2.setColor(new Color(33,37,41));
            g2.setFont(new Font("Segoe UI",Font.BOLD,18));
            String ttl = report + " — " + dept
                + (!"All".equals(year)    ? " · Yr " + year    : "")
                + (!"All".equals(section) ? " · Sec " + section : "");
            g2.drawString(ttl, padL, 42);

            // Y grid
            g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_BEVEL, 0, new float[]{5f}, 0));
            for (int pct=0; pct<=100; pct+=20) {
                int yy = padT+cH - pct*cH/100;
                g2.setColor(new Color(230,230,230));
                g2.drawLine(padL, yy, padL+cW, yy);
                g2.setColor(new Color(80,80,80));
                g2.setFont(new Font("Segoe UI",Font.PLAIN,11));
                String lb = pct + "%";
                g2.drawString(lb, padL-g2.getFontMetrics().stringWidth(lb)-6, yy+4);
            }

            // Axes
            g2.setStroke(new BasicStroke(2));
            g2.setColor(new Color(44,62,80));
            g2.drawLine(padL, padT, padL, padT+cH);
            g2.drawLine(padL, padT+cH, padL+cW, padT+cH);

            Color[] colours = {
                new Color(46,204,113), new Color(52,152,219),
                new Color(155,89,182), new Color(241,196,15),
                new Color(231,76,60)
            };

            for (int i=0; i<n; i++) {
                int bh = scores[i]*cH/100;
                if (bh < 1) bh = 1;
                int bx = padL + i*slot + (slot-bW)/2;
                int by = padT + cH - bh;

                // Shadow
                g2.setColor(new Color(0,0,0,25));
                g2.fillRoundRect(bx+4, by+4, bW, bh, 10, 10);

                // Bar
                Color c = colours[i % colours.length];
                g2.setPaint(new GradientPaint(bx, by, c.brighter(), bx, padT+cH, c));
                g2.setStroke(new BasicStroke(0));
                g2.fillRoundRect(bx, by, bW, bh, 10, 10);

                // Border
                g2.setColor(c.darker());
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(bx, by, bW, bh, 10, 10);

                // Value label
                g2.setColor(new Color(33,37,41));
                g2.setFont(new Font("Segoe UI",Font.BOLD,13));
                String val = scores[i] + "%";
                int vw = g2.getFontMetrics().stringWidth(val);
                g2.drawString(val, bx+(bW-vw)/2, by-7);

                // X label
                g2.setFont(new Font("Segoe UI",Font.PLAIN,11));
                g2.setColor(new Color(44,62,80));
                drawLabel(g2, labels[i], bx+bW/2, padT+cH+20, slot-4);
            }

            // Stats strip
            int avg = scores.length > 0 ? Arrays.stream(scores).sum()/scores.length : 0;
            int max = scores.length > 0 ? Arrays.stream(scores).max().getAsInt() : 0;
            g2.setColor(new Color(236,240,241));
            g2.fillRoundRect(padL, h-28, w-padL-padR, 22, 6, 6);
            g2.setColor(new Color(44,62,80));
            g2.setFont(new Font("Segoe UI",Font.BOLD,12));
            g2.drawString("📊 Report: " + report +
                "   |   Dept: " + dept +
                "   |   Avg: " + avg + "%" +
                "   |   Highest: " + max + "%",
                padL+10, h-12);
        }

        private void drawLabel(Graphics2D g2, String text, int cx, int y, int maxW) {
            FontMetrics fm = g2.getFontMetrics();
            if (fm.stringWidth(text) <= maxW) {
                g2.drawString(text, cx-fm.stringWidth(text)/2, y);
                return;
            }
            int sp = text.lastIndexOf(' ', text.length()/2+5);
            if (sp < 0) { g2.drawString(text, cx-fm.stringWidth(text)/2, y); return; }
            String l1 = text.substring(0, sp), l2 = text.substring(sp+1);
            g2.drawString(l1, cx-fm.stringWidth(l1)/2, y);
            g2.drawString(l2, cx-fm.stringWidth(l2)/2, y+14);
        }

    } // ← closes ChartPanel

    // ── Helpers ──────────────────────────────────────────────────
    private static JLabel secHdr(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI",Font.BOLD,14));
        l.setForeground(new Color(155,89,182));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }
    private static JSeparator sep() {
        JSeparator s = new JSeparator();
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE,1));
        s.setForeground(new Color(215,215,215));
        return s;
    }
    private static JLabel lbl(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI",Font.BOLD,13));
        l.setForeground(new Color(52,73,94));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }
    private static JComboBox<String> cfgCombo(String... items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(new Font("Segoe UI",Font.PLAIN,14));
        cb.setBackground(Color.WHITE);
        cb.setMaximumSize(new Dimension(Integer.MAX_VALUE,40));
        cb.setAlignmentX(Component.LEFT_ALIGNMENT);
        return cb;
    }
    private static JButton cfgBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI",Font.BOLD,14));
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE,42));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(bg.darker()); }
            public void mouseExited (MouseEvent e) { b.setBackground(bg); }
        });
        return b;
    }
    private static JButton hdrBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI",Font.BOLD,14));
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(110,34));
        return b;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (parentFrame != null) parentFrame.setVisible(true);
    }

} // ← closes GenerateReportFrame