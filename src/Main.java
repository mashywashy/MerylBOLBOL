import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        int totalUnits = 0;
        StudentEval se = new StudentEval("bsit");
        Map<String, Boolean> subMap = new HashMap<>();


        subMap.put("eng100", true);
        subMap.put("socio102", true);
        subMap.put("math100", true);
        subMap.put("psych101", true);
        subMap.put("cc-intcom11", true);
        subMap.put("it-webdev11", true);
        subMap.put("pe101", true);



        List<Subject> subs = se.getRecommendedSubjects(subMap, 1, 2);
        for(Subject sub : subs) {
            System.out.println(sub.getCode() + " " + sub.getUnits());
            totalUnits += sub.getUnits();
        }

        System.out.println("Total Units: " + totalUnits);
    }
}
