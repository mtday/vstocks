package vstocks.model;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public final class Symbol {
    private String exchangeId;
    private String id;
    private String symbol;
    private String name;

    public Symbol() {
    }

    private Symbol(Symbol symbol) {
        this.exchangeId = symbol.exchangeId;
        this.id = symbol.id;
        this.symbol = symbol.symbol;
        this.name = symbol.name;
    }

    public String getExchangeId() {
        return exchangeId;
    }

    public Symbol setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId;
        return this;
    }

    public String getId() {
        return id;
    }

    public Symbol setId(String id) {
        this.id = requireNonNull(id);
        return this;
    }

    public String getSymbol() {
        return symbol;
    }

    public Symbol setSymbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    public String getName() {
        return name;
    }

    public Symbol setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Symbol symbol = (Symbol) o;
        return Objects.equals(exchangeId, symbol.exchangeId) &&
                Objects.equals(id, symbol.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(exchangeId, id);
    }

    @Override
    public String toString() {
        return "Symbol{" +
                "exchangeId='" + exchangeId + '\'' +
                ", id='" + id + '\'' +
                ", symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
