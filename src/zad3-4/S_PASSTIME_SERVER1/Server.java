/**
 * @author Strupiechowski Mateusz S18747
 */

package S_PASSTIME_SERVER1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class Server extends Thread {

    private ServerSocketChannel serverChannel = null;
    private Selector selector = null;
    boolean isRunning = true;
    private ServerLog serverLog;
    private ByteBuffer buffer;
    Charset charset = StandardCharsets.UTF_8;
    private StringBuilder requestString;
    private StringBuilder responseString;

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
        requestString = new StringBuilder();
        responseString = new StringBuilder();
        buffer = ByteBuffer.allocate(1024);
        this.serverLog = new ServerLog();
    }

    public void startServer() {
        start();
    }

    public void stopServer() {
        if (this.serverChannel != null && this.serverChannel.isOpen()) {
            try {
                isRunning = false;
                interrupt();
                this.serverChannel.close();
            } catch (IOException e) {
                System.out.println("Exception while closing server socket  or interrupting server thread");
                e.printStackTrace();
            }
        }

        try {
            for (SelectionKey key : this.selector.keys()) {
                SelectableChannel clientChannel = key.channel();

                if (clientChannel instanceof SocketChannel) {
                    SocketChannel socketChannel = (SocketChannel) clientChannel;

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

    @Override
    public void run() {
        while (isRunning) {
            try {
                selector.select();

                Set keys = selector.selectedKeys();
                Iterator iterator = keys.iterator();
                while (iterator.hasNext()) {

                    SelectionKey key = (SelectionKey) iterator.next();
                    iterator.remove();

                    if (key.isAcceptable()) {
                        SocketChannel clientChannel = serverChannel.accept();
                        clientChannel.configureBlocking(false);
                        clientChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                        continue;
                    }

                    if (key.isReadable()) {
                        SocketChannel clientChannel = (SocketChannel) key.channel();
                        handleRequest(clientChannel);
                    }
                }
            } catch (ClosedSelectorException ignored) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleRequest(SocketChannel clientChannel) {
        if (!clientChannel.isOpen()) return;

        requestString.setLength(0);
        buffer.clear();

        try {
            readingLoop:
            while (true) {
                int bytes = clientChannel.read(buffer);
                if (bytes > 0) {
                    buffer.flip();
                    CharBuffer charBuffer = charset.decode(buffer);
                    while (charBuffer.hasRemaining()) {
                        char c = charBuffer.get();
                        if (c == '\r' || c == '\n') break readingLoop;
                        requestString.append(c);
                    }
                }
            }

            String clientRequest = requestString.toString();
            String[] request = clientRequest.split(" ");
            Integer clientPort = clientChannel.socket().getPort();

            if (request[0].equals("login")) {

                serverLog.addClient(clientPort, request[1]);
                serverLog.addToServerLog(clientPort, "logged in at " + serverLog.getTime());
                serverLog.addToClientLog(clientPort, "logged in");

                writeResponse(clientChannel, "logged in");

            } else if (clientRequest.equals("bye")) {

                serverLog.addToServerLog(clientPort, "logged out at " + serverLog.getTime());
                serverLog.addToClientLog(clientPort, "logged out");

                writeResponse(clientChannel, "logged out");
                clientChannel.close();
                clientChannel.socket().close();

            } else if (clientRequest.equals("bye and log transfer")) {

                serverLog.addToServerLog(clientPort, "logged out at " + serverLog.getTime());
                serverLog.addToClientLog(clientPort, "logged out");

                writeResponse(clientChannel, serverLog.get(clientPort));

                clientChannel.close();
                clientChannel.socket().close();

            } else if (request[0].matches("\\d{4}-\\d{2}-\\d{2}")
                    || request[0].matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}")) {

                String requestResult = Time.passed(request[0], request[1]);

                serverLog.addToServerLog(clientPort, "request at " + serverLog.getTime()
                        + ": \"" + request[0] + " " + request[1] + "\"");
                serverLog.addToClientLog(clientPort, "Request: " + request[0] + " " + request[1]);
                serverLog.addToClientLog(clientPort, "Result:\n" + requestResult);

                writeResponse(clientChannel, requestResult);
            }

        } catch (IOException e) {
            e.printStackTrace();
            try {
                clientChannel.close();
                clientChannel.socket().close();
            } catch (Exception ex) {
            }
        }
    }

    private void writeResponse(SocketChannel clientChannel, String message) throws IOException {
        responseString.setLength(0);

        if (message != null)
            responseString.append(message);

        ByteBuffer wrapBuffer = charset.encode(CharBuffer.wrap(responseString));
        clientChannel.write(wrapBuffer);
    }
}
