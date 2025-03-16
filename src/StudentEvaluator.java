import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class StudentEvaluator {
    private String program;
    private Document curriculum;

    public StudentEvaluator(String program, String curriculumFilePath) {
        this.program = program;
        this.curriculum = loadCurriculum(curriculumFilePath);
    }

    // Load the curriculum from the XML file
    private Document loadCurriculum(String filePath) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new File(filePath));
        } catch (Exception e) {
            System.err.println("Error loading curriculum: " + e.getMessage());
            return null;
        }
    }

    // Get default recommendations for 1st year, 1st semester
    public List<Subject> getRecommendedSubjects() {
        return getSubjectsForYearAndSemester(1, 1);
    }

    // Get recommendations based on taken subjects, year, and semester
    public List<Subject> getRecommendedSubjects(Map<String, Boolean> subjectsTaken, int year, int semester) {
        List<Subject> allSubjects = getSubjectsForYearAndSemester(year, semester);
        return allSubjects.stream()
                .filter(subject -> canTakeSubject(subject, subjectsTaken))
                .collect(Collectors.toList());
    }

    // Check if a subject can be taken based on prerequisites
    private boolean canTakeSubject(Subject subject, Map<String, Boolean> subjectsTaken) {
        for (String prerequisite : subject.getPrerequisites()) {
            if (!subjectsTaken.containsKey(prerequisite)) {
                return false; // Prerequisite not taken
            }
            if (!subjectsTaken.get(prerequisite)) {
                return false; // Prerequisite failed
            }
        }
        return true; // All prerequisites satisfied
    }

    // Get subjects for a specific year and semester
    private List<Subject> getSubjectsForYearAndSemester(int year, int semester) {
        List<Subject> subjects = new ArrayList<>();
        if (curriculum == null) {
            return subjects;
        }

        String yearTag = getYearTag(year);
        String semTag = getSemesterTag(semester);

        NodeList subjectNodes = curriculum.getElementsByTagName("subject");
        for (int i = 0; i < subjectNodes.getLength(); i++) {
            Element subjectElement = (Element) subjectNodes.item(i);
            String subjectCode = subjectElement.getAttribute("subjectCode");
            int units = Integer.parseInt(subjectElement.getAttribute("units"));
            Subject subject = new Subject(subjectCode, units);

            // Add prerequisites
            NodeList prerequisiteNodes = subjectElement.getElementsByTagName("prerequisite");
            for (int j = 0; j < prerequisiteNodes.getLength(); j++) {
                String prerequisite = prerequisiteNodes.item(j).getTextContent();
                subject.addPrerequisite(prerequisite);
            }

            subjects.add(subject);
        }

        return subjects;
    }

    // Helper method to get the year tag (e.g., "firstYear", "secondYear")
    private String getYearTag(int year) {
        switch (year) {
            case 1: return "firstYear";
            case 2: return "secondYear";
            case 3: return "thirdYear";
            case 4: return "fourthYear";
            default: throw new IllegalArgumentException("Invalid year: " + year);
        }
    }

    // Helper method to get the semester tag (e.g., "firstSem", "secondSem")
    private String getSemesterTag(int semester) {
        switch (semester) {
            case 1: return "firstSem";
            case 2: return "secondSem";
            default: throw new IllegalArgumentException("Invalid semester: " + semester);
        }
    }
}