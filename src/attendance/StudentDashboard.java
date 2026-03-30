package attendance;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class StudentDashboard extends JFrame {

    private String studentUsername;

    public StudentDashboard(String username) {
        this.studentUsername = username;
        
        setTitle("Student Dashboard - " + username);
        setSize(950, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // Main panel
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth(), h = getHeight();
                Color color1 = new Color(41, 128, 185);
                Color color2 = new Color(142, 68, 173);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(52, 73, 94));
        headerPanel.setPreferredSize(new Dimension(950, 100));
        headerPanel.setLayout(new BorderLayout());
        
        JPanel welcomePanel = new JPanel();
        welcomePanel.setOpaque(false);
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 0));
        
        JLabel welcomeLabel = new JLabel("Welcome Back,");
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        welcomeLabel.setForeground(new Color(189, 195, 199));
        
        // Show roll number if username looks like a name, else show as-is
        String displayId = resolveRollFromDB(username);
        JLabel nameLabel = new JLabel(displayId);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        nameLabel.setForeground(Color.WHITE);
        
        welcomePanel.add(welcomeLabel);
        welcomePanel.add(nameLabel);
        
        JButton logoutBtn = createHeaderButton("Logout");
        logoutBtn.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to logout?", 
                "Confirm Logout", 
                JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                new LoginFrame();
                dispose();
            }
        });
        
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 32));
        logoutPanel.setOpaque(false);
        logoutPanel.add(logoutBtn);
        
        headerPanel.add(welcomePanel, BorderLayout.WEST);
        headerPanel.add(logoutPanel, BorderLayout.EAST);
        
        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 0.5;
        
        // Info cards
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.weighty = 0;
        JPanel infoPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        infoPanel.setOpaque(false);
        infoPanel.setPreferredSize(new Dimension(0, 140));
        
     // Load real data from DB
        String roll = resolveRoll(username);
        String cgpaVal       = calculateCGPA(roll);
        String attendanceVal = calculateAttendance(roll);
        String creditsVal    = calculateCredits(roll);

        infoPanel.add(createInfoCard("CGPA",       cgpaVal,       new Color(241, 196, 15)));
        infoPanel.add(createInfoCard("Attendance", attendanceVal, new Color(52, 152, 219)));
        infoPanel.add(createInfoCard("Credits",    creditsVal,    new Color(46, 204, 113)));
        
        contentPanel.add(infoPanel, gbc);
        
        // Action cards
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.weighty = 1;
        
        gbc.gridx = 0;
        contentPanel.add(createActionCard("My Profile", "View and edit profile", "👤", 
            new Color(155, 89, 182), e -> new StudentProfileFrame(this, studentUsername)), gbc);
        
        gbc.gridx = 1;
        contentPanel.add(createActionCard("My Results", "Check exam results", "📊", 
            new Color(26, 188, 156), e -> new StudentResultFrame(this, studentUsername)), gbc);
        
        gbc.gridx = 2;
        contentPanel.add(createActionCard("Attendance", "View subject-wise attendance", "📅", 
            new Color(230, 126, 34), e -> new AttendanceFrame(this, studentUsername)), gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        contentPanel.add(createActionCard("Timetable", "View weekly class schedule", "🕒", 
            new Color(52, 152, 219), e -> new TimetableFrame(this, studentUsername)), gbc);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        add(mainPanel);
        setVisible(true);
    }
    private String resolveRoll(String username) {
        if (username.matches(".*\\d{3,}.*")) return username.toUpperCase();
        switch (username.toLowerCase()) {
            case "rahul": return "21A91A0501";
            case "sneha": return "21A91A0504";
            default:      return username.toUpperCase();
        }
    }

    private String calculateCGPA(String roll) {
        String sql = "SELECT marks_obtained, max_marks FROM marks WHERE roll_no = ?";
        try (java.sql.Connection con = DBConnection.getConnection();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, roll);
            java.sql.ResultSet rs = ps.executeQuery();

            double totalPoints = 0;
            int    count       = 0;

            while (rs.next()) {
                int marks = rs.getInt("marks_obtained");
                // Convert marks to grade points directly
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

            if (count == 0) return "N/A";
            double cgpa = totalPoints / count;
            return String.format("%.2f", cgpa);

        } catch (Exception e) {
            System.out.println("CGPA error: " + e.getMessage());
        }
        return "N/A";
    }

    private String calculateAttendance(String roll) {
        String sql = "SELECT " +
            "COUNT(*) as total, " +
            "SUM(CASE WHEN status='Present' THEN 1 ELSE 0 END) as present " +
            "FROM attendance WHERE roll_no = ?";
        try (java.sql.Connection con = DBConnection.getConnection();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, roll);
            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int total   = rs.getInt("total");
                int present = rs.getInt("present");
                if (total == 0) return "N/A";
                double pct = (double) present / total * 100;
                return String.format("%.0f%%", pct);
            }
        } catch (Exception e) {
            System.out.println("Attendance error: " + e.getMessage());
        }
        return "N/A";
    }

    private String calculateCredits(String roll) {
        String sql = "SELECT COUNT(*) as subjects FROM marks " +
                     "WHERE roll_no = ? AND marks_obtained >= 40";
        try (java.sql.Connection con = DBConnection.getConnection();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, roll);
            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int subjects = rs.getInt("subjects");
                if (subjects == 0) return "0";
                // Each subject = 3 credits on average
                return String.valueOf(subjects * 3);
            }
        } catch (Exception e) {
            System.out.println("Credits error: " + e.getMessage());
        }
        return "0";
    }
    
    private JPanel createInfoCard(String title, String value, Color color) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        
        card.setLayout(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(5, 0, 5, 0);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        titleLabel.setForeground(Color.GRAY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 38));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        gbc.gridy = 0;
        card.add(titleLabel, gbc);
        gbc.gridy = 1;
        card.add(valueLabel, gbc);
        
        return card;
    }
    
    private JPanel createActionCard(String title, String desc, String icon, Color color, ActionListener action) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        
        card.setLayout(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        iconPanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        iconLabel.setForeground(color);
        iconPanel.add(iconLabel);
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(44, 62, 80));
        
        JLabel descLabel = new JLabel(desc);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLabel.setForeground(Color.GRAY);
        
        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        textPanel.add(descLabel);
        
        JPanel colorBar = new JPanel();
        colorBar.setBackground(color);
        colorBar.setPreferredSize(new Dimension(0, 5));
        
        card.add(iconPanel, BorderLayout.NORTH);
        card.add(textPanel, BorderLayout.CENTER);
        card.add(colorBar, BorderLayout.SOUTH);
        
        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(248, 249, 250));
            }
            public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
            }
            public void mouseClicked(MouseEvent e) {
                action.actionPerformed(null);
            }
        });
        
        return card;
    }
    private String resolveRollFromDB(String username) {
        if (username.matches(".*\\d{3,}.*")) return username.toUpperCase();
        String sql = "SELECT roll_no FROM students WHERE username = ?";
        try (java.sql.Connection con = DBConnection.getConnection();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username.toLowerCase());
            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("roll_no");
        } catch (Exception e) {
            System.out.println("Roll resolve error: " + e.getMessage());
        }
        return username.toUpperCase();
    }

        private JButton createHeaderButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(100, 35));
        button.setBackground(new Color(231, 76, 60));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(192, 57, 43));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(231, 76, 60));
            }
        });
        
        return button;
    }
}
