import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.List;
import java.util.*;

// Main Student Form
public class StudentInfoForm extends JFrame {
    private JTextField nameField;
    private JTextField idField;
    private JComboBox<String> yearComboBox;
    private JComboBox<String> semesterComboBox;
    private JTextField subjectsTakenField;
    private JPanel recommendationsPanel;
    private JDialog recommendationsDialog;
    private JPanel subjectHistoryPanel;
    private JScrollPane subjectHistoryScrollPane;
    private List<Subject> allSubjects; // List of all subjects from the curriculum
    private JButton clearFieldsButton;

    // Add this with other field declarations
    private JComboBox<String> programComboBox;

    public StudentInfoForm() {
        // Set up the frame
        setTitle("Student Information");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500); // Increased size for the additional components
        setLocationRelativeTo(null);

        // Load all subjects from the curriculum
        loadAllSubjects();

        // Initialize components
        initComponents();

        // Set up the layout
        setupLayout();

        // Make the frame visible
        setVisible(true);
    }

    private void loadAllSubjects() {
        allSubjects = new ArrayList<>();
        try {
            // Create DOM parser
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Parse the XML file
            Document document = builder.parse(new File("src/curriculum.xml"));

            // Normalize the XML structure
            document.getDocumentElement().normalize();

            // Get all subject elements across all years and semesters
            NodeList subjectList = document.getElementsByTagName("subject");

            // Process each subject element
            for (int i = 0; i < subjectList.getLength(); i++) {
                Element subjectElement = (Element) subjectList.item(i);

                // Get subject code and units
                String subjectCode = subjectElement.getAttribute("subjectCode");
                int units = Integer.parseInt(subjectElement.getAttribute("units"));

                // Create Subject object
                Subject subject = new Subject(subjectCode, units);

                // Check if this subject has prerequisites
                NodeList prerequisitesList = subjectElement.getElementsByTagName("prerequisite");
                if (prerequisitesList.getLength() > 0) {
                    // Create list to store prerequisites
                    List<String> prerequisites = new ArrayList<>();

                    // Extract each prerequisite
                    for (int j = 0; j < prerequisitesList.getLength(); j++) {
                        Element prerequisiteElement = (Element) prerequisitesList.item(j);
                        String prerequisiteCode = prerequisiteElement.getTextContent();
                        prerequisites.add(prerequisiteCode);
                    }

                    // Set prerequisites for the subject
                    subject.setPrerequisites(prerequisites);
                }

                // Add subject to the collection
                allSubjects.add(subject);
            }

            System.out.println("Successfully loaded " + allSubjects.size() + " subjects from XML.");
        } catch (Exception e) {
            System.err.println("Error loading subjects from XML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initComponents() {
        // Text fields for name and ID
        nameField = new JTextField(20);
        idField = new JTextField(20);

        // Add this in initComponents()
        programComboBox = new JComboBox<>(new String[]{"BSIT", "BSIS", "BSCS"});

        // Combo boxes for year and semester
        yearComboBox = new JComboBox<>(new String[]{"1st Year", "2nd Year", "3rd Year", "4th Year"});
        semesterComboBox = new JComboBox<>(new String[]{"1st Semester", "2nd Semester", "Summer"});

        // Add action listeners to year and semester combo boxes
        yearComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateSubjectsTakenField();
                checkForRecommendations();
            }
        });

        semesterComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateSubjectsTakenField();
                checkForRecommendations();
            }
        });

        // Subjects taken field with button to generate input fields
        subjectsTakenField = new JTextField(5);
        subjectsTakenField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateSubjectInputFields();
            }
        });

        // Clear fields button
        clearFieldsButton = new JButton("Clear Fields");
        clearFieldsButton.addActionListener(e -> {
            clearSubjectFields();
        });
        clearFieldsButton.setEnabled(false);

        // Subject history panel (will contain dynamically added subject selection fields)
        subjectHistoryPanel = new JPanel();
        subjectHistoryPanel.setLayout(new BoxLayout(subjectHistoryPanel, BoxLayout.Y_AXIS));

        subjectHistoryScrollPane = new JScrollPane(subjectHistoryPanel);
        subjectHistoryScrollPane.setPreferredSize(new Dimension(550, 150));
        subjectHistoryScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        subjectHistoryScrollPane.setVisible(false); // Initially hidden
        subjectHistoryScrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Subject History",
                TitledBorder.LEFT,
                TitledBorder.TOP
        ));

        // Initialize the field's state based on the default selections
        updateSubjectsTakenField();

        // Create recommendations panel
        recommendationsPanel = new JPanel();
        recommendationsPanel.setLayout(new BoxLayout(recommendationsPanel, BoxLayout.Y_AXIS));

        // Initialize the recommendations dialog
        recommendationsDialog = new JDialog(this, "Recommended Subjects", true);
        recommendationsDialog.setSize(500, 400);
        recommendationsDialog.setLocationRelativeTo(this);
        JPanel dialogPanel = new JPanel(new BorderLayout());
        dialogPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        dialogPanel.add(new JScrollPane(recommendationsPanel), BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> recommendationsDialog.setVisible(false));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        dialogPanel.add(buttonPanel, BorderLayout.SOUTH);

        recommendationsDialog.setContentPane(dialogPanel);
    }

    private void clearSubjectFields() {
        subjectHistoryPanel.removeAll();
        subjectHistoryScrollPane.setVisible(false);
        subjectsTakenField.setText("");
        clearFieldsButton.setEnabled(false);
        subjectHistoryPanel.revalidate();
        subjectHistoryPanel.repaint();
    }

    private void updateSubjectsTakenField() {
        String selectedYear = (String) yearComboBox.getSelectedItem();
        String selectedSemester = (String) semesterComboBox.getSelectedItem();

        // Only disable if both 1st Year AND 1st Semester are selected
        boolean isFirstYearFirstSemester = Objects.equals(selectedYear, "1st Year") &&
                Objects.equals(selectedSemester, "1st Semester");

        // Enable/disable the subjects taken field accordingly
        subjectsTakenField.setEnabled(!isFirstYearFirstSemester);

        // Clear the field if disabled
        if (isFirstYearFirstSemester) {
            subjectsTakenField.setText("");
            // Hide the subject history panel
            subjectHistoryScrollPane.setVisible(false);
            // Clear the subject history panel
            subjectHistoryPanel.removeAll();
            subjectHistoryPanel.revalidate();
            subjectHistoryPanel.repaint();
            clearFieldsButton.setEnabled(false);
        }
    }

    private void generateSubjectInputFields() {
        String subjectCountText = subjectsTakenField.getText().trim();
        if (subjectCountText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter the number of subjects taken.",
                    "Missing Input",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int subjectCount = Integer.parseInt(subjectCountText);
            String selectedYear = (String) yearComboBox.getSelectedItem();
            boolean isThirdYearOrBelow = selectedYear.equals("1st Year") ||
                    selectedYear.equals("2nd Year") ||
                    selectedYear.equals("3rd Year");


            if (isThirdYearOrBelow) {
                if (subjectCount < 8 || subjectCount > 10) {
                    JOptionPane.showMessageDialog(this,
                            "3rd year students and below must take between 8-10 subjects.",
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                // Original validation for 4th year students
                if (subjectCount <= 0 || subjectCount > 10) {
                    JOptionPane.showMessageDialog(this,
                            "Please enter a valid number of subjects",
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Create new dialog for subject inputs
            JDialog subjectDialog = new JDialog(this, "Subject History Entry", true);
            subjectDialog.setSize(500, 400);
            subjectDialog.setLocationRelativeTo(this);

            // Main panel with border layout
            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

            // Subject inputs panel with BoxLayout
            JPanel subjectInputsPanel = new JPanel();
            subjectInputsPanel.setLayout(new BoxLayout(subjectInputsPanel, BoxLayout.Y_AXIS));

            // Title label
            JLabel titleLabel = new JLabel("Enter Subject History");
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            subjectInputsPanel.add(titleLabel);
            subjectInputsPanel.add(Box.createVerticalStrut(15));

            // Header row with better styling
            JPanel headerPanel = new JPanel(new GridLayout(1, 3));
            headerPanel.setMaximumSize(new Dimension(450, 30));
            headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));

            JLabel numLabel = new JLabel("No.", JLabel.CENTER);
            numLabel.setFont(numLabel.getFont().deriveFont(Font.BOLD));
            headerPanel.add(numLabel);

            JLabel subjectLabel = new JLabel("Subject", JLabel.CENTER);
            subjectLabel.setFont(subjectLabel.getFont().deriveFont(Font.BOLD));
            headerPanel.add(subjectLabel);

            JLabel statusLabel = new JLabel("Status", JLabel.CENTER);
            statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD));
            headerPanel.add(statusLabel);

            subjectInputsPanel.add(headerPanel);
            subjectInputsPanel.add(Box.createVerticalStrut(5));

            // List to store the selected subject data
            List<SubjectData> subjectDataList = new ArrayList<>();

            // Create input fields for each subject
            for (int i = 0; i < subjectCount; i++) {
                final int rowIndex = i;
                JPanel rowPanel = new JPanel(new GridLayout(1, 3, 5, 0));
                rowPanel.setMaximumSize(new Dimension(450, 30));

                // Apply alternating row background for better readability
                if (i % 2 == 1) {
                    rowPanel.setBackground(new Color(245, 245, 250));
                }

                // Subject number label
                JLabel numberLabel = new JLabel((i + 1) + ".", JLabel.CENTER);

                // Subject selection combo box
                JComboBox<String> subjectComboBox = new JComboBox<>(
                        allSubjects.stream()
                                .map(Subject::getCode)
                                .toArray(String[]::new)
                );

                // Add item listener to check for duplicates when selection changes
                subjectComboBox.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        if (e.getStateChange() == ItemEvent.SELECTED) {
                            // Store selected value in the data list
                            if (rowIndex < subjectDataList.size()) {
                                subjectDataList.get(rowIndex).setSubject((String) subjectComboBox.getSelectedItem());
                            }

                            // Check for duplicates
                            checkForDuplicatesInDialog(subjectInputsPanel, rowIndex);
                        }
                    }
                });

                // Pass/Fail combo box with custom rendering
                JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"Pass", "Fail"});
                statusComboBox.setRenderer(new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value,
                                                                  int index, boolean isSelected, boolean cellHasFocus) {
                        Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                        if ("Pass".equals(value)) {
                            setForeground(isSelected ? Color.WHITE : new Color(0, 128, 0));
                        } else if ("Fail".equals(value)) {
                            setForeground(isSelected ? Color.WHITE : new Color(178, 34, 34));
                        }

                        return this;
                    }
                });

                // Add status change listener
                statusComboBox.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        if (e.getStateChange() == ItemEvent.SELECTED) {
                            // Store selected value in the data list
                            if (rowIndex < subjectDataList.size()) {
                                subjectDataList.get(rowIndex).setStatus((String) statusComboBox.getSelectedItem());
                            }
                        }
                    }
                });

                rowPanel.add(numberLabel);
                rowPanel.add(subjectComboBox);
                rowPanel.add(statusComboBox);

                subjectInputsPanel.add(rowPanel);
                subjectInputsPanel.add(Box.createVerticalStrut(5)); // Add some spacing

                // Initialize data structure for this row
                SubjectData data = new SubjectData();
                data.setSubject((String) subjectComboBox.getSelectedItem());
                data.setStatus((String) statusComboBox.getSelectedItem());
                subjectDataList.add(data);
            }

            // Create scroll pane
            JScrollPane scrollPane = new JScrollPane(subjectInputsPanel);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            mainPanel.add(scrollPane, BorderLayout.CENTER);

            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton cancelButton = new JButton("Cancel");
            JButton saveButton = new JButton("Save");

            cancelButton.addActionListener(e -> subjectDialog.dispose());

            saveButton.addActionListener(e -> {
                // Check for duplicates before saving
                boolean hasDuplicates = false;
                Set<String> subjectSet = new HashSet<>();

                for (SubjectData data : subjectDataList) {
                    if (!subjectSet.add(data.getSubject())) {
                        hasDuplicates = true;
                        break;
                    }
                }

                if (hasDuplicates) {
                    JOptionPane.showMessageDialog(subjectDialog,
                            "Please eliminate duplicate subject selections.",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Save the subject data
                saveSubjectData(subjectDataList);
                subjectDialog.dispose();

                // Show confirmation
                JOptionPane.showMessageDialog(this,
                        subjectCount + " subject(s) successfully recorded.",
                        "Data Saved",
                        JOptionPane.INFORMATION_MESSAGE);
            });

            buttonPanel.add(cancelButton);
            buttonPanel.add(saveButton);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            // Set dialog content and display
            subjectDialog.setContentPane(mainPanel);
            subjectDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            subjectDialog.setVisible(true);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid number.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to check for duplicates in the dialog
    private void checkForDuplicatesInDialog(JPanel panel, int changedRowIndex) {
        Set<String> selectedSubjects = new HashSet<>();
        List<JComboBox<?>> subjectBoxes = new ArrayList<>();

        // Collect all subject combo boxes
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel rowPanel = (JPanel) comp;
                // Skip header panel
                if (rowPanel.getComponentCount() == 3 && rowPanel.getComponent(1) instanceof JComboBox) {
                    JComboBox<?> subjectBox = (JComboBox<?>) rowPanel.getComponent(1);
                    subjectBoxes.add(subjectBox);
                    String selectedSubject = (String) subjectBox.getSelectedItem();

                    // Check if this subject was already selected
                    if (selectedSubject != null && !selectedSubjects.add(selectedSubject)) {
                        // Found a duplicate - highlight all instances
                        for (JComboBox<?> box : subjectBoxes) {
                            if (selectedSubject.equals(box.getSelectedItem())) {
                                box.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                            }
                        }
                        return;
                    }
                }
            }
        }

        // No duplicates found, reset borders
        for (JComboBox<?> box : subjectBoxes) {
            box.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("ComboBox.border"));
        }
    }

    // Class to store subject data
    private class SubjectData {
        private String subject;
        private String status;

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    // Method to save subject data


    private void checkForDuplicateSubjects(int changedRowIndex) {
        Set<String> selectedSubjects = new HashSet<>();

        // Collect all currently selected subjects
        for (int i = 0; i < subjectHistoryPanel.getComponentCount(); i++) {
            Component comp = subjectHistoryPanel.getComponent(i);
            if (comp instanceof JPanel && i != 0) { // Skip header panel
                JPanel rowPanel = (JPanel) comp;
                // Check if it's a subject row (has 3 components)
                if (rowPanel.getComponentCount() == 3) {
                    JComboBox<?> subjectBox = (JComboBox<?>) rowPanel.getComponent(1);
                    String selectedSubject = (String) subjectBox.getSelectedItem();

                    // Check if this subject was already selected
                    if (selectedSubject != null && !selectedSubjects.add(selectedSubject)) {
                        // Found a duplicate - highlight the subject boxes
                        for (int j = 0; j < subjectHistoryPanel.getComponentCount(); j++) {
                            Component innerComp = subjectHistoryPanel.getComponent(j);
                            if (innerComp instanceof JPanel && j != 0) { // Skip header
                                JPanel innerRowPanel = (JPanel) innerComp;
                                if (innerRowPanel.getComponentCount() == 3) {
                                    JComboBox<?> innerSubjectBox = (JComboBox<?>) innerRowPanel.getComponent(1);
                                    String innerSelectedSubject = (String) innerSubjectBox.getSelectedItem();

                                    if (selectedSubject.equals(innerSelectedSubject)) {
                                        innerSubjectBox.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                                    }
                                }
                            }
                        }

                        // Show warning
                        JOptionPane.showMessageDialog(this,
                                "Duplicate subject selected: " + selectedSubject + "\nPlease select different subjects.",
                                "Duplicate Subject",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }
        }

        // No duplicates found, reset borders
        for (int i = 0; i < subjectHistoryPanel.getComponentCount(); i++) {
            Component comp = subjectHistoryPanel.getComponent(i);
            if (comp instanceof JPanel && i != 0) { // Skip header
                JPanel rowPanel = (JPanel) comp;
                if (rowPanel.getComponentCount() == 3) {
                    JComboBox<?> subjectBox = (JComboBox<?>) rowPanel.getComponent(1);
                    subjectBox.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("ComboBox.border"));
                }
            }
        }
    }

    // Update the checkForRecommendations method to handle both first-year first-semester
// and other year/semester combinations
    private void checkForRecommendations() {
        String selectedYear = (String) yearComboBox.getSelectedItem();
        String selectedSemester = (String) semesterComboBox.getSelectedItem();

        // Convert selected year to integer (1-4)
        int yearNum = getYearNumber(selectedYear);

        // Convert selected semester to integer (1-3, where 3 is summer)
        int semesterNum = getSemesterNumber(selectedSemester);

        if (yearNum == 1 && semesterNum == 1) {
            // First year, first semester - show default recommendations
            showRecommendationsDialog(null);
        } else {
            // For other years/semesters, collect taken subjects first
            collectSubjectsAndShowRecommendations(yearNum, semesterNum);
        }
    }

    // Helper method to convert year string to integer
    private int getYearNumber(String yearString) {
        if (yearString.contains("1st")) return 1;
        if (yearString.contains("2nd")) return 2;
        if (yearString.contains("3rd")) return 3;
        if (yearString.contains("4th")) return 4;
        return 1; // Default to 1st year
    }

    // Helper method to convert semester string to integer
    private int getSemesterNumber(String semesterString) {
        if (semesterString.contains("1st")) return 1;
        if (semesterString.contains("2nd")) return 2;
        if (semesterString.contains("Summer")) return 3;
        return 1; // Default to 1st semester
    }

    // Method to collect subjects from the UI and show recommendations
    private void collectSubjectsAndShowRecommendations(int yearNum, int semesterNum) {
        // Create a map to store taken subjects
        Map<String, Boolean> subjectsTaken = new HashMap<>();

        // Collect subjects from the UI
        for (Component comp : subjectHistoryPanel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel rowPanel = (JPanel) comp;
                // Skip header row
                if (rowPanel.getComponentCount() == 3 && rowPanel.getComponent(1) instanceof JComboBox) {
                    JComboBox<?> subjectBox = (JComboBox<?>) rowPanel.getComponent(1);
                    JComboBox<?> statusBox = (JComboBox<?>) rowPanel.getComponent(2);

                    String subjectCode = (String) subjectBox.getSelectedItem();
                    String status = (String) statusBox.getSelectedItem();

                    // Add to map - true for "Pass", false for "Fail"
                    subjectsTaken.put(subjectCode, "Pass".equals(status));
                }
            }

            for(SubjectData data : savedSubjectData) {
                subjectsTaken.put(data.subject, "pass".equalsIgnoreCase(data.status));
            }
        }

        // If no subjects have been entered, show a message
        if (subjectsTaken.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter your taken subjects first before viewing recommendations.",
                    "No Subjects Found",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Show recommendations dialog with the collected subjects
        showRecommendationsDialog(subjectsTaken);
    }

    // Updated method to show recommendations based on taken subjects
    private void showRecommendationsDialog(Map<String, Boolean> subjectsTaken) {
        // Clear previous recommendations
        recommendationsPanel.removeAll();

        // Get current year and semester
        int yearNum = getYearNumber((String) yearComboBox.getSelectedItem());
        int semesterNum = getSemesterNumber((String) semesterComboBox.getSelectedItem());

        // Get recommended subjects
        List<Subject> subjects;
        StudentEval se = new StudentEval("bsit");

        if (subjectsTaken == null) {
            // For first year, first semester - use default method

            subjects = se.getRecommendedSubjects();
        } else {
            // For other years/semesters - use overloaded method
            subjects = se.getRecommendedSubjects(subjectsTaken, yearNum, semesterNum);
        }

        System.out.println("\n----- RECOMMENDED SUBJECTS -----");
        System.out.println("Year: " + yearComboBox.getSelectedItem() +
                ", Semester: " + semesterComboBox.getSelectedItem());

        if (subjects.isEmpty()) {
            System.out.println("No subjects recommended");
        } else {
            System.out.println("Subject Code\tUnits");
            System.out.println("------------------------");
            int totalUnits = 0;

            for (Subject subject : subjects) {
                System.out.println(subject.getCode() + "\t\t" + subject.getUnits());
                totalUnits += subject.getUnits();
            }

            System.out.println("------------------------");
            System.out.println("Total Units:\t" + totalUnits);
        }
        System.out.println("--------------------------------\n");

        int totalUnits = 0;

        // Add a title
        JLabel titleLabel = new JLabel("Recommended Subjects for " +
                yearComboBox.getSelectedItem() + ", " +
                semesterComboBox.getSelectedItem());
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        recommendationsPanel.add(titleLabel);
        recommendationsPanel.add(Box.createVerticalStrut(15));

        // Add header
        JPanel headerPanel = new JPanel(new GridLayout(1, 2));
        headerPanel.setMaximumSize(new Dimension(450, 25));
        JLabel codeLabel = new JLabel("Subject Code");
        codeLabel.setFont(codeLabel.getFont().deriveFont(Font.BOLD));
        headerPanel.add(codeLabel);

        JLabel unitsLabel = new JLabel("Units");
        unitsLabel.setFont(unitsLabel.getFont().deriveFont(Font.BOLD));
        headerPanel.add(unitsLabel);

        recommendationsPanel.add(headerPanel);
        recommendationsPanel.add(Box.createVerticalStrut(5));

        // Handle case when no subjects are recommended
        if (subjects.isEmpty()) {
            JPanel noSubjectsPanel = new JPanel(new GridLayout(1, 2));
            noSubjectsPanel.setMaximumSize(new Dimension(450, 25));
            noSubjectsPanel.add(new JLabel("No subjects recommended"));
            noSubjectsPanel.add(new JLabel(""));
            recommendationsPanel.add(noSubjectsPanel);
        } else {
            // Add each subject
            for (int i = 0; i < subjects.size(); i++) {
                Subject subject = subjects.get(i);
                JPanel subjectPanel = new JPanel(new GridLayout(1, 2));
                subjectPanel.setMaximumSize(new Dimension(450, 25));

                // Apply alternating colors for better readability
                if (i % 2 == 1) {
                    subjectPanel.setBackground(new Color(245, 245, 250));
                }

                subjectPanel.add(new JLabel(subject.getCode()));
                subjectPanel.add(new JLabel(String.valueOf(subject.getUnits())));
                recommendationsPanel.add(subjectPanel);

                totalUnits += subject.getUnits();
            }
        }

        // Add total units
        recommendationsPanel.add(Box.createVerticalStrut(15));
        JPanel totalPanel = new JPanel(new GridLayout(1, 2));
        totalPanel.setMaximumSize(new Dimension(450, 25));
        totalPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));

        JLabel totalLabel = new JLabel("Total Units:", JLabel.RIGHT);
        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD));
        totalPanel.add(totalLabel);

        JLabel totalValueLabel = new JLabel(String.valueOf(totalUnits));
        totalValueLabel.setFont(totalValueLabel.getFont().deriveFont(Font.BOLD));
        totalPanel.add(totalValueLabel);

        recommendationsPanel.add(totalPanel);

        // Refresh the panel
        recommendationsPanel.revalidate();
        recommendationsPanel.repaint();

        // Show the dialog
        recommendationsDialog.setVisible(true);
    }

    // Update the saveSubjectData method to store subjects for recommendations
    private List<SubjectData> savedSubjectData;

    private void saveSubjectData(List<SubjectData> subjectDataList) {
        // Store the subject data in the class field
        savedSubjectData = new ArrayList<>(subjectDataList);

        // Update the subject history panel
        updateSubjectHistoryPanel(subjectDataList);

        // Print for debugging
        System.out.println("Saved subject data:");
        for (SubjectData data : subjectDataList) {
            System.out.println(data.getSubject() + ": " + data.getStatus());
        }
    }

    // Method to update the subject history panel
    private void updateSubjectHistoryPanel(List<SubjectData> subjectDataList) {
        // Clear existing content
        subjectHistoryPanel.removeAll();

        // Create header panel
        JPanel headerPanel = new JPanel(new GridLayout(1, 3));
        headerPanel.setMaximumSize(new Dimension(550, 25));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));

        JLabel numLabel = new JLabel("No.", JLabel.CENTER);
        numLabel.setFont(numLabel.getFont().deriveFont(Font.BOLD));
        headerPanel.add(numLabel);

        JLabel subjectLabel = new JLabel("Subject", JLabel.CENTER);
        subjectLabel.setFont(subjectLabel.getFont().deriveFont(Font.BOLD));
        headerPanel.add(subjectLabel);

        JLabel statusLabel = new JLabel("Status", JLabel.CENTER);
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD));
        headerPanel.add(statusLabel);

        subjectHistoryPanel.add(headerPanel);

        // Add each subject row
        for (int i = 0; i < subjectDataList.size(); i++) {
            SubjectData data = subjectDataList.get(i);
            JPanel rowPanel = new JPanel(new GridLayout(1, 3));
            rowPanel.setMaximumSize(new Dimension(550, 25));

            // Apply alternating colors
            if (i % 2 == 1) {
                rowPanel.setBackground(new Color(245, 245, 250));
            }

            JLabel numberLabel = new JLabel((i + 1) + ".", JLabel.CENTER);
            rowPanel.add(numberLabel);

            // Create a non-editable label for the subject code
            JLabel subjectCodeLabel = new JLabel(data.getSubject(), JLabel.CENTER);
            rowPanel.add(subjectCodeLabel);

            // Create a status label with appropriate color
            JLabel subjectStatusLabel = new JLabel(data.getStatus(), JLabel.CENTER);
            if ("Pass".equals(data.getStatus())) {
                subjectStatusLabel.setForeground(new Color(0, 128, 0));
            } else {
                subjectStatusLabel.setForeground(new Color(178, 34, 34));
            }
            rowPanel.add(subjectStatusLabel);

            subjectHistoryPanel.add(rowPanel);
        }

        // Make the scroll pane visible if there are subjects
        if (!subjectDataList.isEmpty()) {
            subjectHistoryScrollPane.setVisible(true);
            clearFieldsButton.setEnabled(true);
        }

        // Refresh the panel
        subjectHistoryPanel.revalidate();
        subjectHistoryPanel.repaint();
    }

    // Update the submitForm method to include recommendations

    private void setupLayout() {
        // Main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Set a school-like background color
        mainPanel.setBackground(new Color(240, 240, 240)); // Light gray

        // Form panel with grid layout
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(255, 255, 255)); // White background
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 51, 102), 2), // Navy blue border
                new EmptyBorder(15, 15, 15, 15) // Padding
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Add title label
        JLabel titleLabel = new JLabel("Student Information Form", JLabel.CENTER);
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 51, 102)); // Navy blue
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(titleLabel, gbc);

        // Reset grid width
        gbc.gridwidth = 1;

        // Add name components
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Student Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        formPanel.add(nameField, gbc);

        // Add ID components
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Student ID:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        formPanel.add(idField, gbc);

        // Add program components
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Program:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        formPanel.add(programComboBox, gbc);

        // Add year components
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Year Level:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        formPanel.add(yearComboBox, gbc);

        // Add semester components
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Semester:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        formPanel.add(semesterComboBox, gbc);

        // Add subjects taken components with a button
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0.0;
        JLabel subjectsLabel = new JLabel("Number of Subjects Taken Recently:");
        formPanel.add(subjectsLabel, gbc);

        JPanel subjectsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        subjectsPanel.add(subjectsTakenField);

        JButton generateButton = new JButton("Generate Fields");
        generateButton.setBackground(new Color(0, 51, 102)); // Navy blue
        generateButton.setForeground(Color.BLACK); // White text
        generateButton.addActionListener(e -> generateSubjectInputFields());
        subjectsPanel.add(generateButton);

        subjectsPanel.add(clearFieldsButton);

        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.weightx = 1.0;
        formPanel.add(subjectsPanel, gbc);

        // Add subject history panel
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(subjectHistoryScrollPane, gbc);

        // Reset grid width
        gbc.gridwidth = 1;
        gbc.weighty = 0.0;

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton submitButton = new JButton("Submit");
        JButton viewRecommendationsButton = new JButton("View Recommendations");

        // Style buttons
        submitButton.setBackground(new Color(0, 51, 102)); // Navy blue
        submitButton.setForeground(Color.BLACK); // White text
        viewRecommendationsButton.setBackground(new Color(0, 51, 102)); // Navy blue
        viewRecommendationsButton.setForeground(Color.BLACK); // White text

        // Add action listeners
        submitButton.addActionListener(e -> submitForm());
        viewRecommendationsButton.addActionListener(e -> checkForRecommendations());

        buttonPanel.add(submitButton);
        buttonPanel.add(viewRecommendationsButton);

        // Add panels to main panel
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Set main panel as content pane
        setContentPane(mainPanel);
    }

    private void submitForm() {
        // Get values from form fields
        String name = nameField.getText().trim();
        String id = idField.getText().trim();
        String year = (String) yearComboBox.getSelectedItem();
        String semester = (String) semesterComboBox.getSelectedItem();
        String subjectsTaken = subjectsTakenField.getText().trim();

        // Validate input
        if (name.isEmpty() || id.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all required fields.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean isFirstYearFirstSemester = Objects.equals(year, "1st Year") &&
                Objects.equals(semester, "1st Semester");

        // Validate number of subjects if not first year first semester
        if (!isFirstYearFirstSemester) {
            if (subjectsTaken.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter the number of subjects taken.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int numSubjects = Integer.parseInt(subjectsTaken);
                if (numSubjects <= 0 || numSubjects > 15) {
                    JOptionPane.showMessageDialog(this,
                            "Number of subjects must be between 1 and 15.",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Check if subject data is available
                if (savedSubjectData == null || savedSubjectData.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Please generate and fill in the subject fields.",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Check for duplicate subjects
                Set<String> subjects = new HashSet<>();
                boolean hasDuplicates = false;

                for (SubjectData data : savedSubjectData) {
                    if (!subjects.add(data.getSubject())) {
                        hasDuplicates = true;
                        break;
                    }
                }

                if (hasDuplicates) {
                    JOptionPane.showMessageDialog(this,
                            "Please eliminate duplicate subject selections.",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Number of subjects must be a valid integer.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // In a real application, save the student data to database

        // If not first year first semester, display subject summary
        if (!isFirstYearFirstSemester && savedSubjectData != null) {
            StringBuilder summary = new StringBuilder();
            summary.append("Subjects submitted:\n\n");

            for (SubjectData data : savedSubjectData) {
                summary.append("• ").append(data.getSubject()).append(" - ").append(data.getStatus()).append("\n");
            }

            JTextArea textArea = new JTextArea(summary.toString());
            textArea.setEditable(false);
            textArea.setBackground(null);
            textArea.setWrapStyleWord(true);
            textArea.setLineWrap(true);

            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(350, 200));

            JOptionPane.showMessageDialog(this,
                    scrollPane,
                    "Subject Summary",
                    JOptionPane.INFORMATION_MESSAGE);

            // Ask if user wants to see recommended subjects
            int choice = JOptionPane.showConfirmDialog(this,
                    "Would you like to see recommended subjects for next semester?",
                    "View Recommendations",
                    JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                checkForRecommendations();
            }
        }

        // Show success message
        JOptionPane.showMessageDialog(this,
                "Student information submitted successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

        // Clear form fields
        nameField.setText("");
        idField.setText("");
        yearComboBox.setSelectedIndex(0);
        semesterComboBox.setSelectedIndex(0);
        clearSubjectFields();
        savedSubjectData = null;
    }

    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Launch the application
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new StudentInfoForm();
            }
        });
    }
}