package edu.vassar.cs.lappsgrid.gost.http

import edu.vassar.cs.lappsgrid.gost.ssl.HostnameVerifier
import edu.vassar.cs.lappsgrid.gost.ssl.TrustManager

import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import java.security.SecureRandom

/**
 * @author Keith Suderman
 */
class Http {

//    static final String BOUNDARY = "----WebKitFormBoundaryAA9EJ32BCQQBBtnm"
    static final String BOUNDARY = UUID.randomUUID().toString()
    static final String UTF8 = 'UTF-8'

    private List accept = []
    private String url = null

    String boundary = BOUNDARY

    Http() { }

    Http(String url) {
        this.url = url
    }

    String encode(input) {
        if (input instanceof GString) {
            return URLEncoder.encode(input.toString(), UTF8)
        }
        if (input instanceof String) {
            return URLEncoder.encode(input, UTF8)
        }
        return input
    }

    void accept(String contentType) {
        accept << contentType
    }

    private HttpURLConnection open(URL url) {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection()
        if (url.protocol == 'https') {
            HttpsURLConnection https = (HttpsURLConnection) connection
            SSLContext ssl = SSLContext.getInstance('TLS')
            ssl.init(null, [new TrustManager()] as TrustManager[], new SecureRandom())
            https.setSSLSocketFactory(ssl.getSocketFactory())
            https.setHostnameVerifier(new HostnameVerifier())
        }
        return connection
    }

    Response get(String query) {
        HttpURLConnection connection = open(new URL(query))
        connection.setRequestMethod("GET")

        Response response = new Response()
        response.status = connection.responseCode
        println response.status
        connection.headerFields.each { name,value ->
            if (name) {
                response.headers[name] = value
            }
        }
        if (response.status == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.inputStream))
            StringBuilder buffer = new StringBuilder()
            String line = reader.readLine()
            while (line != null) {
                buffer << line
                buffer << "\n"
                line = reader.readLine()
            }
            response.body = buffer.toString()
        }
        return response
    }

    Response post(Map params) {
        if (this.url == null) {
            throw new IOException("No URL has been specified.")
        }
        return post(url, params)
    }

    Response post(String url, Map params) {
        String query = params.collect {k,v -> "$k=${encode(v)}"}.join('&')
        return post(url, query)
    }

    Response post(String query) {
        if (this.url == null) {
            throw new IOException("No URL has been specified.")
        }
        return post(url, query)
    }

    Response post(String url, String query) {
        URLConnection connection = open(new URL(url))

        connection.setDoOutput(true); // Triggers POST.
        connection.setRequestProperty("Accept-Charset", UTF8);
        if (accept.size() > 0) {
            connection.setRequestProperty('Accept', accept.join(','))
        }
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + UTF8);

        OutputStream output = connection.getOutputStream();
        try {
            output.write(query.getBytes(UTF8));
        } finally {
            try { output.close(); } catch (IOException ignored) {}
        }

        Response response = new Response()
        connection.headerFields.each { name,value ->
            if (name) {
                response.headers[name] = value
            }
        }

        response.status = connection.responseCode
        response.encoding = connection.contentEncoding
        response.contentType = connection.contentType
        response.message = connection.responseMessage
        if (response.status == 200) {
            response.body = connection.inputStream.text
        }
        else {
            response.body = connection.errorStream.text
        }
        return response
    }

    void write(PrintStream out, String name, String value) {
        out.print('--')
        out.println(boundary)
        out.println('Content-Disposition: form-data; name="' + name + '"')
        out.println()
        out.println(value)
    }

    Response multipart(String url, Map parts) {
        HttpURLConnection connection = open(new URL(url))

        connection.setDoOutput(true); // Triggers POST.
//        connection.setRequestMethod('POST')
        if (accept.size() > 0) {
            connection.setRequestProperty('Accept', accept.join(','))
        }
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary");

        OutputStream output = connection.getOutputStream();
        PrintStream stream = new PrintStream(output)
        try {
            parts.each { key,value -> write(stream, key, value)}
            stream.println('--' + boundary + '--')
        } finally {
            println "Closing output stream."
            try { stream.close(); } catch (IOException ignored) { ignored.printStackTrace()}
        }

        println 'HTTP: waiting for the response.'
        Response response = new Response()
        connection.headerFields.each { name,value ->
            if (name) {
                response.headers[name] = value
            }
        }

        response.status = connection.responseCode
        response.encoding = connection.contentEncoding
        response.contentType = connection.contentType
        response.message = connection.responseMessage
        if (response.status == 200) {
            response.body = connection.inputStream.text
        }
        else {
            response.body = connection.inputStream.text
        }
        println "Returning response: ${response.status}"
        return response
    }

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
}
