package DclusterR;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.frieda.graduation.graph.abstractbase.AbstractGraph;
import com.frieda.graduation.graph.ibase.EdgeFactory;
import com.frieda.graduation.impl.UndirectedGraphImpl;

/*
 * Content defined by user
 */
public class Vertex {
    // 1.設計物件時，不要讓人直接存取物料property，儘量設為private屬性，利用object method存取
    // 即便物件再簡單也要保持這個原則
    // 2.用getter & setter來進行物件的存取
    public static final int TYPE_SATELLITE = 0;
    public static final int TYPE_STAR = 1;

    private String value;
    private int type;
    private double relevance;
    public Set<Vertex> neighbors; 
    public Vertex() {
    	neighbors = new HashSet<Vertex>();
    }
    
    public void addNeighbor(Vertex neighbor){
    	neighbors.add(neighbor);               
    }

    public void setRelevance(double relevance){
    	this.relevance = relevance;
    }
    
    public double getRelevance(){
    	return relevance;
    }
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        if (type < TYPE_SATELLITE || type > TYPE_STAR)
            throw new IllegalArgumentException("Must be TYPE_SATE or TYPE_STAR");
        this.type = type;
    }

    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(value.toString() + "," + (type == TYPE_STAR ? "star" : "sate"));
        return strBuilder.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || !(object instanceof Vertex))
            return false;
        Vertex v = (Vertex)object;
        return value.equalsIgnoreCase(v.getValue());
    }

}
