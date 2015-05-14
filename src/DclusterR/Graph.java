package DclusterR;

import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/*
 * 看了別人的library，自己動手寫出來的，沒有版權問題
 */
public class Graph<V, E> {

    private Set<V> allVertices;
    private Set<E> allEdges;
    private Set<VertexPair<V>> pairSet;
    private Map<E, VertexPair<V>> graphMap; // for query everything
    //private VertexFactory<V> vertexFactory;
    //private Factory<V, E> edgeFactory;

    // 沒有vertex，graph依然可以是個單獨的object存在
    public Graph() {
        allVertices = new HashSet<V>();
        allEdges = new HashSet<E>();
        pairSet = new HashSet<VertexPair<V>>();
        graphMap = new Hashtable<E, VertexPair<V>>();
        //vertexFactory = new VertexFactory<V>();
        //edgeFactory = new EdgeFactory<V, E>();
    }

    public Set<E> getAllEdges() {
        return allEdges;
    }

    public Set<V> getAllVertices() {
        return allVertices;
    }

    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("Content of this graph:\n");
        for (Entry<E, VertexPair<V>> e : graphMap.entrySet()) {
            VertexPair<V> pair = e.getValue();
            E edge = e.getKey();
            strBuilder.append(pair.getFirst()+","+pair.getSecond()+"=>"+edge.toString() + "\n");
        }
        strBuilder.trimToSize();
        return strBuilder.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || !(object instanceof Graph<?, ?>))
            return false;
        Graph<?, ?> g = (Graph<?, ?>)object;
        return this.getAllEdges().equals(g.getAllEdges())
                && this.getAllVertices().equals(g.getAllVertices());
    }

    /*
     * vertices相關操作
     */
    public boolean addVertex(V v) {
        if (v != null)
            return allVertices.add(v);
        return false;
    }

    public boolean removeVertex(V v) {
        if (v == null || !allVertices.contains(v))
            return false;
        Set<E> edges = new HashSet<E>();
        for (Entry<E, VertexPair<V>> e : graphMap.entrySet()) {
            VertexPair<V> vp = e.getValue();
            if (vp.hasVertex(v)) {
                edges.add(e.getKey());
            }
        }
        Set<VertexPair<V>> pairs = new HashSet<VertexPair<V>>();
        for (VertexPair<V> pair : pairSet) {
            if (pair.hasVertex(v)) {
                pairs.add(pair);
            }
        }
        pairSet.removeAll(pairs);
        allEdges.removeAll(edges);
        for (E e : edges) {
            graphMap.remove(e);
        }
        return allVertices.remove(v);
    }

    public boolean removeAllVertices(Collection<? extends V> vertices) {
        if (vertices == null) {
            return false;
        } else {
            boolean result = true;
            for (V v : vertices) {
                result = removeVertex(v) && result;
            }
            return result;
        }
    }

    /*
     * Edges相關操作
     */
    /*public E addEdge(V source, V dest) {
        E e = edgeFactory.createEdge(source, dest);
        if (addEdge(source, dest, e))
            return e;
        return null;
    }*/

    public boolean addEdge(V source, V dest, E e) {
        if (source == null || dest == null || e == null)
            return false;
        boolean containsSource = allVertices.contains(source);
        boolean containsDest = allVertices.contains(dest);
        VertexPair<V> vp = null;
        if (!containsSource || !containsDest) {
            vp = new VertexPair<V>(source, dest);
            pairSet.add(vp);
        } else {
            for (VertexPair<V> pair : pairSet) {
                if (pair.hasVertex(source) && pair.hasVertex(dest)) {
                    vp = pair;
                    break;
                }
            }
            if (vp == null) {
                vp = new VertexPair<V>(source, dest);
                pairSet.add(vp);
            }
        }
        if (!containsSource)
            allVertices.add(source);
        if (!containsDest)
            allVertices.add(dest);
        graphMap.put(e, vp);
        return allEdges.add(e);
    }

    public boolean removeEdge(E e) {
        if (e == null || !allEdges.contains(e))
            return false;
        VertexPair<V> vp = graphMap.remove(e);
        if (!graphMap.containsValue(vp))
            pairSet.remove(vp);
        return allEdges.remove(e);
    }

    public boolean removeEdges(Collection<? extends E> edges) {
        if (edges == null) {
            return false;
        } else {
            boolean result = true;
            for (E e : edges) {
                result = removeEdge(e) && result;
            }
            return result;
        }
    }

    public boolean removeEdgesBetween(V source, V dest) {
        if (source == null || dest == null || !allVertices.contains(source)
                || !allVertices.contains(dest))
            return false;
        Set<E> edges = new HashSet<E>();
        for (Entry<E, VertexPair<V>> entry : graphMap.entrySet()) {
            VertexPair<V> value = entry.getValue();
            if (value.hasVertex(source) && value.hasVertex(dest)) {
                edges.add(entry.getKey());
                break;
            }
        }
        VertexPair<V> vp = null;
        for (VertexPair<V> pair : pairSet) {
            if (pair.hasVertex(source) && pair.hasVertex(dest)) {
                vp = pair;
                break;
            }
        }
        boolean result = true;
        for (E e : edges) {
            result = graphMap.remove(e) == vp && result;
        }
        return allEdges.removeAll(edges) && pairSet.remove(vp) && result;
    }

    public V getSourceOf(E edge) {
        VertexPair<V> pair = graphMap.get(edge);
        if (pair != null)
            return pair.getFirst();
        return null;
    }

    public V getDestOf(E edge) {
        VertexPair<V> pair = graphMap.get(edge);
        if (pair != null)
            return pair.getSecond();
        return null;
    }

    // 取得某個vertex的所有links，可以用於計算total degree
    public Set<E> getAllEdgesOf(V v) {
        Set<E> links = new HashSet<E>();
        for (Entry<E, VertexPair<V>> entry : graphMap.entrySet()) {
            if (entry.getValue().hasVertex(v)) {
                links.add(entry.getKey());
            }
        }
        return links;
    }

    // 這兩個應該是有向圖專屬的，不過目前沒有打算寫太複雜，偷吃步一下
    // out links
    public Set<E> getOutLinksOf(V v) {
        Set<E> links = new HashSet<E>();
        for (Entry<E, VertexPair<V>> entry : graphMap.entrySet()) {
            if (entry.getValue().getFirst().equals(v)) {
                links.add(entry.getKey());
            }
        }
        return links;
    }

    // in links
    public Set<E> getInLinksOf(V v) {
        Set<E> links = new HashSet<E>();
        for (Entry<E, VertexPair<V>> entry : graphMap.entrySet()) {
            if (entry.getValue().getSecond().equals(v)) {
                links.add(entry.getKey());
            }
        }
        return links;
    }

    //所有鄰居
    public Set<V> getAllNeighbors(V v) {
        Set<V> allNeighbors = new HashSet<V>();
        for (VertexPair<V> pair : pairSet) {
            if (pair.hasVertex(v)) {
                allNeighbors.add(pair.getOther(v));
            }
        }
        return allNeighbors;
    }

}
