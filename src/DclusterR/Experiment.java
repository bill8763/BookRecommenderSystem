package DclusterR;

import com.frieda.graduation.graph.abstractbase.AbstractGraph;
import com.frieda.graduation.graph.ibase.EdgeFactory;
import com.frieda.graduation.graph.ibase.Graph;
import com.frieda.graduation.impl.Cluster;
import com.frieda.graduation.impl.UndirectedGraphImpl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

public class Experiment {

    private Graph<Vertex, Edge> betaSimilarityGraph;
    private Graph<Vertex, Edge> maxSGraph;

    // star cover clusters
    private Set<Cluster<Vertex, Edge, AbstractGraph<Vertex, Edge>>> starClusters;

    public static void main(String args[]) throws IOException {
        Experiment e = new Experiment();
        e.excute();
    }

    /** public method **/
    // 這裡要把檔案讀取進來並且製作出一個Graph object 以便接下來的分群應用
    private boolean prepare(String... args) {
        try {
            // 建立beta similarity graph
            betaSimilarityGraph = createBSimilarityGraph("D:/DataTemp/Processing/Concept/BetaSimilarityResult.txt");
            //System.out.println(betaSimilarityGraph);
            // max-S graph
            maxSGraph = createMaxSGraph(betaSimilarityGraph);
            //System.out.println(maxSGraph);
            // star cover分群
            starClusters = starCoverClustering(maxSGraph);
            //System.out.println(starClusters);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void mainProcess(String... args) {
        // TODO: star cover clusters再合併
    }

    // args參數，可以把他看成string array使用，Ex. args[0]、args[1]…，依此類推
    public void excute(String... args) throws IOException {
        // 確定prepare完成才繼續
        if (prepare(args))
            mainProcess(args);
    }

    /** private method **/
    private Set<Cluster<Vertex, Edge, AbstractGraph<Vertex, Edge>>> starCoverClustering(
            Graph<Vertex, Edge> maxSGraph) throws IOException {
    	
        // 1.先找出擁有最大分支度的節點，並設該節點的型態為Star
        Set<Vertex> all = maxSGraph.getAllVertices();
        int maxDegree = 0;
        Vertex starVertex = null;
        for (Vertex v : all) {
            Set<Edge> links = maxSGraph.getAllEdgesOf(v);
            if (links.size() > maxDegree) {
                maxDegree = links.size();
                starVertex = v;
            }
        }
        starVertex.setType(Vertex.TYPE_STAR);

        // 2.找出其它的節點形態
        for (Vertex v : all) {
            boolean b = false;
            if (v.getType() == Vertex.TYPE_SATELLITE) {
                Set<Edge> links = maxSGraph.getAllEdgesOf(v);
                int degreeOfV = links.size();
                for (Edge e : links) {
                    Vertex neighbor = maxSGraph.getSourceOf(e).equals(v) ? maxSGraph.getDestOf(e)
                            : maxSGraph.getSourceOf(e);
                    if (!(neighbor.getType() == Vertex.TYPE_STAR)) {
                        int degreeOfNeighbor = maxSGraph.getAllEdgesOf(neighbor).size();
                        if (degreeOfV >= degreeOfNeighbor)
                            b = true;
                    }
                    if (v.equals(neighbor))
                        b = true;
                }
            }
            if (b)
                v.setType(Vertex.TYPE_STAR);
        }

        // 3.尋找各概念群集的編號
        Hashtable<String, String> conceptNumber = new Hashtable<String, String>();// 記錄概念群編號
        File fr2 = new File("D:\\StarCoverGraph.txt");
        long conceptMax = 0;
        if (fr2.exists()) {
            FileReader FileStream2 = new FileReader(fr2);
            BufferedReader BufferedStream2 = new BufferedReader(FileStream2);
            String line2 = "";
            ArrayList<String> list2 = new ArrayList<String>();

            while ((line2 = BufferedStream2.readLine()) != null)
                list2.add(line2);

            for (String str : list2) {
                conceptNumber.put(str.split(":")[1].split(",")[0], str.split(":")[0]);
                if (Integer.parseInt(str.split(":")[0]) > conceptMax)
                    conceptMax = Long.parseLong(str.split(":")[0]);
            }

            BufferedStream2.close();
        }

        // 輸出
        BufferedWriter bw = new BufferedWriter(new FileWriter("D:\\StarCoverGraph.txt"));
        Set<Cluster<Vertex, Edge, AbstractGraph<Vertex, Edge>>> clusters = new HashSet<Cluster<Vertex, Edge, AbstractGraph<Vertex, Edge>>>();
        for (Vertex v : all) {
            if (v.getType() == Vertex.TYPE_STAR) {
                Cluster<Vertex, Edge, AbstractGraph<Vertex, Edge>> c = new Cluster<Vertex, Edge, AbstractGraph<Vertex, Edge>>(Edge.class);
                if (conceptNumber.containsKey(v.getValue())) {
                    c.setGroupId(Long.parseLong(conceptNumber.get(v.getValue())));
                } else {
                    c.setGroupId(++conceptMax);
                }
                Set<Edge> links = maxSGraph.getAllEdgesOf(v);
                for (Edge e : links) {
                    c.addEdge(maxSGraph.getSourceOf(e), maxSGraph.getDestOf(e), e);
                }
                c.setStarVertex(v);
                StringBuilder output = new StringBuilder();
                output.append(c.getGroupId() + ":" + v.getValue());
                for (Edge e : links) {
                    Vertex neighbor = maxSGraph.getSourceOf(e).equals(v) ? maxSGraph.getDestOf(e)
                            : maxSGraph.getSourceOf(e);
                    output.append("," + neighbor.getValue());
                }
                clusters.add(c);
                try {
                    output.trimToSize();
                    bw.write(output.toString());
                    bw.newLine();
                } catch (IOException f) {
                    f.printStackTrace();
                }
            }
        }
        bw.flush();
        bw.close();
        return clusters;
    }

    private Graph<Vertex, Edge> createBSimilarityGraph(String path) throws IOException {
        /*
         * 這這裡開始把經過beta篩選過後的txt檔作為輸入，轉為一個Graph object 檔案內容可能如下：
         * 3001,3002:0.558884354 3002,3003:0.0 3003,3004:0.768899112 然後一行一行讀取
         */
        File fr1 = new File(path);
        FileReader FileStream1 = new FileReader(fr1);
        BufferedReader BufferedStream1 = new BufferedReader(FileStream1);

        AbstractGraph<Vertex, Edge> betaSimilarityGraph = new UndirectedGraphImpl<Vertex, Edge>(Edge.class);
        EdgeFactory<Vertex, Edge> edgeFactory = betaSimilarityGraph.getEdgeFactory();
        String line;
        while ((line = BufferedStream1.readLine()) != null) {
            String[] info = line.split("[,|:]");
            String value1 = info[0];
            String value2 = info[1];
            double simValue = Double.valueOf(info[2]);
            if (simValue <= 0.0)
                continue;
            Vertex v1 = getVertex(value1, betaSimilarityGraph);
            Vertex v2 = getVertex(value2, betaSimilarityGraph);
            Edge e = edgeFactory.createEdge(v1, v2);
            e.setSimValue(simValue);
            betaSimilarityGraph.addEdge(v1, v2, e);
            
        }
        BufferedStream1.close();
        return betaSimilarityGraph;
    }

    private Vertex getVertex(String str, AbstractGraph<Vertex, Edge> betaSimilarityGraph) {
        for (Vertex v : betaSimilarityGraph.getAllVertices()) {
            if (v.getValue().equals(str)) {
                return v;
            }
        }
        Vertex v = new Vertex();
        v.setValue(str);
        return v;
    }

    private Graph<Vertex, Edge> createMaxSGraph(Graph<Vertex, Edge> graph) {
        Set<Vertex> N = graph.getAllVertices();
        Set<Edge> maxSimEdges = new HashSet<Edge>();
        for (Vertex v : N) {
            Set<Edge> links = graph.getAllEdgesOf(v);
            double maxSim = 0.0;
            Edge maxSimEdge = null;
            for (Edge e : links) {
                if (e.getSimValue() > maxSim) {
                    maxSim = e.getSimValue();
                    maxSimEdge = e;
                }
            }
            maxSimEdges.add(maxSimEdge);
        }

        AbstractGraph<Vertex, Edge> maxSGraph = new UndirectedGraphImpl<Vertex, Edge>(Edge.class);
        for (Edge e : maxSimEdges) {
            maxSGraph.addEdge(graph.getSourceOf(e), graph.getDestOf(e), e);
        }
        return maxSGraph;
    }
}
