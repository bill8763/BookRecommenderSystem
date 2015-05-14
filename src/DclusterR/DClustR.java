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
	
	//藉由計算節點相似度 分析哪些節點可被加入候選主星list
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
            // 建立beta similarity graph
            betaSimilarityGraph = createBSimilarityGraph("D:\\DataTemp\\Processing\\Concept\\MaxSimilarityMatrix.txt");
            System.out.println(betaSimilarityGraph);
            // 初始群集
            initialClusters = calculatingRelClustering(betaSimilarityGraph);
            System.out.println(initialClusters);
            //更新群集
            updatedClusters = chooseVertex(initialClusters);
            System.out.println(updatedClusters);
            
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
	
	//計算節點相似度 形成初始群集
	private SortedSet<Cluster<Vertex, Edge, AbstractGraph<Vertex, Edge>>> calculatingRelClustering(Graph<Vertex, Edge> betaSimilarityGraph)
            throws IOException{
		Set<Vertex> all = betaSimilarityGraph.getAllVertices();             //圖中所有節點
		double densityR;                               //可加入的衛星比例                       
		double compactnessR;                           //加入衛星的相似度比例
		double intraSim;                              //子圖內部相似度
		double NeighborintraSim;                      //鄰居子圖內部相似度
		double sumOfVEdge = 0;
		
	/*	for(Vertex v: all){
			System.out.println("size:"+v.neighbors.size());
		}*/
		
		for (Vertex v : all){
			Set<Edge> links = betaSimilarityGraph.getAllEdgesOf(v);    //點鄰居邊
	        double degreeOfV = links.size();          //v節點分支度
	        double valueOfEdge ;
	        for (Edge e : links) {
                Vertex neighbor = betaSimilarityGraph.getSourceOf(e).equals(v) ? betaSimilarityGraph.getDestOf(e)
                        : betaSimilarityGraph.getSourceOf(e);
                //degreeOfNeighbor = betaSimilarityGraph.getAllEdgesOf(neighbor).size();  //點鄰居分支度
                valueOfEdge = e.getSimValue();                        //邊值
                sumOfVEdge += valueOfEdge;	
                System.out.println("test"+valueOfEdge);
                //	System.out.println("V.densityR" + density);       
	        }
	        double density = 0;                 //小於v的鄰居節點總數
	        for (Vertex neighbor :v.neighbors){
	        	
                int degreeOfNeighbor = betaSimilarityGraph.getAllEdgesOf(neighbor).size();
                
	        	
	        	if(degreeOfNeighbor < degreeOfV){
	        		density++;
	        		}
	        	
	        }
	        	densityR = density / degreeOfV;                  //可加入的衛星比例;計算v.densityR
	        	System.out.println("density" + densityR);
	        
	        
	        double compactness = 0 ;
	        intraSim = sumOfVEdge / degreeOfV;            //與v相連的圖型內部相似度
	        for(Vertex neighbor:v.neighbors){
	        	Set<Edge> Neighborlink = betaSimilarityGraph.getAllEdgesOf(neighbor);  //v點的鄰居的邊
                
	        	double valueOfNeighborEdge= 0;
                double sumOfNeighborEdge =0;
                double degreeOfNeighbor = betaSimilarityGraph.getAllEdgesOf(neighbor).size();
                
                for(Edge e2 : Neighborlink){	
	        		valueOfNeighborEdge = e2.getSimValue();                        //邊值
                	sumOfNeighborEdge += valueOfNeighborEdge;       
                }
                NeighborintraSim = sumOfNeighborEdge / degreeOfNeighbor;  //與v相連的鄰居圖型內部相似度
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
			//satVertex.add(v);                          //先將排序完的點存進衛星列中
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
		}                                         //此for可測試看看!
			
		
		
		
		
		
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
			 Vertex starVertex = (Vertex)c.getStarVertex();      //取得主星
			 int degree = starVertex.neighbors.size();           //主星分支度
			 int overlapSat;
			 //可能要加sort
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
         * 這這裡開始把經過beta篩選過後的txt檔作為輸入，轉為一個Graph object
         * 檔案內容可能如下：
         * 3001,3002:0.558884354
         * 3002,3003:0.0
         * 3003,3004:0.768899112
         * 然後一行一行讀取
         */
        File fr1 = new File(path);
        FileReader FileStream1 = new FileReader(fr1);
        BufferedReader BufferedStream1 = new BufferedReader(FileStream1);

        Graph<Vertex, Edge> betaSimilarityGraph = new Graph<Vertex, Edge>();
        String line;
        while ((line = BufferedStream1.readLine()) != null) {
            String[] info = line.split("[,|:]");
            String value1 = info[0];                    //A文件
            String value2 = info[1];					//B文件
            double simValue = Double.valueOf(info[2]);  //兩文件間相似度
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

