import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserService {

    public static boolean registerUser(String username, String password) {
        String sql = "INSERT INTO users (username, password_hash) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            String protectedPassword = PasswordUtil.hashPassword(password);

            statement.setString(1, username);
            statement.setString(2, protectedPassword);

            statement.executeUpdate();

            System.out.println("Registration successful!");
            return true;

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.out.println("This username already exists.");
            } else {
                System.out.println("Registration failed: " + e.getMessage());
            }

            return false;
        }
    }

    public static boolean loginUser(String username, String password) {
        String sql = "SELECT password_hash FROM users WHERE username = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    String savedPassword = result.getString("password_hash");

                    if (PasswordUtil.verifyPassword(password, savedPassword)) {
                        System.out.println("Login successful. Welcome, " + username + "!");
                        return true;
                    }
                }
            }

            System.out.println("Invalid username or password.");
            return false;

        } catch (SQLException e) {
            System.out.println("Login failed: " + e.getMessage());
            return false;
        }
    }
}