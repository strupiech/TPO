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

    private static Charset REQ_RES_CHARSET = StandardCharsets.UTF_8;

    private ServerSocketChannel serverChannel;
    private Selector selector;
    private boolean isRunning;
    private ServerLog serverLog;
    private String host;
    private int port;

    public Server(String host, int port) {
        this.isRunning = true;
        this.serverLog = new ServerLog();
        this.host = host;
        this.port = port;
    }

    public void startServer() {
        try {
            serverChannel = ServerSocketChannel.open();
            serverChannel.bind(new InetSocketAddress(host, port));
            serverChannel.configureBlocking(false);

            selector = Selector.open();
        } catch (IOException e) {
            printStackTraceAndSystemShutDown(e);
        }

        try {
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (ClosedChannelException e) {
            printStackTraceAndSystemShutDown(e);
        }
        start();
    }

    private void printStackTraceAndSystemShutDown(IOException e) {
        e.printStackTrace();
        System.exit(1);
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
                    serverLog.clearServerLogIfMoreThan500Records();
                }
            } catch (ClosedSelectorException ignored) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleRequest(SocketChannel clientChannel) {
        if (!clientChannel.isOpen()) return;

        StringBuilder requestStringBuilder = new StringBuilder();
        ByteBuffer inputBuffer = ByteBuffer.allocate(1024);

        requestStringBuilder.setLength(0);
        inputBuffer.clear();

        try {
            readingLoop:
            while (true) {
                int bytes = clientChannel.read(inputBuffer);
                if (bytes > 0) {
                    inputBuffer.flip();
                    CharBuffer charBuffer = REQ_RES_CHARSET.decode(inputBuffer);
                    while (charBuffer.hasRemaining()) {
                        char c = charBuffer.get();
                        if (c == '\r' || c == '\n') break readingLoop;
                        requestStringBuilder.append(c);
                    }
                }
            }

            String clientRequest = requestStringBuilder.toString();
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
                serverLog.clearClientLog(clientPort);

                clientChannel.close();
                clientChannel.socket().close();

            } else if (clientRequest.equals("bye and log transfer")) {

                serverLog.addToServerLog(clientPort, "logged out at " + serverLog.getTime());
                serverLog.addToClientLog(clientPort, "logged out");

                writeResponse(clientChannel, serverLog.get(clientPort));
                serverLog.clearClientLog(clientPort);

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
            clearClientLogAndCloseConnection(clientChannel);
        }
    }

    private void clearClientLogAndCloseConnection(SocketChannel clientChannel) {
        try {
            serverLog.clearClientLog(clientChannel.socket().getPort());

            clientChannel.close();
            clientChannel.socket().close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void writeResponse(SocketChannel clientChannel, String message) throws IOException {
        StringBuilder responseStringBuilder = new StringBuilder();

        responseStringBuilder.setLength(0);

        if (message != null)
            responseStringBuilder.append(message);

        ByteBuffer wrapBuffer = REQ_RES_CHARSET.encode(CharBuffer.wrap(responseStringBuilder));
        clientChannel.write(wrapBuffer);
    }
}
