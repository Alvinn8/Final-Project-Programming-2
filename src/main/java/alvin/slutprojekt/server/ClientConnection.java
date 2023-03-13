package alvin.slutprojekt.server;

import alvin.slutprojekt.Debugging;
import alvin.slutprojekt.networking.NetworkConnection;
import alvin.slutprojekt.networking.PacketType;
import alvin.slutprojekt.networking.ToClientPacket;
import alvin.slutprojekt.networking.ToServerPacket;
import alvin.slutprojekt.networking.packet.ToClientDisconnectPacket;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

/**
 * A connection from the server to a client.
 */
public class ClientConnection extends NetworkConnection implements Runnable {
    private final ServerSidePacketHandler handler;
    private final ClientManager clientManager;

    public ClientConnection(Socket socket, ServerMain main, ClientManager clientManager) throws IOException {
        super(socket, main, "ClientConnection");
        this.handler = new ServerSidePacketHandler(this, main);
        this.clientManager = clientManager;
        this.thread.start();
    }

    @Override
    public void run() {
        while (!this.socket.isClosed() && !Thread.interrupted()) {
            try {
                String packetId = this.in.readUTF();
                if (Debugging.DEBUGGING_PACKETS) {
                    System.out.println("got " + packetId);
                }
                PacketType<ToServerPacket> packetType = this.main.getToServerPackets().get(packetId);
                if (packetType != null) {
                    ToServerPacket packet = packetType.getReader().read(this.in);
                    packet.handle(this.handler);
                } else {
                    this.disconnect("Unknown packet id: " + packetId);
                }

            } catch (EOFException | SocketException e) {
                this.disconnect(e.getMessage());
                break;
            } catch (IOException e) {
                e.printStackTrace();
                this.disconnect("Invalid packet.");
            }
        }
    }

    /**
     * Send a packet to the client.
     *
     * @param packet The packet to send.
     */
    public void sendPacket(ToClientPacket packet) {
        String packetId = this.main.getToClientPackets().getIdFor(packet.getType());
        this.sendPacket(packetId, packet);
    }

    /**
     * Disconnect this player and close the connection.
     *
     * @param message The message to send to the client, the reason for the disconnect.
     */
    public void disconnect(String message) {
        System.out.println("Disconnecting a user: " + message);
        this.handler.disconnected();
        if (!this.socket.isClosed()) {
            ToClientDisconnectPacket packet = new ToClientDisconnectPacket(message);
            this.sendPacket(packet);
            try {
                this.socket.close();
            } catch (IOException e) {
                System.err.println("Failed to close client connection: " + e.getMessage());
            }
        }
        this.clientManager.remove(this);
    }

    public ServerSidePacketHandler getHandler() {
        return this.handler;
    }

    public void setPlayerName(String name) {
        this.thread.setName(this.thread.getName() + " ("+ name +")");
    }

    public void flush() throws IOException {
        this.out.flush();
    }

    public boolean isConnected() {
        return !this.socket.isClosed();
    }
}
