//INFINTIE LOOP WITH LETTERS
import java.sql.*;
import java.util.Scanner;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.text.DecimalFormat;

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
			try{
				menu();
				System.out.println("Enter Choice (by integer): ");
				choice = userInput.nextInt();
				switch(choice)
				{
					case 1:
						test.buyPolicy("204",116);
						test.Agents();
						break;
					case 2:
						test.addUser();
						break;
					case 3:
						test.policiesSold();
						break;
					case 4:
						test.cancelPolicy();
						break;
					case 5:
						test.addAgent();
						break;
					case 6:
						test.disConnect();
						flag = false;
						System.out.println("Program Ended");
						break;
					default:
						System.out.println("invalid choice");
				}
			}
			catch (java.util.InputMismatchException err) {
				System.out.println("");
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
	
	public String truncate(String word)
	{
		if (word.length() > 50)
		{
			word = word.substring(0,49);
			System.out.println("Entry has been truncated.");
		}
		return word;
	}
	
	//Display agents and clients from a certain city
	//Part 1
	public void Agents() throws SQLException 
	{
		try 
		{
			Scanner userInput = new Scanner(System.in);
			String cityName;
			System.out.print("Enter city: ");
			cityName = truncate(userInput.next());
			
			String queryAgents = "SELECT * FROM AGENTS WHERE A_CITY = '" + cityName + "'";						
			String queryClients = "SELECT * FROM CLIENTS WHERE C_CITY = '" + cityName + "'";	
			String queryAgentsCheck = "SELECT A_CITY FROM AGENTS WHERE A_CITY = '" + cityName + "'";						
			String queryClientsCheck = "SELECT C_CITY FROM CLIENTS WHERE C_CITY = '" + cityName + "'";				
			ResultSet rsAgents = statement.executeQuery(queryAgentsCheck);
				
			//Check if policy type is found
			if (rsAgents.next()) 
			{
				System.out.println("All Agents in the requested city");
				query(queryAgents);
			}
			else
				System.out.println("No Agents in that city");
			
			ResultSet rsClients = statement.executeQuery(queryClientsCheck);
			if(rsClients.next())
			{
				System.out.println("All Clients in the requested city");
				query(queryClients);
			}
			else
				System.out.println("No Clients in that city");

		}
		catch (Exception e) 
		{
            throw e;
        }
	}

	//Add user to CLIENTS table
	//Part 2
	public void addUser()throws SQLException 
	{
		try 
		{
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
			System.out.print("Enter Client's name: ");
			userName = truncate(userInput.next());
			System.out.print("Enter Client's city: ");
			userCity = truncate(userInput.next());
			System.out.print("Enter Client's zipcode: ");
			userZip = userInput.next();
			
			while (!userZip.matches("[0-9]+"))
			{
				System.out.print("Enter Client's zipcode: ");
				userZip = userInput.next();	
			}
			
			int userID = max + 1;
			
			//Insert user ID into CLIENTS table
			insert("CLIENTS", "'" + userID + "', '" + userName + "', '" + userCity + "', '" + userZip + "'");
			String queryClients = "SELECT * FROM CLIENTS";
			query(queryClients);
			
			//Send the clients ID and city to lookupPolicy
			lookupPolicy(userCity, userID);
		}
		catch (Exception e) 
		{
            throw e;
        }
	}
	
	//Check agents and type of policy
	//Part 2
	public void lookupPolicy(String city, int clientID)throws SQLException 
	{
        try 
		{
			Scanner userInput = new Scanner(System.in);
			String type, amount, agentID;
			System.out.println("Policies available for purchase: ");
			String queryTypeIn = "SELECT TYPE FROM POLICY";
			query(queryTypeIn);
			System.out.print("Enter type of policy you want to purchase: ");
			type = truncate(userInput.next());
			
			String queryType = "SELECT TYPE FROM POLICY WHERE TYPE = '" + type + "'";
			ResultSet rsType = statement.executeQuery(queryType);
			
			//Check if policy type is found
			if (rsType.next()) 
			{
				//Display all agents in the clients city
				System.out.println("Available Agents: ");
				String queryCity = "SELECT * FROM AGENTS WHERE A_CITY = '" + city + "'";
				ResultSet rsCity = statement.executeQuery(queryCity);
				
				//Check if agents in the client's city
				if (rsCity.next())
				{
					query(queryCity);
					//POTENTIALLY ADD COUNT AND AUTO PICK AGENT IF ONLY ONE
					//Let user select the agent they want to purchase the policy from
					System.out.print("Enter ID of the agent you want to purchase the policy from: ");
					agentID = userInput.next();	
					
					while (!agentID.matches("[0-9]+"))
					{
						System.out.print("Enter the ID of the angent you want to purchase the policy from: ");
						agentID = userInput.next();	
					}			
					
					String queryAgent = "SELECT A_ID FROM AGENTS WHERE A_CITY = '" + city + "' AND A_ID = '" + agentID +"'";
					ResultSet rsAgent = statement.executeQuery(queryAgent);
					//If selected agent is valid, send agent ID and client ID to buyPolicy
					
					if (rsAgent.next()) 
					{
						agentID = rsAgent.getString(1);
						String queryAllP = "SELECT * FROM POLICY";
						query(queryAllP);
						buyPolicy(agentID, clientID);
					}
					else
						System.out.println("Not a valid agent");
				}
				else
					System.out.println("No Agents avaiable in that city.");
			}
			else
				System.out.println("That type of policy isn't available");
		}
		catch (Exception e) 
		{
            throw e;
        }
	}

	//Buy policy and insert into table
	//Part 2
	public void buyPolicy(String agentID, int clientID) throws SQLException {
		try {
			Scanner userInput = new Scanner(System.in);
			String policyID, amount;
			float amountFloat = 77000.01f;
			System.out.print("Enter the Policy ID you want to purchase: ");
			policyID = userInput.next();
			
			while (!policyID.matches("[0-9]+"))
			{
				System.out.print("Enter the Policy ID you want to purchase: ");
				policyID = userInput.next();	
			}
			
			String queryComPer = "SELECT COMMISSION_PERCENTAGE FROM POLICY WHERE POLICY_ID = '" + policyID + "'";
			ResultSet rsComPer = statement.executeQuery(queryComPer);
			//Check if policy type is found
			if (rsComPer.next()) 
			{
				statement = connection.createStatement();
				
				//Get today's date
				java.util.Date utilDate = new java.util.Date();
				java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
				
				amount = "a";
	
				while (true && amountFloat < 10000.00)
				{
					try 
					{	 
						System.out.print("Enter the amount you want to purchase: ");
						amount = userInput.next();
						//Checking valid float using parseInt() method 
						amountFloat = Float.parseFloat(amount); 
						DecimalFormat df = new DecimalFormat("#.##");
						amount = df.format(amount); 
						
						System.out.println(amount + " is a valid float number"); 
						
						break;
					}  
					catch (NumberFormatException e) 
					{ 
							System.out.println(amount + " is not a valid float number"); 
							continue;
					}
				}					
				
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
				System.out.println("Policy ID not available");
		}
		catch (Exception e) {
            throw e;
        }
	}
	
	//List policies sold by agent
	//Part 3
	public void policiesSold()throws SQLException {
		try {
		Scanner userInput = new Scanner(System.in);
		String agentCity, agentName;
		System.out.print("Enter Agent's name: ");
		agentName = truncate(userInput.next());
		System.out.print("Enter Agent's city: ");
		agentCity = truncate(userInput.next());
		
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

	//cancel a policy
	//Part 4
	public void cancelPolicy() throws SQLException
	{
		try {
			String queryPoliciesSold = "SELECT * FROM POLICIES_SOLD";
			query(queryPoliciesSold);

			Scanner userInput = new Scanner(System.in);
			String purchID;
			System.out.println("Enter Purchase ID of policy to be cancelled: ");
			purchID = userInput.next();

			String queryPolicy = "DELETE FROM POLICIES_SOLD WHERE " +
									"PURCHASE_ID = '" + purchID + "'";
			queryUp(queryPolicy);
			//query(queryPoliciesSold);
		}
		catch (Exception e) {
            throw e;
        }
	}
	
	//add a new agent
	//Part 5
	public void addAgent() throws SQLException
	{
		try {
			//Get highest ID value in AGENTS table
			statement = connection.createStatement();
			int max = 0;
			String queryID = "SELECT MAX(A_ID) from AGENTS";
			ResultSet rs = statement.executeQuery(queryID);
			if (rs.next()) 
				max = rs.getInt(1);
			
			//Ask for user details
			Scanner userInput = new Scanner(System.in);
			String userName, userCity, userZip;
			System.out.print("Enter city: ");
			userCity = userInput.next();
			System.out.print("Enter zipcode: ");
			userZip = userInput.next();
			System.out.print("Enter name: ");
			userName = userInput.next();
			int userID = max + 1;
			
			//Insert user ID into AGENTS table
			insert("AGENTS", "'" + userID + "', '" + userName + "', '" + userCity + "', '" + userZip + "'");
			String queryClients = "SELECT * FROM AGENTS";
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
            //System.out.println("\n---------------------------------");
            //System.out.println("Query: \n" + q + "\n\nResult: ");
            print(resultSet);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
	}
	
	public void queryUp(String q) {
        try {
            int resultSet = statement.executeUpdate(q);
            //System.out.println("\n---------------------------------");
            //System.out.println("Query: \n" + q + "\n\nResult: ");
            //print(resultSet);
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
		insert("AGENTS", "202, 'PHILIP', 'PHOENIX', 85011");
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
