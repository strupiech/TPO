/**
 *
 *  @author Strupiechowski Mateusz S18747
 *
 */

package S_PASSTIME_SERVER1;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {

    String host;
    int port;
    ServerLog serverLog;
    volatile boolean isRunning;

    public Server(String host, int port) {
        this.host = host;
        this.port = port;
        this.serverLog = new ServerLog();
    }

    public void startServer() throws IOException {
        isRunning = true;
        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(host, port));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while(isRunning){
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();
            ByteBuffer buffer = ByteBuffer.allocate(512);
            while(iterator.hasNext()){
                SelectionKey key = iterator.next();

                if (key.isAcceptable())
                    register(selector, serverSocketChannel);

                if (key.isReadable())
                    answerWithEcho(buffer, key);
            }
        }
    }

    private void register(Selector selector, ServerSocketChannel serverSocketChannel)
            throws IOException {
        SocketChannel client = serverSocketChannel.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
    }

    private static void answerWithEcho(ByteBuffer buffer, SelectionKey key)
            throws IOException {

        SocketChannel client = (SocketChannel) key.channel();
        client.read(buffer);

        System.out.println("request: " + new String(buffer.array()).trim());

        buffer.flip();
        client.write(buffer);
        buffer.clear();
    }

    public void stopServer() {
        isRunning = false;
    }

    public String getServerLog() {
        return serverLog.get();
    }
}
