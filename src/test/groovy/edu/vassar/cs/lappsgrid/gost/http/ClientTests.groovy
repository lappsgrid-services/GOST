package edu.vassar.cs.lappsgrid.gost.http

import edu.vassar.cs.lappsgrid.gost.SimpleServer
import org.junit.Ignore
import org.junit.Test

/**
 *
 */
@Ignore
class ClientTests {

    @Test
    void builder() {
        test(new BuilderClient())
    }

    @Test
    void builderLocal() {
        HttpClient client = new BuilderClient("http://localhost:8000")
        local(client)
    }

    void local(HttpClient client) {
        SimpleServer server = new SimpleServer(8000)
        server.start()
        Response response = client.post("Karen flew to New York.")
        println response.body
        server.stop()
    }

    void test(HttpClient client) {
        Response response = client.post("Karen flew to New York.")
        assert 200 == response.status
        println "---"
        println response.body
        println "---"
        List<String> lines = response.body.trim()readLines()
        assert 9 == lines.size()
        assert lines[0].startsWith('#')
    }
}
