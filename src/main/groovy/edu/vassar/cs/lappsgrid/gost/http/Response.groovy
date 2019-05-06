package edu.vassar.cs.lappsgrid.gost.http

/**
 * Common response object returned by the various HTTP clients.
 */
public class Response {
    int status
    String encoding
    String contentType
    String message
    Map headers = [:]
    String body

    void print() {
        print(System.out)
    }

    void print(PrintStream out) {
        out.println(status)
        out.println("Encoding: $encoding")
        out.println("Content type: ${contentType}")
        out.println("Message: $message")
        headers.each { k,v -> "${k}: ${v}"}
        out.println(body)
    }
}
