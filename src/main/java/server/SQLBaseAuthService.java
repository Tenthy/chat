package server;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class SQLBaseAuthService implements AuthService{

    private static Connection connection;
    private static Statement statement;

    @Override
    public void start() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:javadb.db");
            statement = connection.createStatement();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void stop() {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            if (connection != null) {
                connection.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public String getNickByLoginAndPass(String login, String pass) {
        try (ResultSet rs = statement.executeQuery("select * from users")) {
            while (rs.next()) {
                if (login.equals(rs.getString("login")) && pass.equals(rs.getString("password"))) {
                    return rs.getString("nickname");
                };
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
