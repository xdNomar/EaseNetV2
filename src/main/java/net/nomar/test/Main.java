package net.nomar.test;

import net.nomar.test.network.Client;
import net.nomar.test.network.Server;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        var server = Server.open()
                .onRead(buffer -> {
                    System.out.println("Server: " + Arrays.toString(buffer.array()));
                }).start(123);

        var client = Client.open()
                .bind("localhost", 124)
                .onRead(buffer -> {
                    System.out.println("Client: " + Arrays.toString(buffer.array()));

                }).connect("localhost", 123);

        server.writeToClients(ByteBuffer.allocate(100).put((byte) 1).slice(), server.getConnectedChannels());
    }
}
