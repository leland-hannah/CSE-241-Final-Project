import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;
import java.util.TimeZone;

import static java.lang.System.exit;
public class CreateProduct {
    static Connection con;
    static Scanner s;
    public CreateProduct() {}
    public static void startC(Connection c) throws SQLException {
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
//                con.setAutoCommit(false);
//                System.out.println("connection successfully made.");
//                login = false;
                con = c;
                con.setAutoCommit(false);
                System.out.println("Enter your manufacturing name: ");
                String n = s.nextLine();
                String mName = n;
                System.out.println();

                boolean shouldEnd = false;
                for(int i = 0; i < 3; i++) {
                    if(i == 2 && !check("manufacturing", mName)) {
                        System.out.println("You have used all your attempts to login");
                        shouldEnd = true;
                        break;
                    }
                    else if(!check("manufacturing", mName)) {
                        System.out.println("Ivalid manufactuer name, you have " + (3 - (i+1)) + " more tries" );
                        mName = s.nextLine();
                        System.out.println();
                    }
                    if(check("manufacturing", mName)) {
                        break;
                    }
                }
                
                if(shouldEnd) {
                    System.out.println("Try again later");
                    System.out.println();
                    return;
                }
                String ans = "";
                System.out.println("Would you like to make a new product, respond yes or no");
                ans = s.next();
                while(!ans.equals("yes") && !ans.equals("no")) {
                    System.out.println("That is not a valid entry please try again");
                    ans = s.next();
                }
                if(ans.equals("yes")) {
                    System.out.println();
                    findType(mName); 
                }
                
                
                
            }

            catch (SQLException e) {
                e.printStackTrace();
                System.out.println("failed");
            }
        }
    
    public static boolean check(String table, String n) throws SQLException {
        String from = "select * from " + table + " where name = ?";
        PreparedStatement preparedStatement = con.prepareStatement(from);
        preparedStatement.setString(1, n);
        ResultSet res = preparedStatement.executeQuery();
        if (!(res.next())) {
            //System.out.println("does not exist");
            return false;
        }
        return true;
    }
    
    public static void findType(String mName) throws SQLException {
        String list = "select cat_name from category";
        PreparedStatement preparedStatement = con.prepareStatement(list);
        ResultSet res = preparedStatement.executeQuery();
        System.out.println("Out of these categories which is related to your product?");
        
        int count = 0;
        ArrayList<String> track = new ArrayList<String>();
           while(res.next())
            {
                String name = res.getString(1);
                track.add(name);
                System.out.print(String.format("%-"+(2 + name.length())+"s", name));
                count++;
                if(count % 3 == 0) {
                    System.out.println();
                }
            }
        s.nextLine();
        String cat = s.nextLine();
        
        while(!track.contains(cat)) {
            System.out.println();
            System.out.println("That is not a valid input please try again");
            cat = s.nextLine();
        }
        track.clear();
        //System.out.println(cat);
        System.out.println();
        System.out.println("Which component would you like to use?");
        String l = "select comp_type from components";
        PreparedStatement p = con.prepareStatement(l);
        ResultSet r = p.executeQuery();
           while(r.next())
            {
                String comp = r.getString(1);
                track.add(comp);
                System.out.print(String.format("%-"+(2 + comp.length())+"s", comp));
            }
        System.out.println();
        //s.nextLine();
        String comp = s.nextLine();
        while(!track.contains(comp)) {
            System.out.println();
            System.out.println("That is not a valid input please try again");
            comp = s.nextLine();
        }
        System.out.println();
        //System.out.println(comp);
        creates(mName, cat, comp);
    }
    
    public static void creates(String mName, String cat, String comp) throws SQLException {
        try {
        String l = "select supply_id, comp_type from used natural join components where comp_type = ? ";
        PreparedStatement p = con.prepareStatement(l);
        p.setString(1, comp);
        ResultSet r = p.executeQuery();
        
        //just picks the first supplier, might change later 
        int supplyID = 0;
        while(r.next()) {
            supplyID = r.getInt(1);
            if(!isIn(mName, supplyID)) {
                break;
            }
        }
        String insert = "insert into sold (name, supply_id, day, month, year, timeOfDay, price, quantity) values (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(insert);
        ps.setString(1, mName);
        ps.setInt(2, supplyID);
        
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
        
        ps.setInt(3, cal.get(Calendar.DATE));
        ps.setInt(4, cal.get(Calendar.MONTH)+1);
        ps.setInt(5, cal.get(Calendar.YEAR));
        ps.setString(6, tod);
        ps.setDouble(7,(Math.random()*999.99)+1);
        ps.setInt(8, (int) (Math.random()*999)+1);
        ps.executeQuery();
        
        System.out.println("What is the product name? ");
        String n = s.nextLine();
        String pname = n;
        //System.out.println();
        makeP(mName, pname, cat);
        
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
    private static boolean isIn(String n, int sid) throws SQLException {
        String in = "select * from sold where name = ? and supply_id = ?";
        PreparedStatement ps = con.prepareStatement(in);
        ps.setString(1, n);
        ps.setInt(2, sid);
        ResultSet res = ps.executeQuery();
        
        if(res.next()) {
            return true;
        }
        return false;
    }
    public static void makeP(String mName, String name, String cat) throws SQLException {
        
        String newp = "insert into product (id, p_name, price) values (?, ?, ?)";
        PreparedStatement pds = con.prepareStatement(newp);
        int newID = (int)(Math.random()*99999999)+1; //random
        pds.setInt(1, newID);
        pds.setString(2, name);
        pds.setDouble(3, (Math.random()*999.99)+1); //random it 
        pds.executeQuery();
        
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
        
        String c = "insert into creates (name, id, quantity, day, month, year, timeOfDay) values (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement prepared = con.prepareStatement(c);
        prepared.setString(1, mName);
        prepared.setInt(2, newID);
        prepared.setInt(3, (int) (Math.random()*999)+1); //random it 
        prepared.setInt(4, cal.get(Calendar.DATE));
        prepared.setInt(5, cal.get(Calendar.MONTH)+1);
        prepared.setInt(6, cal.get(Calendar.YEAR));
        prepared.setString(7, tod);
        prepared.executeQuery();
        
        String in = "insert into type (id, cat_name) values (?, ?)";
        //System.out.println(cat);
        PreparedStatement ps = con.prepareStatement(in);
        ps.setInt(1, newID);
        ps.setString(2, cat);
        ps.executeQuery();
  
        con.commit();
        System.out.println("Your product has been made!");
        System.out.println();
    }
}


