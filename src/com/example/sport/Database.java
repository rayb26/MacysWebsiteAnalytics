package com.example.sport;

import java.sql.*;

/**
 * Class handles database functionality of storing website information into
 * two distinct tables.
 *
 * @author Rayhan Biju
 */
public class Database {

    public static final String DB_NAME = "MacyAnalytics.db";

    public static final String CONNECTION = "jdbc:sqlite:C:\\Users\\sport\\Desktop\\Macy_WebScraper\\" + DB_NAME;

    /*
    First table creation (static data) with product ID, product name, product url,
    product image url, and product price.
     */
    public static final String TABLE_STATIC_DATA = "STATIC_WEB_ANALYTICS";
    public static final String COLUMN_PRODUCT_ID = "Product_ID";
    public static final String COLUMN_PRODUCT_NAME = "Product_Name";
    public static final String COLUMN_PRODUCT_URL = "Product_URL";
    public static final String COLUMN_IMAGE_URL = "Image_URL";
    public static final String COLUMN_IN_STOCK = "Stock";
    public static final String COLUMN_PRICE = "Original_Price";

    public static final String CREATE_TABLE_STATIC = "CREATE TABLE IF NOT EXISTS " + TABLE_STATIC_DATA + " (" + COLUMN_PRODUCT_ID + " TEXT, " + COLUMN_PRODUCT_NAME + " TEXT, " + COLUMN_PRODUCT_URL + " TEXT, "
            + COLUMN_IMAGE_URL + " TEXT, " + COLUMN_PRICE + " TEXT, " + COLUMN_IN_STOCK + " TEXT);";

    /*
    Second table creation (dynamic data) with product id, sale price, stock, and
    timestamp.
     */
    public static final String TABLE_DYNAMIC_DATA = "DYNAMIC_WEB_ANALYTICS";
    public static final String COLUMN_PRODUCT_ID_DYNAMIC = "Product_ID";
    public static final String COLUMN_PRODUCT_SALE_PRICE_DYNAMIC = "Sale_Price";
    public static final String COLUMN_TIMESTAMP_DYNAMIC = "Timestamp";
    public static final String COLUMN_IN_STOCK_DYNAMIC = "Stock";

    public static final String CREATE_TABLE_DYNAMIC = "CREATE TABLE IF NOT EXISTS " + TABLE_DYNAMIC_DATA + " (" + COLUMN_TIMESTAMP_DYNAMIC + " TEXT, " + COLUMN_PRODUCT_ID_DYNAMIC + " TEXT, "
            + COLUMN_PRODUCT_SALE_PRICE_DYNAMIC + " TEXT, " + COLUMN_IN_STOCK_DYNAMIC + " TEXT);";

    /**
     * Establishing connection to database.
     */
    private Connection connection;

    public boolean open() {
        try {
            connection = DriverManager.getConnection(CONNECTION);
            return true;
        } catch (SQLException e) {
            System.out.println("Error connecting to database");
            return false;

        }
    }

    /**
     * Closes the database connection once all data has been written into.
     */
    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Error");
        }
    }
    /**
     * Creating database to hold static website information below.
     *
     * @param tableDataType Specific table to insert data into.
     */
    public void createStaticTable(String tableDataType) {
        if (open()) {
            try (Statement statement = connection.createStatement()) {

                statement.executeUpdate(tableDataType);
            } catch (SQLException e) {
                System.out.println("Error establishing data connection");

            }
            close();
        } else {
            System.out.println("Can't Open Database");
        }

    }

    /**
     * Method handles insertion of data into database using prepared statements.
     *
     * @param productID   Identification of product.
     * @param productName Name of product.
     * @param productURL  URL of product.
     * @param imageURL    Image URL of the product.
     * @param price       Price of the product.
     * @param inStock     Whether item is in stock.
     */
    public void insertIntoStaticTable(String productID, String productName, String productURL, String imageURL, String price, String table, String inStock) {
        if (open()) {
            String sqlCode = "INSERT INTO " + table + " VALUES(?,?,?,?,?,?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlCode)) {
                preparedStatement.setString(1, productID);
                preparedStatement.setString(2, productName);
                preparedStatement.setString(3, productURL);
                preparedStatement.setString(4, imageURL);
                preparedStatement.setString(5, price);
                preparedStatement.setString(6, inStock);
                preparedStatement.executeUpdate();


            } catch (SQLException e) {
                System.out.println("SQL Exception for static table");
            }
            close();
        } else {
            System.out.println("Fatal Error");
        }

    }

    /**
     * Creating database to hold dynamic website information below.
     *
     * @param tableDataType Specific table to insert data into.
     */
    public void createDynamicTable(String tableDataType) {
        if (open()) {
            try (Statement statement = connection.createStatement()) {

                statement.executeUpdate(tableDataType);
            } catch (SQLException e) {
                System.out.println("Error establishing data connection");

            }
            close();
        } else {
            System.out.println("Can't Open Database");
        }

    }
    /**
     * Handles inserting data into database table to hold dynamic data of website.
     */
    /**
     * Method handles insertion of data into database of the dynamic table using prepared statements.
     *
     * @param productID Identification of product.
     * @param price     Price of product.
     * @param stock     Stock of product.
     * @param timestamp Time product information was obtained.
     */
    public void insertIntoDynamicTable(String productID, String price, String stock, String timestamp, String table) {
        if (open()) {
            String sqlCode = "INSERT INTO " + table + " VALUES(?,?,?,?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlCode)) {
                preparedStatement.setString(1, timestamp);
                preparedStatement.setString(2, productID);
                preparedStatement.setString(3, price);
                preparedStatement.setString(4, stock);
                preparedStatement.executeUpdate();


            } catch (SQLException e) {
                System.out.println("SQL Exception for dynamic table");
            }
            close();
        } else {
            System.out.println("Fatal Error");
        }

    }


}
