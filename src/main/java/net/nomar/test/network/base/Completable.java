package net.nomar.test.network.base;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class Completable<T> {

    private final AtomicBoolean completed = new AtomicBoolean();
    private T result;

    public void onComplete(Consumer<T> consumer) {
        while (completed.get()) {
            consumer.accept(result);
            break;
        }
    }

    public void setCompleted(T result) {
        this.result = result;
        completed.set(true);
    }
}
