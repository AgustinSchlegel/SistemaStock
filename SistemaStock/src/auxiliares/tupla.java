package auxiliares;

public class tupla<T1, T2> {
    private final T1 item1;
    private final T2 item2;

    public tupla(T1 item1, T2 item2) {
        this.item1 = item1;
        this.item2 = item2;
    }

    public T1 getItem1() {
        return item1;
    }

    public T2 getItem2() {
        return item2;
    }

    @Override
    public String toString() {
        return "(" + item1 + ", " + item2 + ")";
    }
}
