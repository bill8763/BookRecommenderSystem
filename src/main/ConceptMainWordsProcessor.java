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
import tw.edu.ncu.im.Preprocess.Decorator.NGDistanceDecorator;
import tw.edu.ncu.im.Preprocess.Decorator.NgdEdgeFilter;
import tw.edu.ncu.im.Preprocess.Decorator.PartOfSpeechFilter;
import tw.edu.ncu.im.Preprocess.Decorator.SearchResultFilter;
import tw.edu.ncu.im.Preprocess.Decorator.StemmingDecorator;
import tw.edu.ncu.im.Preprocess.Decorator.TermFreqDecorator;
import tw.edu.ncu.im.Preprocess.Decorator.TermToLowerCaseDecorator;
import tw.edu.ncu.im.Util.EmbeddedIndexSearcher;
import tw.edu.ncu.im.Util.HttpIndexSearcher;

public class ConceptMainWordsProcessor {
	public static String path="";
	public static String ouputPath="";
	public static  void readFromDTG(File doc, String outputPath) throws IOException{
		
		AmazonBookPrepocessor<TermNode,CEdge<Double>> prepocessor = new AmazonBookPrepocessor<TermNode,CEdge<Double>>(new Factory<TermNode>(){

			@Override
			public TermNode create() {
				return new TermNode();
			}
			
		},new Factory<CEdge<Double>>(){

			@Override
			public CEdge<Double> create() {
				return new CEdge<Double>();
			}
			
		});
		prepocessor.execute(doc);

		HttpIndexSearcher searcher = new HttpIndexSearcher();
		searcher.url="http://140.115.82.105/searchweb/";
		PartOfSpeechFilter<TermNode,CEdge<Double>> posComp = new PartOfSpeechFilter<TermNode,CEdge<Double>>(prepocessor, prepocessor.vertexContent);
		TermToLowerCaseDecorator<TermNode,CEdge<Double>> lowerComp = new TermToLowerCaseDecorator<TermNode,CEdge<Double>>(posComp, posComp.getVertexResultsTerms());
		FilteredTermLengthDecorator<TermNode,CEdge<Double>> termLengthComp = new FilteredTermLengthDecorator<TermNode,CEdge<Double>>(lowerComp, posComp.getVertexResultsTerms(), 3);
		
		StemmingDecorator<TermNode,CEdge<Double>> stemmedC = new StemmingDecorator<TermNode,CEdge<Double>>(termLengthComp, posComp.getVertexResultsTerms());
		TermFreqDecorator<TermNode,CEdge<Double>> tfComp = new TermFreqDecorator<TermNode,CEdge<Double>>(stemmedC, posComp.getVertexResultsTerms());
System.out.println("ready to search");
		SearchResultFilter<TermNode,CEdge<Double>> filitedTermComp = new SearchResultFilter<TermNode,CEdge<Double>>(tfComp,  posComp.getVertexResultsTerms(), 10, 1000, searcher);
		System.out.println("nodes searching done");
		NGDistanceDecorator<TermNode,CEdge<Double>> ngdComp = new NGDistanceDecorator<TermNode,CEdge<Double>>(filitedTermComp,posComp.getVertexResultsTerms(),searcher);
		System.out.println("edges searching done");
		NgdEdgeFilter<TermNode,CEdge<Double>> ngdflitedComp = new NgdEdgeFilter<TermNode,CEdge<Double>>(ngdComp, ngdComp.getEdgeDistance(), 0.5);
		KcoreDecorator<TermNode,CEdge<Double>> kcoreComp = new KcoreDecorator<TermNode,CEdge<Double>>(ngdflitedComp, ngdflitedComp.getNgdMap(), 0.4);
		Graph<TermNode,CEdge<Double>> docGraph = kcoreComp.execute(doc);

		/**
		 * test
		 */
		System.out.print(docGraph.getVertexCount());
		new GetMainWords(docGraph,posComp.getVertexResultsTerms(),kcoreComp.getCoreMap(),outputPath);
	}
	public static void main(String[] args) throws IOException{
		path="D:/dataset/Oldabstract/";
		ouputPath="D:/dataset/mainWords/";
		
		File outputDir = new File(ouputPath);
		if (!outputDir.exists()) {
			outputDir.mkdirs();
			try {
				outputDir.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		List<String> list = null;
		list = fileList.getFileList(path);
		File tempFile = null;
		for(int i=0;i<list.size();i++){
			tempFile= new File(path+list.get(i));
			readFromDTG(tempFile,ouputPath+list.get(i));
		}
	}
}
