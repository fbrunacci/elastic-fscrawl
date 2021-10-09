package org.nyway.proto.crawler

import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.graphstream.graph.Graph
import org.graphstream.graph.Node
import org.nyway.proto.tdif.Tfidf
import java.util.*


class MyCrawler(var graph: Graph) : WebCrawler() {

    companion object {
        private val FILTERS: Pattern = Pattern.compile(".*(\\.(css|js|gif|jpg"
                + "|png|mp3|mp4|zip|gz))$") // TODO retirer les truc genre sdfsdf.css?ff=43...
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
    override fun shouldVisit(referringPage: Page?, webURL: WebURL): Boolean {
        val url: String = webURL.getURL().toLowerCase()
        if (FILTERS.matcher(stripUrlParameters(url)).matches()) return false

        val subDomainNode = graph.getSubDomainNode(webURL)
        if (subDomainNode.getVisit() > 10) return false
        subDomainNode.incVisit()
        println("shouldVisit:${subDomainNode.getVisit()} (${subDomainNode.getVisit()})  url: ${url}"  )
        return true
    }

    /**
     * This function is called when a page is fetched and ready
     * to be processed by your program.
     */
    override fun visit(page: Page) {
        println("visit:${page.webURL}"  )
        val webURL = page.webURL
        val url: String = webURL.url.toLowerCase()
        println("URL: $url")
        if (page.getParseData() is HtmlParseData) {
            val htmlData: HtmlParseData = page.getParseData() as HtmlParseData
            val text: String = htmlData.getText()
            val html: String = htmlData.getHtml()
            val links = htmlData.getOutgoingUrls()
            println("Text length: " + text.length)
            println("Html length: " + html.length)
            println("Number of outgoing links: " + links.size)

            val tags = Tfidf.getFrequentWords(htmlData.text).map { it.word }
            println("Tags: " + tags)

            val subDomainNode = graph.getSubDomainNode(webURL)
            for (link in links) {
                graph.addSubDomainEdge(webURL,link)
            }
        } else {
            println("NOT HtmlParseData: ${url}")
        }
    }

}

private fun Graph.addSubDomainEdge(webURL: WebURL, link: WebURL) {
    val fromSubDomainNode = this.getSubDomainNode(webURL)
    val toSubDomainNode = this.getSubDomainNode(link)
    val edgeToward = fromSubDomainNode.getEdgeToward(toSubDomainNode)
    if (edgeToward == null) {
        this.addEdge(UUID.randomUUID().toString(), fromSubDomainNode, toSubDomainNode)
    }
}

private fun Node.getVisit() : Int {
    val visit = this.getAttribute("visit")
    return if (visit != null) visit as Int else 0
}

private fun Node.incVisit() {
    val visit = this.getAttribute("visit")
    if (visit == null) {
        this.setAttribute("visit", 1);
    } else {
        this.setAttribute("visit", visit as Int + 1 );
    }
}

private fun Graph.getSubDomainNode(webURL: WebURL): Node {
    val label = webURL.subDomain + "." + webURL.domain
    var subDomainNode = this.getNode(label)
    if( subDomainNode == null ) {
        subDomainNode = this.addNode(label)
        subDomainNode.setAttribute("ui.label", label);
        subDomainNode.setAttribute("ui.class", "subDomain")
    }

    val domainNode = getDomainNode(webURL)
    if (domainNode.getEdgeToward(subDomainNode) == null ) {
        val subdomainEdge = this.addEdge(UUID.randomUUID().toString(), domainNode, subDomainNode)
        subdomainEdge.setAttribute("ui.class", "subDomainEdge")
    }
    return subDomainNode
}

private fun Graph.getDomainNode(webURL: WebURL): Node {
    val label = webURL.domain
    val node = this.getNode(label)
    if (node != null) { return node }
    val addNode = this.addNode(label)
    addNode.setAttribute("ui.label", label)
    addNode.setAttribute("ui.class", "domain")
    return addNode
}

private fun stripUrlParameters(url: String): String {
    val paramStartIndex = url.indexOf("?")
    return if (paramStartIndex == -1) {
        url
    } else {
        url.substring(0, paramStartIndex)
    }
}