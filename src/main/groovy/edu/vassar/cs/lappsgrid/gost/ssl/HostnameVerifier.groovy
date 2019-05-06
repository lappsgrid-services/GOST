package edu.vassar.cs.lappsgrid.gost.ssl

import javax.net.ssl.SSLSession

/**
 * @author Keith Suderman
 */
class HostnameVerifier implements javax.net.ssl.HostnameVerifier {
    @Override
    boolean verify(String s, SSLSession sslSession) {
        return true;
    }
}
