package org.nyway.proto.crawler

import edu.uci.ics.crawler4j.crawler.CrawlConfig
import edu.uci.ics.crawler4j.crawler.CrawlController
import edu.uci.ics.crawler4j.crawler.CrawlController.WebCrawlerFactory
import edu.uci.ics.crawler4j.fetcher.PageFetcher
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer


object OnionCrawlerTest {

    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {

        /*
        sudo systemctl start tor.service; polipo socksParentProxy=localhost:9050
         */

        val crawlStorageFolder = "/tmp/crawl"
        val numberOfCrawlers = 7
        val config = CrawlConfig()

        config.isIncludeHttpsPages = true
        config.proxyHost = "localhost";
        config.proxyPort = 8123;

        config.crawlStorageFolder = crawlStorageFolder
        config.maxDepthOfCrawling = 10
        config.isResumableCrawling = false

        // Instantiate the controller for this crawl.
        val pageFetcher = PageFetcher(config)
        val robotstxtConfig = RobotstxtConfig()
        robotstxtConfig.isEnabled = false
        val robotstxtServer = RobotstxtServer(robotstxtConfig, pageFetcher)
        val controller = CrawlController(config, pageFetcher, robotstxtServer)

        // For each crawl, you need to add some seed urls. These are the first
        // URLs that are fetched and then the crawler starts following links
        // which are found in these pages
        // controller.addSeed("https://www.ics.uci.edu/")
        //controller.addSeed("http://3g2upl4pq6kufc4m.onion")
        controller.addSeed("http://wikitjerrta4qgz4.onion/")
        controller.addSeed("http://torwikignoueupfm.onion/index.php?title=Main_Page")
        // "http://m37rczgigmnvycc7b6ihkiatjv6kyobv7lp2tvvfxl43qaowqyue3qid.onion/categories"



        // The factory which creates instances of crawlers.
        val factory: WebCrawlerFactory<OnionCrawler> = WebCrawlerFactory { OnionCrawler() }

        // Start the crawl. This is a blocking operation, meaning that your code
        // will reach the line after this only when crawling is finished.
        controller.start(factory, numberOfCrawlers)
    }
}