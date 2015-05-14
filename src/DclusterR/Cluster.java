package DclusterR;


/*
 * 把Cluster或稱為Group，可視為一種sub graph，但不要直接繼承Graph
 */
public class Cluster<V, E, G extends Graph<V, E>> extends Graph<V, E> implements Comparable<Cluster>{
    private long groupId;
    private V starVertex;

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public void setStarVertex(V v) {
        if (v != null)
            starVertex = v;
    }

    public V getStarVertex() {
        return starVertex;
    }

    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("Content of this cluster "+ groupId+":\n");
        for (E e : getAllEdges()) {
            strBuilder.append(e.toString()+"\n");
        }
        strBuilder.trimToSize();
        return strBuilder.toString();
    }

	@Override
	public int compareTo(Cluster o) {
		Vertex v = (Vertex)this.starVertex;
		Vertex v2 = (Vertex)o.getStarVertex();
		if(v.neighbors.size() > v2.neighbors.size())
		{
			return 1;
		}
		else if (v.neighbors.size() < v2.neighbors.size())
		return -1;
		else
		return 0;
	}

}
