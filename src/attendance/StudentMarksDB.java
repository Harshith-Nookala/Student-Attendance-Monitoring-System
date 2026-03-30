package attendance;
import java.util.*;

/**
 * Shared static database for student marks.
 * Both UpdateMarksFrame and ResultCalculationFrame read/write here.
 */
public class StudentMarksDB {

    // Columns: [0]SubjectCode [1]SubjectName [2]MaxMarks [3]MarksObtained [4]Grade [5]GradePoints [6]Status
    private static final Map<String, Object[][]> MARKS_DB = new LinkedHashMap<>();

    static {
        MARKS_DB.put("21A91A0501", new Object[][]{
            {"CS301","Data Structures",      100, 82, "B+", 8.0, "Pass"},
            {"CS302","Operating Systems",    100, 75, "B",  7.0, "Pass"},
            {"CS303","Database Management",  100, 90, "A+",10.0, "Pass"},
            {"CS304","Computer Networks",    100, 68, "B-", 6.5, "Pass"},
            {"CS305","Software Engineering", 100, 55, "C",  5.5, "Pass"},
        });
        MARKS_DB.put("21A91A0502", new Object[][]{
            {"CS301","Data Structures",      100, 91, "A+",10.0, "Pass"},
            {"CS302","Operating Systems",    100, 88, "A",  9.0, "Pass"},
            {"CS303","Database Management",  100, 94, "A+",10.0, "Pass"},
            {"CS304","Computer Networks",    100, 85, "A",  9.0, "Pass"},
            {"CS305","Software Engineering", 100, 79, "B+", 8.0, "Pass"},
        });
        MARKS_DB.put("21A91A0503", new Object[][]{
            {"CS301","Data Structures",      100, 70, "B+", 8.0, "Pass"},
            {"CS302","Operating Systems",    100, 62, "B",  7.0, "Pass"},
            {"CS303","Database Management",  100, 78, "B+", 8.0, "Pass"},
            {"CS304","Computer Networks",    100, 55, "C",  5.5, "Pass"},
            {"CS305","Software Engineering", 100, 45, "D",  5.0, "Pass"},
        });
        MARKS_DB.put("21A91A0504", new Object[][]{
            {"CS301","Data Structures",      100, 88, "A",  9.0, "Pass"},
            {"CS302","Operating Systems",    100, 92, "A+",10.0, "Pass"},
            {"CS303","Database Management",  100, 85, "A",  9.0, "Pass"},
            {"CS304","Computer Networks",    100, 79, "B+", 8.0, "Pass"},
            {"CS305","Software Engineering", 100, 91, "A+",10.0, "Pass"},
        });
        MARKS_DB.put("21A91A0505", new Object[][]{
            {"CS301","Data Structures",      100, 80, "B+", 8.0, "Pass"},
            {"CS302","Operating Systems",    100, 74, "B",  7.0, "Pass"},
            {"CS303","Database Management",  100, 83, "A",  9.0, "Pass"},
            {"CS304","Computer Networks",    100, 69, "B-", 6.5, "Pass"},
            {"CS305","Software Engineering", 100, 77, "B+", 8.0, "Pass"},
        });
        MARKS_DB.put("20A95A0101", new Object[][]{
            {"EC301","Signals & Systems",    100, 84, "A",  9.0, "Pass"},
            {"EC302","Digital Electronics",  100, 77, "B+", 8.0, "Pass"},
            {"EC303","Microprocessors",      100, 90, "A+",10.0, "Pass"},
            {"EC304","Communication Theory", 100, 72, "B",  7.0, "Pass"},
            {"EC305","VLSI Design",          100, 65, "B-", 6.5, "Pass"},
        });
        MARKS_DB.put("20A95A0102", new Object[][]{
            {"EC301","Signals & Systems",    100, 79, "B+", 8.0, "Pass"},
            {"EC302","Digital Electronics",  100, 68, "B-", 6.5, "Pass"},
            {"EC303","Microprocessors",      100, 82, "A",  9.0, "Pass"},
            {"EC304","Communication Theory", 100, 75, "B",  7.0, "Pass"},
            {"EC305","VLSI Design",          100, 88, "A",  9.0, "Pass"},
        });
    }

    public static final Map<String, String> STUDENT_NAMES = new LinkedHashMap<>();
    public static final Map<String, String> STUDENT_DEPT  = new LinkedHashMap<>();
    static {
        STUDENT_NAMES.put("21A91A0501","Rahul Kumar");  STUDENT_DEPT.put("21A91A0501","CSE");
        STUDENT_NAMES.put("21A91A0502","Priya Sharma"); STUDENT_DEPT.put("21A91A0502","CSE");
        STUDENT_NAMES.put("21A91A0503","Amit Patel");   STUDENT_DEPT.put("21A91A0503","CSE");
        STUDENT_NAMES.put("21A91A0504","Sneha Reddy");  STUDENT_DEPT.put("21A91A0504","CSE");
        STUDENT_NAMES.put("21A91A0505","Vikram Singh"); STUDENT_DEPT.put("21A91A0505","CSE");
        STUDENT_NAMES.put("20A95A0101","Arjun Reddy");  STUDENT_DEPT.put("20A95A0101","ECE");
        STUDENT_NAMES.put("20A95A0102","Sita Devi");    STUDENT_DEPT.put("20A95A0102","ECE");
    }

    /** Get a deep copy of a student's marks rows. Returns null if not found. */
    public static Object[][] getMarks(String roll) {
        Object[][] data = MARKS_DB.get(roll.toUpperCase());
        if (data == null) return null;
        Object[][] copy = new Object[data.length][];
        for (int i = 0; i < data.length; i++) copy[i] = Arrays.copyOf(data[i], data[i].length);
        return copy;
    }

    /** Save updated marks back into the database. */
    public static void saveMarks(String roll, Object[][] rows) {
        MARKS_DB.put(roll.toUpperCase(), rows);
    }

    public static boolean exists(String roll) {
        return MARKS_DB.containsKey(roll.toUpperCase());
    }

    // ── Grade helpers (shared) ────────────────────────────────────
    public static String marksToGrade(int m) {
        if (m >= 90) return "A+";
        if (m >= 80) return "A";
        if (m >= 70) return "B+";
        if (m >= 60) return "B";
        if (m >= 55) return "B-";
        if (m >= 50) return "C+";
        if (m >= 45) return "C";
        if (m >= 40) return "D";
        return "F";
    }

    public static double gradeToPoint(String g) {
        switch (g) {
            case "A+": return 10.0;
            case "A":  return 9.0;
            case "B+": return 8.0;
            case "B":  return 7.0;
            case "B-": return 6.5;
            case "C+": return 6.0;
            case "C":  return 5.5;
            case "D":  return 5.0;
            default:   return 0.0;
        }
    }

    public static double computeCgpa(Object[][] rows) {
        if (rows == null || rows.length == 0) return 0.0;
        double total = 0;
        for (Object[] r : rows) total += gradeToPoint(r[4].toString());
        return total / rows.length;
    }
}
