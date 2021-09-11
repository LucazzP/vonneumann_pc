package vonneumann;

public class Tagged<T> {
    final int tag;
    boolean modified;
    final T data;

    public Tagged(int tag, T data) {
        this.tag = tag;
        this.modified = false;
        this.data = data;
    }

    @Override
    public String toString() {
        return "Tagged{" +
                "tag=" + tag +
                ", modified=" + modified +
                ", data=" + data +
                '}';
    }
}
