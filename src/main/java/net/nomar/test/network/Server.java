package net.nomar.test.network;

import net.nomar.test.network.base.Channel;
import net.nomar.test.network.base.Listenable;
import net.nomar.test.network.base.Provider;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends Listenable<Server> implements Provider, Channel {

    private final Deque<AsynchronousSocketChannel> clients = new ConcurrentLinkedDeque<>();
    private static AsynchronousServerSocketChannel server;
    private final ExecutorService service = Executors.newSingleThreadExecutor();

    private Server() {
    }

    public static Server open() {
        try {
            server = AsynchronousServerSocketChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Server();
    }

    public Server start(int port) {
        try {
            server.bind(new InetSocketAddress(port));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        accept(server).onComplete(result -> {
            System.out.println("Accepted");
            clients.add(result);

            readContinously(result).onComplete(buffer -> {
                if (readHandler != null)
                    readHandler.accept(buffer);
            });
        });

        service.submit(() -> {
            try {
                System.in.read();
            } catch (IOException ignored) {
            }
        });

        return this;
    }

    public Server close() {
        try {
            server.close();
            service.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return this;
    }

    public AsynchronousServerSocketChannel getChannel() {
        return server;
    }

    public Deque<AsynchronousSocketChannel> getConnectedChannels() {
        return clients;
    }
}
