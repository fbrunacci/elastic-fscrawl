package org.nyway.proto.crawler

import edu.uci.ics.crawler4j.crawler.CrawlConfig
import edu.uci.ics.crawler4j.crawler.CrawlController
import edu.uci.ics.crawler4j.crawler.CrawlController.WebCrawlerFactory
import edu.uci.ics.crawler4j.fetcher.PageFetcher
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer
import org.graphstream.graph.Graph
import org.graphstream.graph.implementations.SingleGraph


object MyCrawlerTest {

    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val crawlStorageFolder = "/tmp/crawl"
        val numberOfCrawlers = 7
        val config = CrawlConfig()
        config.crawlStorageFolder = crawlStorageFolder

        System.setProperty("org.graphstream.ui", "swing");
        val graph: Graph = SingleGraph("MyCrawlerTest")
        graph.setAttribute(
            "ui.stylesheet",
            "node {" +
                    "shape: circle;" +
                    "size: 1px;" +
                    "fill-color: #777;" +
//                    "text-mode: normal;" +
//                    "text-color: red;"+
                    "text-size: 4;" +
//                    "text-offset: 0px, 10px;" +
                    "z-index: 0;" +
                    "}" +
                    "node.domain { size: 10px; fill-color: red; text-mode: normal; text-color: red; text-size: 8; text-offset: 0px, 10px; z-index: 10; }"

        )
        graph.display()

        // Instantiate the controller for this crawl.
        val pageFetcher = PageFetcher(config)
        val robotstxtConfig = RobotstxtConfig()
        val robotstxtServer = RobotstxtServer(robotstxtConfig, pageFetcher)
        val controller = CrawlController(config, pageFetcher, robotstxtServer)

        // For each crawl, you need to add some seed urls. These are the first
        // URLs that are fetched and then the crawler starts following links
        // which are found in these pages
        controller.addSeed("https://www.ics.uci.edu/")

        // The factory which creates instances of crawlers.
        val factory: WebCrawlerFactory<MyCrawler> = WebCrawlerFactory { MyCrawler(graph) }

        // Start the crawl. This is a blocking operation, meaning that your code
        // will reach the line after this only when crawling is finished.
        controller.start(factory, numberOfCrawlers)
    }
}