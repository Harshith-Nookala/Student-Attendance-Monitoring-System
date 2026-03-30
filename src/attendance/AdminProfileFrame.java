package attendance;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Single frame for both viewing AND updating Admin profile.
 * View-only by default; "Update Profile" button enables editing.
 */
public class AdminProfileFrame extends JFrame {

    private JFrame parentFrame;
    private boolean editMode = false;

    private JTextField nameField, emailField, phoneField;
    private JButton updateBtn, saveBtn, cancelBtn;

    private String origName, origEmail, origPhone;
    private static final Color ACCENT = new Color(41, 128, 185);

    public AdminProfileFrame(JFrame parent) {
        this.parentFrame = parent;
        setTitle("My Profile — Admin");
        setSize(680, 640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);

        if (parentFrame != null) parentFrame.setVisible(false);
        setVisible(true);
    }

    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(ACCENT);
        h.setPreferredSize(new Dimension(0, 100));

        JButton back = hdrBtn("← Back", ACCENT.darker());
        back.addActionListener(e -> dispose());
        JPanel bw = new JPanel(new FlowLayout(FlowLayout.LEFT,15,14));
        bw.setOpaque(false); bw.add(back);

        JPanel centre = new JPanel();
        centre.setOpaque(false);
        centre.setLayout(new BoxLayout(centre, BoxLayout.Y_AXIS));
        centre.setBorder(BorderFactory.createEmptyBorder(14,0,12,0));

        JLabel icon = new JLabel("👨‍💼");
        icon.setFont(new Font("Segoe UI Emoji",Font.PLAIN,40));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLbl = new JLabel("System Administrator");
        nameLbl.setFont(new Font("Segoe UI",Font.BOLD,20));
        nameLbl.setForeground(Color.WHITE);
        nameLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel roleLbl = new JLabel("Role: Administrator  •  ADM-001");
        roleLbl.setFont(new Font("Segoe UI",Font.PLAIN,13));
        roleLbl.setForeground(new Color(180,210,240));
        roleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        centre.add(icon);
        centre.add(Box.createRigidArea(new Dimension(0,4)));
        centre.add(nameLbl);
        centre.add(Box.createRigidArea(new Dimension(0,3)));
        centre.add(roleLbl);

        h.add(bw, BorderLayout.WEST);
        h.add(centre, BorderLayout.CENTER);
        return h;
    }

    private JPanel buildContent() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(Color.WHITE);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(28,48,28,48));

        GridBagConstraints g = new GridBagConstraints();
        g.fill=GridBagConstraints.HORIZONTAL; g.weightx=1; g.gridx=0;
        g.insets = new Insets(7,0,7,0);
        int row = 0;

        // Notice banner
        g.gridy = row++;
        form.add(buildNotice(), g);

        g.gridy = row++; g.insets = new Insets(16,0,6,0);
        form.add(secHdr("Profile Information", ACCENT), g);
        g.insets = new Insets(2,0,6,0);
        g.gridy = row++; form.add(sep(), g);
        g.insets = new Insets(7,0,7,0);

        // Employee ID (always read-only)
        g.gridy = row++; form.add(lbl("Employee ID"), g);
        g.gridy = row++; form.add(readOnly("ADM-001"), g);

        // Full Name
        g.gridy = row++; form.add(lbl("Full Name"), g);
        g.gridy = row++;
        nameField = editField("System Administrator");
        origName  = nameField.getText();
        form.add(nameField, g);

        // Email
        g.gridy = row++; form.add(lbl("Email Address"), g);
        g.gridy = row++;
        emailField = editField("admin@college.edu");
        origEmail  = emailField.getText();
        form.add(emailField, g);

        // Phone
        g.gridy = row++; form.add(lbl("Phone Number"), g);
        g.gridy = row++;
        phoneField = editField("+91 9000000001");
        origPhone  = phoneField.getText();
        form.add(phoneField, g);

        // Buttons
        g.gridy = row; g.insets = new Insets(24,0,10,0);
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER,14,0));
        btns.setOpaque(false);
        updateBtn = actionBtn("✏  Update Profile",  ACCENT);
        saveBtn   = actionBtn("💾  Save Changes",    new Color(46,204,113));
        cancelBtn = actionBtn("✕  Cancel",           new Color(149,165,166));
        saveBtn.setVisible(false); cancelBtn.setVisible(false);
        btns.add(updateBtn); btns.add(saveBtn); btns.add(cancelBtn);
        form.add(btns, g);

        setFieldsEditable(false);

        updateBtn.addActionListener(e -> enterEdit());
        saveBtn.addActionListener(e -> saveChanges());
        cancelBtn.addActionListener(e -> cancelEdit());

        outer.add(form, BorderLayout.CENTER);
        return outer;
    }

    private void enterEdit() {
        origName=nameField.getText(); origEmail=emailField.getText(); origPhone=phoneField.getText();
        setFieldsEditable(true);
        updateBtn.setVisible(false); saveBtn.setVisible(true); cancelBtn.setVisible(true);
        nameField.requestFocus();
    }
    private void saveChanges() {
        if (nameField.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this,"Name cannot be empty.","Validation",JOptionPane.WARNING_MESSAGE); return; }
        setFieldsEditable(false);
        updateBtn.setVisible(true); saveBtn.setVisible(false); cancelBtn.setVisible(false);
        editMode=false;
        JOptionPane.showMessageDialog(this,"Profile updated successfully!","Saved ✓",JOptionPane.INFORMATION_MESSAGE);
    }
    private void cancelEdit() {
        nameField.setText(origName); emailField.setText(origEmail); phoneField.setText(origPhone);
        setFieldsEditable(false);
        updateBtn.setVisible(true); saveBtn.setVisible(false); cancelBtn.setVisible(false);
    }
    private void setFieldsEditable(boolean e) {
        Color bg = e ? Color.WHITE : new Color(245,247,249);
        Color bd = e ? new Color(41,128,185) : new Color(180,190,200);
        int bw = e ? 2 : 1;
        for (JTextField f : new JTextField[]{nameField,emailField,phoneField}) {
            f.setEditable(e); f.setBackground(bg);
            f.setBorder(new CompoundBorder(new LineBorder(bd,bw,true),BorderFactory.createEmptyBorder(7,12,7,12)));
        }
    }

    private JPanel buildNotice() {
        JPanel p=new JPanel(new BorderLayout(10,0));
        p.setBackground(new Color(232,244,255));
        p.setBorder(new CompoundBorder(new LineBorder(new Color(174,214,241),1,true),BorderFactory.createEmptyBorder(10,14,10,14)));
        JLabel icon=new JLabel("🔒"); icon.setFont(new Font("Segoe UI Emoji",Font.PLAIN,18));
        JLabel msg=new JLabel("<html><b>View-only mode.</b> Click <i>Update Profile</i> to edit.</html>");
        msg.setFont(new Font("Segoe UI",Font.PLAIN,13)); msg.setForeground(new Color(30,90,150));
        p.add(icon,BorderLayout.WEST); p.add(msg,BorderLayout.CENTER); return p;
    }

    private static JLabel secHdr(String t,Color c){JLabel l=new JLabel(t);l.setFont(new Font("Segoe UI",Font.BOLD,15));l.setForeground(c);return l;}
    private static JSeparator sep(){return new JSeparator();}
    private static JLabel lbl(String t){JLabel l=new JLabel(t);l.setFont(new Font("Segoe UI",Font.BOLD,13));l.setForeground(new Color(52,73,94));return l;}
    private static JTextField readOnly(String v){JTextField f=new JTextField(v);f.setFont(new Font("Segoe UI",Font.PLAIN,14));f.setEditable(false);f.setBackground(new Color(230,233,235));f.setPreferredSize(new Dimension(0,42));f.setBorder(new CompoundBorder(new LineBorder(new Color(190,195,200),1,true),BorderFactory.createEmptyBorder(7,12,7,12)));return f;}
    private static JTextField editField(String v){JTextField f=new JTextField(v);f.setFont(new Font("Segoe UI",Font.PLAIN,14));f.setPreferredSize(new Dimension(0,42));f.setEditable(false);f.setBackground(new Color(245,247,249));f.setBorder(new CompoundBorder(new LineBorder(new Color(180,190,200),1,true),BorderFactory.createEmptyBorder(7,12,7,12)));return f;}
    private static JButton actionBtn(String t,Color bg){JButton b=new JButton(t);b.setFont(new Font("Segoe UI",Font.BOLD,14));b.setBackground(bg);b.setForeground(Color.WHITE);b.setFocusPainted(false);b.setBorderPainted(false);b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));b.setPreferredSize(new Dimension(180,42));b.addMouseListener(new MouseAdapter(){public void mouseEntered(MouseEvent e){b.setBackground(bg.darker());}public void mouseExited(MouseEvent e){b.setBackground(bg);}});return b;}
    private static JButton hdrBtn(String t,Color bg){JButton b=new JButton(t);b.setFont(new Font("Segoe UI",Font.BOLD,14));b.setBackground(bg);b.setForeground(Color.WHITE);b.setFocusPainted(false);b.setBorderPainted(false);b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));b.setPreferredSize(new Dimension(110,34));return b;}

    @Override public void dispose(){super.dispose();if(parentFrame!=null)parentFrame.setVisible(true);}
}
