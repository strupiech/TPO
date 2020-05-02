/**
 *
 *  @author Strupiechowski Mateusz S18747
 *
 */

package S_PASSTIME_SERVER1;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client {

    String id;
    SocketChannel clientChannel;
    ByteBuffer buffer;

    public Client(String host, int port, String id) {
        try {
            clientChannel = SocketChannel.open(new InetSocketAddress(host, port));
        } catch (IOException e) {
            e.printStackTrace();
        }
        buffer = ByteBuffer.allocate(512);
        this.id = id;
    }

    public void connect() {
    }

    public String send(String request) {
        buffer = ByteBuffer.wrap(request.getBytes());
        String response = null;
        try{
            clientChannel.write(buffer);
            buffer.clear();
            clientChannel.read(buffer);
            response = new String(buffer.array()).trim();
            System.out.println("response: " + response);
            buffer.clear();
        }catch (IOException e){
            e.printStackTrace();
        }
        return response;
    }
}
