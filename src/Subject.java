import java.util.ArrayList;
import java.util.List;

public class Subject {
    private String code;
    private int units;
    private List<String> prerequisites;

    public Subject(String code, int units) {
        this.code = code;
        this.units = units;
        this.prerequisites = new ArrayList<>();
    }

    public String getCode() {
        return code;
    }

    public int getUnits() {
        return units;
    }

    public List<String> getPrerequisites() {
        return prerequisites;
    }

    public void addPrerequisite(String prerequisite) {
        prerequisites.add(prerequisite);
    }

    @Override
    public String toString() {
        return code + " (" + units + " units)";
    }
}