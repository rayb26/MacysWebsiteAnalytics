package com.example.sport;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Class handles static information of obtaining product name, product id, product url,
 * image url, price, or whether the product is out of stock, and saves data
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
    List<String> productPrice;

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
            System.out.println("Static Scraper Running...");
            fetchOriginalPrice();
            fetchProductID();
            fetchProductName();
            fetchProductURL();
            fetchImageURL();

            int minElement = determineMinimumElement(productNameList.size(), productIDList.size(), productURL_List.size(),
                    productImageURL.size(), productPrice.size());

            for (int element = 0; element < minElement; element++) {
                db.insertIntoStaticTable(productIDList.get(element), productNameList.get(element),
                        productURL_List.get(element), productImageURL.get(element), productPrice.get(element), Database.TABLE_STATIC_DATA, "In Stock");
            }
            System.out.println("Static Scraper Finished...");

        } catch (IOException e) {
            System.out.println("Error");
            e.printStackTrace();
        }
    }

    /**
     * Method retrieves product name information.
     *
     * @throws IOException
     */
    private void fetchProductName() throws IOException {
        productNameList = new ArrayList<>();

        Document page = Jsoup.connect(website).get();
        Elements elementsByThumbnailImage = page.select("div.productThumbnailImage");

        for (Element element : elementsByThumbnailImage) {
            Elements elementsByHref = element.getElementsByClass("productDescLink");

            for (Element elementsBySource : elementsByHref) {
                productNameList.add(elementsBySource.attributes().get("title"));
            }
        }
    }

    /**
     * Method retrieves product identification information.
     * @throws IOException
     */
    public void fetchProductID() throws IOException {

        productIDList = new ArrayList<>();

        Document page = Jsoup.connect(website).get();
        Elements elementsByClass = page.select("div.productThumbnailImage");

        for (Element element : elementsByClass) {
            Elements elementsBySource = element.getElementsByClass("productDescLink");

            for (Element elementsByLink : elementsBySource) {
                String productID = elementsByLink.attributes().get("href");
                productIDList.add(productID.substring(productID.indexOf("?") + 4, productID.indexOf("&")));
            }

        }

    }

    /**
     * Method retrieves product URL information.
     *
     * @throws IOException
     */
    private void fetchProductURL() throws IOException {

        productURL_List = new ArrayList<>();

        Document page = Jsoup.connect(website).get();
        Elements elements = page.select("div.productThumbnailImage");
        for (Element elementsBySource : elements) {
            Elements elementsByLink = elementsBySource.getElementsByClass("productDescLink");

            for (Element elementHref : elementsByLink) {
                String productLink = "www.macys.com/" + elementHref.attributes().get("href");
                productURL_List.add(productLink);

            }

        }

    }

    /**
     * Method retrieves the url of each image from each product.
     *
     * @throws IOException
     */
    private void fetchImageURL() throws IOException {

        productImageURL = new ArrayList<>();
        Document page = Jsoup.connect(website).get();
        Elements elementsByClass = page.getElementsByClass("thumbnailImage");

        for (Element element : elementsByClass) {
            Elements elementsByThumbnail = element.getElementsByClass("thumbnailImage");

            for (Element elementsBySource : elementsByThumbnail) {
                productImageURL.add(elementsBySource.attributes().get("src"));
            }
        }
    }


    /**
     * Method retrieves the original price of each of the products.
     *
     * @throws IOException
     */
    private void fetchOriginalPrice() throws IOException {

        productPrice = new ArrayList<>();

        Document page = Jsoup.connect(website).get();


        Elements elements = page.select("span.regular.originalOrRegularPriceOnSale");

        for (Element element : elements) {
            productPrice.add(element.text());
        }
    }

    /**
     * Method determines the minimum list size to be used in the for loop to
     * extract the product information.
     *
     * @param productName Size of the productNameList.
     * @param productID   Size of the productIDList.
     * @param productURL  Size of the productURL_List.
     * @param imageURL    Size of the imageURList;
     * @param price       Size of the minimumElementList.
     * @return Minimum list size of all 5 parameters.
     */
    private int determineMinimumElement(int productName, int productID, int productURL, int imageURL, int price) {

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
