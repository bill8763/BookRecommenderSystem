package main;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.Factory;
import org.apache.solr.client.solrj.SolrServerException;

import edu.uci.ics.jung.graph.Graph;
import tw.edu.ncu.im.Preprocess.PreprocessComponent;
import tw.edu.ncu.im.Preprocess.Decorator.PreprocessDecorator;
import tw.edu.ncu.im.Util.IndexSearchable;
import tw.edu.ncu.im.Util.NGD_calculate;

/**
 * 
 * @author chiang
 *
 * @param <V>
 * @param <E>
 */
public class NGDistanceDecorator<V, E> extends PreprocessDecorator<V, E> {
	Map<V, String> vertexTerms = new HashMap<V, String>();
	Map<V, Long> termsSearchResult = new HashMap<V, Long>();
	Map<E, Double> edgeNGDistance = new HashMap<E, Double>();
	public Map<Set<V>,Long> pairOfTermsSearchResult = new HashMap<>();
	IndexSearchable searcher;

	/**
	 * 若無提供term搜尋結果數的hashmap 則用以下建構子
	 * 
	 * @param _component
	 * @param _vertexTerms
	 *            存放node與term的string
	 * @param termsSearchResult
	 *            term和對應的搜尋結果數
	 * @param searcher
	 *            決定搜尋的方法
	 */
	public NGDistanceDecorator(PreprocessComponent<V, E> _component,
			Map<V, String> _vertexTerms, IndexSearchable searcher) {
		super(_component);
		this.vertexTerms = _vertexTerms;
		this.searcher = searcher;
	}

	/**
	 * 若有提供term搜尋結果數的hashmap，加速搜尋速度
	 * @param _component
	 * @param _vertexTerms
	 *            存放node與term的string
	 * @param termsSearchResult
	 *            term和對應的搜尋結果數
	 * @param serverURL
	 *            決定搜尋的方法
	 */
	public NGDistanceDecorator(PreprocessComponent<V, E> _component,
			Map<V, String> _vertexTerms,
			Map<V, Long> termsSearchResult, IndexSearchable searcher) {
		this( _component, _vertexTerms,  searcher);
		this.termsSearchResult = termsSearchResult;
	}




	@Override
	public Graph<V, E> execute(File doc) {
		Graph<V, E> originGraph = this.originComponent.execute(doc);
		ArrayList<V> terms = new ArrayList<>( originGraph.getVertices());
		
		/**
		 * 若termsSearchResult 為空 則先行計算
		 */
		if (this.termsSearchResult.isEmpty() ) {
			for (V term : terms) {
				long SearchResult = 0;
				try {
					SearchResult = this.searcher.searchTermSize(vertexTerms.get(term));
				} catch (SolrServerException e) {
					e.printStackTrace();
				}
				this.termsSearchResult.put(term, SearchResult);
			}
		}

		long term1term2Result = 0;
		for (V term1 : terms) {
			for (V term2 : terms) {
				if (!term1.equals(term2)
						&& originGraph.findEdge(term1, term2) == null) {
					String term1Content = this.vertexTerms.get(term1);
					long term1SearchResult = this.termsSearchResult.get(term1);
					String term2Content = this.vertexTerms.get(term2);
					long term2SearchResult = this.termsSearchResult.get(term2);
					Set<V> nodeSet = new HashSet<>();
					nodeSet.add(term1);
					nodeSet.add(term2);
					try {
						term1term2Result = this.searcher.searchMultipleTerm(new String[]{term1Content,term2Content});
						pairOfTermsSearchResult.put(nodeSet, term1term2Result);
					} catch (SolrServerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					double ngDistance = NGD_calculate.NGD_cal(
							term1SearchResult, term2SearchResult,
							term1term2Result);
					if(ngDistance<1){
						E edge = this.edgeFactory.create();
						edgeNGDistance.put(edge, ngDistance);
						originGraph.addEdge(edge, term1, term2);
					}
				}
			}
		}
		return originGraph;
	}
	/**
	 * @return the edgeNGDistance
	 */
	public Map<E, Double> getEdgeDistance() {
		return edgeNGDistance;
	}
}
