package com.example.sport;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class handles static information of obtaining product name, product id, product url,
 * image url, sale price, or whether the product is out of stock, and saves data
 * into SQL database.
 *
 * @author Rayhan Biju
 */


public class StaticScraper implements Runnable {
    Database db = new Database();


    List<String> productNameList;
    List<String> productIDList;
    List<String> productURL_List;
    List<String> productImageURL;
    List<String> productSalePrice;

    /*
    Field holds website to crawl into.
     */
    public static final String website = "https://www.macys.com/shop/mens-clothing/sale-clearance?id=9559";

    /**
     * Method obtains all required data from the Macy's website.
     */

    @Override
    public void run() {
        db.createStaticTable(Database.CREATE_TABLE_STATIC);

        try {
            System.out.println("Static Scraper Running");
            fetchSalePrice();
            fetchProductID();
            fetchProductName();
            fetchProductURL();
            fetchImageURL();


            int minElement = determineMinimumElement(productNameList.size(), productIDList.size(), productURL_List.size(),
                    productImageURL.size(), productSalePrice.size());

            for (int i = 0; i < minElement; i++) {
                db.insertIntoStaticTable(productIDList.get(i), productNameList.get(i), productURL_List.get(i), productImageURL.get(i), productSalePrice.get(i), Database.TABLE_STATIC_DATA, "Yes");
            }
            System.out.println("Static Scraper Finished");


        } catch (IOException e) {
            System.out.println("Error" );
            e.printStackTrace();
        }


    }

    /**
     * Method retrieves product name information.
     * @throws IOException
     */
    public void fetchProductName() throws IOException {
        productNameList = new ArrayList<>();
        Document page = Jsoup.connect(website).get();

        Elements elements = page.getElementsByClass("productDescLink");

        for (Element element : elements) {
            String productName = element.attributes().get("title");
            productNameList.add(productName);
        }
    }

    /**
     * Method retrieves product identification information.
     * @throws IOException
     */
    public void fetchProductID() throws IOException {

        productIDList = new ArrayList<>();

        Document page = Jsoup.connect(website).get();
        Elements elements = page.getElementsByClass("productDescLink");

        for (Element element : elements) {

            String productID = element.attributes().get("href");
            String productIDCleanup = productID.substring(productID.lastIndexOf("?") + 4, productID.indexOf("&"));
            productIDList.add(productIDCleanup);

        }
    }

    /**
     * Method retrieves product URL information.
     * @throws IOException
     */
    public void fetchProductURL() throws IOException {

        productURL_List = new ArrayList<>();

        Document page = Jsoup.connect(website).get();
        Elements elements = page.getElementsByClass("productDescLink");

        for (Element element : elements) {

            String productLink = "www.macys.com/" + element.attributes().get("href");
            productURL_List.add(productLink);
        }
    }

    /**
     * Method retrieves the url of each image from each product.
     * @throws IOException
     */
    public void fetchImageURL() throws IOException {

        productImageURL = new ArrayList<>();
        Document page = Jsoup.connect(website).get();
        Elements elements = page.getElementsByClass("thumbnailImage");

        for (Element element : elements) {
            productImageURL.add(element.attributes().get("src"));
        }
    }

    /**
     * Method retrieves the original price of each of the products.
     * @throws IOException
     */
    public void fetchSalePrice() throws IOException {

        productSalePrice = new ArrayList<>();

        Document page = Jsoup.connect(website).get();


        Elements elements = page.select("span.regular.originalOrRegularPriceOnSale");

        for (Element element : elements) {
            productSalePrice.add(element.text());
        }
    }

    /**
     * Method determines the minimum list size to be used in the for loop to
     * extract the product information.
     * @param productName Size of the productNameList.
     * @param productID Size of the productIDList.
     * @param productURL Size of the productURL_List.
     * @param imageURL Size of the imageURList;
     * @param price Size of the minimumElementList.
     * @return Minimum list size of all 5 parameters.
     */
    public int determineMinimumElement(int productName, int productID, int productURL, int imageURL, int price) {

        int[] elements = {productName, productID, productURL, imageURL, price};

        int minElement = Integer.MAX_VALUE;

        for (int element : elements) {

            if (element < minElement) {
                minElement = element;
            }

        }
        return minElement;

    }


}
