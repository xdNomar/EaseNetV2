package net.nomar.test.network.base;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

public class Listenable<T extends Listenable> {

    public Consumer<ByteBuffer> readHandler;

    public T onRead(Consumer<ByteBuffer> readHandler) {
        this.readHandler = readHandler;
        return (T) this;
    }

}
