package DclusterR;

import com.frieda.graduation.graph.abstractbase.AbstractGraph;
import com.frieda.graduation.graph.ibase.EdgeFactory;
import com.frieda.graduation.impl.UndirectedGraphImpl;

/*
 * Content defined by user
 */
public class Edge {

    private double simValue;

    public Edge() {
        this(0.0);
    }

    public Edge(double v) {
        this.simValue = v;
    }

    public double getSimValue() {
        return simValue;
    }

    public void setSimValue(double simValue) {
    	/*AbstractGraph<Vertex, Edge> betaSimilarityGraph = new UndirectedGraphImpl<Vertex, Edge>(Edge.class);
        EdgeFactory<Vertex, Edge> edgeFactory = betaSimilarityGraph.getEdgeFactory();
        String line = null;
            String[] info = line.split("[,|:]");
            String value1 = info[0];
            String value2 = info[1];
            double simvalue = Double.valueOf(info[2]);*/
        this.simValue = simValue;
    }

    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("simValue="+simValue);
        return strBuilder.toString();
    }

    // TODO: 未來實作-有向edge、無向edge
    public static final int TYPE_DIRECTION = 0;
    public static final int TYPE_INDIRECTION = 1;
    private int type;

    public int getType() {
        return type;
    }
}
