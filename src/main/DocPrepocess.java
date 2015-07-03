package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.Factory;

import DatasetPocess.fileList;
import edu.uci.ics.jung.graph.Graph;
import tw.edu.ncu.im.Preprocess.AmazonBookPrepocessor;
import tw.edu.ncu.im.Preprocess.Decorator.FilteredTermLengthDecorator;
import tw.edu.ncu.im.Preprocess.Decorator.KcoreDecorator;
import tw.edu.ncu.im.Preprocess.Decorator.NgdEdgeFilter;
import tw.edu.ncu.im.Preprocess.Decorator.PartOfSpeechFilter;
import tw.edu.ncu.im.Preprocess.Decorator.SearchResultFilter;
import tw.edu.ncu.im.Preprocess.Decorator.StemmingDecorator;
import tw.edu.ncu.im.Preprocess.Decorator.TermFreqDecorator;
import tw.edu.ncu.im.Preprocess.Decorator.TermToLowerCaseDecorator;
import tw.edu.ncu.im.Util.EmbeddedIndexSearcher;
import tw.edu.ncu.im.Util.HttpIndexSearcher;

public class DocPrepocess {
	public static String path = "";
	public static String mainWordsOutputPath = "";
	public static String numOfPathOuputPath = "";
	public static void main(String[] args) throws IOException {
		path="D:/dataset/Abstract/";
		//path = "D:/dataset/Abstract/";
		mainWordsOutputPath = "D:/dataset/mainWords/";
		//mainWordsOutputPath = "D:/dataset/mainWords/";
		File outputDir = new File(mainWordsOutputPath);
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}
		numOfPathOuputPath="D:/dataset/numOfPair/";
		//numOfPathOuputPath="D:/dataset/numOfPair/";
		File numOfPathOuputDir = new File(numOfPathOuputPath);
		if (!numOfPathOuputDir.exists()) {
			numOfPathOuputDir.mkdirs();
		}		
		List<String> list = null;
		list = fileList.getFileList(path);
		File tempFile = null;
		for (int i = 0; i < list.size(); i++) {
			tempFile = new File(path + list.get(i));
//			readFromDTG(tempFile, mainWordsOutputPath + list.get(i),numOfPathOuputPath+list.get(i));
			if (new File(mainWordsOutputPath + list.get(i)).exists()) {
				System.out.println("exists!");
			} else {
				readFromDTG(tempFile, mainWordsOutputPath + list.get(i),
						numOfPathOuputPath + list.get(i));
			}
		}
	}
	
	
	public static void readFromDTG(File doc, String mainwordsOutputPath,String numOfPathOuputPath)
			throws IOException {

		AmazonBookPrepocessor<TermNode, CEdge<Double>> prepocessor = new AmazonBookPrepocessor<TermNode, CEdge<Double>>(
				new Factory<TermNode>() {

					@Override
					public TermNode create() {
						return new TermNode();
					}

				}, new Factory<CEdge<Double>>() {

					@Override
					public CEdge<Double> create() {
						return new CEdge<Double>();
					}

				});
		prepocessor.execute(doc);

		HttpIndexSearcher searcher = new HttpIndexSearcher();
		searcher.url = "http://140.115.82.105/searchweb/";
		PartOfSpeechFilter<TermNode, CEdge<Double>> posComp = new PartOfSpeechFilter<TermNode, CEdge<Double>>(
				prepocessor, prepocessor.vertexContent);
		TermToLowerCaseDecorator<TermNode, CEdge<Double>> lowerComp = new TermToLowerCaseDecorator<TermNode, CEdge<Double>>(
				posComp, posComp.getVertexResultsTerms());
		FilteredTermLengthDecorator<TermNode, CEdge<Double>> termLengthComp = new FilteredTermLengthDecorator<TermNode, CEdge<Double>>(
				lowerComp, posComp.getVertexResultsTerms(), 3);

		StemmingDecorator<TermNode, CEdge<Double>> stemmedC = new StemmingDecorator<TermNode, CEdge<Double>>(
				termLengthComp, posComp.getVertexResultsTerms());
		TermFreqDecorator<TermNode, CEdge<Double>> tfComp = new TermFreqDecorator<TermNode, CEdge<Double>>(
				stemmedC, posComp.getVertexResultsTerms());

		SearchResultFilter<TermNode, CEdge<Double>> filitedTermComp = new SearchResultFilter<TermNode, CEdge<Double>>(
				tfComp, posComp.getVertexResultsTerms(), 10, 10000, searcher);
		NGDistanceDecorator<TermNode, CEdge<Double>> ngdComp = new NGDistanceDecorator<TermNode, CEdge<Double>>(
				filitedTermComp, posComp.getVertexResultsTerms(), searcher);
		NgdEdgeFilter<TermNode, CEdge<Double>> ngdflitedComp = new NgdEdgeFilter<TermNode, CEdge<Double>>(
				ngdComp, ngdComp.getEdgeDistance(), 0.25);
		KcoreDecorator<TermNode, CEdge<Double>> kcoreComp = new KcoreDecorator<TermNode, CEdge<Double>>(
				ngdflitedComp, ngdflitedComp.getNgdMap(), 1.0);
		System.out.println("ready to processing:" + doc);
		Graph<TermNode, CEdge<Double>> docGraph = kcoreComp.execute(doc);
		/** 刪掉degree0的點 */
		HashSet<TermNode> termsToRemove = new HashSet<>();
		HashSet<Set<TermNode>> setToRemove = new HashSet<>();
		for (TermNode term : docGraph.getVertices()) {
			if (docGraph.getNeighborCount(term) == 0) {
				termsToRemove.add(term);
				for( Set<TermNode> termSet:ngdComp.pairOfTermsSearchResult.keySet()){
					if(termSet.contains(term)){
						setToRemove.add(termSet);
					}
				}
			}
		}
		for (TermNode term : termsToRemove) {
			docGraph.removeVertex(term);
			posComp.getVertexResultsTerms().remove(term);
		}
		for ( Set<TermNode> termSet : setToRemove) {
			ngdComp.pairOfTermsSearchResult.remove(termSet);
		}
		/**
		 * test
		 */
		System.out.println(docGraph.getVertexCount());
		if (docGraph.getVertexCount() > 0) {
		gerenratingTXT.gerenratingMainWords(docGraph, posComp.getVertexResultsTerms(),
					filitedTermComp.termsSearchResult, kcoreComp.getCoreMap(),
					mainwordsOutputPath);
			gerenratingTXT.gerenratingNumOfPair(docGraph,posComp.getVertexResultsTerms(),ngdComp.pairOfTermsSearchResult,numOfPathOuputPath);
		}
	}
}
