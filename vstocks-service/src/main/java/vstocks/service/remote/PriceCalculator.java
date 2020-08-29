package vstocks.service.remote;

public interface PriceCalculator<T> {
    long getPrice(T t);
}
