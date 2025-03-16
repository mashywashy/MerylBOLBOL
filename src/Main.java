import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // Initialize the evaluator with the curriculum file path
        StudentEvaluator evaluator = new StudentEvaluator("bsit", "src/curriculum.xml");

        // Example: Get default recommendations for 1st year, 1st semester
        List<Subject> defaultRecommendations = evaluator.getRecommendedSubjects();
        System.out.println("Default Recommendations:");
        defaultRecommendations.forEach(System.out::println);

        // Example: Get recommendations based on taken subjects, year, and semester
        Map<String, Boolean> subjectsTaken = new HashMap<>();
        subjectsTaken.put("eng100", true); // Student has passed eng100
        subjectsTaken.put("math100", true); // Student has passed math100

        List<Subject> recommendations = evaluator.getRecommendedSubjects(subjectsTaken, 1, 2);
        System.out.println("\nRecommendations for 1st Year, 2nd Semester:");
        recommendations.forEach(System.out::println);
    }
}