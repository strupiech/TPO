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

    String id;
    private String host;
    private int port;
    private SocketChannel channel = null;
    Charset charset = StandardCharsets.UTF_8;

    public Client(String host, int port, String id) {
        this.host = host;
        this.port = port;
        this.id = id;
    }

    public void connect() {
        try {
            channel = SocketChannel.open();
            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress(host, port));
            while (!channel.finishConnect()) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    return;
                }
            }
        } catch (IOException e) {
            System.out.println("błąd podczas laczenia sie z serwerem");
            e.printStackTrace();
        }
    }

    public String send(String request) {
        StringBuilder response = new StringBuilder();
        ByteBuffer responseBuffer = ByteBuffer.allocate(1024);
        CharBuffer charBuffer = CharBuffer.wrap(request + "\n");

        try {
            channel.write(charset.encode(charBuffer));
            responseBuffer.clear();
            int readBytes;

            while ((readBytes = channel.read(responseBuffer)) != -1) {
                if (readBytes != 0) {
                    responseBuffer.flip();
                    charBuffer = charset.decode(responseBuffer);
                    response.append(charBuffer);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response.toString();
    }
}
