//bash file permission denied: chmod u+x compileandrun.bash
import java.sql.*;
import java.util.Scanner;

public class HW4 {

    // The instance variables for the class
    private Connection connection;
    private Statement statement;

    // The constructor for the class
    public HW4() {
        connection = null;
        statement = null;
    }

    // The main program, that tests the methods
    public static void main(String[] args) throws SQLException {
        String Username = "anyates";              // Change to your own username
        String mysqlPassword = "Simonsays2025!";    // Change to your own mysql Password

        HW4 test = new HW4();
        test.connect(Username, mysqlPassword);
        test.initDatabase(Username, mysqlPassword, Username);

		//Part 1
		//test.city();
		//Part 2
		test.addUser();

		//some of Part 6
        test.disConnect();
    }
	
	public void city ()
	{
		Scanner userInput = new Scanner(System.in);
		String cityName;
		System.out.print("Enter city: ");
		cityName = userInput.next();
		
		String queryAgents = "SELECT * " +
								"FROM AGENTS " +
								"WHERE A_CITY = '" + cityName + "'";
								
		String queryClients = "SELECT * " +
								"FROM CLIENTS " +
								"WHERE C_CITY = '" + cityName + "'";											
								
		query(queryAgents);
		query(queryClients);
	}
	
	public void addUser()throws SQLException {
        try {
		//Get highest ID value in CLIENTS table
		int max = 0;
		statement = connection.createStatement();
		String sql = "SELECT MAX(C_ID) from CLIENTS";
		ResultSet rs = statement.executeQuery(sql);
		if (rs.next()) 
			max = rs.getInt(1);
		
		//Ask for user details
		Scanner userInput = new Scanner(System.in);
		String userName, userCity, userZip;
		System.out.print("Enter name: ");
		userName = userInput.next();
		System.out.print("Enter city: ");
		userCity = userInput.next();
		System.out.print("Enter zipcode: ");
		userZip = userInput.next();
		int userID = max + 1;
		
		//Insert user IDs into CLIENTS table
		insert("CLIENTS", "'" + userID + "', '" + userName + "', '" + userCity + "', '" + userZip + "'");
		String queryClients = "SELECT * FROM CLIENTS";
		query(queryClients);
		}
		catch (Exception e) {
            throw e;
        }
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
		
		//DELETE FROM HERE ON IF DON'T WANT TO DELETE DATABASE EACH TIME
		statement.executeUpdate("DELETE from POLICIES_SOLD");
        statement.executeUpdate("DELETE from CLIENTS");
        statement.executeUpdate("DELETE from AGENTS");
        statement.executeUpdate("DELETE from POLICY");

        insert("CLIENTS", "101, 'CHRIS', 'DALLAS', 43214");
        insert("CLIENTS", "102, 'OLIVIA', 'BOSTON', 83125");
        insert("CLIENTS", "103, 'ETHAN', 'FAYETTEVILLE', 72701");
		insert("CLIENTS", "104, 'DANIEL', 'NEWYORK', 53421");
		insert("CLIENTS", "105, 'TAYLOR', 'ROGERS', 78291");
		insert("CLIENTS", "106, 'CLAIRE', 'PHOENIX', 43214");

		insert("AGENTS", "201, 'ANDRE', 'DALLAS', 43214");
		insert("AGENTS", "202, 'PHILIP', 'PHEOENIX', 85011");
		insert("AGENTS", "203, 'JERRY', 'BOSTON', 83125");
		insert("AGENTS", "204, 'BRYAN', 'ROGERS', 78291");
		insert("AGENTS", "205, 'TOMMY', 'DALLAS', 43214");
		insert("AGENTS", "206, 'BRANT', 'FAYETTEVILLE', 72701");
		insert("AGENTS", "207, 'SMITH', 'ROGERS', 78291");	

		insert("POLICY", "301, 'CIGNAHEALTH', 'DENTAL', 5");
		insert("POLICY", "302, 'GOLD', 'LIFE', 8");
		insert("POLICY", "303, 'WELLCARE', 'HOME', 10");
		insert("POLICY", "304, 'UNITEDHEALTH', 'HEALTH', 7");
		insert("POLICY", "305, 'UNITEDCAR', 'VEHICLE', 9");

		insert("POLICIES_SOLD", "401, 204, 106, 303, '2020-01-02', 2000.00");
		insert("POLICIES_SOLD", "402, 201, 105, 305, '2019-08-11', 1500.00");
		insert("POLICIES_SOLD", "403, 203, 106, 301, '2019-09-08', 3000.00");
		insert("POLICIES_SOLD", "404, 207, 101, 305, '2019-06-21', 1500.00");
		insert("POLICIES_SOLD", "405, 203, 104, 302, '2019-11-14', 4500.00");
		insert("POLICIES_SOLD", "406, 207, 105, 305, '2019-12-25', 1500.00");
		insert("POLICIES_SOLD", "407, 205, 103, 304, '2020-10-15', 5000.00");
		insert("POLICIES_SOLD", "408, 204, 103, 304, '2020-02-15', 5000.00");
		insert("POLICIES_SOLD", "409, 203, 103, 304, '2020-01-10', 5000.00");
		insert("POLICIES_SOLD", "410, 202, 103, 303, '2020-01-30', 2000.00");
    }
}
