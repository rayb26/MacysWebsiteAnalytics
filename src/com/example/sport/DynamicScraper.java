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

                System.out.println("Dynamic Scrapper Running...");
                fetchProductID();
                fetchSalePrice();

                int minElement = Math.min(productIDListDynamic.size(), productSalePriceDynamic.size());

                for (int element = 0; element < minElement; element++) {
                    db.insertIntoDynamicTable(productIDListDynamic.get(element), productSalePriceDynamic.get(element),
                            "In Stock", getTimeStamp(), Database.TABLE_DYNAMIC_DATA);
                }
                System.out.println("Dynamic Scraper waiting for 5 minutes");
                Thread.sleep(300000);
                System.out.println("Dynamic Scraper restarted");
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
        Elements elementsByClass = page.select("div.productThumbnailImage");

        for (Element element : elementsByClass) {
            Elements elementsBySource = element.getElementsByClass("productDescLink");

            for (Element elementsByLink : elementsBySource) {
                String productID = elementsByLink.attributes().get("href");
                productIDListDynamic.add(productID.substring(productID.indexOf("?") + 4, productID.indexOf("&")));
            }

        }
    }

    /**
     * Method overrides the fetchSalePrice method in superclass to retrieve sale price
     * information.
     *
     * @throws IOException
     */
    public void fetchSalePrice() throws IOException {
        productSalePriceDynamic = new ArrayList<>();

        Document page = Jsoup.connect(website).get();

        Elements elements = page.select("span.discount");

        for (Element element : elements) {
            String salePriceCleanup = "$" + element.text().substring(element.text().indexOf("$") + 1);
            productSalePriceDynamic.add(salePriceCleanup);
        }
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
