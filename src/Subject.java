import java.util.ArrayList;
import java.util.List;

public class Subject {
    private String code;
    private int units;
    private String year;
    private String semester;
    private List<String> prerequisites;

    // Original constructor with all parameters
    public Subject(String code, int units, String year, String semester) {
        this.code = code;
        this.units = units;
        this.year = year;
        this.semester = semester;
        this.prerequisites = new ArrayList<>();
    }

    // New constructor that only requires code and units
    public Subject(String code, int units) {
        this.code = code;
        this.units = units;
        this.year = "";
        this.semester = "";
        this.prerequisites = new ArrayList<>();
    }

    public String getCode() { return code; }
    public int getUnits() { return units; }
    public String getYear() { return year; }
    public String getSemester() { return semester; }
    public List<String> getPrerequisites() { return prerequisites; }
    public void addPrerequisite(String prereq) { prerequisites.add(prereq); }

    // Added setter for prerequisites
    public void setPrerequisites(List<String> prerequisites) {
        this.prerequisites = prerequisites;
    }
}