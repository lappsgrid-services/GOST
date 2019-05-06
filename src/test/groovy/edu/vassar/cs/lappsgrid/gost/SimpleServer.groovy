package edu.vassar.cs.lappsgrid.gost

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 *
 */
class SimpleServer {

    int port
    boolean running
    Thread thread
    ServerSocket server

    SimpleServer(int port) {
        this.port = port
    }

    void start() {
        thread = Thread.start {
            println 'Starting server thread.'
            running = true
            server = new ServerSocket(port)
            while (running) {
                try {
                    Socket socket = server.accept()
                    println 'connection accepted.'
                    new ServiceThread().service(socket)
                }
                catch (SocketException e) {
                    println "server.accept() was interrupted"
                }
            }
        }
    }

    void stop() {
        if (running) {
            println 'Attempting to stop the server.'
            running = false
            server.close()
        }
        else {
            println "The server is not running."
        }
    }

    class ServiceThread {

        boolean running = false

        void service(Socket socket) {
            Thread.start {
                println 'servicing the socket.'
                byte[] buffer = new byte[4096]
                InputStream stream = socket.inputStream
                StringWriter writer = new StringWriter()
                int n = stream.read(buffer)
                while (n > 0) {
                    writer.write(new String(buffer, 0, n))
                    if (stream.available() == 0) {
                        n = 0
                    }
                    else {
                        n = stream.read(buffer)
                    }
//                println new String(buffer, 0, n)
                }
                Instant now = Instant.now()
                String timestamp = DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneId.of("UTC")).format(now)
                String output = writer.toString()
                println "Read ${output.length()} characters."
                PrintStream pstream = new PrintStream(socket.outputStream)
                pstream.println """HTTP/1.1 200 OK
Date: ${timestamp}
Server: Custom
Content-Length: ${output.length()}
Content-Type: text/plain

${output}
"""
                println "Service thread done."
            }
        }
    }
}
