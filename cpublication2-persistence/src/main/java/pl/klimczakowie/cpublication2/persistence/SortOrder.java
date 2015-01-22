package pl.klimczakowie.cpublication2.persistence;

public class SortOrder {
    private String column;
    private SortOrderType type;

    public enum SortOrderType {
        ASC, DESC
    }

    public SortOrder(String column, SortOrderType type) {
        this.column = column;
        this.type = type;

    }

    public String getColumn() {
        return column;
    }

    public SortOrderType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "SortOrder [column=" + column + ", type=" + type + "]";
    }
}
