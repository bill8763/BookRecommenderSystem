package DclusterR;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.frieda.graduation.graph.abstractbase.AbstractGraph;
import com.frieda.graduation.impl.Cluster;

public class DClustR {
	private Graph<Vertex, Edge> betaSimilarityGraph;
	
	//�ǥѭp��`�I�ۦ��� ���R���Ǹ`�I�i�Q�[�J�Կ�D�Plist
	private SortedSet<Cluster<Vertex, Edge, AbstractGraph<Vertex, Edge>>> initialClusters;
	private SortedSet<Cluster<Vertex, Edge, AbstractGraph<Vertex, Edge>>> updatedClusters;
	
	public static void main(String args[]) throws IOException {
        //Experiment e = new Experiment();
        //e.excute();
        
        DClustR d = new DClustR();
        d.calculatingRelClustering(d.createBSimilarityGraph("D:\\DataTemp\\Processing\\Concept\\MaxSimilarityMatrix.txt"));
    }
	
	private boolean prepare(String... args) {
        try {
            // �إ�beta similarity graph
            betaSimilarityGraph = createBSimilarityGraph("D:\\DataTemp\\Processing\\Concept\\MaxSimilarityMatrix.txt");
            System.out.println(betaSimilarityGraph);
            // ��l�s��
            initialClusters = calculatingRelClustering(betaSimilarityGraph);
            System.out.println(initialClusters);
            //��s�s��
            updatedClusters = chooseVertex(initialClusters);
            System.out.println(updatedClusters);
            
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
	
	//�p��`�I�ۦ��� �Φ���l�s��
	private SortedSet<Cluster<Vertex, Edge, AbstractGraph<Vertex, Edge>>> calculatingRelClustering(Graph<Vertex, Edge> betaSimilarityGraph)
            throws IOException{
		Set<Vertex> all = betaSimilarityGraph.getAllVertices();             //�Ϥ��Ҧ��`�I
		double densityR;                               //�i�[�J���ìP���                       
		double compactnessR;                           //�[�J�ìP���ۦ��פ��
		double intraSim;                              //�l�Ϥ����ۦ���
		double NeighborintraSim;                      //�F�~�l�Ϥ����ۦ���
		double sumOfVEdge = 0;
		
	/*	for(Vertex v: all){
			System.out.println("size:"+v.neighbors.size());
		}*/
		
		for (Vertex v : all){
			Set<Edge> links = betaSimilarityGraph.getAllEdgesOf(v);    //�I�F�~��
	        double degreeOfV = links.size();          //v�`�I�����
	        double valueOfEdge ;
	        for (Edge e : links) {
                Vertex neighbor = betaSimilarityGraph.getSourceOf(e).equals(v) ? betaSimilarityGraph.getDestOf(e)
                        : betaSimilarityGraph.getSourceOf(e);
                //degreeOfNeighbor = betaSimilarityGraph.getAllEdgesOf(neighbor).size();  //�I�F�~�����
                valueOfEdge = e.getSimValue();                        //���
                sumOfVEdge += valueOfEdge;	
                System.out.println("test"+valueOfEdge);
                //	System.out.println("V.densityR" + density);       
	        }
	        double density = 0;                 //�p��v���F�~�`�I�`��
	        for (Vertex neighbor :v.neighbors){
	        	
                int degreeOfNeighbor = betaSimilarityGraph.getAllEdgesOf(neighbor).size();
                
	        	
	        	if(degreeOfNeighbor < degreeOfV){
	        		density++;
	        		}
	        	
	        }
	        	densityR = density / degreeOfV;                  //�i�[�J���ìP���;�p��v.densityR
	        	System.out.println("density" + densityR);
	        
	        
	        double compactness = 0 ;
	        intraSim = sumOfVEdge / degreeOfV;            //�Pv�۳s���ϫ������ۦ���
	        for(Vertex neighbor:v.neighbors){
	        	Set<Edge> Neighborlink = betaSimilarityGraph.getAllEdgesOf(neighbor);  //v�I���F�~����
                
	        	double valueOfNeighborEdge= 0;
                double sumOfNeighborEdge =0;
                double degreeOfNeighbor = betaSimilarityGraph.getAllEdgesOf(neighbor).size();
                
                for(Edge e2 : Neighborlink){	
	        		valueOfNeighborEdge = e2.getSimValue();                        //���
                	sumOfNeighborEdge += valueOfNeighborEdge;       
                }
                NeighborintraSim = sumOfNeighborEdge / degreeOfNeighbor;  //�Pv�۳s���F�~�ϫ������ۦ���
	        	if(NeighborintraSim < intraSim)
	        		compactness ++ ; 	
	        	
	        	
	        }
	        compactnessR = compactness / degreeOfV;
	        v.setRelevance((densityR + compactnessR) / 2);
	        v.setType(Vertex.TYPE_SATELLITE);
	        System.out.println("relevance: " + v.getRelevance());
		}
		ArrayList<Vertex> allSorted = new ArrayList<Vertex>();
		for(Vertex v : all){
			if(v.getRelevance() > 0)
				allSorted.add(v);
		}
		Collections.sort(allSorted, new Comparator<Vertex>(){
			@Override
			public int compare(Vertex v1, Vertex v2) {	
				double delta = v2.getRelevance() - v1.getRelevance();
				if(delta>0)
					return 1;
				else if (delta < 0)
					return -1;
				else
					return 0;
			}
		});
		System.out.println(allSorted.size());
		for(Vertex v:allSorted){
			
			
			System.out.println("relevance:"+v.getRelevance());
			//satVertex.add(v);                          //���N�Ƨǧ����I�s�i�ìP�C��
			for(Vertex neighbor:v.neighbors){
				if(neighbor.getType()==Vertex.TYPE_SATELLITE)
					v.setType(Vertex.TYPE_STAR);				
				}
		}
		for(Vertex v : allSorted){
			Boolean allStar = true;
			if(v.getType() == Vertex.TYPE_STAR){
				for(Vertex neighbor : v.neighbors){
					if(neighbor.getType()==Vertex.TYPE_SATELLITE){
						allStar =false;
						break;
					}
				}
			if(allStar==true)
				v.setType(Vertex.TYPE_SATELLITE);
			
			}
		}                                         //��for�i���լݬ�!
			
		
		
		
		
		
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
	        initialClusters = new TreeSet<Cluster<Vertex, Edge, AbstractGraph<Vertex, Edge>>>();	        	
	        
	        for (Vertex v : all) {
	            if (v.getType() == Vertex.TYPE_STAR) {
	                Cluster<Vertex, Edge, AbstractGraph<Vertex, Edge>> c = new Cluster<Vertex, Edge, AbstractGraph<Vertex, Edge>>(Edge.class);
	                if (conceptNumber.containsKey(v.getValue())) {
	                    c.setGroupId(Long.parseLong(conceptNumber.get(v.getValue())));
	                } else {
	                    c.setGroupId(++conceptMax);
	                }
	               
	                c.setStarVertex(v);
	                StringBuilder output = new StringBuilder();
	                output.append(c.getGroupId() + ":" + v.getValue());
	                for(Vertex neighbor : v.neighbors){
	                output.append("," + neighbor.getValue());
	                }
	                initialClusters.add(c);
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
        
        
		

		
		
	//	return new HashSet<Cluster<Vertex, Edge, Graph<Vertex, Edge>>>();  
		return initialClusters;
		
	}
	
	private SortedSet<Cluster<Vertex, Edge, AbstractGraph<Vertex, Edge>>> chooseVertex (SortedSet<Cluster<Vertex, Edge, AbstractGraph<Vertex, Edge>>> cluster)
            throws IOException{
		 for(Cluster c:cluster){
			 Vertex starVertex = (Vertex)c.getStarVertex();      //���o�D�P
			 int degree = starVertex.neighbors.size();           //�D�P�����
			 int overlapSat;
			 //�i��n�[sort
			 for(Cluster d : cluster){
				 Vertex star2 = (Vertex)d.getStarVertex();
				 for(Vertex v:starVertex.neighbors){
					 for(Vertex v2:star2.neighbors){
						 if(v.equals(v2)){
							 
						 }
					 }
				 }
			 }
		/*	 int vShared = starVertex.neighbors.getTy;
			 int vNonShared = starVertex.*/
			 
		 }
				return updatedClusters;
		
	}
	
	private Graph<Vertex, Edge> createBSimilarityGraph(String path) throws IOException {
        /*
         * �o�o�̶}�l��g�Lbeta�z��L�᪺txt�ɧ@����J�A�ର�@��Graph object
         * �ɮפ��e�i��p�U�G
         * 3001,3002:0.558884354
         * 3002,3003:0.0
         * 3003,3004:0.768899112
         * �M��@��@��Ū��
         */
        File fr1 = new File(path);
        FileReader FileStream1 = new FileReader(fr1);
        BufferedReader BufferedStream1 = new BufferedReader(FileStream1);

        Graph<Vertex, Edge> betaSimilarityGraph = new Graph<Vertex, Edge>();
        String line;
        while ((line = BufferedStream1.readLine()) != null) {
            String[] info = line.split("[,|:]");
            String value1 = info[0];                    //A���
            String value2 = info[1];					//B���
            double simValue = Double.valueOf(info[2]);  //���󶡬ۦ���
            if (simValue <= 0.0)
                continue;
            Vertex v1 = getVertex(value1, betaSimilarityGraph);
            Vertex v2 = getVertex(value2, betaSimilarityGraph);
            v1.addNeighbor(v2);
            v2.addNeighbor(v1);
            // betaSimilarityGraph.addEdge(v1, v2);
            Edge e = new Edge(simValue);
            betaSimilarityGraph.addEdge(v1, v2, e);
        }
        BufferedStream1.close();
        return betaSimilarityGraph;
    }
	
	private Vertex getVertex(String str, Graph<Vertex, Edge> betaSimilarityGraph) {
        for (Vertex v : betaSimilarityGraph.getAllVertices()) {
            if (v.getValue().equals(str)) {
                return v;
            }
        }
        Vertex v = new Vertex();
        v.setValue(str);
        return v;
    }

	
}

