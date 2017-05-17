package database;

import java.sql.*;

/**
 * Created by DevM on 5/17/2017.
 */
public class Database {
    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    private static Database ourInstance = new Database();

    public static Database getInstance() {
        return ourInstance;
    }

    private Database() {
        try {
            connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/carendar","carendar","carendar");
            statement = connect.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeDatabase(){
        try {
            connect.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
