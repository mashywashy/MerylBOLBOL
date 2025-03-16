import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class SubjectInputDialog extends JDialog {
    private List<SubjectData> subjectDataList;

    public SubjectInputDialog(JFrame parent, int subjectCount, List<Subject> allSubjects) {
        super(parent, "Subject History Entry", true);
        setSize(500, 400);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel subjectInputsPanel = new JPanel();
        subjectInputsPanel.setLayout(new BoxLayout(subjectInputsPanel, BoxLayout.Y_AXIS));

        // Add input fields for each subject
        for (int i = 0; i < subjectCount; i++) {
            JPanel rowPanel = new JPanel(new GridLayout(1, 3, 5, 0));
            JLabel numberLabel = new JLabel((i + 1) + ".");
            JComboBox<String> subjectComboBox = new JComboBox<>(
                    allSubjects.stream().map(Subject::getCode).toArray(String[]::new)
            );
            JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"Pass", "Fail"});
            rowPanel.add(numberLabel);
            rowPanel.add(subjectComboBox);
            rowPanel.add(statusComboBox);
            subjectInputsPanel.add(rowPanel);
        }

        JScrollPane scrollPane = new JScrollPane(subjectInputsPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            // Save subject data
            dispose();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    public List<SubjectData> getSubjectDataList() {
        return subjectDataList;
    }
}