package net.nomar.test.network.base;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Deque;

public interface Channel {

    default Completable<ByteBuffer> readOnce(AsynchronousSocketChannel channel) {
        var completable = new Completable<ByteBuffer>();
        var buffer = ByteBuffer.allocate(200);

        channel.read(buffer, null, new CompletionHandler<>() {
            @Override
            public void completed(Integer result, Object attachment) {
                completable.setCompleted(buffer.slice());
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                exc.printStackTrace();
            }
        });

        return completable;
    }

    default Completable<ByteBuffer> readContinously(AsynchronousSocketChannel channel) {
        var completable = new Completable<ByteBuffer>();
        var buffer = ByteBuffer.allocate(200);

        channel.read(buffer, null, new CompletionHandler<>() {
            @Override
            public void completed(Integer result, Object attachment) {
                completable.setCompleted(buffer.slice());
                channel.read(buffer, null, this);
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                exc.printStackTrace();
            }
        });

        return completable;
    }

    default void writeToClients(ByteBuffer buffer, Deque<AsynchronousSocketChannel> channels) {
        for (AsynchronousSocketChannel channel : channels) {
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
    }
}
