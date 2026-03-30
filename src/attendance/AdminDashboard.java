package attendance;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AdminDashboard extends JFrame {

    private static final Color BG   = new Color(44, 62, 80);
    private static final Color HDR  = new Color(41, 128, 185);

    public AdminDashboard() {
        setTitle("Admin Dashboard - Student Result Management");
        setSize(1100, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
        setVisible(true);
    }

    // ── HEADER ────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(HDR);
        h.setPreferredSize(new Dimension(0, 72));

        JLabel title = new JLabel("  🏫  Admin Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);

        JButton logout = hdrBtn("Logout", new Color(231, 76, 60));
        logout.addActionListener(e -> {
            int ok = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?", "Confirm Logout",
                JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) { new LoginFrame(); dispose(); }
        });
        JPanel rp = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        rp.setOpaque(false); rp.add(logout);

        h.add(title, BorderLayout.WEST);
        h.add(rp,    BorderLayout.EAST);
        return h;
    }

    // ── CARD GRID: 3×2 ────────────────────────────────────────────
    private JPanel buildContent() {
        JPanel bg = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0,0, new Color(52,73,94), 0,getHeight(), BG));
                g2.fillRect(0,0,getWidth(),getHeight());
            }
        };
        bg.setLayout(new GridBagLayout());
        bg.setBorder(BorderFactory.createEmptyBorder(30, 36, 30, 36));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(14, 14, 14, 14);
        gbc.fill    = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;

        Object[][] cards = {
            // {title, description, emoji, Color}
            {"Add Student",     "Register student by dept/year/section", "🎓", new Color(46, 204, 113)},
            {"View Result",     "Browse student results & marks",         "📊", new Color(52, 152, 219)},
            {"Generate Report", "Charts, analytics & exports",            "📈", new Color(155, 89, 182)},
            {"My Profile",      "View & update admin profile",            "👤", new Color(41, 128, 185)},
            {"Edit Student",    "Edit student profile by roll number",    "✏️", new Color(241, 196, 15)},
        };

        int col = 0, row = 0;
        for (int i = 0; i < cards.length; i++) {
            String title = (String) cards[i][0];
            String desc  = (String) cards[i][1];
            String emoji = (String) cards[i][2];
            Color  color = (Color)  cards[i][3];

            ActionListener action = buildAction(title);

            gbc.gridx = col;
            gbc.gridy = row;
            bg.add(createCard(title, desc, emoji, color, action), gbc);

            col++;
            if (col == 3) { col = 0; row++; }
        }
        return bg;
    }

    private ActionListener buildAction(String title) {
        switch (title) {
            case "Add Student":     return e -> { setVisible(false); new AddStudentFrame(this); };
            case "View Result":     return e -> { setVisible(false); new ViewResultFrame(this); };
            case "Generate Report": return e -> { setVisible(false); new GenerateReportFrame(this); };
            case "My Profile":      return e -> { setVisible(false); new AdminProfileFrame(this); };
            case "Edit Student":    return e -> { setVisible(false); new AdminEditStudentFrame(this); };
            default: return e -> {};
        }
    }

    // ── Card widget ───────────────────────────────────────────────
    private JPanel createCard(String title, String desc, String emoji,
                              Color color, ActionListener action) {
        JPanel card = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0,0,getWidth(),getHeight(),20,20);
            }
        };
        card.setLayout(new BorderLayout(10, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(22, 24, 14, 24));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setOpaque(false);

        // Icon + title row
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        top.setOpaque(false);
        JLabel iconLbl = new JLabel(emoji);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 34));
        iconLbl.setForeground(color);
        top.add(iconLbl);

        // Text
        JPanel txt = new JPanel();
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
        txt.setOpaque(false);
        JLabel tl = new JLabel(title);
        tl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tl.setForeground(new Color(33,37,41));
        JLabel dl = new JLabel(desc);
        dl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dl.setForeground(Color.GRAY);
        txt.add(tl);
        txt.add(Box.createRigidArea(new Dimension(0,4)));
        txt.add(dl);

        // Colour bar at bottom
        JPanel bar = new JPanel();
        bar.setOpaque(true);
        bar.setBackground(color);
        bar.setPreferredSize(new Dimension(0,5));

        card.add(top, BorderLayout.NORTH);
        card.add(txt, BorderLayout.CENTER);
        card.add(bar, BorderLayout.SOUTH);

        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { card.setBackground(new Color(248,249,250)); card.repaint(); }
            public void mouseExited (MouseEvent e) { card.setBackground(Color.WHITE); card.repaint(); }
            public void mouseClicked(MouseEvent e) { action.actionPerformed(null); }
        });
        return card;
    }

    private static JButton hdrBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(110, 36));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(bg.darker()); }
            public void mouseExited (MouseEvent e) { b.setBackground(bg); }
        });
        return b;
    }
}
