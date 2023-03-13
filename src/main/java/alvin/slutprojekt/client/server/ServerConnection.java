package alvin.slutprojekt.client.server;

import alvin.slutprojekt.Debugging;
import alvin.slutprojekt.client.ClientMain;
import alvin.slutprojekt.client.screen.TextScreenWithBack;
import alvin.slutprojekt.networking.NetworkConnection;
import alvin.slutprojekt.networking.PacketType;
import alvin.slutprojekt.networking.ToClientPacket;
import alvin.slutprojekt.networking.ToServerPacket;
import alvin.slutprojekt.networking.packet.ToServerHelloPacket;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class ServerConnection extends NetworkConnection implements Runnable {
    private final ClientMain clientMain;
    private final ClientSidePacketHandler handler;

    public ServerConnection(Socket socket, ClientMain clientMain, RemoteEnvironment environment) throws IOException {
        super(socket, clientMain, "ServerConnection");
        this.clientMain = clientMain;
        this.handler = new ClientSidePacketHandler(this, clientMain, environment);
        this.thread.start();
    }

    @Override
    public void run() {
        while (!this.socket.isClosed() && !Thread.interrupted()) {
            try {
                String packetId = this.in.readUTF();
                if (Debugging.DEBUGGING_PACKETS) {
                    System.out.println("got " + packetId + " (thread: " + Thread.currentThread().getName() + ")");
                }
                PacketType<ToClientPacket> packetType = this.main.getToClientPackets().get(packetId);
                if (packetType != null) {
                    ToClientPacket packet = packetType.getReader().read(this.in);
                    packet.handle(this.handler);
                } else {
                    System.err.println("Got invalid packet id from server: " + packetId);
                    this.disconnect("Unknown packet id: " + packetId);
                }

            } catch (EOFException | SocketException e) {
                System.err.println("Disconnected: " + e.getMessage());
                this.disconnect(e.getMessage());
                break;
            } catch (IOException e) {
                System.err.println("Got invalid packet from server.");
                e.printStackTrace();
                this.disconnect("Got invalid packet from the server.");
            }
        }
    }

    /**
     * Send a packet to the server.
     *
     * @param packet The packet to send.
     */
    public synchronized void sendPacket(ToServerPacket packet) {
        String packetId = this.main.getToServerPackets().getIdFor(packet.getType());
        this.sendPacket(packetId, packet);

        // The client does not send packets that often, so let's flush directly
        try {
            this.out.flush();
        } catch (IOException e) {
            System.err.println("Failed to send packet: " + packet);
            e.printStackTrace();
        }
    }

    public void disconnect(String message) {
        System.out.println("Disconnecting");
        this.clientMain.getGameWindow().setScreen(new TextScreenWithBack(this.clientMain, "Disconnected: " + message));
    }
}
