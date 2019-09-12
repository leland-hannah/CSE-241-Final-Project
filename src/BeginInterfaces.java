import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class BeginInterfaces {
    
    public static void main(String[] args) {
        boolean login = true;
        while (login) {
            try {
                Scanner scan = new Scanner(System.in);
                System.out.println("Enter userid: ");
                String userid = scan.nextLine();
                //String userid = "hel222";

                System.out.println("Enter password: ");
                String password = scan.nextLine();
                //String password = "Leland123";

                login = false;
                System.out.println("Login entered");

                Connection con = DriverManager.getConnection("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241", userid, password);
                con.setAutoCommit(false);
                Statement state = con.createStatement();
                System.out.println("connection successfully made.");
                login = false;
                System.out.println();
                boolean quit = false;
                while (!quit) {
                        
                    System.out.println("Would you like to access an interface or quit, q for quit a for access?");
                    String a = scan.nextLine();
                  
                    if(a.equals("a")) {
                        System.out.println("Which user are you: store, manufactuer or customer");
                        String ans = scan.nextLine();
                        System.out.println();
                        if(ans.equals("store")) {
                            DeliveryItems.startD(con);
                        }
                        else if(ans.equals("manufactuer")) {
                            CreateProduct.startC(con);
                        }
                        else if (ans.equals("customer")){
                            RecallItems.startR(con);
                        }
                        else {
                            System.out.println("invalid input try again");
                        }
                    }
                    
                    if (a.equals("q")) {
                    quit = true;
                    }
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
                System.out.println("failed");
            }
        }
    }
}

