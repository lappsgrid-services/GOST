package edu.vassar.cs.lappsgrid.gost.http

/**
 *
 */
interface HttpClient {
    final String SERVICE_URL = "http://ucrel-api.lancaster.ac.uk/cgi-bin/gost.pl"
    Response post(String text)
}