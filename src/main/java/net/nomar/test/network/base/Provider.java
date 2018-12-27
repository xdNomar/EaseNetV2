package net.nomar.test.network.base;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public interface Provider {

    default Completable<AsynchronousSocketChannel> accept(AsynchronousServerSocketChannel server) {
        var completable = new Completable<AsynchronousSocketChannel>();

        server.accept(null, new CompletionHandler<>() {
            @Override
            public void completed(AsynchronousSocketChannel result, Object attachment) {
                completable.setCompleted(result);
                server.accept(attachment, this);
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                exc.printStackTrace();
            }
        });

        return completable;
    }

    default Completable<Void> connect(AsynchronousSocketChannel channel, String ip, int port) {
        var completable = new Completable<Void>();

        channel.connect(new InetSocketAddress(ip, port), null, new CompletionHandler<>() {
            @Override
            public void completed(Void result, Object attachment) {
                completable.setCompleted(result);
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                exc.printStackTrace();
            }
        });

        return completable;
    }
}
