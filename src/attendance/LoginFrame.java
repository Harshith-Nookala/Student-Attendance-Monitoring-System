package attendance;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;
    
    public LoginFrame() {
        setTitle("Student Result Management System");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Left panel with gradient
        JPanel left = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(41,128,185), 
                                                      0, getHeight(), new Color(142,68,173));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        left.setPreferredSize(new Dimension(490, 0));
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(BorderFactory.createEmptyBorder(80, 60, 80, 60));
        
        left.add(lbl("🎓", "Segoe UI Emoji", Font.PLAIN, 72, Color.WHITE));
        left.add(Box.createRigidArea(new Dimension(0, 20)));
        left.add(lbl("Welcome Back!", "Segoe UI", Font.BOLD, 36, Color.WHITE));
        left.add(Box.createRigidArea(new Dimension(0, 12)));
        left.add(lbl("Please login to continue", "Segoe UI", Font.PLAIN, 17, new Color(189,195,199)));
        left.add(Box.createRigidArea(new Dimension(0, 40)));
        left.add(lbl("Student Result", "Segoe UI", Font.BOLD, 28, Color.WHITE));
        left.add(Box.createRigidArea(new Dimension(0, 8)));
        left.add(lbl("Management System", "Segoe UI", Font.BOLD, 22, new Color(220,220,240)));
        left.add(Box.createRigidArea(new Dimension(0, 30)));
        
        JLabel desc = lbl("Manage student marks, results,", "Segoe UI", Font.PLAIN, 15, new Color(200,210,220));
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);
        left.add(desc);
        JLabel desc2 = lbl("and generate comprehensive", "Segoe UI", Font.PLAIN, 15, new Color(200,210,220));
        desc2.setAlignmentX(Component.LEFT_ALIGNMENT);
        left.add(desc2);
        JLabel desc3 = lbl("reports with ease.", "Segoe UI", Font.PLAIN, 15, new Color(200,210,220));
        desc3.setAlignmentX(Component.LEFT_ALIGNMENT);
        left.add(desc3);
        
        // Right panel - form
        JPanel right = new JPanel();
        right.setBackground(Color.WHITE);
        right.setLayout(new GridBagLayout());
        right.setBorder(BorderFactory.createEmptyBorder(60, 50, 60, 50));
        
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        
        // Username
        form.add(fieldLabel("Username"));
        form.add(Box.createRigidArea(new Dimension(0,8)));
        usernameField = new JTextField();
        styleField(usernameField);
        form.add(usernameField);
        form.add(Box.createRigidArea(new Dimension(0,20)));
        
        // Password
        form.add(fieldLabel("Password"));
        form.add(Box.createRigidArea(new Dimension(0,8)));
        passwordField = new JPasswordField();
        styleField(passwordField);
        form.add(passwordField);
        form.add(Box.createRigidArea(new Dimension(0,20)));
        
        // Login As
        form.add(fieldLabel("Login As"));
        form.add(Box.createRigidArea(new Dimension(0,8)));
        roleBox = new JComboBox<>(new String[]{"Student","Faculty","Admin"});
        roleBox.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        roleBox.setBackground(Color.WHITE);
        roleBox.setMaximumSize(new Dimension(340, 48));
        roleBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        roleBox.setBorder(new CompoundBorder(
            new LineBorder(new Color(180,190,200), 2, true),
            new EmptyBorder(7, 12, 7, 12)));
        form.add(roleBox);
        form.add(Box.createRigidArea(new Dimension(0,30)));
        
        // Buttons
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        btns.setBackground(Color.WHITE);
        btns.setAlignmentX(Component.LEFT_ALIGNMENT);
        btns.setMaximumSize(new Dimension(340, 52));
        
        JButton loginBtn = styledBtn("  Login  ", new Color(41,128,185));
        JButton clearBtn = styledBtn("Clear", new Color(149,165,166));
        btns.add(loginBtn);
        btns.add(clearBtn);
        form.add(btns);
        
        right.add(form);
        
        add(left, BorderLayout.WEST);
        add(right, BorderLayout.CENTER);
        
        // Actions
        loginBtn.addActionListener(e -> handleLogin());
        clearBtn.addActionListener(e -> {
            usernameField.setText("");
            passwordField.setText("");
            roleBox.setSelectedIndex(0);
        });
        
        passwordField.addActionListener(e -> handleLogin());
        
        setVisible(true);
    }
    
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role = roleBox.getSelectedItem().toString();
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password", 
                "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (AuthHelper.authenticate(username, password, role)) {
            dispose();
            switch (role) {
                case "Admin":
                    new AdminDashboard();
                    break;
                case "Faculty":
                    new FacultyDashboard(username);
                    break;
                case "Student":
                    new StudentDashboard(username);
                    break;
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials", 
                "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private static JLabel lbl(String text, String family, int style, int size, Color fg) {
        JLabel l = new JLabel(text);
        l.setFont(new Font(family, style, size));
        l.setForeground(fg);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }
    
    private static JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 15));
        l.setForeground(new Color(44, 62, 80));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }
    
    private void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setMaximumSize(new Dimension(340, 48));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setBackground(new Color(247,249,251));
        field.setBorder(new CompoundBorder(
            new LineBorder(new Color(180,190,200), 2, true),
            new EmptyBorder(7, 12, 7, 12)));
        
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                field.setBackground(Color.WHITE);
                field.setBorder(new CompoundBorder(
                    new LineBorder(new Color(41,128,185), 2, true),
                    new EmptyBorder(7, 12, 7, 12)));
            }
            public void focusLost(FocusEvent e) {
                field.setBackground(new Color(247,249,251));
                field.setBorder(new CompoundBorder(
                    new LineBorder(new Color(180,190,200), 2, true),
                    new EmptyBorder(7, 12, 7, 12)));
            }
        });
    }
    
    private JButton styledBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setPreferredSize(new Dimension(120, 48));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bg.darker());
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });
        
        return btn;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}
