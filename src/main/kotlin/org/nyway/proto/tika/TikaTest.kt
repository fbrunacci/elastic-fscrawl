package org.nyway.proto.tika

import org.apache.tika.parser.AutoDetectParser
import org.apache.tika.parser.ParseContext
import org.apache.tika.parser.Parser
import org.apache.tika.metadata.Metadata
import org.apache.tika.sax.BodyContentHandler
import org.xml.sax.ContentHandler

object TikaTest {

    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {

//        val file = "pdf-test.pdf"
//        val file = "file-sample_100kB.doc"
        val file = "file-sample_100kB.docx"
        val handler: ContentHandler = getContent(file)
        println(handler.toString())
    }

    private fun getContent(file: String): ContentHandler {
        val stream = TikaTest.javaClass.classLoader.getResourceAsStream(file)
        val parser: Parser = AutoDetectParser()
        val handler: ContentHandler = BodyContentHandler()
        val metadata = Metadata()
        val context = ParseContext()

        parser.parse(stream, handler, metadata, context)
        return handler
    }
}