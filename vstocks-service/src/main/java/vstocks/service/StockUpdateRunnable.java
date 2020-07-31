package vstocks.service;

import vstocks.model.Stock;

import java.io.Closeable;
import java.io.IOException;
import java.util.function.Consumer;

public interface StockUpdateRunnable extends Runnable, Closeable, Consumer<Stock> {
    @Override
    void accept(Stock stock);

    @Override
    void run();

    @Override
    void close() throws IOException;
}
