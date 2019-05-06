package edu.vassar.cs.lappsgrid.gost.http

import org.apache.http.Header
import org.apache.http.HttpEntity
import org.apache.http.StatusLine
import org.apache.http.client.methods.CloseableHttpResponse


import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.mime.FormBodyPart
import org.apache.http.entity.mime.content.StringBody
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.entity.mime.MultipartEntityBuilder

/**
 *
 */
class ApacheClient implements HttpClient{
    String url

    ApacheClient(String url = HttpClient.SERVICE_URL) {
        this.url = url
    }


    FormBodyPart part(String name, String value) {
        return new FormBodyPart(name, new StringBody(value, ContentType.TEXT_PLAIN))
    }

    Response post(String text) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        HttpEntity multipart = MultipartEntityBuilder.create()
            .addPart(part('email', 'services@lappsgrid.org'))
            .addPart(part('type', 'rest'))
            .addPart(part('tagset', 'c7'))
            .addPart(part('style', 'vert'))
            .addPart(part('text', text))
            .setContentType(ContentType.MULTIPART_FORM_DATA)
            .build()
        post.setEntity(multipart)
        CloseableHttpResponse response = client.execute(post)
        StatusLine status = response.getStatusLine()
        HttpEntity entity = response.entity
        Response result = new Response()
        result.status = status.statusCode
        result.message = status.reasonPhrase
        result.body = entity.content.text
        result.headers = [:]
        response.getAllHeaders().each { Header h -> result.headers[h.name] = h.value }
        return result
    }


    static void main(String[] args) {
        new ApacheClient().remote()
    }
}
