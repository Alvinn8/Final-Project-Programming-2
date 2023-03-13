package alvin.slutprojekt.networking;

public class PacketType<T extends Packet> {
    private final String id;
    private final ReadPacketFunction<T> reader;

    public PacketType(String id, ReadPacketFunction<T> reader) {
        this.id = id;
        this.reader = reader;
    }

    public String getId() {
        return this.id;
    }

    public ReadPacketFunction<T> getReader() {
        return reader;
    }
}
