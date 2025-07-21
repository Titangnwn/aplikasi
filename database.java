import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class database {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/dompet_db"; 
        String user = "root"; 
        String password = ""; 

        try {
            // Membuat koneksi
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Koneksi berhasil!");

            // Tutup koneksi
            conn.close();
        } catch (SQLException e) {
            System.out.println("Koneksi gagal: " + e.getMessage());
        }
    }
}

