import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by smith on 03.01.17.
 */
public class Main {

    static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/apartmentdb";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "24071998";

    static Connection conn;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        try {
            try {
                conn = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);

                while (true) {
                    System.out.println("1. Add apartment");
                    System.out.println("2. Find apartments");
                    System.out.println("<return> to quit");

                    String s = sc.nextLine();
                    switch (s) {
                        case "1":
                            addApp(sc);
                            break;
                        case "2":
                            findApp(sc);
                            break;
                        default:
                            return;
                    }
                }
            }
            finally {
                sc.close();
                if (conn != null) conn.close();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }

    private static void findApp(Scanner sc) throws SQLException {
        System.out.println("Use (<return> to complete or skip)");

        System.out.println("Enter preferred areas: ");
        List<String> areas = new ArrayList<>();
        while (true) {
            String s = sc.nextLine();
            if ("".equals(s)) break;
            else areas.add(s);
        }


        System.out.println("Enter preferred addresses: ");
        List<String> addresses = new ArrayList<>();
        while (true) {
            String s = sc.nextLine();
            if ("".equals(s)) break;
            else addresses.add(s);
        }

        System.out.println("Enter min square range");
        String sq1 = sc.nextLine();
        System.out.println("Enter max square range");
        String sq2 = sc.nextLine();

        System.out.println("Enter room quantity: ");
        String room_num = sc.nextLine();

        System.out.println("Enter min price range: ");
        String pr1 = sc.nextLine();
        System.out.println("Enter max price range: ");
        String pr2 = sc.nextLine();

        String areasS = formWhereRequest(areas, "area");
        System.out.println(areasS);

        String addressS = formWhereRequest(addresses, "address");
        System.out.println(addressS);

        String squareRangeS = "";
        if (!"".equals(sq1) && !"".equals(sq2)) {
            Range sr = new Range(Integer.parseInt(sq1), Integer.parseInt(sq2));
            squareRangeS = "(price > " + sr.getMin() + " AND price < " + sr.getMax() + ")";
        }
        System.out.println(squareRangeS);

        String room_numS = "";
        if (!"".equals(room_num)) {
            room_numS = "room_num = " + room_num;
        }
        System.out.println(room_numS);

        String priceRangeS = "";
        if (!"".equals(pr1) && !"".equals(pr2)) {
            Range pr = new Range(Integer.parseInt(pr1), Integer.parseInt(pr2));
            priceRangeS = "(price > " + pr.getMin() + " AND price < " + pr.getMax() + ")";
        }
        System.out.println(priceRangeS);

        String request = "";
        if (!"".equals(areasS)) request += "WHERE " + areasS;
        if (!"".equals(addressS)) request += " AND " + addressS;
        if (!"".equals(squareRangeS)) request += " AND " + squareRangeS;
        if (!"".equals(room_numS)) request += " AND " + room_numS;
        if (!"".equals(priceRangeS)) request += " AND " + priceRangeS;

        PreparedStatement ps = conn.prepareStatement("SELECT * FROM apartments " + request);
        try {
            // table of data representing a database result set,
            ResultSet rs = ps.executeQuery();
            try {
                // can be used to get information about the types and properties of the columns in a ResultSet object
                ResultSetMetaData md = rs.getMetaData();

                for (int i = 1; i <= md.getColumnCount(); i++)
                    System.out.print(md.getColumnName(i) + "\t\t");
                System.out.println();

                while (rs.next()) {
                    for (int i = 1; i <= md.getColumnCount(); i++) {
                        System.out.print(rs.getString(i) + "\t\t");
                    }
                    System.out.println();
                }
            } finally {
                rs.close(); // rs can't be null according to the docs
            }
        } finally {
            ps.close();
        }
    }

    private static String formWhereRequest(List<String> list, String field) {
        String fieldS;
        if (list.size() > 0) {
            fieldS = "(" + field + "= ";
            for (int i = 0; i < list.size(); i++) {
                fieldS += "\"" + list.get(i) + "\"";
                if (i == list.size() - 1) fieldS += ")";
                else fieldS += " OR "+ field + " = ";
            }
        }
        else fieldS = "";
        return fieldS;
    }

    private static void addApp(Scanner sc) throws SQLException {
        System.out.println("Enter area where apartments are located: ");
        String area = sc.nextLine();
        System.out.println("Enter address: ");
        String address = sc.nextLine();
        System.out.println("Enter square: ");
        float square = Float.parseFloat(sc.nextLine());
        System.out.println("Enter room quantity: ");
        int room_num = Integer.parseInt(sc.nextLine());
        System.out.println("Enter price($): ");
        float price = Float.parseFloat(sc.nextLine());

        PreparedStatement ps = conn.prepareStatement("INSERT INTO apartments (area, address, square, room_num, price)" +
        "VALUES (?,?,?,?,?)");
        try {
            ps.setString(1, area);
            ps.setString(2, address);
            ps.setFloat(3, square);
            ps.setInt(4, room_num);
            ps.setFloat(5, price);
            ps.executeUpdate();
        }
        finally {
            ps.close();
        }
    }

    private static void initDB() throws SQLException {
        Statement st = conn.createStatement();
        try {
            st.execute("DROP TABLE IF EXISTS apartments");
            st.execute("CREATE TABLE apartments (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, name VARCHAR(20) NOT NULL, age INT)");
        } finally {
            st.close();
        }
    }

}
