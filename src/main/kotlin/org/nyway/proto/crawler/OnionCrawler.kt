package org.nyway.proto.crawler

import edu.uci.ics.crawler4j.crawler.Page
import edu.uci.ics.crawler4j.crawler.WebCrawler
import edu.uci.ics.crawler4j.parser.HtmlParseData
import edu.uci.ics.crawler4j.url.WebURL
import org.nyway.proto.es.data.PageIndexer
import org.nyway.proto.tdif.Tfidf
import java.util.regex.Pattern


class OnionCrawler : WebCrawler() {

    companion object {
        private val EXCLUDE_FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
                + "|png|mp3|mp4|zip|gz))$")

        private val EXCLUDE_SITES = listOf("torgatedga35slsu.onion","wikitjerrta4qgz4.onion")
    }

    /**
     * This method receives two parameters. The first parameter is the page
     * in which we have discovered this new url and the second parameter is
     * the new url. You should implement this function to specify whether
     * the given url should be crawled or not (based on your crawling logic).
     * In this example, we are instructing the crawler to ignore urls that
     * have css, js, git, ... extensions and to only accept urls that start
     * with "https://www.ics.uci.edu/". In this case, we didn't need the
     * referringPage parameter to make the decision.
     */
    override fun shouldVisit(referringPage: Page?, url: WebURL): Boolean {
        val domain: String = url.domain.toLowerCase()
        val href = url.url.toLowerCase()

        return !EXCLUDE_FILTERS.matcher(href).matches()
                && domain.endsWith("onion")
                && !EXCLUDE_SITES.contains(domain)
    }

    /**
     * This function is called when a page is fetched and ready
     * to be processed by your program.
     */
    override fun visit(page: Page) {
        val url: String = page.getWebURL().getURL()
        println("URL: $url")
        if (page.getParseData() is HtmlParseData) {
            val htmlData: HtmlParseData = page.getParseData() as HtmlParseData
            val title = htmlData.title.let{"no title"}
            val description = htmlData.metaTags.get("description").let {""}
            val tags = Tfidf.getFrequentWords(htmlData.text).map { it.word }

            println("Title: " + title + " [" + htmlData.text.length + " , " + htmlData.html.length + "]")
            println("Description: " + description)
            println("Number of outgoing links: " + htmlData.outgoingUrls?.size)
            println("Fpoorequent Words: " + Tfidf.getFrequentWords(htmlData.text).map { it.word })
            PageIndexer.indexPage(org.nyway.proto.es.data.Page(url, title, description, tags, page.getWebURL().domain))
        }
    }

    override fun onContentFetchError(page: Page) {
        logger.warn("OO Can't fetch content of: {}", page.webURL.url)
        // Do nothing by default (except basic logging)
        // Sub-classed can override this to add their custom functionality
    }

    override fun onContentFetchError(webUrl: WebURL) {
        logger.warn("OO Can't fetch content of: {}", webUrl.url)
        // Do nothing by default (except basic logging)
        // Sub-classed can override this to add their custom functionality
    }

}