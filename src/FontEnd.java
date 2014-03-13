import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


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
		int s;
		while(on == true){
			System.out.println("Welcome to the Hotel App");
			System.out.println("Please enter the number of the command you would like to execute");
			System.out.println("1. Change Salary");
			System.out.println("7. Exit");
			s = in.nextInt();
			if (s == 1){
				System.out.println("Enter employee id");
				s = in.nextInt();
				changeSalary(s);
			}
			if (s == 7){
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
	public static void changeSalary(int i) throws SQLException{
		ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Hotel");
		int hID = rs.getInt(1);
		System.out.println(hID);
	}
}
