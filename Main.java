import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        // make system look like the same as the current os themes
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // starts the GUI
        SwingUtilities.invokeLater(() -> new Events().setVisible(true));
    }
}