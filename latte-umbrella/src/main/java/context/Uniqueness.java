package context;

public enum Uniqueness {
    BORROWED (4),
    SHARED (2),
    UNIQUE (3),
    // ALIAS (6),
    BOTTOM (1),
    FREE (5);


    private final int order;

    Uniqueness(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

    // Custom comparison method
    public boolean isLessEqualThan(Uniqueness other) {
        return this.order <= other.order;
    }
    // Custom comparison method
    public boolean isGreaterEqualThan(Uniqueness other) {
        return this.order >= other.order;
    }


}
