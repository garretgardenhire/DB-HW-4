import java.sql.*;

public class jdbc_example {

    // The instance variables for the class
    private Connection connection;
    private Statement statement;

    // The constructor for the class
    public jdbc_example() {
        connection = null;
        statement = null;
    }

    // The main program", that tests the methods
    public static void main(String[] args) throws SQLException {
        String Username = "MYUSERNAME";              // Change to your own username
        String mysqlPassword = "MYMYSQLPASSWORD";    // Change to your own mysql Password

        jdbc_example test = new jdbc_example();
        test.connect(Username, mysqlPassword);
        test.initDatabase(Username, mysqlPassword, Username);

        String query1 = "SELECT * from Dish";
        String query2 = "SELECT restaurantName, city, dishName, price " +
                "FROM Restaurant, Dish, MenuItem " +
                "WHERE MenuItem.restaurantNo=Restaurant.restaurantID " +
                "AND MenuItem.dishNo=Dish.dishNo";

        test.query(query1);
        test.query(query2);

        test.disConnect();
    }

    // Connect to the database
    public void connect(String Username, String mysqlPassword) throws SQLException {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/" + Username + "?" +
                    "user=" + Username + "&password=" + mysqlPassword);
            //connection = DriverManager.getConnection("jdbc:mysql://localhost/" + Username +
             //       "?user=" + Username + "&password=" + mysqlPassword);
        }
        catch (Exception e) {
            throw e;
        }
    }

    // Disconnect from the database
    public void disConnect() throws SQLException {
        connection.close();
        statement.close();
    }

    // Execute an SQL query passed in as a String parameter
    // and print the resulting relation
    public void query(String q) {
        try {
            ResultSet resultSet = statement.executeQuery(q);
            System.out.println("\n---------------------------------");
            System.out.println("Query: \n" + q + "\n\nResult: ");
            print(resultSet);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Print the results of a query with attribute names on the first line
    // Followed by the tuples, one per line
    public void print(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int numColumns = metaData.getColumnCount();

        printHeader(metaData, numColumns);
        printRecords(resultSet, numColumns);
    }

    // Print the attribute names
    public void printHeader(ResultSetMetaData metaData, int numColumns) throws SQLException {
        for (int i = 1; i <= numColumns; i++) {
            if (i > 1)
                System.out.print(",  ");
            System.out.print(metaData.getColumnName(i));
        }
        System.out.println();
    }

    // Print the attribute values for all tuples in the result
    public void printRecords(ResultSet resultSet, int numColumns) throws SQLException {
        String columnValue;
        while (resultSet.next()) {
            for (int i = 1; i <= numColumns; i++) {
                if (i > 1)
                    System.out.print(",  ");
                columnValue = resultSet.getString(i);
                System.out.print(columnValue);
            }
            System.out.println("");
        }
    }

    // Insert into any table, any values from data passed in as String parameters
    public void insert(String table, String values) {
        String query = "INSERT into " + table + " values (" + values + ")" ;
        try {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Remove all records and fill them with values for testing
    // Assumes that the tables are already created
    public void initDatabase(String Username, String Password, String SchemaName) throws SQLException {
        statement = connection.createStatement();
        statement.executeUpdate("DELETE from FoodOrder");
        statement.executeUpdate("DELETE from MenuItem");
        statement.executeUpdate("DELETE from Dish");
        statement.executeUpdate("DELETE from Restaurant");

        insert("Restaurant", "0, 'Tasty Thai', 'Asian', 'Dallas'");
        insert("Restaurant", "3,'Eureka Pizza','Pizza', 'Fayetteville'");
        insert("Restaurant", "5,'Tasty Thai','Asian', 'Las Vegas'");

        insert("Dish", "13,'Spring Roll','ap'");
        insert("Dish", "15,'Pad Thai','en'");
        insert("Dish", "16,'Pad Stickers','ap'");
        insert("Dish", "22,'Masaman Curry','en'");
        insert("Dish", "10,'Custard','ds'");
        insert("Dish", "12,'Garlic Bread','ap'");
        insert("Dish", "44,'Salad','ap'");
        insert("Dish", "07,'Cheese Pizza','en'");
        insert("Dish", "19,'Pepperoni Pizza','en'");
        insert("Dish", "77,'Vegi Supreme Pizza','en'");

        insert("MenuItem", "0,0,13,8.00");
        insert("MenuItem", "1,0,16,9.00");
        insert("MenuItem", "2,0,44,10.00");
        insert("MenuItem", "3,0,15,19.00");
        insert("MenuItem", "4, 0,22,19.00");
        insert("MenuItem", "5, 3,44,6.25");
        insert("MenuItem", "6, 3,12,5.50");
        insert("MenuItem", "7, 3,7,12.50");
        insert("MenuItem", "8, 3,19,13.50");
        insert("MenuItem", "9,5,13,6.00");
        insert("MenuItem", "10,5,15,15.00");
        insert("MenuItem", "11,5,22,14.00");

        insert("FoodOrder", "0,2,STR_To_DATE('01,03,2017', '%d,%m,%Y'), '10:30'");
        insert("FoodOrder", "1,0,STR_To_DATE('02,03,2017', '%d,%m,%Y'), '15:33'");
        insert("FoodOrder", "2,3,STR_To_DATE('01,03,2017', '%d,%m,%Y'), '15:35'");
        insert("FoodOrder", "3,5,STR_To_DATE('03,03,2017', '%d,%m,%Y'), '21:00'");
        insert("FoodOrder", "4,7,STR_To_DATE('01,03,2017', '%d,%m,%Y'), '18:11'");
        insert("FoodOrder", "5,7,STR_To_DATE('04,03,2017', '%d,%m,%Y'), '18:51'");
        insert("FoodOrder", "6,9,STR_To_DATE('01,03,2017', '%d,%m,%Y'), '19:00'");
        insert("FoodOrder", "7,11,STR_To_DATE('05,03,2017', '%d,%m,%Y'), '17:15'");
    }
}