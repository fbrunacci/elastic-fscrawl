package org.nyway.proto.es.data

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.CredentialsProvider
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestClientBuilder
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.client.indices.CreateIndexRequest
import org.elasticsearch.client.indices.GetIndexRequest
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.xcontent.XContentType
import java.io.IOException
import java.util.*


object PageIndexer {

    val client = client()
    val index = "pages"

    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {

        println(client)
        deleteIndex()
        createIndex()
        indexPage(Page("http://localhost:9200&mode=view/41" , "41", "bob", listOf("test"), "localhost" ))
        indexPage(Page("http://localhost:9200&mode=view/42" , "42", "hello", listOf("hello"), "localhost" ))
        indexPage(Page("http://localhost:9200&mode=view/43" , "43", "hello 2", listOf("hello"), "localhost" ))
        indexPage(Page("http://localhost:9200&mode=view/44" , "44", "hello 3", listOf("hello"), "localhost" ))

        println("http://localhost:1358/?appname=pages&url=http://localhost:9200&mode=view")
        client.close()
    }

    fun indexPage(page: Page) {
        val request = IndexRequest(index)
        request.id(page.url)
        request.source(ObjectMapper().writeValueAsString(page), XContentType.JSON)
        val indexResponse = client.index(request, RequestOptions.DEFAULT)
        println("response id: " + indexResponse.id)
    }

    fun client(): RestHighLevelClient {
        val credentialsProvider: CredentialsProvider = BasicCredentialsProvider()
        credentialsProvider.setCredentials(AuthScope.ANY, UsernamePasswordCredentials("username", "password"))
        val builder: RestClientBuilder = RestClient.builder(HttpHost("localhost", 9200, "http"))
                .setHttpClientConfigCallback(HttpClientConfigCallback {
                    httpClientBuilder: HttpAsyncClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider) }
                )
        return RestHighLevelClient(builder)
    }

    @Throws(IOException::class)
    fun deleteIndex() {
        val getIndexRequest = GetIndexRequest(index)
        val exists = client.indices().exists(getIndexRequest, RequestOptions.DEFAULT)
        if (exists) {
            val request = DeleteIndexRequest(index)
            val indexResponse = client.indices().delete(request, RequestOptions.DEFAULT)
            println("deleteIndex response id: " + indexResponse)
        }
    }

    @Throws(IOException::class)
    fun createIndex() {
        val request = CreateIndexRequest(index)
        request.settings(Settings.builder()
                .put("index.number_of_shards", 1)
                .put("index.number_of_replicas", 2)
        )
        val message: MutableMap<String, Any> = HashMap()
        message["type"] = "text"
        val properties: MutableMap<String, Any> = HashMap()
        properties["url"] = message
        properties["title"] = message
        properties["text"] = message
        properties["domain"] = message
        //properties["tags"] = message

        val mapping: MutableMap<String, Any?> = HashMap()
        mapping["properties"] = properties
        request.mapping(mapping)

        val getIndexRequest = GetIndexRequest(index)
        val exists = client.indices().exists(getIndexRequest, RequestOptions.DEFAULT)
        if (!exists) {
            val indexResponse = client.indices().create(request, RequestOptions.DEFAULT)
            println("response id: " + indexResponse.index())
        }
    }

    @Throws(IOException::class)
    fun createIndex2() {
        val request = CreateIndexRequest(index)
        request.settings(Settings.builder()
                .put("index.number_of_shards", 1)
                .put("index.number_of_replicas", 2)
        )
        val message: MutableMap<String, Any> = HashMap()
        message["type"] = "text"
        val keyWordMap: MutableMap<String, Any> = HashMap()
        val keyWordValueMap: MutableMap<String, Any> = HashMap()
        keyWordValueMap["type"] = "keyword"
        keyWordValueMap["ignore_above"] = 256
        keyWordMap["keyword"] = keyWordValueMap
        message["fields"] = keyWordMap
        val properties: MutableMap<String, Any> = HashMap()
        properties["url"] = message
        properties["title"] = message
        val mapping: MutableMap<String, Any?> = HashMap()
        mapping["properties"] = properties
        request.mapping(mapping)
        val getIndexRequest = GetIndexRequest(index)
        val exists = client.indices().exists(getIndexRequest, RequestOptions.DEFAULT)
        if (!exists) {
            val indexResponse = client.indices().create(request, RequestOptions.DEFAULT)
            println("response id: " + indexResponse.index())
        }
    }


}