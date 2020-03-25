//bash file permission denied: chmod u+x compileandrun.bash
import java.sql.*;
import java.util.Scanner;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
		
		Scanner userInput = new Scanner(System.in);

		int choice;
		boolean flag = true;

		while(flag)
		{
			menu();
			System.out.println("Enter Choice (by integer): ");
			choice = userInput.nextInt();
			switch(choice)
			{
				case 1:
					test.Agents();
					break;
				case 2:
					test.addUser();
					break;
				case 3:
					test.policiesSold();
					break;
				case 4:
					//code
					break;
				case 5:
					//code
					break;
				case 6:
					//code
					test.disConnect();
					userInput.close();
					flag = false;
					System.out.println("Program Ended");
					break;
				default:
					System.out.println("invalid choice");
			}
		}
    }
	
	static public void menu()
	{
		System.out.println(" ");
		System.out.println("Menu:");
		System.out.println("1. Find all existing agents in a given city");
		System.out.println("2. Purchase an avaiable policy from a particular agent");
		System.out.println("3. List all policies sold by a particular agent");
		System.out.println("4. Cancel a policy");
		System.out.println("5. Add a new agent for a city");
		System.out.println("6. Quit");
		System.out.println(" ");
	}
	
	//Display agents and clients from a certain city
	public void Agents ()
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

	//Add user to CLIENTS table
	public void addUser()throws SQLException {
		try {
			//Get highest ID value in CLIENTS table
			statement = connection.createStatement();
			int max = 0;
			String queryID = "SELECT MAX(C_ID) from CLIENTS";
			ResultSet rs = statement.executeQuery(queryID);
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
			
			//Insert user ID into CLIENTS table
			insert("CLIENTS", "'" + userID + "', '" + userName + "', '" + userCity + "', '" + userZip + "'");
			String queryClients = "SELECT * FROM CLIENTS";
			query(queryClients);
			
			//Send the clients ID and city to lookupPolicy
			lookupPolicy(userCity, userID);
		}
		catch (Exception e) {
            throw e;
        }
	}
	
	//Check agents and type of policy
	public void lookupPolicy(String city, int clientID)throws SQLException {
        try {
			Scanner userInput = new Scanner(System.in);
			String type, amount;
			int agentID;
			System.out.print("Enter type of policy you want to purchase: ");
			type = userInput.next();
			
			String queryType = "SELECT TYPE FROM POLICY WHERE TYPE = '" + type + "'";
			ResultSet rsType = statement.executeQuery(queryType);
			//Check if policy type is found
			if (rsType.next()) 
			{
				//Display all agents in the clients city
				String queryCity = "SELECT * FROM AGENTS WHERE A_CITY = '" + city + "'";
				ResultSet rsCity = statement.executeQuery(queryCity);
				query(queryCity);
				
				//POTENTIALLY ADD COUNT AND AUTO PICK AGENT IF ONLY ONE
				//Let user select the client they want to purchase the policy from
				System.out.print("Enter ID of the agent you want to purchase the policy from: ");
				agentID = userInput.nextInt();				
				String queryAgent = "SELECT A_ID FROM AGENTS WHERE A_CITY = '" + city + "' AND A_ID = '" + agentID +"'";
				ResultSet rsAgent = statement.executeQuery(queryAgent);
				//If selected agent is valid, send agent ID and client ID to buyPolicy
				if (rsAgent.next()) 
				{
					agentID = rsAgent.getInt(1);
					String queryAllP = "SELECT * FROM POLICY";
					query(queryAllP);
					buyPolicy(agentID, clientID);
				}
				else
					System.out.println("Not a valid agent");
			}
			else
				System.out.println("That type of policy isn't available");
		}
		catch (Exception e) {
            throw e;
        }
	}

	//Buy policy and insert into table
	public void buyPolicy(int agentID, int clientID) throws SQLException {
		try {
			Scanner userInput = new Scanner(System.in);
			String policyID;
			float amount;
			System.out.print("Enter the POLICY_ID you want to purchase: ");
			policyID = userInput.next();
			
			String queryComPer = "SELECT COMMISSION_PERCENTAGE FROM POLICY WHERE POLICY_ID = '" + policyID + "'";
			ResultSet rsComPer = statement.executeQuery(queryComPer);
			//Check if policy type is found
			if (rsComPer.next()) 
			{
				statement = connection.createStatement();
				
				//Get today's date
				java.util.Date utilDate = new java.util.Date();
				java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
				
				System.out.print("Enter the amount you want to purchase: ");
				amount = userInput.nextFloat();
				
				//Get highest ID value in POLICIES_SOLD table
				int max = 0;
				String queryID = "SELECT MAX(PURCHASE_ID) from POLICIES_SOLD";
				ResultSet rs = statement.executeQuery(queryID);
				if (rs.next())					
					max = rs.getInt(1);
				int policiessoldID = max + 1;
				insert("POLICIES_SOLD", "'" + policiessoldID + "', '" + agentID + "', '" + clientID + "', '" + policyID + "', '" + sqlDate +"' , '" + amount + "'");
				String queryAllPS = "SELECT * FROM POLICIES_SOLD";
				query(queryAllPS);
			}
			else
				System.out.println("POLICY_ID not available");
		}
		catch (Exception e) {
            throw e;
        }
	}
	
	//List policies sold by agent
	public void policiesSold()throws SQLException {
		try {
		Scanner userInput = new Scanner(System.in);
		String agentCity, agentName;
		System.out.print("Enter Agent's name: ");
		agentName = userInput.next();
		System.out.print("Enter Agent's city: ");
		agentCity = userInput.next();
		
		String queryAgentID = "SELECT A_ID FROM AGENTS WHERE A_NAME = '" + agentName + "' AND A_CITY = '" + agentCity + "'";
		ResultSet rsAgentID = statement.executeQuery(queryAgentID);
		//Check if agent is found
		if (rsAgentID.next())
		{
			int agentID = rsAgentID.getInt(1);
			System.out.println("The agent was found");
			String queryPoliciesSold = "SELECT P.NAME, P.TYPE, P.COMMISSION_PERCENTAGE " +
								"FROM POLICY P, POLICIES_SOLD PS " +
								"WHERE PS.AGENT_ID = '" + agentID + "' AND PS.POLICY_ID = P.POLICY_ID";										
								
			query(queryPoliciesSold);
		}
		else
			System.out.println("The agent was not found");
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
		/*statement.executeUpdate("DELETE from POLICIES_SOLD");
        statement.executeUpdate("DELETE from CLIENTS");
        statement.executeUpdate("DELETE from AGENTS");
        statement.executeUpdate("DELETE from POLICY");

        insert("CLIENTS", "101, 'CHRIS', 'DALLAS', 43214");
        insert("CLIENTS", "102, 'OLIVIA', 'BOSTON', 83125");
        insert("CLIENTS", "103, 'ETHAN', 'FAYETTEVILLE', 72701");
		insert("CLIENTS", "104, 'DANIEL', 'NEWYORK', 53421");
		insert("CLIENTS", "105, 'TAYLOR', 'ROGERS', 78291");
		insert("CLIENTS", "106, 'CLAIRE', 'PHOENIX', 43214");

		insert("AGENTS", "201, 'ANDREW', 'DALLAS', 43214");
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
		insert("POLICIES_SOLD", "410, 202, 103, 303, '2020-01-30', 2000.00");*/
    }
}
