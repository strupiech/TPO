/**
 * @author Strupiechowski Mateusz S18747
 */

package S_PASSTIME_SERVER1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class Server {

    private ServerSocketChannel serverChannel = null;
    private Selector selector = null;
    boolean isServerRunning = true;
    private ServerLog serverLog;

    public Server(String host, int port) {
        try {
            serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            serverChannel.socket().bind(new InetSocketAddress(host, port));

            selector = Selector.open();

            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        this.serverLog = new ServerLog();
    }

    public void startServer() throws IOException {
        System.out.println("Server started and ready");

        while (isServerRunning) {
            try {
                selector.select();

                Set keys = selector.selectedKeys();
                Iterator iterator = keys.iterator();
                while (iterator.hasNext()) {

                    SelectionKey key = (SelectionKey) iterator.next();
                    iterator.remove();

                    if (key.isAcceptable()) {
                        SocketChannel channel = serverChannel.accept();
                        channel.configureBlocking(false);
                        channel.register(selector, SelectionKey.OP_READ);
                        System.out.println("polaczony klient");
                        continue;
                    }

                    if (key.isReadable()) {
                        SocketChannel channel = (SocketChannel) key.channel();
                        //TODO OBSLUGA REQUESTU
                        continue;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    public void stopServer() {
        if (this.serverChannel != null && this.serverChannel.isOpen()) {
            try {
                isServerRunning = false;
                this.serverChannel.close();
            } catch (IOException e) {
                System.out.println("Exception while closing server socket");
                e.printStackTrace();
            }
        }

        try {
            for (SelectionKey key : this.selector.keys()) {
                SelectableChannel channel = key.channel();

                if (channel instanceof SocketChannel) {
                    SocketChannel socketChannel = (SocketChannel) channel;

                    try {
                        socketChannel.close();
                    } catch (IOException e) {
                        System.out.println("Exception while closing client socket");
                        e.printStackTrace();
                    }

                    key.cancel();
                }
            }
            selector.close();
        } catch (Exception ex) {
            System.out.println("Exception while closing selector");
            ex.printStackTrace();
        }
    }

    public String getServerLog() {
        return serverLog.get();
    }
}
