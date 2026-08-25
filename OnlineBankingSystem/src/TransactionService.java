import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionService {

    public static boolean deposit(
            String username,
            String accountNumber,
            BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Deposit amount must be greater than zero.");
            return false;
        }

        return updateMoney(
                username, accountNumber, amount,
                "DEPOSIT", true
        );
    }

    public static boolean withdraw(
            String username,
            String accountNumber,
            BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Withdrawal amount must be greater than zero.");
            return false;
        }

        return updateMoney(
                username, accountNumber, amount,
                "WITHDRAWAL", false
        );
    }

    private static boolean updateMoney(
            String username,
            String accountNumber,
            BigDecimal amount,
            String transactionType,
            boolean isDeposit) {

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                Integer accountId = getAccountId(
                        connection, username, accountNumber
                );

                if (accountId == null) {
                    System.out.println("Account not found.");
                    connection.rollback();
                    return false;
                }

                if (!isDeposit) {
                    BigDecimal balance = getBalance(connection, accountId);

                    if (balance.compareTo(amount) < 0) {
                        System.out.println("Insufficient balance.");
                        connection.rollback();
                        return false;
                    }
                }

                String updateBalance;

                if (isDeposit) {
                    updateBalance =
                            "UPDATE accounts SET balance = balance + ? "
                            + "WHERE account_id = ?";
                } else {
                    updateBalance =
                            "UPDATE accounts SET balance = balance - ? "
                            + "WHERE account_id = ?";
                }

                try (PreparedStatement statement =
                             connection.prepareStatement(updateBalance)) {

                    statement.setBigDecimal(1, amount);
                    statement.setInt(2, accountId);
                    statement.executeUpdate();
                }

                String saveTransaction = """
                        INSERT INTO transactions
                        (account_id, transaction_type, amount, description)
                        VALUES (?, ?, ?, ?)
                        """;

                try (PreparedStatement statement =
                             connection.prepareStatement(saveTransaction)) {

                    statement.setInt(1, accountId);
                    statement.setString(2, transactionType);
                    statement.setBigDecimal(3, amount);
                    statement.setString(
                            4,
                            isDeposit
                                    ? "Money deposited"
                                    : "Money withdrawn"
                    );
                    statement.executeUpdate();
                }

                connection.commit();
                System.out.println(
                        isDeposit
                                ? "Deposit successful!"
                                : "Withdrawal successful!"
                );
                return true;

            } catch (SQLException e) {
                connection.rollback();
                System.out.println("Transaction failed: " + e.getMessage());
                return false;

            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return false;
        }
    }

    private static Integer getAccountId(
            Connection connection,
            String username,
            String accountNumber) throws SQLException {

        String sql = """
                SELECT account_id
                FROM accounts
                WHERE account_number = ?
                AND user_id = (
                    SELECT user_id FROM users WHERE username = ?
                )
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, accountNumber);
            statement.setString(2, username);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return result.getInt("account_id");
                }
            }
        }

        return null;
    }

    private static BigDecimal getBalance(
            Connection connection,
            int accountId) throws SQLException {

        String sql = "SELECT balance FROM accounts WHERE account_id = ?";

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, accountId);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return result.getBigDecimal("balance");
                }
            }
        }

        return BigDecimal.ZERO;
    }
}