package com.example.sport;


import java.io.IOException;
/**
 * Class handles running the static and dynamic crawlers for the Macy's Website.
 * @author Rayhan Biju
 */

public class Main {


    public static void main(String[] args) throws IOException {

        StaticScraper scraper = new StaticScraper();
        scraper.run();

        DynamicScraper dynamicScraper = new DynamicScraper();
        dynamicScraper.run();

    }
}
