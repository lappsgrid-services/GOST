package edu.vassar.cs.lappsgrid.gost.http

/**
 *
 */
class SimpleClient implements HttpClient {
    String boundary = UUID.randomUUID().toString()

    String url

    SimpleClient(String url = HttpClient.SERVICE_URL) {
        this.url = url
    }

    void write(PrintStream out, String name, String value) {
        out.print('--')
        out.println(boundary)
        out.println('Content-Disposition: form-data; name="' + name + '"')
        out.println('Content-Type: text/plain')
        out.println()
        out.println(value)
    }

    Response post(String text) {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection()

        connection.setDoOutput(true); // Triggers POST.
//        if (accept.size() > 0) {
//            connection.setRequestProperty('Accept', accept.join(','))
//        }
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary");

        OutputStream output = connection.getOutputStream();
        PrintStream stream = new PrintStream(output)
        try {
            write(stream, 'type', 'rest')
            write(stream, 'email', 'services@lappsgrid.org')
            write(stream, 'tagset', 'c7')
            write(stream, 'style', 'vert')
            write(stream, 'text', text)

            stream.println('--' + boundary + '--')
        } finally {
            try { stream.close(); } catch (IOException ignored) { }
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


}
