import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/** 
 *  TO TEST YOUR SQL COMMANDS A QUICK WAY IS TO COMMENT OUT THE WHILE LOOP 
 *  AND CALL YOUR FUNCTION DIRECTLY WITH HARDCODED ARGUMENTS
 *	I MADE A TEXT MENU MAINLY BECAUSE I WAS BORED AND I WANTED SOMETHING UNTL THE GUI
 */
public class FontEnd {
	private static Statement stmt;
	
	public class InputException extends Exception
	{
		public InputException() { super(); }
		public InputException(String message) { super(message); }
	}
	
	public static void main(String args[]) throws SQLException
	{
		boolean on = true;
		try {
			Class.forName("com.ibm.db2.jcc.DB2Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Connect to the database
		Connection conn = DriverManager.getConnection("jdbc:db2://db2.cs.mcgill.ca:50000/cs421",
													  "cs421g16",
													  "-H0T3L421");
		Scanner in = new Scanner(System.in);
		stmt = conn.createStatement ( ) ;
		int s;
		while(on == true){
			System.out.println("Welcome to the Hotel App");
			System.out.println("Please enter the number of the command you would like to execute");
			System.out.println("1. Add Hotel");
			System.out.println("2. Add Employee");
			System.out.println("3. Add Customer");
			System.out.println("4. Add Reservation");
			System.out.println("----------------");
			System.out.println("6. View Year Total Payments");
			System.out.println("7. Change Employee Salary");
			System.out.println("8. Get Emails Of Unpaid Reservations");
			System.out.println("9. Exit");
			s = in.nextInt();
			if (s == 2){
				int salary;
				System.out.println("Enter new salary");
				salary = in.nextInt();
				addEmployee(salary);
			}
			if (s == 4){
				int amount = 200;
				System.out.println("Enter total amount");
				amount = in.nextInt();
				String departureDate = "2014-03-23";
				String arrivalDate = "2014-03-20";
				in.nextLine();
				System.out.println("Enter Arrival Date ex: 2014-03-20");
				arrivalDate = in.nextLine();
				System.out.println("Enter Departure Date ex: 2014-03-25");
				departureDate = in.nextLine();
				addReservation(amount, arrivalDate, departureDate);
			}
			if (s == 6){
				int year;
				System.out.println("Enter year");
				year = in.nextInt();
				getTotalPaymentsYear(year);
			}
			if (s == 7){
				int salary;
				int eid;
				System.out.println("Enter employee id");
				eid = in.nextInt();
				System.out.println("Enter new salary");
				salary = in.nextInt();
				changeSalary(eid, salary);
			}
			if (s == 8){
				
			}
			if (s == 9){
				on = false;
			}
		}
		
		// (test cmds)
		
		// on program exit
		conn.close();
	}
	
	public static int addCustomer(String name, String dateOfBirth, String homeAddress, String phoneNumber, String email) throws InputException, SQLException
	{
		if (name.length() > 20 || homeAddress.length() > 30 || phoneNumber.length() > 15 || email.length() > 20)
		{
			//throw new InputException("Incorrect length for one of the parameters!");
			return -1;
		}
		
		// todo: date input validation
		
		// get cID value
		ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Customer");
		int cID = rs.getInt(1);
		
		stmt.executeUpdate("INSERT INTO Customer VALUES (" + cID + ", " + name + ", " + dateOfBirth + ", " + homeAddress + ", " + phoneNumber + ", " + email + ")");
		
		return cID;
	}
	
	public static int addHotel(String location, int hotelChainID) throws InputException, SQLException
	{
		if (location.length() > 20)
		{
			//throw new InputException("Incorrect length for location parameter.");
			return -1;
		}
		
		// todo: check if hotelChainID is a valid ID already in the database.
		
		// get hID val
		ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Hotel");
		int hID = rs.getInt(1);
		
		// we assume that managerID = 0...
		
		stmt.executeUpdate("INSERT INTO Hotel VALUES (" + hID + ", " + location + ", " + hotelChainID + ", 0)");
		
		return hID;
	}
	
	public static List<String> getEmailsOfUnpaidReservations() throws SQLException
	{ // Gets emails of customers who have not yet paid their reservations, and the reservation is exactly one week away.
		List<String> emails = new ArrayList<String>();
		
		ResultSet rs = stmt.executeQuery("SELECT DISTINCT email FROM UnpaidReservations WHERE DAYS(arrivalDate) - DAYS(current date) = 7");
		while (rs.next()) // code from slides
		{
			emails.add(rs.getString("email"));
		}
		
		return emails;
	}
	public static void changeSalary(int eid, int salary) throws SQLException{
		try{
			int rs = stmt.executeUpdate("UPDATE Employee SET salary = " + salary + " WHERE eid = " + eid);
		}catch (SQLException e){
			int sqlCode = e.getErrorCode(); // Get SQLCODE
			String sqlState = e.getSQLState(); // Get SQLSTATE
        
			// Your code to handle errors comes here;
			// something more meaningful than a print would be good
			System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
		}
	}
	public static void getTotalPaymentsYear(int year) throws SQLException{
		try{
			String endDate = year+1+"-01-01";
			String startDate= year-1+"-12-31";
			ResultSet rs = stmt.executeQuery("SELECT SUM(amount) AS TOTAL FROM Payment "
									  + "JOIN ReservationPayment ON Payment.pid = ReservationPayment.pid "
									  + "JOIN Reservation ON ReservationPayment.rid = Reservation.rid "
									  + "WHERE Reservation.arrivalDate <'" + endDate + "' AND Reservation.arrivalDate >'" + startDate + "'");
			ResultSetMetaData rsmd = rs.getMetaData();
		    int columnsNumber = rsmd.getColumnCount();
		    while (rs.next()) {
		        for (int i = 1; i <= columnsNumber; i++) {
		            if (i > 1) System.out.print(",  ");
		            String columnValue = rs.getString(i);
		            System.out.print(rsmd.getColumnName(i) + " " + columnValue);
		        }
		        System.out.println("");
		    }
			
		}catch (SQLException e){
			int sqlCode = e.getErrorCode(); // Get SQLCODE
			String sqlState = e.getSQLState(); // Get SQLSTATE
        
			// Your code to handle errors comes here;
			// something more meaningful than a print would be good
			System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
		}
	}
	
	//** ADDERS **//
	public static void addEmployee(int salary) throws SQLException{
		// get eID val
		int eID = 0;
		ResultSet count = stmt.executeQuery("SELECT COUNT(*) FROM Employee");
		while ( count.next ( ) ) {
			eID = count.getInt(1)+1;
		}
		System.out.println(eID);
		try{
			stmt.executeUpdate("INSERT INTO Employee VALUES (" + eID + ", " + salary + ", 1)");
		}catch (SQLException e){
			System.out.println(e.getMessage());
			int sqlCode = e.getErrorCode(); // Get SQLCODE
			String sqlState = e.getSQLState(); // Get SQLSTATE
        
			// Your code to handle errors comes here;
			// something more meaningful than a print would be good
			System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
		}
		
	}	
	public static void addReservation(int amount, String arrivalDate, String departureDate) throws SQLException{
		// get rID val
		int rID = 0, pID = 0;
		ResultSet count = stmt.executeQuery("SELECT COUNT(*) FROM Reservation");
		while ( count.next ( ) ) {
			rID = count.getInt(1)+1;
		}
		
		count = stmt.executeQuery("SELECT COUNT(*) FROM Payment");
		while ( count.next ( ) ) {
			pID = count.getInt(1)+1;
		}
		
		int state = 0;
		String roomType = "Single";
		
		int ccNumber = 12345678;
		String ccType = "Visa";
		String ccName = "Customer";
		
		try{
			stmt.executeUpdate("INSERT INTO Reservation VALUES (" + rID + ", '" + roomType + "', '" + arrivalDate + "', '" + departureDate + "', " + state + ")");
			stmt.executeUpdate("INSERT INTO Payment VALUES (" + pID + ", " + amount + ", " + ccNumber + ", '" + ccType + "', '" + ccName + "')");
			stmt.executeUpdate("INSERT INTO ReservationPayment VALUES (" + rID + ", " + pID + ")");
		}catch (SQLException e){
			int sqlCode = e.getErrorCode(); // Get SQLCODE
			String sqlState = e.getSQLState(); // Get SQLSTATE
        
			// Your code to handle errors comes here;
			// something more meaningful than a print would be good
			System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
		}
	}
	
}
