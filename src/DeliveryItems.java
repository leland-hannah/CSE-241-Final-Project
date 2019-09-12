import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;
import java.util.TimeZone;

import static java.lang.System.exit;
public class DeliveryItems {
    static Connection con;
    static Scanner s;
    public  DeliveryItems() {}
    public static void startD(Connection c) throws SQLException {
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
//                con.setAutoCommit(false);
//                Statement state = con.createStatement();
//                System.out.println("connection successfully made.");
//                login = false;
                con = c;
                System.out.println("Enter store id: ");
                String store = s.nextLine();
                System.out.println();
                //s.nextLine();
                int s_id = 0;

                //sentToStore("distribution", s_id);
                //sentToStore("store", s_id);
                
                boolean shouldEnd = false;
                for(int i = 0; i < 3; i++) {
                    try {
                        s_id = Integer.parseInt(store);
                    }
                    catch(NumberFormatException nFE) {
                        System.out.println("Not an Integer");
                    }
                    
                    if(i == 2 && !sentToStore("store", s_id)) {
                        System.out.println("You have used all your attempts to login");
                        shouldEnd = true;
                        break;
                    }
                    else if(!sentToStore("store", s_id)) {
                        System.out.println("Ivalid store id, you have " + (3 - (i+1)) + " more tries" );
                        store = s.nextLine();
                        System.out.println();
                    }
                    if(sentToStore("store", s_id)) {
                        break;
                    }
                }
                System.out.println();
                if(shouldEnd) {
                    System.out.println("Try again later");
                    return;
                }
                String ans = "";
                System.out.println("Do you want to look at the deliveries to your store, yes or no?");
                ans = s.next();
                while(!ans.equals("yes") && !ans.equals("no")) {
                    System.out.println("That is not a valid entry please try again");
                    ans = s.next();
                }
                if(ans.equals("yes")) {
                    System.out.println();
                    dList(s_id);
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
                System.out.println("failed");
            }
        }

    
    public static boolean sentToStore(String table, int s_id) throws SQLException {
            String from = "select * from " + table + " where s_id = ?";
            PreparedStatement preparedStatement;
            preparedStatement = con.prepareStatement(from);
        
            preparedStatement.setInt(1, s_id);
            ResultSet res = preparedStatement.executeQuery();
            if (!(res.next())) {
                //System.out.println("does not exist");
            return false;
            }
           return true;
    }
    
    public static void dList(int ID) throws SQLException {
        String list = "select distinct d_id, price from distribution natural join contains where s_id = '" + ID + "'";
        PreparedStatement preparedStatement = con.prepareStatement(list);
        ResultSet res = preparedStatement.executeQuery();

        ArrayList<String> track = new ArrayList<String>();
        while(res.next()) {
            String d_id = res.getString(1);
            String price = res.getString(2);
            track.add(d_id);
            System.out.println(String.format("Delivery ID:%-"+(2+ d_id.length())+"sPrice of Delivery:%-"+(2+ price.length())+"s", d_id, price));
            
        }
        System.out.println();
        System.out.println("Enter the delivery ID of the delivery you want to look at");
        String sDelivery = s.next();
        while(!track.contains(sDelivery)) {
            System.out.println("That is not a valid delivery id please try again");
            sDelivery = s.next();
        }
        System.out.println();
        System.out.println("Here is the details of that delivery");
        int sD = Integer.parseInt(sDelivery);
        dItems(sD, ID);
        
    }
    
    public static void dItems(int dID, int s_ID) throws SQLException {
        String list = "select price, day, month, year, timeOfDay, transit, quantity  "
                + "from distribution natural join delivery where d_id = '" + dID + "'";
        PreparedStatement pS = con.prepareStatement(list);
        //preparedStatement.setInt(1, id);
        ResultSet res = pS.executeQuery();
        
        
        if(res.next()) {
            String  price = res.getString(1);
            String day = res.getString(2);
            String month = res.getString(3);
            String year = res.getString(4);
            String timeOfDay = res.getString(5);
            String trans = res.getString(6);
            String quantity = res.getString(7);
            System.out.println(String.format("Price:%-"+(2+ price.length())+"sDay:%-"+(2+ day.length())+"sMonth:%-"+
            (2+ month.length())+"sYear:%-"+(2+ year.length())+"s", price, day, month, year));
            
            System.out.println(String.format("Time Of Day:%-"+(2+ timeOfDay.length())+
                    "sTransit:%-"+(2+ trans.length())+"sQuantity:%-"+(2+ quantity.length())+"s", timeOfDay, trans, quantity));
            
            System.out.println();
            
            String ans = "";
            System.out.println("Do you want the products in the delivery, yes or no?");
            ans = s.next();
            while(!ans.equals("yes") && !ans.equals("no")) {
                
                System.out.println("That is not a valid entry please try again");
                ans = s.next();
            }
            
            if(ans.equals("yes")) {
                String l = "select p_name, id, d_price from delivery natural join contains natural join product where d_id = '" + dID + "'";
                PreparedStatement p = con.prepareStatement(l);
                ResultSet r = p.executeQuery();
                ArrayList<Integer> pID = new ArrayList<Integer>();
                ArrayList<Double> pPrice = new ArrayList<Double>();
                
                int count = 0;
                   while(r.next())
                    {
                       String p_name = r.getString(1);
                       pID.add(r.getInt(2));
                       pPrice.add(r.getDouble(3));
                       //System.out.println(r.getInt(2) + " 120");
                       //System.out.println(r.getDouble(3) + " 121");
                       System.out.print(String.format("%-"+(2 + p_name.length())+"s", p_name));
   
                    }
                   
                System.out.println();   
                String a = "";
                System.out.println("Do you want to order this delivery again, yes or no?");
                a = s.next();
                while(!a.equals("yes") && !a.equals("no")) {
                    System.out.println("That is not a valid entry please try again");
                    a = s.next();
                }
                
                if (a.equals("yes")) {
                    System.out.println();
                    reorder(s_ID, dID, price, quantity, trans, pID, pPrice);
                }
                
            }
        }
        
       
     }
      
    
    public static void reorder(int s_id, int dID, String p, String q, String transit, ArrayList<Integer> prods, ArrayList<Double> prices) {
        try {
        String list = "insert into delivery (d_id, transit , quantity ) values (?, ?, ?)";
        PreparedStatement pS = con.prepareStatement(list);
        int newDID = (int)(Math.random()*99999999)+1;
        pS.setInt(1, newDID);
        pS.setString(2, transit);
        pS.setInt(3, Integer.parseInt(q)); 
        pS.executeQuery();
        
        String dis = "insert into distribution (d_id, s_id, day, month, year, timeOfDay, price) values (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pstm = con.prepareStatement(dis);
            Calendar cal = Calendar.getInstance(TimeZone.getDefault());
            String tod;
            if(cal.getTime().getHours() < 12) {
             tod = "Morning";
            }
            else if (cal.getTime().getHours() > 12 && cal.getTime().getHours() < 15) {
                tod = "Afternoon";
            }
            else {
                tod = "Evening";
            }
            
            pstm.setInt(1, newDID);
            pstm.setInt(2, s_id);
            pstm.setInt(3, cal.get(Calendar.DATE));
            pstm.setInt(4, cal.get(Calendar.MONTH)+1);
            pstm.setInt(5, cal.get(Calendar.YEAR));
            pstm.setString(6, tod);
            pstm.setDouble(7, Double.parseDouble(p));
            pstm.executeQuery();

            String pInsert = "insert into contains (d_id, id, d_price) values (?, ?, ?)";
            PreparedStatement P = con.prepareStatement(pInsert);
            for(int i = 0; i < prods.size(); i++) {
                //System.out.println(prods.get(i) + " 170");
                //System.out.println(prices.get(i) + " 171");
                P.setInt(1, newDID);
                P.setInt(2, prods.get(i));
                P.setDouble(3, prices.get(i));
                P.executeQuery();
                }
            con.commit();
            System.out.println("Your delivery has now been made!");
            System.out.println();
        }
        catch (SQLException e) {
            try
            {
                con.rollback();
            }
            catch (SQLException rollbackException)
            {
                return;
            }
        }
        
    }
}
