import javax.xml.xpath.XPath;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.sql.*;

class Main {
    private static final String url = "jdbc:mysql://localhost:3306/hotel_DB";
    private static final String url1 = "jdbc:mysql://localhost:3306/DemoBank";
    private static final String username = "root";
    private static final String password = "Pass@123aditya";


    public static void main(String[] args) throws IOException {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Connection connection1 = DriverManager.getConnection(url1, username, password);
            while (true) {
                System.out.println();
                System.out.println(" HOTEL MANAGEMENT SYSTEM ");
                Scanner sc = new Scanner(System.in);
                System.out.println("1. reserve a room. ");
                System.out.println("2. view reservation. ");
                System.out.println("3. Get room number");
                System.out.println("4. update reservation.");
                System.out.println("5. Delete reservation.");
                System.out.println("0. exit.");
                System.out.print("Choose option : ");
                int choice = sc.nextInt();
                switch (choice) {
                    case 1:
                        ReserveRoom(connection, sc, connection1);
                        break;
                    case 2:
                        ViewReservation(connection);
                        break;
                    case 3:
                        GetRoomNumber(connection, sc);
                        break;
                    case 4:
                        updateReservation(connection, sc);
                        break;
                    case 5:
                        DeleteReservation(connection, sc);

                        break;
                    case 0:
                        exit();
                        sc.close();
                        return;

                    default:
                        System.out.println("invalid choice!!! try again...");
                }
            }


        } catch (SQLException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
        /*catch (InterruptedException e){
            throw new RuntimeException(e);
        }*/

    }

    public static void ReserveRoom(Connection connection, Scanner sc, Connection connection1) {
        try {
            System.out.print("Enter Guest name :");
            sc.nextLine();
            String guest_name = sc.nextLine();
            System.out.print("Enter Room Number :");
            int room_number = sc.nextInt();
            System.out.print(" Enter Contact number: ");
            String contact_number = sc.next();
            String sql = "insert into reservations(guest_name, room_number, contact_number) " +
                    "values('" + guest_name + "', " + room_number + ", '" + contact_number + "')";

            System.out.println("payment process");
            Statement statement = connection.createStatement();
            DoTransaction(connection1,sc);
            int Affected_rows_by_reservation = statement.executeUpdate(sql);

            if (Affected_rows_by_reservation > 0) {
                System.out.println("reservation successful ");
            } else {
                System.out.println("reservation not successful");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void ViewReservation(Connection connection) {
        try {

            String sql_query = "select reservation_id, guest_name, room_number, contact_number, reservation_date from reservations;";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql_query);

            while (resultSet.next()) {
                System.out.println("reservation ID = " + resultSet.getInt("reservation_id"));
                System.out.println("guest name  = " + resultSet.getString("guest_name"));
                System.out.println("room number =" + resultSet.getInt("room_number"));
                System.out.println("contact number = " + resultSet.getString("contact_number"));
                System.out.println("reservation date = " + resultSet.getTimestamp("reservation_date"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


    }

    public static void GetRoomNumber(Connection connection, Scanner sc) {
        System.out.println("1. for Getting room number by reservation ID: ");
        System.out.println("2. for Getting room number by name: ");
        System.out.println("enter choice");
        int choice1 = sc.nextInt();
        if (choice1 == 1) {
            room_no_by_reservationID(connection, sc);
        } else if (choice1 == 2) {
            room_no_by_name(connection, sc);
        } else {
            System.out.println("invalid choice");
        }

    }

    public static void room_no_by_reservationID(Connection connection, Scanner sc) {
        try {
            Statement statement = connection.createStatement();
            System.out.println("enter reservation id");
            int room = sc.nextInt();
            String SQL_Query1 = String.format("select room_number from reservations where reservation_id = %d", room);
            ResultSet resultSet = statement.executeQuery(SQL_Query1);
            if (resultSet.next()) {
                System.out.println("room number is :" + resultSet.getInt("room_number"));
            } else {
                System.out.println("invalid choice there is no any entry about this id");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void room_no_by_name(Connection connection, Scanner sc) {
        try {
            Statement statement = connection.createStatement();
            System.out.println("enter name of guest ");
            sc.nextLine();
            String room1 = sc.nextLine();
            String SQL_Query2 = String.format("select room_number from reservations where guest_name = '%s'", room1);
            ResultSet resultSet1 = statement.executeQuery(SQL_Query2);
            if (resultSet1.next()) {
                System.out.println("room number is :" + resultSet1.getInt("room_number"));
            } else {
                System.out.println("invalid choice there is no any entry about this name");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public static void updateReservation(Connection connection, Scanner sc) {
        try {
            System.out.print("Enter reservation id to update data :");
            int reservation_id = sc.nextInt();
            System.out.print("Enter new Guest name :");
            sc.nextLine();
            String New_guest_name = sc.nextLine();
            System.out.print("Enter new Room Number :");
            int New_room_number = sc.nextInt();
            System.out.print(" Enter new Contact number: ");
            String New_contact_number = sc.next();
            String sql = "UPDATE reservations SET guest_name = '" + New_guest_name + "', room_number = " + New_room_number + ", contact_number = '" + New_contact_number + "' WHERE reservation_id = " + reservation_id;

            Statement statement = connection.createStatement();
            int Affected_rows_by_reservation = statement.executeUpdate(sql);
            if (Affected_rows_by_reservation > 0) {
                System.out.println("reservation updated  successful ");
            } else {
                System.out.println("reservation not updated successful");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public static void DeleteReservation(Connection connection, Scanner sc) {
        System.out.println("enter reservation ID :");
        int reservation_id = sc.nextInt();
        if (ReservationExists(connection, reservation_id, sc)) {
            try {
                String query_for_delete = String.format("delete from reservations where reservation_id = %d ", reservation_id);
                Statement statement = connection.createStatement();
                int affected_rows = statement.executeUpdate(query_for_delete);
                if (affected_rows > 0) {
                    System.out.println("reservation cancelled successfully");
                } else {
                    System.out.println("OOPS something went wrong ");
                }


            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }


        } else {
            System.out.println("reservation ID does not not exists");
        }
    }


    public static boolean ReservationExists(Connection connection, int reservation_id, Scanner sc) {

        String query = String.format("select * from reservations where reservation_id = %d ", reservation_id);
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            System.out.println(" room no is : ");
            room_no_by_name(connection, sc);
            return resultSet.next();


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }


   /* public static void UploadImage(Connection connection, Scanner sc) throws IOException, SQLException {
        System.out.println("Enter guest name: ");
        String guestName = sc.nextLine();

        System.out.println("Enter image path: ");
        String imagePath = sc.nextLine();

        try {
            // Read the image file into a byte array
            FileInputStream fileInputStream = new FileInputStream(imagePath);
            byte[] imageData = new byte[fileInputStream.available()];
            fileInputStream.read(imageData);

            // Prepare the SQL query to insert guest name and image
            String query_for_image = "INSERT INTO reservations (guest_name, room_number, photo_of_guest) VALUES (?, ?, ?)";

            PreparedStatement preparedStatement = connection.prepareStatement(query_for_image);

            System.out.println("Enter room number: ");
            int roomNumber = sc.nextInt(); // Get room number

            preparedStatement.setString(1, guestName); // Set guest name
            preparedStatement.setInt(2, roomNumber);   // Set room number
            preparedStatement.setBytes(3, imageData);  // Set image data


            // Execute the query
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Image uploaded successfully for guest: " + guestName);
            } else {
                System.out.println("OOPS something went wrong... cannot upload image...");
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        }
    }*/

    public static void exit() throws InterruptedException {
        System.out.print("EXITING");
        int i = 5;
        while (i != 0) {
            System.out.print(".");
            Thread.sleep(400);
            i--;
        }
        System.out.println();
        System.out.println("thank you for using HOTEL MANAGEMENT SYSTEM !!! ");
    }

    public static final double amount_for_room = 3000;

    public static void DoTransaction(Connection connection1, Scanner sc) throws SQLException {
        System.out.print("Enter Account Number: ");
        int account_number = sc.nextInt();
        System.out.print("Enter 4-digit PIN: ");
        int pin = sc.nextInt();

        if (ValidateAccount(connection1, account_number, pin)) {
            double balance = GetAccountBalance(connection1, account_number);
            if (balance >= amount_for_room) {
                // Proceed with the transaction
                System.out.println("Transaction successful!");
                System.out.println("Amount Deducted: " + amount_for_room);
                // Update the account balance after deduction
                UpdateAccountBalance(connection1, account_number, balance - amount_for_room);
                System.out.println("Remaining Balance: " + (balance - amount_for_room));
            } else {
                System.out.println("Insufficient funds for the transaction.");
            }
        } else {
            System.out.println("Invalid account number or PIN.");
        }
    }

    public static double GetAccountBalance(Connection connection1, int account_number) {
        double balance = 0.0;
        String sql = "SELECT balence FROM account_details WHERE Account_Number = ?";
        try (PreparedStatement preparedStatement = connection1.prepareStatement(sql)) {
            preparedStatement.setInt(1, account_number);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                balance = resultSet.getDouble("balence");
            } else {
                System.out.println("No account found with the given account number.");
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
        return balance;
    }

    private static boolean ValidateAccount(Connection connection1, int account_number, int pin) {
        String sql = "SELECT * FROM account_details WHERE Account_Number = ? AND pin = ?";
        try (PreparedStatement preparedStatement = connection1.prepareStatement(sql)) {
            preparedStatement.setInt(1, account_number);
            preparedStatement.setInt(2, pin);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
        return false;
    }

    private static void UpdateAccountBalance(Connection connection1, int account_number, double new_balance) {
        String sql = "UPDATE account_details SET balence = ? WHERE Account_Number = ?";
        try (PreparedStatement preparedStatement = connection1.prepareStatement(sql)) {
            preparedStatement.setDouble(1, new_balance);
            preparedStatement.setInt(2, account_number);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
    }

}






