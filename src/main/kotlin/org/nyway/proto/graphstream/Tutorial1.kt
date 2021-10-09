package org.nyway.proto.graphstream

import org.graphstream.graph.Graph
import org.graphstream.graph.implementations.SingleGraph


object Tutorial1 {
    @JvmStatic
    fun main(args: Array<String>) {
        System.setProperty("org.graphstream.ui", "swing");

        val graph: Graph = SingleGraph("Tutorial 1")
        graph.setAttribute("ui.stylesheet", 
            "node {" +
                    "shape: box;" +
                    "size: 10px, 10px;" +
                    "fill-color: #777;" +
                    "text-mode: normal;" +
                    "z-index: 0;" +
                    "}" +
                    "" +
                    "edge {" +
                    "shape: line;" +
                    "fill-color: #222;" +
                    "arrow-size: 3px, 2px;" +
                    "}" +
                    "edge.tollway { size: 2px; stroke-color: red; stroke-width: 1px; stroke-mode: plain; }" +
                    "edge.tunnel { stroke-color: blue; stroke-width: 1px; stroke-mode: plain; }" +
                    "edge.bridge { stroke-color: yellow; stroke-width: 1px; stroke-mode: plain; }" +
                    "");
        
        graph.addNode("A" );
        graph.addNode("B" );
        graph.addNode("C" );
        graph.addEdge("AB", "A", "B").setAttribute("ui.class", "tollway")
        graph.addEdge("BC", "B", "C").setAttribute("ui.class", "tunnel")
        graph.addEdge("CA", "C", "A").setAttribute("ui.class", "bridge")

        graph.display()
        Thread.sleep(1000);
        graph.addNode("D" );
        graph.addNode("E" );
        graph.addEdge("AD", "A", "D");
        graph.addEdge("BE", "B", "E");

    }
}