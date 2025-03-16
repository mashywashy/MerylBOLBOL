import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RecommendationsPanel extends JPanel {
    public RecommendationsPanel(List<Subject> subjects) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Recommended Subjects"));

        for (Subject subject : subjects) {
            JLabel subjectLabel = new JLabel(subject.getCode() + " - " + subject.getUnits() + " units");
            add(subjectLabel);
        }
    }
}