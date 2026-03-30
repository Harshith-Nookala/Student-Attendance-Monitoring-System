package attendance;
import java.sql.Connection;
import java.util.*;

public class AuthHelper {
    
    private static Map<String, String> adminCreds = new HashMap<>();
    private static Map<String, String> facultyCreds = new HashMap<>();
    private static Map<String, String> studentCreds = new HashMap<>();
    
    static {
        // Admin credentials
        adminCreds.put("admin", "admin123");
        
        // Faculty credentials
        facultyCreds.put("rajesh", "rajesh123");
        facultyCreds.put("priya", "priya123");
        facultyCreds.put("amit", "amit123");
        
        // Student credentials
        studentCreds.put("rahul", "rahul123");
        studentCreds.put("21A91A0501", "rahul123");
        studentCreds.put("sneha", "sneha123");
        studentCreds.put("21A91A0504", "sneha123");
        studentCreds.put("student1", "student123");
        studentCreds.put("21A91A0505", "student123");
    }
    
    public static boolean authenticate(String username, String password, String role) {
        String table = role.equals("Admin")   ? "admin_users" :
                       role.equals("Faculty") ? "faculty" : "students";
        String sql = "SELECT * FROM " + table + " WHERE username=? AND password=?";
        try (Connection con = DBConnection.getConnection();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            java.sql.ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.out.println("Auth error: " + e.getMessage());
            return false;
        }
    }
    
    public static class FacultyInfo {
        public String name;
        public String department;
        public String[] subjects;
        
        public FacultyInfo(String name, String department, String... subjects) {
            this.name = name;
            this.department = department;
            this.subjects = subjects;
        }
    }
    
    public static FacultyInfo getFacultyInfo(String username) {
        String sql = "SELECT name, department, subjects FROM faculty WHERE username = ?";
        try (Connection con = DBConnection.getConnection();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username.toLowerCase());
            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String name     = rs.getString("name");
                String dept     = rs.getString("department");
                String subjects = rs.getString("subjects");
                String[] subArr = subjects != null
                    ? subjects.split(",\\s*")
                    : new String[]{"Subject"};
                return new FacultyInfo(name, dept, subArr);
            }
        } catch (Exception e) {
            System.out.println("Faculty info error: " + e.getMessage());
        }
        return new FacultyInfo("Faculty Member", "CSE", "Subject");
    }
}
