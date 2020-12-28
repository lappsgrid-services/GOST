package edu.vassar.cs.lappsgrid.gost.http

import groovyx.net.http.CoreEncoders

//import groovyx.net.http.ApacheEncoders
//import groovyx.net.http.OkHttpEncoders

import groovyx.net.http.HttpBuilder

import static groovyx.net.http.MultipartContent.multipart
/**
 *
 */
class BuilderClient implements HttpClient {
    String url

    BuilderClient(String url = HttpClient.SERVICE_URL) {
        this.url = url
    }


    Response post(String text) {
        Response result = new Response()
        def http = HttpBuilder.configure {
            request.uri = url
        }
        String body = http.post {
            request.contentType = "multipart/form-data"
            request.body = multipart {
                field 'type', 'rest'
                field 'email', 'services@lappsgrid.org'
                field 'tagset' ,'c7'
                field 'style', 'vert'
                field 'text', text
            }
            request.encoder 'multipart/form-data', CoreEncoders.&multipart
        }
        result.status = 200
        result.contentType = 'text/plain'
        result.encoding = 'UTF-8'
        result.body = body
        return result
    }
}
