package attendance;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class TableUtil {
    
    public static void applyHeader(JTable table, String[] columnNames, Color accentColor) {
        for (int i = 0; i < columnNames.length; i++) {
            final int col = i;
            final boolean isLast = (i == columnNames.length - 1);
            
            table.getColumnModel().getColumn(i).setHeaderRenderer(
                new TableCellRenderer() {
                    public Component getTableCellRendererComponent(JTable t, Object value,
                            boolean isSelected, boolean hasFocus, int row, int column) {
                        
                        JPanel panel = new JPanel(new GridBagLayout());
                        panel.setOpaque(true);
                        panel.setBackground(new Color(28, 40, 51));
                        panel.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 0, 4, isLast ? 0 : 1, accentColor),
                            BorderFactory.createEmptyBorder(6, 8, 6, 8)
                        ));
                        
                        JLabel label = new JLabel(columnNames[col], SwingConstants.CENTER);
                        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
                        label.setForeground(Color.WHITE);
                        
                        panel.add(label);
                        return panel;
                    }
                }
            );
        }
        
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        table.getTableHeader().setPreferredSize(new Dimension(0, 50));
    }
    
    public static void applyRowStripes(JTable table, Color evenColor, Color oddColor) {
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? evenColor : oddColor);
                }
                
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return this;
            }
        });
    }
}
