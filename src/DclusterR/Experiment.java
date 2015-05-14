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
    // �o�̭n���ɮ�Ū���i�ӨåB�s�@�X�@��Graph object �H�K���U�Ӫ����s����
    private boolean prepare(String... args) {
        try {
            // �إ�beta similarity graph
            betaSimilarityGraph = createBSimilarityGraph("D:/DataTemp/Processing/Concept/BetaSimilarityResult.txt");
            //System.out.println(betaSimilarityGraph);
            // max-S graph
            maxSGraph = createMaxSGraph(betaSimilarityGraph);
            //System.out.println(maxSGraph);
            // star cover���s
            starClusters = starCoverClustering(maxSGraph);
            //System.out.println(starClusters);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void mainProcess(String... args) {
        // TODO: star cover clusters�A�X��
    }

    // args�ѼơA�i�H��L�ݦ�string array�ϥΡAEx. args[0]�Bargs[1]�K�A�̦�����
    public void excute(String... args) throws IOException {
        // �T�wprepare�����~�~��
        if (prepare(args))
            mainProcess(args);
    }

    /** private method **/
    private Set<Cluster<Vertex, Edge, AbstractGraph<Vertex, Edge>>> starCoverClustering(
            Graph<Vertex, Edge> maxSGraph) throws IOException {
    	
        // 1.����X�֦��̤j����ת��`�I�A�ó]�Ӹ`�I�����A��Star
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

        // 2.��X�䥦���`�I�κA
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

        // 3.�M��U�����s�����s��
        Hashtable<String, String> conceptNumber = new Hashtable<String, String>();// �O�������s�s��
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

        // ��X
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
         * �o�o�̶}�l��g�Lbeta�z��L�᪺txt�ɧ@����J�A�ର�@��Graph object �ɮפ��e�i��p�U�G
         * 3001,3002:0.558884354 3002,3003:0.0 3003,3004:0.768899112 �M��@��@��Ū��
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
