import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

class User {
    String nama, id, password;
    double saldo;
    String foto;

    User(String nama, String id, String password, double saldo, String foto) {
        this.nama = nama;
        this.id = id;
        this.password = password;
        this.saldo = saldo;
        this.foto = foto;
    }
}

public class DompetDigitalGUI {
    static Connection conn;
    static User loggedInUser = null;

    public static void main(String[] args) {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dompet_digital", "root", "");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal koneksi DB: " + e.getMessage());
            return;
        }
        SwingUtilities.invokeLater(DompetDigitalGUI::showMainMenu);
    }

    static void showMainMenu() {
        JFrame frame = new JFrame("Dompet Digital");
        frame.setSize(450, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(new Color(245, 245, 255));

        JPanel panel = new JPanel(new GridLayout(6, 1, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        panel.setBackground(new Color(245, 245, 255));

        JLabel title = new JLabel("Selamat Datang!", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(60, 60, 120));

        JButton btnRegister = createButton("Registrasi");
        JButton btnLogin = createButton("Login");
        JButton btnExit = createButton("Keluar");

        btnRegister.addActionListener(e -> showRegisterForm(frame));
        btnLogin.addActionListener(e -> showLoginForm(frame));
        btnExit.addActionListener(e -> System.exit(0));

        panel.add(title);
        panel.add(btnRegister);
        panel.add(btnLogin);
        panel.add(btnExit);

        frame.setContentPane(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    static JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        button.setBackground(new Color(100, 149, 237));
        button.setForeground(Color.white);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    static void showRegisterForm(JFrame frame) {
        JTextField nameField = new JTextField();
        JTextField idField = new JTextField();
        JPasswordField passField = new JPasswordField();

        Object[] message = {
            "Nama:", nameField,
            "ID:", idField,
            "Password:", passField
        };

        int option = JOptionPane.showConfirmDialog(frame, message, " Registrasi", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO users (id, nama, password, saldo) VALUES (?, ?, ?, 0)");
                ps.setString(1, idField.getText());
                ps.setString(2, nameField.getText());
                ps.setString(3, new String(passField.getPassword()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(frame, "Registrasi berhasil!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(frame, "Gagal registrasi: " + e.getMessage());
            }
        }
    }

    static void showLoginForm(JFrame frame) {
        JTextField idField = new JTextField();
        JPasswordField passField = new JPasswordField();

        Object[] message = {
            "ID:", idField,
            "Password:", passField
        };

        int option = JOptionPane.showConfirmDialog(frame, message, " Login", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE id = ? AND password = ?");
                ps.setString(1, idField.getText());
                ps.setString(2, new String(passField.getPassword()));
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    loggedInUser = new User(
                            rs.getString("nama"),
                            rs.getString("id"),
                            rs.getString("password"),
                            rs.getDouble("saldo"),
                            rs.getString("foto")
                        
                    );
                    showDashboard(frame);
                } else {
                    JOptionPane.showMessageDialog(frame, "ID atau password salah!");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(frame, "Gagal login: " + e.getMessage());
            }
        }
    }

    static void showDashboard(JFrame frame) {
        JFrame dashboard = new JFrame(" Dashboard - " + loggedInUser.nama);
        dashboard.setSize(500, 450);
        dashboard.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dashboard.getContentPane().setBackground(new Color(240, 248, 255));

JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
panel.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));
panel.setBackground(new Color(240, 248, 255));

if (loggedInUser.foto != null && !loggedInUser.foto.isEmpty()) {
    try {
        ImageIcon icon = new ImageIcon(loggedInUser.foto);
        Image image = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        JLabel fotoLabel = new JLabel(new ImageIcon(image));
        fotoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(fotoLabel);
    } catch (Exception e) {
        System.out.println("Gagal menampilkan foto profil: " + e.getMessage());
    }
}

        JLabel welcome = new JLabel("Halo, " + loggedInUser.nama, SwingConstants.CENTER);
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 20));
        welcome.setForeground(new Color(25, 25, 112));

        final boolean[] showSaldo = {true};
        JLabel saldoLabel = new JLabel("", SwingConstants.CENTER);
        saldoLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        saldoLabel.setForeground(new Color(0, 128, 0));

        ImageIcon eyeIcon = new ImageIcon("eye.png");
        ImageIcon eyeOffIcon = new ImageIcon("eye-off.png");

        JButton btnUbahFoto = createButton("Ubah Foto Profil");
btnUbahFoto.addActionListener(e -> {
    JFileChooser fileChooser = new JFileChooser();
    int result = fileChooser.showOpenDialog(dashboard);
    if (result == JFileChooser.APPROVE_OPTION) {
        String pathFotoBaru = fileChooser.getSelectedFile().getAbsolutePath();
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE users SET foto = ? WHERE id = ?");
            ps.setString(1, pathFotoBaru);
            ps.setString(2, loggedInUser.id);
            ps.executeUpdate();
            loggedInUser.foto = pathFotoBaru;
            JOptionPane.showMessageDialog(dashboard, "Foto profil berhasil diubah.\nSilakan logout dan login kembali untuk melihat perubahan.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(dashboard, "Gagal menyimpan foto: " + ex.getMessage());
        }
    }
});


        JButton toggleVisibility = new JButton(eyeOffIcon);
        toggleVisibility.setFocusPainted(false);
        toggleVisibility.setContentAreaFilled(false);
        toggleVisibility.setBorderPainted(false);
        toggleVisibility.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        Runnable updateSaldoLabel = () -> {
            if (showSaldo[0]) {
               saldoLabel.setText("Saldo Anda: ••••••");
                toggleVisibility.setIcon(eyeIcon);
            } else {
                saldoLabel.setText("Saldo Anda: Rp" + loggedInUser.saldo);
                toggleVisibility.setIcon(eyeOffIcon);
            }
        };
        updateSaldoLabel.run();

        toggleVisibility.addActionListener(e -> {
            showSaldo[0] = !showSaldo[0];
            updateSaldoLabel.run();
        });

        JPanel saldoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        saldoPanel.setBackground(new Color(240, 248, 255));
        saldoPanel.add(saldoLabel);
        saldoPanel.add(toggleVisibility);

        JButton btnTambah = createButton("Tambah Saldo");
        JButton btnTransfer = createButton("Transfer");
        JButton btnRiwayat = createButton("Riwayat Transaksi");
        JButton btnHapusAkun = createButton("Hapus Akun");
        JButton btnLogout = createButton("Logout");

        btnTambah.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(dashboard, "Masukkan jumlah saldo:");
            if (input != null) {
                try {
                    double tambah = Double.parseDouble(input);
                    loggedInUser.saldo += tambah;
                    conn.prepareStatement("UPDATE users SET saldo = " + loggedInUser.saldo + " WHERE id = '" + loggedInUser.id + "'").executeUpdate();
                    conn.prepareStatement("INSERT INTO riwayat (id_user, keterangan) VALUES ('" + loggedInUser.id + "', 'Tambah saldo: Rp" + tambah + "')").executeUpdate();
                    updateSaldoLabel.run();
                    JOptionPane.showMessageDialog(dashboard, "Saldo berhasil ditambahkan!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dashboard, "Input tidak valid.");
                }
            }
        });

        btnTransfer.addActionListener(e -> {
            JTextField idField = new JTextField();
            JTextField amountField = new JTextField();
            Object[] message = {
                "ID Penerima:", idField,
                "Jumlah:", amountField
            };

            int opt = JOptionPane.showConfirmDialog(dashboard, message, "Transfer Dana", JOptionPane.OK_CANCEL_OPTION);
            if (opt == JOptionPane.OK_OPTION) {
                try {
                    String idPenerima = idField.getText();
                    double jumlah = Double.parseDouble(amountField.getText());

                    PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE id = ?");
                    ps.setString(1, idPenerima);
                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {
                        if (loggedInUser.saldo >= jumlah) {
                            double saldoPenerima = rs.getDouble("saldo") + jumlah;
                            loggedInUser.saldo -= jumlah;

                            conn.prepareStatement("UPDATE users SET saldo = " + loggedInUser.saldo + " WHERE id = '" + loggedInUser.id + "'").executeUpdate();
                            conn.prepareStatement("UPDATE users SET saldo = " + saldoPenerima + " WHERE id = '" + idPenerima + "'").executeUpdate();

                            conn.prepareStatement("INSERT INTO riwayat (id_user, keterangan) VALUES ('" + loggedInUser.id + "', 'Transfer ke " + rs.getString("nama") + ": Rp" + jumlah + "')").executeUpdate();
                            conn.prepareStatement("INSERT INTO riwayat (id_user, keterangan) VALUES ('" + idPenerima + "', 'Terima dari " + loggedInUser.nama + ": Rp" + jumlah + "')").executeUpdate();

                            updateSaldoLabel.run();
                            JOptionPane.showMessageDialog(dashboard, "Transfer berhasil!");
                        } else {
                            JOptionPane.showMessageDialog(dashboard, "Saldo tidak cukup!");
                        }
                    } else {
                        JOptionPane.showMessageDialog(dashboard, "Penerima tidak ditemukan.");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dashboard, "Input tidak valid.");
                }
            }
        });

        btnRiwayat.addActionListener(e -> {
            try {
                StringBuilder sb = new StringBuilder();
                PreparedStatement ps = conn.prepareStatement("SELECT keterangan FROM riwayat WHERE id_user = ?");
                ps.setString(1, loggedInUser.id);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    sb.append("- ").append(rs.getString("keterangan")).append("\n");
                }
                JOptionPane.showMessageDialog(dashboard, sb.length() > 0 ? sb.toString() : "Tidak ada transaksi.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dashboard, "Gagal ambil riwayat: " + ex.getMessage());
            }
        });

        btnHapusAkun.addActionListener(e -> {
            int konfirmasi = JOptionPane.showConfirmDialog(dashboard, "Apakah Anda yakin ingin menghapus akun Anda?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (konfirmasi == JOptionPane.YES_OPTION) {
                try {
                    PreparedStatement delRiwayat = conn.prepareStatement("DELETE FROM riwayat WHERE id_user = ?");
                    delRiwayat.setString(1, loggedInUser.id);
                    delRiwayat.executeUpdate();

                    PreparedStatement delUser = conn.prepareStatement("DELETE FROM users WHERE id = ?");
                    delUser.setString(1, loggedInUser.id);
                    delUser.executeUpdate();

                    JOptionPane.showMessageDialog(dashboard, "Akun Anda berhasil dihapus.");
                    loggedInUser = null;
                    dashboard.dispose();
                    showMainMenu();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dashboard, "Gagal menghapus akun: " + ex.getMessage());
                }
            }
        });

        btnLogout.addActionListener(e -> {
            loggedInUser = null;
            dashboard.dispose();
        });

        panel.add(welcome);
        panel.add(saldoPanel);
        panel.add(btnTambah);
        panel.add(btnTransfer);
        panel.add(btnRiwayat);
        panel.add(btnHapusAkun);
        panel.add(btnLogout);

        dashboard.setContentPane(panel);
        dashboard.setLocationRelativeTo(null);
        dashboard.setVisible(true);
    }
}
