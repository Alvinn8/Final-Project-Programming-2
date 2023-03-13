package alvin.slutprojekt.server;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * Starts the TCP server and a new thread that waits for connections.
 */
public class ClientManager implements Runnable {
    public static final int DEFAULT_PORT = 4137;

    private final ServerSocket serverSocket;
    private final Thread thread;
    private final List<ClientConnection> clientConnections = new ArrayList<>();
    private final ServerMain main;

    public ClientManager(int port, ServerMain main) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.thread = new Thread(this, "ClientManager Thread");
        this.thread.start();
        this.main = main;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                // Wait for a connection
                Socket socket = this.serverSocket.accept();

                System.out.println("new connection: " + socket);

                // Create a new client
                this.clientConnections.add(new ClientConnection(socket, this.main, this));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void flushPackets() {
        for (ClientConnection clientConnection : this.clientConnections) {
            try {
                clientConnection.flush();
            } catch (IOException e) {
                if (!(e instanceof SocketException || e instanceof EOFException)) {
                    System.err.println("Failed to send packets (flush)");
                    e.printStackTrace();
                }
                clientConnection.disconnect(e.getMessage());
                // Break to avoid ConcurrentModificationException, we'll let the other
                // packets flush next tick instead
                break;
            }
        }
    }

    /**
     * Remove the specified connection.
     *
     * @param connection The connection to remove.
     */
    public void remove(ClientConnection connection) {
        this.clientConnections.remove(connection);
    }
}
