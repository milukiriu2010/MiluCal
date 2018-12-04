package milu.kiriu2010.net.v2

import java.net.InetAddress
import java.net.Socket
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

// https://stackoverflow.com/questions/33567596/android-https-web-service-communication-ssl-tls-1-2
class MySSLSocketFactory(val sslSocketFactory: SSLSocketFactory): SSLSocketFactory() {
    override fun getDefaultCipherSuites(): Array<String> {
        return this.sslSocketFactory.defaultCipherSuites
    }

    override fun createSocket(s: Socket?, host: String?, port: Int, autoClose: Boolean): Socket {
        val socket = this.sslSocketFactory.createSocket( s, host, port, autoClose ) as SSLSocket
        socket.enabledProtocols = arrayOf("TLSv1.2")
        // TLSv1.3 is not supported
        //socket.enabledProtocols = arrayOf("TLSv1.2","TLSv1.3")
        return socket
    }

    override fun createSocket(host: String?, port: Int): Socket {
        val socket = this.sslSocketFactory.createSocket( host, port ) as SSLSocket
        socket.enabledProtocols = arrayOf("TLSv1.2")
        return socket
    }

    override fun createSocket(host: String?, port: Int, localHost: InetAddress?, localPort: Int): Socket {
        val socket = this.sslSocketFactory.createSocket( host, port, localHost, localPort ) as SSLSocket
        socket.enabledProtocols = arrayOf("TLSv1.2")
        return socket
    }

    override fun createSocket(host: InetAddress?, port: Int): Socket {
        val socket = this.sslSocketFactory.createSocket( host, port ) as SSLSocket
        socket.enabledProtocols = arrayOf("TLSv1.2")
        return socket
    }

    override fun createSocket(address: InetAddress?, port: Int, localAddress: InetAddress?, localPort: Int): Socket {
        val socket = this.sslSocketFactory.createSocket( address, port, localAddress, localPort ) as SSLSocket
        socket.enabledProtocols = arrayOf("TLSv1.2")
        return socket
    }

    override fun getSupportedCipherSuites(): Array<String> {
        return this.sslSocketFactory.supportedCipherSuites
    }
}