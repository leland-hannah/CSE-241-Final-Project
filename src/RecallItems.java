    import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
    import java.util.Scanner;
    import java.util.TimeZone;
    import static java.lang.System.exit;
public class RecallItems {
static Connection con;
static Scanner s;

    public RecallItems() {}
    public static void startR(Connection c) throws SQLException {
//        boolean login = true;
//        while (login) {
             try {
                  s = new Scanner(System.in);
//                System.out.println("Enter userid: ");
//                String userid = s.nextLine();
//                //String userid = "hel222";
//
//                System.out.println("Enter password: ");
//                String password = s.nextLine();
//                //String password = "Leland123";
//
//                login = false;
//                System.out.println("Login entered");
//
//                con = DriverManager.getConnection("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241", userid, password);
//                Statement state = con.createStatement();
//                System.out.println("connection successfully made.");
//                login = false;
//                con.setAutoCommit(false);
                con = c;
                System.out.println("Enter the delivery id: ");
                String id = s.nextLine();
                int did = 0;
                System.out.println();
                
                boolean shouldEnd = false;
                for(int i = 0; i < 3; i++) {
                    try {
                        did = Integer.parseInt(id);
                    }
                    catch(NumberFormatException nFE) {
                        System.out.println("Not an Integer");
                    }
                    
                    if(i == 2 && !check("Delivery", did)) {
                        System.out.println("You have used all your attempts to login");
                        shouldEnd = true;
                        break;
                    }
                    else if(!check("Delivery", did)) {
                        System.out.println("Ivalid delivery id, you have " + (3 - (i+1)) + " more tries" );
                        id = s.nextLine();
                        System.out.println();
                    }
                    if(check("Delivery", did)) {
                        break;
                    }
                }
                
                if(shouldEnd) {
                    System.out.println("Try again later");
                    return;
                    
                }
                getInfo(did);
             }
                    
            catch (SQLException e) {
                e.printStackTrace();
                System.out.println("failed");
                }
        }
    
             
        public static boolean check(String table, int n) throws SQLException {
            String from = "select * from " + table + " where d_id = ?";
            PreparedStatement preparedStatement = con.prepareStatement(from);
            preparedStatement.setInt(1, n);
            ResultSet res = preparedStatement.executeQuery();
            if (!(res.next())) {
                     //System.out.println("does not exist");
                return false;
            }
               return true;
        }
             
    public static void getInfo(int d_id) throws SQLException {
        String st = "select p_name from contains natural join product where d_id =" + d_id;
        PreparedStatement p = con.prepareStatement(st);
        ResultSet r = p.executeQuery();
        String prod = "";
            System.out.println("These are all the products in that delivery:");
            while(r.next()) {
                System.out.print(String.format("%-"+(5 + r.getString(1).length()) + "s", r.getString(1)));
            }
            System.out.println();
            System.out.println();
            System.out.println("Which product do you have an issue with?");
            //s.nextLine();
            prod = s.nextLine();
            System.out.println();
            showList(d_id, prod);
        
    }
    
    public static void showList(int d_id, String prod) throws SQLException {
        String c = "select id, cat_name from product natural join type where p_name = ?";
        PreparedStatement ps = con.prepareStatement(c);
        ps.setString(1, prod);
        ResultSet rs = ps.executeQuery();
        
        if(rs.next()) {
            int id = rs.getInt(1);
            String type = rs.getString(2);
            String man = "select name from sends where d_id =" + d_id;
            PreparedStatement pre = con.prepareStatement(man);
            ResultSet rS = pre.executeQuery();
            
            
            if(rS.next()) {
                String mName = rS.getString(1);
                
                
                String findD_id = "select d_id from sends where name = ?";
                PreparedStatement pst = con.prepareStatement(findD_id);
                pst.setString(1, mName);
                ResultSet rset = pst.executeQuery();
             
                ArrayList<Integer> allDs = new ArrayList<Integer>();
                while(rset.next()) {
                    allDs.add(rset.getInt(1));
                }
             ArrayList<Integer> delivs = new ArrayList<Integer>();
             System.out.println("These are all the deliveries this product is involved with:");
             
             for(int i = 0; i < allDs.size(); i++) {
                 
                 String ifContains = "select d_id from contains where d_id = ? and id = ?";
                 PreparedStatement pS = con.prepareStatement(ifContains);
                 pS.setInt(1, allDs.get(i));
                 pS.setInt(2, id);
                 ResultSet reSet = pS.executeQuery();
                 
                 while(reSet.next()) {
                     int d = reSet.getInt(1);
                     delivs.add(d);
                     System.out.println(d);
                 }
             }
             System.out.println();
             
             System.out.println("Should this product be recalled, yes or no?");
             if(s.next().equals("yes")) {
                 for(int i = 0; i < delivs.size(); i++) {
                     String deleteFrom = "delete from contains where d_id = ? and id = ?";
                     PreparedStatement pS = con.prepareStatement(deleteFrom);
                     pS.setInt(1, delivs.get(i));
                     pS.setInt(2, id);
                     pS.executeQuery();
                     
                     String only1 = "select * from contains where d_ID = ?";
                     PreparedStatement prst = con.prepareStatement(only1);
                     prst.setInt(1, d_id);
                     ResultSet r = prst.executeQuery();
                     int count = 0;
                     while (r.next()) {
                        count++; 
                     }
                     
                     if (count == 0) {
                         String dDis = "delete from distribution where d_id = ?";
                         PreparedStatement p = con.prepareStatement(dDis);
                         p.setInt(1, d_id);
                         p.executeQuery();
                         
                         String dSend = "delete from sends where d_id = ?";
                         PreparedStatement P = con.prepareStatement(dSend);
                         P.setInt(1, d_id);
                         P.executeQuery();
                         
                         String dDelivery = "delete from delivery where d_id = ?";
                         PreparedStatement pres = con.prepareStatement(dDelivery);
                         pres.setInt(1, d_id);
                         pres.executeQuery();
                     }
                 }
                 System.out.println();
                 System.out.println("The product has been recalled.");
             }
       
            }
            
            
        }
        System.out.println();
        con.commit();
        System.out.println("Thank your for your input on the product.");
        System.out.println();
    }
}







