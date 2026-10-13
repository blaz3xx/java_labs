import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TreeWindow extends JFrame {

    private RedBlackTree tree = new RedBlackTree();
    private final Random random = new Random();

    private final TreePanel treePanel = new TreePanel();
    private final JTextField numberField = new JTextField(6);
    private final JSpinner countSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 30, 1));
    private final JLabel infoLabel = new JLabel();
    private final JLabel inOrderLabel = new JLabel();
    private final JLabel preOrderLabel = new JLabel();
    private final JLabel postOrderLabel = new JLabel();
    private final JLabel statusLabel = new JLabel(" ");

    private final List<JButton> actionButtons = new ArrayList<>();
    private final JButton nextButton = new JButton("Next step");
    private final JButton playButton = new JButton("Auto play");
    private final JButton finishButton = new JButton("Finish");
    private final JLabel stepLabel = new JLabel(" ");
    private final Timer timer = new Timer(1200, e -> nextStep());
    private List<RedBlackTree.Step> steps;
    private int stepIndex;
    private int stepNumber;
    private boolean stepNumberExisted;
    private boolean stepDelete;

    public TreeWindow() {
        super("Red-black tree");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(createControls(), BorderLayout.NORTH);
        add(new JScrollPane(treePanel), BorderLayout.CENTER);
        add(createInfo(), BorderLayout.SOUTH);

        setSize(980, 680);
        setLocationRelativeTo(null);
        refresh(null);
    }

    private JPanel createControls() {
        JButton addButton = new JButton("Add");
        JButton stepAddButton = new JButton("Add step by step");
        JButton deleteButton = new JButton("Delete");
        JButton stepDeleteButton = new JButton("Delete step by step");
        JButton findButton = new JButton("Find");
        JButton randomButton = new JButton("Random numbers");
        JButton sortedButton = new JButton("Sorted numbers");
        JButton clearButton = new JButton("Clear");
        actionButtons.addAll(List.of(addButton, stepAddButton, deleteButton, stepDeleteButton, findButton,
                randomButton, sortedButton, clearButton));

        addButton.addActionListener(e -> addNumber());
        numberField.addActionListener(e -> addNumber());
        stepAddButton.addActionListener(e -> startSteps(false));
        deleteButton.addActionListener(e -> deleteNumber());
        stepDeleteButton.addActionListener(e -> startSteps(true));
        findButton.addActionListener(e -> findNumber());
        randomButton.addActionListener(e -> fillTree(false));
        sortedButton.addActionListener(e -> fillTree(true));
        clearButton.addActionListener(e -> clearTree());

        nextButton.addActionListener(e -> nextStep());
        playButton.addActionListener(e -> togglePlay());
        finishButton.addActionListener(e -> finishSteps());
        nextButton.setEnabled(false);
        playButton.setEnabled(false);
        finishButton.setEnabled(false);

        JPanel manual = new JPanel(new FlowLayout(FlowLayout.LEFT));
        manual.add(new JLabel("Number:"));
        manual.add(numberField);
        manual.add(addButton);
        manual.add(stepAddButton);
        manual.add(deleteButton);
        manual.add(stepDeleteButton);
        manual.add(findButton);

        JPanel stepButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        stepButtons.add(nextButton);
        stepButtons.add(playButton);
        stepButtons.add(finishButton);
        JPanel stepRow = new JPanel(new BorderLayout());
        stepRow.add(stepButtons, BorderLayout.WEST);
        stepRow.add(stepLabel, BorderLayout.CENTER);

        JPanel automatic = new JPanel(new FlowLayout(FlowLayout.LEFT));
        automatic.add(new JLabel("How many:"));
        automatic.add(countSpinner);
        automatic.add(randomButton);
        automatic.add(sortedButton);
        automatic.add(clearButton);

        JPanel controls = new JPanel(new GridLayout(3, 1));
        controls.add(manual);
        controls.add(automatic);
        controls.add(stepRow);
        return controls;
    }

    private JPanel createInfo() {
        JPanel info = new JPanel(new GridLayout(5, 1));
        info.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        info.add(infoLabel);
        info.add(inOrderLabel);
        info.add(preOrderLabel);
        info.add(postOrderLabel);
        info.add(statusLabel);
        return info;
    }

    private Integer readNumber() {
        try {
            int number = Integer.parseInt(numberField.getText().trim());
            if (number >= -999 && number <= 999) {
                return number;
            }
        } catch (NumberFormatException e) {
        }
        statusLabel.setText("Enter a whole number from -999 to 999.");
        return null;
    }

    private void addNumber() {
        Integer number = readNumber();
        if (number == null) {
            return;
        }
        if (tree.insert(number)) {
            refresh(number);
            statusLabel.setText(number + " added.");
        } else {
            statusLabel.setText(number + " is already in the tree.");
        }
        numberField.setText("");
        numberField.requestFocusInWindow();
    }

    private void deleteNumber() {
        Integer number = readNumber();
        if (number == null) {
            return;
        }
        if (tree.delete(number)) {
            refresh(null);
            statusLabel.setText(number + " deleted.");
        } else {
            statusLabel.setText(number + " is not in the tree.");
        }
        numberField.setText("");
    }

    private void findNumber() {
        Integer number = readNumber();
        if (number == null) {
            return;
        }
        if (tree.contains(number)) {
            refresh(number);
            statusLabel.setText(number + " is in the tree (highlighted).");
        } else {
            statusLabel.setText(number + " is not in the tree.");
        }
    }

    private void fillTree(boolean sorted) {
        int count = (Integer) countSpinner.getValue();
        int[] numbers = new int[count];
        for (int i = 0; i < count; i++) {
            numbers[i] = random.nextInt(99) + 1;
        }
        if (sorted) {
            Arrays.sort(numbers);
        }

        int skipped = 0;
        for (int number : numbers) {
            if (!tree.insert(number)) {
                skipped++;
            }
        }
        refresh(null);
        statusLabel.setText((sorted ? "Sorted array: " : "Random array: ") + Arrays.toString(numbers)
                + (skipped > 0 ? "  (duplicates skipped: " + skipped + ")" : ""));
    }

    private void clearTree() {
        tree = new RedBlackTree();
        refresh(null);
        statusLabel.setText("The tree is cleared.");
    }

    private void startSteps(boolean delete) {
        Integer number = readNumber();
        if (number == null) {
            return;
        }
        stepDelete = delete;
        stepNumber = number;
        stepNumberExisted = tree.contains(number);
        steps = delete ? tree.deleteWithSteps(number) : tree.insertWithSteps(number);
        stepIndex = 0;
        setStepMode(true);
        statusLabel.setText((delete ? "Deleting " : "Adding ") + number
                + " step by step: press Next step, Auto play or Finish.");
        showStep();
    }

    private void showStep() {
        RedBlackTree.Step step = steps.get(stepIndex);
        treePanel.setTree(step.tree(), step.marked());
        stepLabel.setText("Step " + (stepIndex + 1) + " of " + steps.size() + ": " + step.text());
        nextButton.setText(stepIndex == steps.size() - 1 ? "Done" : "Next step");
    }

    private void nextStep() {
        if (stepIndex < steps.size() - 1) {
            stepIndex++;
            showStep();
        } else {
            finishSteps();
        }
    }

    private void togglePlay() {
        if (timer.isRunning()) {
            timer.stop();
            playButton.setText("Auto play");
        } else {
            timer.start();
            playButton.setText("Pause");
        }
    }

    private void finishSteps() {
        timer.stop();
        playButton.setText("Auto play");
        nextButton.setText("Next step");
        stepLabel.setText(" ");
        setStepMode(false);
        refresh(stepDelete ? null : stepNumber);
        if (stepDelete) {
            statusLabel.setText(stepNumber + (stepNumberExisted ? " deleted." : " is not in the tree."));
        } else {
            statusLabel.setText(stepNumber + (stepNumberExisted ? " is already in the tree." : " added."));
        }
        numberField.setText("");
    }

    // while the steps are shown, the other buttons are switched off
    private void setStepMode(boolean on) {
        for (JButton button : actionButtons) {
            button.setEnabled(!on);
        }
        numberField.setEnabled(!on);
        nextButton.setEnabled(on);
        playButton.setEnabled(on);
        finishButton.setEnabled(on);
    }

    private void refresh(Integer marked) {
        treePanel.setTree(tree.getRoot(), marked);
        infoLabel.setText("Nodes: " + tree.size() + ", height: " + tree.height()
                + ", red-black rules: " + (tree.isValid() ? "OK" : "BROKEN"));
        inOrderLabel.setText("In-order (ascending): " + tree.inOrder());
        preOrderLabel.setText("Pre-order (node first): " + tree.preOrder());
        postOrderLabel.setText("Post-order (node last): " + tree.postOrder());
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        SwingUtilities.invokeLater(() -> new TreeWindow().setVisible(true));
    }
}
