/**
 * @author Strupiechowski Mateusz S18747
 */

package S_PASSTIME_SERVER1;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Client {

    private static Charset REQ_RES_CHARSET = StandardCharsets.UTF_8;

    private String id;
    private String host;
    private int port;
    private SocketChannel serverChannel;

    public Client(String host, int port, String id) {
        this.host = host;
        this.port = port;
        this.id = id;
    }

    public void connect() {
        try {
            serverChannel = SocketChannel.open();
            serverChannel.connect(new InetSocketAddress(host, port));
            serverChannel.configureBlocking(false);
        } catch (IOException e) {
            System.out.println("błąd podczas laczenia sie z serwerem");
            e.printStackTrace();
        }
    }

    public String send(String request) {
        StringBuilder responseStringBuilder = new StringBuilder();
        ByteBuffer responseByteBuffer = ByteBuffer.allocate(1024);
        CharBuffer outputCharBuffer = CharBuffer.wrap(request + "\n");

        try {
            serverChannel.write(REQ_RES_CHARSET.encode(outputCharBuffer));
            responseByteBuffer.clear();
            int readBytes;

            while ((readBytes = serverChannel.read(responseByteBuffer)) != -1) {
                if (readBytes != 0) {
                    responseByteBuffer.flip();
                    outputCharBuffer = REQ_RES_CHARSET.decode(responseByteBuffer);
                    responseStringBuilder.append(outputCharBuffer);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return responseStringBuilder.toString();
    }

    public String getId() {
        return id;
    }
}
