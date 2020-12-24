package com.example.sport;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Class handles saving product id, sale price, stock, timestamp of all products every
 * 5 minutes into an SQL database.
 *
 * @author Rayhan Biju
 */
public class DynamicScraper extends StaticScraper implements Runnable {

    List<String> productIDListDynamic;
    List<String> productSalePriceDynamic;

    /**
     * Method uses a thread and a while loop that runs every 5 minutes.
     */

    @Override
    public void run() {

        boolean regenerate = true;

        while (regenerate) {
            db.createDynamicTable(Database.CREATE_TABLE_DYNAMIC);

            try {

                fetchProductID();
                fetchSalePrice();

                int minElement = Math.min(productIDListDynamic.size(), productSalePriceDynamic.size());


                for (int element = 0; element < minElement; element++) {
                    db.insertIntoDynamicTable(productIDListDynamic.get(element), productSalePriceDynamic.get(element),
                            "Yes", getTimeStamp(), Database.TABLE_DYNAMIC_DATA);
                }

                Thread.sleep(300000);
            } catch (InterruptedException | IOException e) {
                regenerate = false;
                e.printStackTrace();
            }

        }
    }

    /**
     * Method overrides the fetchProductID method in superclass to retrieve product identification
     * information.
     *
     * @throws IOException
     */
    @Override
    public void fetchProductID() throws IOException {
        productIDListDynamic = new ArrayList<>();

        Document page = Jsoup.connect(website).get();
        Elements elements = page.getElementsByClass("productDescLink");

        for (Element element : elements) {

            String productID = element.attributes().get("href");
            String productIDCleanup = productID.substring(productID.lastIndexOf("?") + 4, productID.indexOf("&"));
            productIDListDynamic.add(productIDCleanup);

        }
        System.out.println("Product id list size " + productIDListDynamic.size());
    }

    /**
     * Method overrides the fetchSalePrice method in superclass to retrieve price
     * information.
     *
     * @throws IOException
     */
    @Override
    public void fetchSalePrice() throws IOException {
        productSalePriceDynamic = new ArrayList<>();

        Document page = Jsoup.connect(website).get();

        Elements elements = page.select("span.regular.originalOrRegularPriceOnSale");

        for (Element element : elements) {
            productSalePriceDynamic.add(element.text());
        }

        System.out.println("Sale price size " + productSalePriceDynamic.size()
        );
    }
    /**
     * Method obtains current timestamp to be matched with when the information was acquired from
     * the Macy's website.
     *
     * @return String value of timestamp.
     */
    private String getTimeStamp() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        return timestamp.toString();
    }

}
