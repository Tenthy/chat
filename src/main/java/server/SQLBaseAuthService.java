package server;
import java.sql.*;

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
    public String getNickByLoginAndPass(String login, String password) {
        try (ResultSet rs = statement.executeQuery("select * from users")) {
            while (rs.next()) {
                if (login.equals(rs.getString("login")) && password.equals(rs.getString("password"))) {
                    return rs.getString("nickname");
                };
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public String setNickname(String newNickname, String oldNickname, String login, String password) {
        try {
            statement.executeUpdate("update users set nickname='" + newNickname +
                    "' where nickname='" + oldNickname + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return getNickByLoginAndPass(login, password);
    }
}
