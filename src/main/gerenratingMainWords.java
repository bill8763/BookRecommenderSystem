package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import edu.uci.ics.jung.graph.Graph;
/**
 * 
 * @author chiang
 *
 */
public class gerenratingMainWords {
	public gerenratingMainWords(Graph<TermNode, CEdge<Double>> docGraph, Map<TermNode,String> termMap, Map<TermNode, Integer> kcoreMap, String outputPath) throws IOException {
		@SuppressWarnings("resource")
		BufferedWriter writter = new BufferedWriter(new FileWriter(outputPath, false));
		for( TermNode node: docGraph.getVertices()){
			writter.write(termMap.get(node)+","+kcoreMap.get(node)+","+docGraph.degree(node));
			writter.newLine();
			writter.flush(); // 
		}
	}
}
