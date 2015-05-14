package DclusterR;

public interface Factory<V, E> {
    public E createEdge(V source, V dest);
}
