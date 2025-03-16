import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;
import java.util.List;

public class StudentInfoForm extends JFrame {
    private JTextField nameField, idField, subjectsTakenField;
    private JComboBox<String> yearComboBox, semesterComboBox;
    private JButton clearFieldsButton, generateButton, submitButton, viewRecommendationsButton;
    private JPanel subjectHistoryPanel;
    private JScrollPane subjectHistoryScrollPane;
    private JPanel recommendationsPanel;
    private JDialog recommendationsDialog;
    private List<Subject> allSubjects;
    private List<SubjectData> savedSubjectData;

    public StudentInfoForm() {
        setTitle("Student Information");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Load subjects
        allSubjects = loadAllSubjects();

        // Initialize components
        initComponents();

        // Set up layout
        setupLayout();

        setVisible(true);
    }

    private List<Subject> loadAllSubjects() {
        // In a real app, this would load from XML or database
        // For now, we'll extract subjects from the curriculum
        List<Subject> subjects = new ArrayList<>();

        // First Year, First Semester
        subjects.add(new Subject("eng100", 3));
        subjects.add(new Subject("socio102", 3));
        subjects.add(new Subject("math100", 3));
        subjects.add(new Subject("psych101", 3));
        subjects.add(new Subject("cc-intcom11", 3));
        subjects.add(new Subject("cc-comprog11", 3));
        subjects.add(new Subject("it-webdev11", 3));
        subjects.add(new Subject("pe101", 2));
        subjects.add(new Subject("nstp101", 3));

        // First Year, Second Semester
        subjects.add(new Subject("eng101", 3, List.of("eng100")));
        subjects.add(new Subject("entrep101", 3));
        subjects.add(new Subject("math101", 3, List.of("math100")));
        subjects.add(new Subject("hist101", 3));
        subjects.add(new Subject("hum101", 3));
        subjects.add(new Subject("cc-comprog12", 3, List.of("cc-comprog11")));
        subjects.add(new Subject("cc-discret12", 3, List.of("cc-intcom11")));
        subjects.add(new Subject("pe102", 2, List.of("pe101")));
        subjects.add(new Subject("nstp102", 3, List.of("nstp101")));

        // Second Year, First Semester
        subjects.add(new Subject("socio101", 3));
        subjects.add(new Subject("rizal101", 3));
        subjects.add(new Subject("cc-digilog21", 3, List.of("cc-discret12")));
        subjects.add(new Subject("cc-ooprog21", 3, List.of("cc-comprog12")));
        subjects.add(new Subject("it-sad21", 3, List.of("cc-comprog12")));
        subjects.add(new Subject("cc-acctg21", 3, List.of("math101")));
        subjects.add(new Subject("cc-twrite21", 3, List.of("eng101", "cc-intcom11")));
        subjects.add(new Subject("pe103", 2, List.of("pe102")));

        // Second Year, Second Semester
        subjects.add(new Subject("sts101", 3));
        subjects.add(new Subject("philo101", 3));
        subjects.add(new Subject("cc-quameth22", 3, List.of("cc-discret12")));
        subjects.add(new Subject("it-platech22", 3, List.of("cc-digilog21")));
        subjects.add(new Subject("cc-appsdev22", 3, List.of("cc-ooprog21", "it-sad21")));
        subjects.add(new Subject("cc-datastruc22", 3, List.of("cc-ooprog21")));
        subjects.add(new Subject("cc-datacom22", 3, List.of("cc-digilog21")));
        subjects.add(new Subject("pe104", 2, List.of("pe103")));

        // Third Year, First Semester
        subjects.add(new Subject("it-imdbsys31", 3, List.of("cc-appsdev22")));
        subjects.add(new Subject("it-network31", 3, List.of("cc-datacom22")));
        subjects.add(new Subject("it-testqua31", 3, List.of("cc-appsdev22")));
        subjects.add(new Subject("cc-hci31", 3, List.of("cc-appsdev22")));
        subjects.add(new Subject("cc-rescom31", 3, List.of("cc-twrite21", "cc-quameth22")));
        subjects.add(new Subject("it-el1", 3));
        subjects.add(new Subject("it-fre1", 3));
        subjects.add(new Subject("it-fre2", 3));

        // Third Year, Second Semester
        subjects.add(new Subject("it-imdbsys32", 3, List.of("it-imdbsys31")));
        subjects.add(new Subject("it-infosec32", 3, List.of("it-imdbsys31", "it-network31")));
        subjects.add(new Subject("it-sysarch32", 3, List.of("it-testqua31", "cc-hci31")));
        subjects.add(new Subject("cc-techno32", 3, List.of("cc-rescom31")));
        subjects.add(new Subject("it-intprog32", 3, List.of("it-imdbsys31")));
        subjects.add(new Subject("it-sysadmin32", 3, List.of("it-network31")));
        subjects.add(new Subject("it-el2", 3));
        subjects.add(new Subject("it-fre3", 3));

        // Fourth Year, First Semester
        subjects.add(new Subject("lit11", 3));
        subjects.add(new Subject("it-cpstone40", 3));
        subjects.add(new Subject("it-el3", 3));
        subjects.add(new Subject("it-fre4", 3));

        // Fourth Year, Second Semester
        subjects.add(new Subject("cc-pract40", 9));
        subjects.add(new Subject("it-el4", 3));

        return subjects;
    }

    private void initComponents() {
        nameField = new JTextField(20);
        idField = new JTextField(20);
        yearComboBox = new JComboBox<>(new String[]{"1st Year", "2nd Year", "3rd Year", "4th Year"});
        semesterComboBox = new JComboBox<>(new String[]{"1st Semester", "2nd Semester", "Summer"});
        subjectsTakenField = new JTextField(5);

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

        clearFieldsButton = new JButton("Clear Fields");
        clearFieldsButton.addActionListener(e -> clearSubjectFields());
        clearFieldsButton.setEnabled(false);

        generateButton = new JButton("Generate Fields");
        generateButton.addActionListener(e -> generateSubjectInputFields());

        submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> submitForm());

        viewRecommendationsButton = new JButton("View Recommendations");
        viewRecommendationsButton.addActionListener(e -> checkForRecommendations());

        // Initialize subject history panel
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

        // Initialize recommendations panel
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

        // Initialize field's state based on default selections
        updateSubjectsTakenField();
    }

    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Student Information Panel
        JPanel studentInfoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Add name components
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Student Name:");
        studentInfoPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        studentInfoPanel.add(nameField, gbc);

        // Add ID components
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        JLabel idLabel = new JLabel("Student ID:");
        studentInfoPanel.add(idLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        studentInfoPanel.add(idField, gbc);

        // Add year components
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        JLabel yearLabel = new JLabel("Year Level:");
        studentInfoPanel.add(yearLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        studentInfoPanel.add(yearComboBox, gbc);

        // Add semester components
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        JLabel semesterLabel = new JLabel("Semester:");
        studentInfoPanel.add(semesterLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        studentInfoPanel.add(semesterComboBox, gbc);

        // Add subjects taken components with buttons
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        JLabel subjectsLabel = new JLabel("Number of Subjects Taken Recently:");
        studentInfoPanel.add(subjectsLabel, gbc);

        JPanel subjectsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        subjectsPanel.add(subjectsTakenField);
        subjectsPanel.add(generateButton);
        subjectsPanel.add(clearFieldsButton);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        studentInfoPanel.add(subjectsPanel, gbc);

        // Add subject history panel
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        studentInfoPanel.add(subjectHistoryScrollPane, gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(submitButton);
        buttonPanel.add(viewRecommendationsButton);

        // Add panels to main panel
        mainPanel.add(studentInfoPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
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

    private void clearSubjectFields() {
        subjectHistoryPanel.removeAll();
        subjectHistoryScrollPane.setVisible(false);
        subjectsTakenField.setText("");
        clearFieldsButton.setEnabled(false);
        subjectHistoryPanel.revalidate();
        subjectHistoryPanel.repaint();
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

            // Header row with styling
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
                    rowPanel.setBackground(new Color(240, 240, 240));
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
                // Add item listener to check for duplicates when selection changes
                subjectComboBox.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        if (e.getStateChange() == ItemEvent.SELECTED) {
                            String selectedSubject = (String) subjectComboBox.getSelectedItem();

                            // Check if this subject is already selected in another dropdown
                            for (int j = 0; j < subjectCount; j++) {
                                if (j != rowIndex && subjectDataList.size() > j) {
                                    SubjectData otherSubject = subjectDataList.get(j);
                                    if (otherSubject != null && selectedSubject.equals(otherSubject.getSubjectCode())) {
                                        JOptionPane.showMessageDialog(subjectDialog,
                                                "Subject " + selectedSubject + " is already selected.",
                                                "Duplicate Subject",
                                                JOptionPane.WARNING_MESSAGE);

                                        // Reset to previous selection or first item
                                        String previousSelection = (String) e.getItem();
                                        if (e.getStateChange() == ItemEvent.SELECTED && previousSelection != null) {
                                            subjectComboBox.setSelectedItem(previousSelection);
                                        } else {
                                            subjectComboBox.setSelectedIndex(0);
                                        }
                                        return;
                                    }
                                }
                            }

                            // Update the subject data in the list
                            if (subjectDataList.size() > rowIndex) {
                                subjectDataList.set(rowIndex, new SubjectData(selectedSubject, null));
                            } else {
                                subjectDataList.add(new SubjectData(selectedSubject, null));
                            }
                        }
                    }
                });

                // Status combo box
                JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"Passed", "Failed", "Ongoing"});
                statusComboBox.addItemListener(e -> {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        String selectedStatus = (String) statusComboBox.getSelectedItem();
                        String selectedSubject = (String) subjectComboBox.getSelectedItem();

                        if (subjectDataList.size() > rowIndex) {
                            subjectDataList.set(rowIndex, new SubjectData(selectedSubject, selectedStatus));
                        } else {
                            subjectDataList.add(new SubjectData(selectedSubject, selectedStatus));
                        }
                    }
                });

                // Add components to row
                rowPanel.add(numberLabel);
                rowPanel.add(subjectComboBox);
                rowPanel.add(statusComboBox);

                // Initialize the subject data with default selections
                subjectDataList.add(new SubjectData(
                        (String) subjectComboBox.getSelectedItem(),
                        (String) statusComboBox.getSelectedItem()
                ));

                subjectInputsPanel.add(rowPanel);
                subjectInputsPanel.add(Box.createVerticalStrut(5));
            }

            // Add dialog buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton cancelButton = new JButton("Cancel");
            JButton saveButton = new JButton("Save");

            cancelButton.addActionListener(e -> subjectDialog.dispose());

            saveButton.addActionListener(e -> {
                // Check for prerequisites
                boolean prerequisitesMet = checkPrerequisites(subjectDataList);

                if (prerequisitesMet) {
                    savedSubjectData = new ArrayList<>(subjectDataList);
                    updateSubjectHistoryPanel(savedSubjectData);
                    subjectDialog.dispose();
                } else {
                    // Error message shown in checkPrerequisites method
                }
            });

            buttonPanel.add(cancelButton);
            buttonPanel.add(saveButton);

            // Add panels to main dialog panel
            mainPanel.add(new JScrollPane(subjectInputsPanel), BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            subjectDialog.setContentPane(mainPanel);
            subjectDialog.setVisible(true);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid number.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean checkPrerequisites(List<SubjectData> subjectDataList) {
        Set<String> passedSubjects = new HashSet<>();

        // First, collect all passed subjects
        for (SubjectData data : subjectDataList) {
            if ("Passed".equals(data.getStatus())) {
                passedSubjects.add(data.getSubjectCode());
            }
        }

        // Then check if all prerequisites are met
        for (SubjectData data : subjectDataList) {
            Subject subject = findSubjectByCode(data.getSubjectCode());

            if (subject != null && !subject.getPrerequisites().isEmpty()) {
                for (String prerequisite : subject.getPrerequisites()) {
                    if (!passedSubjects.contains(prerequisite)) {
                        JOptionPane.showMessageDialog(this,
                                "Prerequisite not met: " + prerequisite + " is required for " + data.getSubjectCode(),
                                "Prerequisite Error",
                                JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private Subject findSubjectByCode(String code) {
        for (Subject subject : allSubjects) {
            if (subject.getCode().equals(code)) {
                return subject;
            }
        }
        return null;
    }

    private void updateSubjectHistoryPanel(List<SubjectData> subjectDataList) {
        // Clear existing content
        subjectHistoryPanel.removeAll();

        // Add header
        JPanel headerPanel = new JPanel(new GridLayout(1, 3));
        headerPanel.add(new JLabel("Subject Code", JLabel.CENTER));
        headerPanel.add(new JLabel("Units", JLabel.CENTER));
        headerPanel.add(new JLabel("Status", JLabel.CENTER));

        // Style header
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
        for (Component comp : headerPanel.getComponents()) {
            if (comp instanceof JLabel) {
                ((JLabel) comp).setFont(comp.getFont().deriveFont(Font.BOLD));
            }
        }

        subjectHistoryPanel.add(headerPanel);

        // Add rows for each subject
        for (SubjectData data : subjectDataList) {
            Subject subject = findSubjectByCode(data.getSubjectCode());

            if (subject != null) {
                JPanel rowPanel = new JPanel(new GridLayout(1, 3));
                rowPanel.add(new JLabel(subject.getCode(), JLabel.CENTER));
                rowPanel.add(new JLabel(String.valueOf(subject.getUnits()), JLabel.CENTER));
                rowPanel.add(new JLabel(data.getStatus(), JLabel.CENTER));

                subjectHistoryPanel.add(rowPanel);
            }
        }

        // Show the scroll pane
        subjectHistoryScrollPane.setVisible(true);
        clearFieldsButton.setEnabled(true);

        // Refresh the panel
        subjectHistoryPanel.revalidate();
        subjectHistoryPanel.repaint();
    }

    private void submitForm() {
        String name = nameField.getText().trim();
        String id = idField.getText().trim();
        String year = (String) yearComboBox.getSelectedItem();
        String semester = (String) semesterComboBox.getSelectedItem();

        // Validate required fields
        if (name.isEmpty() || id.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all required fields.",
                    "Missing Information",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if subject history is required and present
        boolean isFirstYearFirstSemester = "1st Year".equals(year) && "1st Semester".equals(semester);

        if (!isFirstYearFirstSemester && (savedSubjectData == null || savedSubjectData.isEmpty())) {
            JOptionPane.showMessageDialog(this,
                    "Please enter your subject history.",
                    "Missing Information",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Calculate total units
        int totalUnits = 0;
        if (savedSubjectData != null) {
            for (SubjectData data : savedSubjectData) {
                Subject subject = findSubjectByCode(data.getSubjectCode());
                if (subject != null) {
                    totalUnits += subject.getUnits();
                }
            }
        }

        // Confirm submission
        String message = "Student Name: " + name + "\n" +
                "Student ID: " + id + "\n" +
                "Year Level: " + year + "\n" +
                "Semester: " + semester + "\n" +
                "Total Units: " + totalUnits;

        int result = JOptionPane.showConfirmDialog(this,
                message + "\n\nSubmit this information?",
                "Confirm Submission",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this,
                    "Information submitted successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            // Reset form
            resetForm();
        }
    }

    private void resetForm() {
        // Clear all fields
        nameField.setText("");
        idField.setText("");
        yearComboBox.setSelectedIndex(0);
        semesterComboBox.setSelectedIndex(0);
        subjectsTakenField.setText("");

        // Clear subject history
        subjectHistoryPanel.removeAll();
        subjectHistoryScrollPane.setVisible(false);
        savedSubjectData = null;

        clearFieldsButton.setEnabled(false);

        // Refresh panels
        subjectHistoryPanel.revalidate();
        subjectHistoryPanel.repaint();
    }

    private void checkForRecommendations() {
        String selectedYear = (String) yearComboBox.getSelectedItem();
        String selectedSemester = (String) semesterComboBox.getSelectedItem();

        // Only show recommendations if we have subject history data
        if (savedSubjectData == null || savedSubjectData.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter your subject history first to get recommendations.",
                    "No Subject History",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Clear previous recommendations
        recommendationsPanel.removeAll();

        // Get list of passed subjects
        Set<String> passedSubjects = new HashSet<>();
        for (SubjectData data : savedSubjectData) {
            if ("Passed".equals(data.getStatus())) {
                passedSubjects.add(data.getSubjectCode());
            }
        }

        // Get list of subjects currently taking
        Set<String> currentSubjects = new HashSet<>();
        for (SubjectData data : savedSubjectData) {
            if ("Ongoing".equals(data.getStatus())) {
                currentSubjects.add(data.getSubjectCode());
            }
        }

        // Find recommended subjects
        List<Subject> recommendedSubjects = new ArrayList<>();
        for (Subject subject : allSubjects) {
            // Skip subjects already passed or currently taking
            if (passedSubjects.contains(subject.getCode()) || currentSubjects.contains(subject.getCode())) {
                continue;
            }

            // Check if all prerequisites are met
            boolean prerequisitesMet = true;
            for (String prerequisite : subject.getPrerequisites()) {
                if (!passedSubjects.contains(prerequisite)) {
                    prerequisitesMet = false;
                    break;
                }
            }

            if (prerequisitesMet) {
                recommendedSubjects.add(subject);
            }
        }

        // Add recommendations to panel
        JLabel titleLabel = new JLabel("Recommended Subjects for " + selectedYear + ", " + selectedSemester);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        recommendationsPanel.add(titleLabel);
        recommendationsPanel.add(Box.createVerticalStrut(10));

        if (recommendedSubjects.isEmpty()) {
            JLabel noRecommendationsLabel = new JLabel("No recommendations available.");
            noRecommendationsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            recommendationsPanel.add(noRecommendationsLabel);
        } else {
            // Add header
            JPanel headerPanel = new JPanel(new GridLayout(1, 3));
            headerPanel.setMaximumSize(new Dimension(450, 30));
            headerPanel.add(new JLabel("Subject Code", JLabel.CENTER));
            headerPanel.add(new JLabel("Units", JLabel.CENTER));
            headerPanel.add(new JLabel("Prerequisites", JLabel.CENTER));

            // Style header
            headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
            for (Component comp : headerPanel.getComponents()) {
                if (comp instanceof JLabel) {
                    ((JLabel) comp).setFont(comp.getFont().deriveFont(Font.BOLD));
                }
            }

            recommendationsPanel.add(headerPanel);
            recommendationsPanel.add(Box.createVerticalStrut(5));

            // Add subject rows
            for (Subject subject : recommendedSubjects) {
                JPanel rowPanel = new JPanel(new GridLayout(1, 3));
                rowPanel.setMaximumSize(new Dimension(450, 30));

                rowPanel.add(new JLabel(subject.getCode(), JLabel.CENTER));
                rowPanel.add(new JLabel(String.valueOf(subject.getUnits()), JLabel.CENTER));

                // Format prerequisites
                String prerequisites = String.join(", ", subject.getPrerequisites());
                if (prerequisites.isEmpty()) {
                    prerequisites = "None";
                }

                rowPanel.add(new JLabel(prerequisites, JLabel.CENTER));

                recommendationsPanel.add(rowPanel);
                recommendationsPanel.add(Box.createVerticalStrut(2));
            }
        }

        // Show the dialog
        recommendationsPanel.revalidate();
        recommendationsPanel.repaint();
        recommendationsDialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentInfoForm());
    }

    // Inner classes for data models
    private static class Subject {
        private final String code;
        private final int units;
        private final List<String> prerequisites;

        public Subject(String code, int units) {
            this(code, units, new ArrayList<>());
        }

        public Subject(String code, int units, List<String> prerequisites) {
            this.code = code;
            this.units = units;
            this.prerequisites = prerequisites;
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
    }

    private static class SubjectData {
        private final String subjectCode;
        private final String status;

        public SubjectData(String subjectCode, String status) {
            this.subjectCode = subjectCode;
            this.status = status;
        }

        public String getSubjectCode() {
            return subjectCode;
        }

        public String getStatus() {
            return status;
        }
    }
}