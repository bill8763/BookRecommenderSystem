package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;

/**
 * 
 * @author chiang
 *
 */
public class gerenratingTXT {
	public static void gerenratingMainWords(
			Graph<TermNode, CEdge<Double>> docGraph,
			Map<TermNode, String> termMap, Map<TermNode, Long> searchMap,
			Map<TermNode, Integer> kcoreMap, String outputPath)
			throws IOException {
		@SuppressWarnings("resource")
		BufferedWriter writter = new BufferedWriter(new FileWriter(outputPath,
				false));
		for (TermNode node : docGraph.getVertices()) {
			writter.write(termMap.get(node) + "," + searchMap.get(node) + ","
					+ kcoreMap.get(node) + "," + docGraph.degree(node));
			writter.newLine();
			writter.flush(); //
		}
	}

	public static void gerenratingNumOfPair(
			Graph<TermNode, CEdge<Double>> docGraph,
			Map<TermNode, String> termMap, Map<Set<TermNode>, Long> pairOfTermsSearchResult,
			String outputPath) throws IOException {
		BufferedWriter writter = new BufferedWriter(new FileWriter(outputPath,
				false));
		for (TermNode term1 : docGraph.getVertices()) {
			for (TermNode term2 : docGraph.getVertices()) {
				if (!term1.equals(term2)) {
					HashSet<TermNode> termSet = new HashSet<>();
					termSet.add(term1);
					termSet.add(term2);
					writter.write("\"" + termMap.get(term1) + "\"+\""
							+ termMap.get(term2) + "\","
							+ pairOfTermsSearchResult.get(termSet));
					writter.newLine();
					writter.flush(); //
				}
			}
		}
	}
}
