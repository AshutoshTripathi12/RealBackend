package com.affiliate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MarketplaceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MarketplaceApplication.class, args);
	}

}

//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//
//public class MarketplaceApplication {
//    public static void main(String[] args) {
//        String url = "jdbc:sqlserver://DESKTOP-L9PN2BQ\\SQLEXPRESS;databaseName=InfluencerMarketPlace;encrypt=true;trustServerCertificate=true";
//        String user = "123"; // Replace with your actual username
//        String password = "123"; // Replace with your actual password
//
//        try {
//            Connection connection = DriverManager.getConnection(url, user, password);
//            System.out.println("Connection successful!");
//            connection.close();
//        } catch (SQLException e) {
//            System.err.println("Connection failed: " + e.getMessage());
//        }
//    }
//}
