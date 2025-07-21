import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class login {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(login::showLoginForm);
    }

    static void showLoginForm() {
        JFrame frame = new JFrame("Login Form");
        frame.setSize(350, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        frame.setContentPane(panel);
        placeComponents(panel);

        frame.setVisible(true);
    }

    static void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel userLabel = new JLabel("ID:");
        userLabel.setBounds(30, 30, 80, 25);
        panel.add(userLabel);

        JTextField userText = new JTextField(20);
        userText.setBounds(120, 30, 160, 25);
        panel.add(userText);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(30, 70, 80, 25);
        panel.add(passwordLabel);

        JPasswordField passwordText = new JPasswordField(20);
        passwordText.setBounds(120, 70, 160, 25);
        panel.add(passwordText);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(120, 110, 80, 25);
        panel.add(loginButton);

        loginButton.addActionListener(e -> {
            String id = userText.getText();
            String password = new String(passwordText.getPassword());

            // Data user hardcoded
            String validId = "admin";
            String validPassword = "12345";

            if (id.equals(validId) && password.equals(validPassword)) {
                JOptionPane.showMessageDialog(panel, "Login berhasil! Selamat datang, " + validId + "!");
            } else {
                JOptionPane.showMessageDialog(panel, "Login gagal! ID atau password salah.");
            }
        });
    }
}
