package attendance;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;

public class AdminEditStudentFrame extends JFrame {

    private JFrame parentFrame;
    private JComboBox<String> deptBox, yearBox, sectionBox;
    private JTextField rollSearchField;
    private JTextField nameField, emailField, phoneField, dobField, guardianField, guardianPhoneField;
    private JTextArea addressArea;
    private JComboBox<String> genderBox;
    private JTextField rollDispField, admYearField;
    private JButton loadBtn, saveBtn, cancelBtn;
    private JPanel formPanel;

    private static final Color ACCENT = new Color(241, 196, 15);
    private static final Color DARK   = new Color(44,62,80);

    // Original values for cancel
    private String[] origValues;

    public AdminEditStudentFrame(JFrame parent) {
        this.parentFrame = parent;
        setTitle("Edit Student Profile");
        setSize(820, 760);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        add(buildHeader(),   BorderLayout.NORTH);
        add(buildContent(),  BorderLayout.CENTER);

        if (parentFrame != null) parentFrame.setVisible(false);
        setVisible(true);
    }

    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(DARK);
        h.setPreferredSize(new Dimension(0, 60));

        JButton back = hdrBtn("← Back", new Color(60,80,100));
        back.addActionListener(e -> dispose());
        JPanel bw = new JPanel(new FlowLayout(FlowLayout.LEFT,14,13));
        bw.setOpaque(false); bw.add(back);

        JLabel t = new JLabel("✏  Edit Student Profile");
        t.setFont(new Font("Segoe UI",Font.BOLD,20));
        t.setForeground(Color.WHITE);
        JPanel tw = new JPanel(new FlowLayout(FlowLayout.CENTER,0,15));
        tw.setOpaque(false); tw.add(t);

        h.add(bw, BorderLayout.WEST);
        h.add(tw, BorderLayout.CENTER);
        return h;
    }

    private JPanel buildContent() {
        JPanel outer = new JPanel(new BorderLayout(0,0));
        outer.setBackground(Color.WHITE);

        // ── Search strip ─────────────────────────────────────────
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT,12,10));
        searchBar.setBackground(new Color(248,249,250));
        searchBar.setBorder(BorderFactory.createMatteBorder(0,0,1,0,new Color(220,220,220)));

        searchBar.add(boldLbl("Dept:"));
        deptBox = cmb("CSE","ECE","MECH","CIVIL","EEE","IT");
        searchBar.add(deptBox);

        searchBar.add(boldLbl("Year:"));
        yearBox = cmb("1","2","3","4");
        searchBar.add(yearBox);

        searchBar.add(boldLbl("Section:"));
        sectionBox = cmb("A","B","C","D");
        searchBar.add(sectionBox);

        searchBar.add(boldLbl("Roll No:"));
        rollSearchField = new JTextField(14);
        rollSearchField.setFont(new Font("Segoe UI",Font.PLAIN,14));
        rollSearchField.setPreferredSize(new Dimension(150,32));
        rollSearchField.setBorder(new CompoundBorder(new LineBorder(new Color(180,190,200),1,true),BorderFactory.createEmptyBorder(4,10,4,10)));
        searchBar.add(rollSearchField);

        loadBtn = fBtn("🔍 Load Student", ACCENT);
        searchBar.add(loadBtn);

        // ── Form (initially hidden) ───────────────────────────────
        formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20,40,20,40));
        formPanel.setVisible(false);

        GridBagConstraints g = new GridBagConstraints();
        g.fill=GridBagConstraints.HORIZONTAL; g.weightx=1; g.gridx=0;
        g.insets=new Insets(6,0,6,0);
        int row=0;

        // ── Personal ─────────────────────────────────────────────
        g.gridy=row++; g.insets=new Insets(4,0,6,0);
        formPanel.add(secHdr("Personal Information",ACCENT),g);
        g.insets=new Insets(2,0,6,0);
        g.gridy=row++; formPanel.add(sep(),g);
        g.insets=new Insets(6,0,6,0);

        g.gridy=row++; formPanel.add(twoLbl("Full Name *","Email Address *"),g);
        g.gridy=row++;
        nameField  = eField(""); emailField = eField("");
        formPanel.add(twoFld(nameField,emailField),g);

        g.gridy=row++; formPanel.add(twoLbl("Phone Number","Date of Birth"),g);
        g.gridy=row++;
        phoneField=eField(""); dobField=eField("");
        formPanel.add(twoFld(phoneField,dobField),g);

        g.gridy=row++; formPanel.add(twoLbl("Guardian Name","Guardian Phone"),g);
        g.gridy=row++;
        guardianField=eField(""); guardianPhoneField=eField("");
        formPanel.add(twoFld(guardianField,guardianPhoneField),g);

        g.gridy=row++; formPanel.add(lblFor("Gender"),g);
        g.gridy=row++;
        genderBox=new JComboBox<>(new String[]{"Male","Female","Other"});
        genderBox.setFont(new Font("Segoe UI",Font.PLAIN,14));
        genderBox.setBackground(Color.WHITE);
        genderBox.setPreferredSize(new Dimension(0,42));
        JPanel genderWrap=new JPanel(new GridLayout(1,2,14,0)); genderWrap.setOpaque(false);
        genderWrap.add(genderBox); genderWrap.add(new JLabel());
        formPanel.add(genderWrap,g);

        g.gridy=row++; formPanel.add(lblFor("Address"),g);
        g.gridy=row++;
        addressArea=new JTextArea(3,20);
        addressArea.setFont(new Font("Segoe UI",Font.PLAIN,14));
        addressArea.setLineWrap(true); addressArea.setWrapStyleWord(true);
        addressArea.setBorder(new CompoundBorder(new LineBorder(new Color(180,190,200),1,true),BorderFactory.createEmptyBorder(8,10,8,10)));
        JScrollPane addrScroll=new JScrollPane(addressArea);
        addrScroll.setPreferredSize(new Dimension(0,80));
        addrScroll.setBorder(BorderFactory.createEmptyBorder());
        formPanel.add(addrScroll,g);

        // ── Academic ─────────────────────────────────────────────
        g.gridy=row++; g.insets=new Insets(14,0,6,0);
        formPanel.add(secHdr("Academic Information (Read-Only)",new Color(100,100,100)),g);
        g.insets=new Insets(2,0,6,0);
        g.gridy=row++; formPanel.add(sep(),g);
        g.insets=new Insets(6,0,6,0);

        g.gridy=row++; formPanel.add(twoLbl("Roll Number","Admission Year"),g);
        g.gridy=row++;
        rollDispField=rField(""); admYearField=rField("");
        formPanel.add(twoFld(rollDispField,admYearField),g);

        // Buttons
        g.gridy=row; g.insets=new Insets(20,0,10,0);
        JPanel btns=new JPanel(new FlowLayout(FlowLayout.CENTER,14,0));
        btns.setOpaque(false);
        saveBtn  =fBtn("💾  Save Changes",  new Color(46,204,113));
        cancelBtn=fBtn("✕  Cancel",         new Color(149,165,166));
        saveBtn.addActionListener(e2->saveStudent());
        cancelBtn.addActionListener(e2->cancelEdit());
        btns.add(saveBtn); btns.add(cancelBtn);
        formPanel.add(btns,g);

        // Placeholder when no student loaded
        JLabel placeholder=new JLabel("Enter Dept, Year, Section and Roll Number above, then click 🔍 Load Student",SwingConstants.CENTER);
        placeholder.setFont(new Font("Segoe UI",Font.PLAIN,15));
        placeholder.setForeground(new Color(160,160,160));

        JScrollPane formScroll=new JScrollPane(formPanel);
        formScroll.setBorder(null);
        formScroll.getVerticalScrollBar().setUnitIncrement(16);

        // Centre panel switches between placeholder and form
        JPanel centre=new JPanel(new BorderLayout());
        centre.setBackground(Color.WHITE);
        centre.add(placeholder,BorderLayout.CENTER);

        // Override loadStudent to swap panels
        loadBtn.addActionListener(e->{
            if(loadStudentData()){
                formPanel.setVisible(true);
                centre.remove(placeholder);
                centre.add(formScroll,BorderLayout.CENTER);
                centre.revalidate(); centre.repaint();
            }
        });

        outer.add(searchBar, BorderLayout.NORTH);
        outer.add(centre,    BorderLayout.CENTER);
        return outer;
    }

    private boolean loadStudentData() {
        String roll = rollSearchField.getText().trim();
        if (roll.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Roll Number.", "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        String sql = "SELECT * FROM students WHERE roll_no = ?";
        try (Connection con = DBConnection.getConnection();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, roll);
            java.sql.ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                nameField.setText(rs.getString("name") != null ? rs.getString("name") : "");
                emailField.setText(rs.getString("email") != null ? rs.getString("email") : "");
                phoneField.setText(rs.getString("phone") != null ? rs.getString("phone") : "");
                dobField.setText(rs.getString("dob") != null ? rs.getString("dob") : "");
                guardianField.setText(rs.getString("guardian_name") != null ? rs.getString("guardian_name") : "");
                guardianPhoneField.setText(rs.getString("guardian_phone") != null ? rs.getString("guardian_phone") : "");
                addressArea.setText(rs.getString("address") != null ? rs.getString("address") : "");
                genderBox.setSelectedItem(rs.getString("gender") != null ? rs.getString("gender") : "Male");
                rollDispField.setText(roll);
                admYearField.setText(rs.getString("year") != null ? rs.getString("year") : "");

                origValues = new String[]{
                    nameField.getText(), emailField.getText(), phoneField.getText(),
                    dobField.getText(), guardianField.getText(), guardianPhoneField.getText(),
                    addressArea.getText()
                };
                return true;
            } else {
                JOptionPane.showMessageDialog(this,
                    "No student found with Roll Number: " + roll,
                    "Not Found", JOptionPane.WARNING_MESSAGE);
                return false;
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading student: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    private void saveStudent() {
        if (nameField.getText().trim().isEmpty() || emailField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Email are required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "UPDATE students SET name=?, email=?, phone=?, dob=?, " +
                     "guardian_name=?, guardian_phone=?, address=?, gender=? " +
                     "WHERE roll_no=?";

        try (Connection con = DBConnection.getConnection();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nameField.getText().trim());
            ps.setString(2, emailField.getText().trim());
            ps.setString(3, phoneField.getText().trim());
            ps.setString(4, dobField.getText().trim());
            ps.setString(5, guardianField.getText().trim());
            ps.setString(6, guardianPhoneField.getText().trim());
            ps.setString(7, addressArea.getText().trim());
            ps.setString(8, (String) genderBox.getSelectedItem());
            ps.setString(9, rollDispField.getText().trim());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this,
                    "Student profile updated successfully!\n\nRoll: " + rollDispField.getText() +
                    "\nName: " + nameField.getText(),
                    "Saved", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error updating student: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelEdit() {
        if (origValues!=null) {
            nameField.setText(origValues[0]); emailField.setText(origValues[1]);
            phoneField.setText(origValues[2]); dobField.setText(origValues[3]);
            guardianField.setText(origValues[4]); guardianPhoneField.setText(origValues[5]);
            addressArea.setText(origValues[6]);
        }
    }

    // helpers
    private static JComboBox<String> cmb(String...a){JComboBox<String>c=new JComboBox<>(a);c.setFont(new Font("Segoe UI",Font.PLAIN,13));c.setBackground(Color.WHITE);c.setPreferredSize(new Dimension(88,30));return c;}
    private static JLabel boldLbl(String t){JLabel l=new JLabel(t);l.setFont(new Font("Segoe UI",Font.BOLD,13));l.setForeground(DARK);return l;}
    private static JLabel secHdr(String t,Color c){JLabel l=new JLabel(t);l.setFont(new Font("Segoe UI",Font.BOLD,15));l.setForeground(c);return l;}
    private static JSeparator sep(){return new JSeparator();}
    private static JLabel lblFor(String t){JLabel l=new JLabel(t);l.setFont(new Font("Segoe UI",Font.BOLD,13));l.setForeground(DARK);return l;}
    private static JPanel twoLbl(String a,String b){JPanel p=new JPanel(new GridLayout(1,2,14,0));p.setOpaque(false);p.add(lblFor(a));p.add(lblFor(b));return p;}
    private static JPanel twoFld(JComponent a,JComponent b){JPanel p=new JPanel(new GridLayout(1,2,14,0));p.setOpaque(false);a.setPreferredSize(new Dimension(0,42));b.setPreferredSize(new Dimension(0,42));p.add(a);p.add(b);return p;}
    private static JTextField eField(String v){JTextField f=new JTextField(v);f.setFont(new Font("Segoe UI",Font.PLAIN,14));f.setBackground(new Color(248,249,250));f.setBorder(new CompoundBorder(new LineBorder(new Color(220,225,230),1,true),BorderFactory.createEmptyBorder(6,12,6,12)));f.addFocusListener(new FocusAdapter(){public void focusGained(FocusEvent e){f.setBackground(Color.WHITE);f.setBorder(new CompoundBorder(new LineBorder(new Color(241,196,15),2,true),BorderFactory.createEmptyBorder(6,12,6,12)));}public void focusLost(FocusEvent e){f.setBackground(new Color(248,249,250));f.setBorder(new CompoundBorder(new LineBorder(new Color(220,225,230),1,true),BorderFactory.createEmptyBorder(6,12,6,12)));}});return f;}
    private static JTextField rField(String v){JTextField f=new JTextField(v);f.setFont(new Font("Segoe UI",Font.PLAIN,14));f.setEditable(false);f.setBackground(new Color(230,232,235));f.setBorder(new CompoundBorder(new LineBorder(new Color(190,195,200),1,true),BorderFactory.createEmptyBorder(7,12,7,12)));return f;}
    private static JButton fBtn(String t,Color bg){JButton b=new JButton(t);b.setFont(new Font("Segoe UI",Font.BOLD,13));b.setBackground(bg);b.setForeground(Color.WHITE);b.setFocusPainted(false);b.setBorderPainted(false);b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));b.setPreferredSize(new Dimension(160,32));b.addMouseListener(new MouseAdapter(){public void mouseEntered(MouseEvent e){b.setBackground(bg.darker());}public void mouseExited(MouseEvent e){b.setBackground(bg);}});return b;}
    private static JButton hdrBtn(String t,Color bg){JButton b=new JButton(t);b.setFont(new Font("Segoe UI",Font.BOLD,14));b.setBackground(bg);b.setForeground(Color.WHITE);b.setFocusPainted(false);b.setBorderPainted(false);b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));b.setPreferredSize(new Dimension(110,34));return b;}

    @Override public void dispose(){super.dispose();if(parentFrame!=null)parentFrame.setVisible(true);}
}
