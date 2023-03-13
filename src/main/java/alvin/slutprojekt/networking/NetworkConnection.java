package alvin.slutprojekt.networking;

import alvin.slutprojekt.AbstractMain;
import alvin.slutprojekt.Debugging;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

/**
 * A connection between the server and the client.
 *
 * @see alvin.slutprojekt.client.server.ServerConnection
 * @see alvin.slutprojekt.server.ClientConnection
 */
public abstract class NetworkConnection implements Runnable {
    protected final AbstractMain main;
    protected final Thread thread;
    protected final Socket socket;
    protected final DataInputStream in;
    protected final DataOutputStream out;

    public NetworkConnection(Socket socket, AbstractMain main, String name) throws IOException {
        this.socket = socket;
        this.main = main;
        this.in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        this.thread = new Thread(this, name + " Thread " + (int) (Math.random() * 100));
    }

    /**
     * Send a packet to the other end of the connection.
     * <p>
     * If the packetId is null an exception will be printed due to attempting to send
     * an unregistered packet.
     *
     * @param packetId The id of the packet to send, or null.
     * @param packet The packet to send.
     */
    protected synchronized void sendPacket(String packetId, Packet packet) {
        if (Debugging.DEBUGGING_PACKETS) {
            System.out.println("server --> client sending " + packetId + " (from " + Thread.currentThread().getName() +")");
        }
        try {
            if (packetId != null) {
                this.out.writeUTF(packetId);
                packet.write(this.out);
            } else {
                throw new IOException("Tried to send unregistered packet " + packet);
            }
        } catch(EOFException | SocketException e) {
            System.err.println("Tried to send packet to disconnected user.");
        } catch (IOException e) {
            System.err.println("Failed to send packet: " + packet);
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
