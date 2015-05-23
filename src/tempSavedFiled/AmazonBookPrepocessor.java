package tempSavedFiled;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections15.Factory;

import tw.edu.ncu.im.Preprocess.graph.*;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

public class AmazonBookPrepocessor<V,E> extends PreprocessComponent<V,E> {
	HashSet<KeyTerm> terms;
	HashSet<TestEdge> edges;
	public HashMap<V,String> vertexContent = new HashMap<>();
	public AmazonBookPrepocessor(Factory<V> _vertexFactory,Factory<E>_edgeFactory){
		this.vertexFactory = _vertexFactory;
		this.edgeFactory = _edgeFactory;
	}
	@Override
	public Graph<V,E> execute(File doc) {
		Graph<V,E> documentGraph = null;
		if(!doc.isFile()){
			//TODO ?à§?ñ∑?òØ?ê¶?Ç∫?èØËÆ?ÂØ´‰?îÁÇ∫txt ?ê¶??áthrow Exception
		}
		try(BufferedReader r = new BufferedReader(new FileReader(doc));){
			documentGraph = new UndirectedSparseGraph<V,E>();
			for(String line=r.readLine();line!=null;line = r.readLine()){
				line=line.replace("]", "");
				line=line.replace("[", "");

		        Matcher m = Pattern.compile("[(),.\"\\?!:;]").matcher(line);
		        line = m.replaceAll("");
				V node = this.vertexFactory.create();
				this.vertexContent.put(node, line);
				documentGraph.addVertex(node);
				
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		return documentGraph;
		
	}
	
	
	
}