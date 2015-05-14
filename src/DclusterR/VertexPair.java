package DclusterR;

public class VertexPair<V> {

    private V vertexFirst;
    private V vertexSecond;

    public VertexPair(V v1, V v2) {
        if (v1 == null || v2 == null)
            throw new IllegalArgumentException("v1 or v2 can't be null value");
        vertexFirst = v1;
        vertexSecond = v2;
    }

    public V getFirst() {
        return vertexFirst;
    }

    public V getSecond() {
        return vertexSecond;
    }

    public V getOther(V v) {
        if (vertexFirst.equals(v)) {
            return vertexSecond;
        } else if (vertexSecond.equals(v)) {
            return vertexFirst;
        }
        return null;
    }

    public boolean hasVertex(V v) {
        if (vertexFirst.equals(v) || vertexSecond.equals(v)) {
            return true;
        } else {
            return false;
        }
    }
}
