package org.example.spark;

import java.sql.*;
import java.sql.SQLException;

import static spark.Spark.*;

public class Main {
    private static final String connectionUrl = "jdbc:mysql://localhost:3306/doll_demo";
    private static final String username = "root";
    private static final String password = "topsecretpassword";
    private static Connection connection;


    public static void main(String[] args) {

        try {
            connection = DriverManager.getConnection(connectionUrl, username, password);

//            getAllDolls();
//            insertDoll("Papusa1", 100, 19);
//            insertDoll("Papusa2", 150, 39);
        } catch (SQLException e) {
            System.out.println(e.getStackTrace());
        }


        get("/dolls", (req, res) -> {
            try{
                getAllDolls();
                return "Done";
            }catch (SQLException e){
                res.status(500);
                        return "Database error: " + e.getMessage();
            }
        });

        post("/dolls/:nume/:pret/:stoc", (req, res) -> {
            try{
                insertDoll(req.params(":nume"), Double.parseDouble(req.params(":pret")), Integer.parseInt(req.params(":stoc")));
                return "Done inserting";
            }catch(SQLException e) {
                res.status(500);
                return "Database error: " + e.getMessage();
            }
        });

        delete("/dolls/:id", (req, res) -> {
            try {
                deleteDoll(Integer.parseInt(req.params(":id")));
                return "Done deleting";
            } catch (SQLException e) {
                res.status(500);
                return "Database error: " + e.getMessage();
            }
        });

        post("/dolls/:id/:nume/:pret/:stoc", (req, res) -> {
            try {
                updateDoll(Integer.parseInt(req.params(":id")), req.params(":nume"), Double.parseDouble(req.params(":pret")), Integer.parseInt(req.params(":stoc")));
                return "Done update";
            } catch (SQLException e) {
                res.status(500);
                return "Database error: " + e.getMessage();
            }
        });

        get("/dolls/:id", (req, res) -> {
            try {
                Doll doll = getDollById(Integer.parseInt(req.params(":id")));
                if(doll != null) {
                    return "Doll fetched: " + doll.toString();
                } else {
                    res.status(404);
                    return "Doll not found.";
                }
            } catch (SQLException e) {
                res.status(500);
                return "Database error: " + e.getMessage();
            }
        });
    }

    public static void getAllDolls() throws SQLException {
        Statement ps = connection.createStatement();
        ResultSet rs = ps.executeQuery("SELECT * FROM `Doll`;");
        while (rs.next()) {
            Doll d = new Doll(rs.getString("nume"), rs.getDouble("pret"), rs.getInt("stoc"));
            System.out.println(d);
        }
    }

    private static void insertDoll(String nume, double pret, int stoc) throws SQLException {

        PreparedStatement ps2 = connection.prepareStatement("INSERT INTO `Doll` (`nume`,`pret`, `stoc`) VALUES ( ?, ?, ?);");
        ps2.setString(1, nume);
        ps2.setDouble(2, pret);
        ps2.setInt(3, stoc);
        ps2.execute();
    }

    private static void deleteDoll(int id) throws SQLException {

        PreparedStatement ps3 = connection.prepareStatement("DELETE FROM `Doll` WHERE id = ? ;");
        ps3.setInt(1, id);
        ps3.execute();

    }

    private static void updateDoll(int id, String nume, double pret, int stoc ) throws SQLException {

        PreparedStatement ps4 = connection.prepareStatement("UPDATE `Doll` SET `nume` = ? , `pret` = ? , `stoc` = ?  WHERE id = ? ;");

        ps4.setString(1, nume);
        ps4.setDouble(2,pret);
        ps4.setInt(3, stoc);
        ps4.setInt(4, id);

        ps4.execute();

    }

    public static Doll getDollById(int id) throws SQLException {
        PreparedStatement ps5 = connection.prepareStatement("SELECT * FROM `Doll` WHERE id = ?;");
        ps5.setInt(1, id);
        ResultSet rs = ps5.executeQuery();
        if (rs.next()) {
            Doll d = new Doll(rs.getString("nume"), rs.getDouble("pret"), rs.getInt("stoc"));
            System.out.println(d);
            return d;
        } else {
            return null;
        }
    }

}
