package net.nomar.test.network;

import net.nomar.test.network.base.Channel;
import net.nomar.test.network.base.Listenable;
import net.nomar.test.network.base.Provider;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client extends Listenable<Client> implements Provider, Channel {

    private static AsynchronousSocketChannel channel;
    private final ExecutorService service = Executors.newSingleThreadExecutor();

    private Client() {
    }

    public static Client open() {
        try {
            channel = AsynchronousSocketChannel.open();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new Client();
    }

    public Client bind(String ip, int port) {
        try {
            channel.bind(new InetSocketAddress(ip, port));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return this;
    }

    public Client connect(String ip, int port) {
        connect(channel, ip, port).onComplete(obj -> {
            System.out.println("Connecting");

            readContinously(channel).onComplete(buffer -> {
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

    public Client close() {
        try {
            channel.close();
            service.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return this;
    }

    public void writeToServer(ByteBuffer buffer) {
        channel.write(buffer.slice(), null, new CompletionHandler<>() {
            @Override
            public void completed(Integer result, Object attachment) {

            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                exc.printStackTrace();
            }
        });
    }

    public AsynchronousSocketChannel getChannel() {
        return channel;
    }
}
