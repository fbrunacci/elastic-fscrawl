package org.nyway.proto.es.data

// BEGIN model classes
data class Page(
    val url: String,
    val title: String,
    val text: String,
    val tags: List<String>,
    val domain: String
)
// END model classes
