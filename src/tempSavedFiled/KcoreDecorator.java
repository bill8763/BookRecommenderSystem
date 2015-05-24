package tempSavedFiled;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;
import tw.edu.ncu.im.Preprocess.PreprocessComponent;
import tw.edu.ncu.im.Preprocess.graph.KeyTerm;
import tw.edu.ncu.im.Preprocess.graph.TestEdge;
import tw.edu.ncu.im.Util.NgdEdgeSorter;

/**
 * ??�濾??��?�於??檻�?��?��?? ?��?��??��?�degree?��?��??��?�k-core?? ??��?�k-core?? ?��??��?��?大k-core?��?��?�形
 * 
 * @author chiang
 *
 * @param <V>
 * @param <E>
 */
public class KcoreDecorator<V, E> extends PreprocessDecorator<V, E> {
	Map<E, Double> ngdMap = new HashMap<E, Double>();
	private Map<V, Integer> coreMap = new HashMap<V, Integer>();
	Double edgeThreshold;

	public Map<V, Integer> getCoreMap() {
		return coreMap;
	}

	public void setCoreMap(Map<V, Integer> coreMap) {
		this.coreMap = coreMap;
	}

	public KcoreDecorator(PreprocessComponent<V, E> _component,
			Map<E, Double> _edgeDistance, Double _edgeThreshold) {
		super(_component);
		this.ngdMap = _edgeDistance;
		this.edgeThreshold = _edgeThreshold;
	}

	@SuppressWarnings("null")
	@Override
	public Graph<V, E> execute(File doc) {
		Graph<V, E> originGraph = this.originComponent.execute(doc);
		Set<V> toRemoveVerteices = new HashSet<V>();
		Set<E> toRemoveEdges = new HashSet<E>();
		// 將�?��?��?檻�?��?��?��?�入toRemoveEdges後�?��?�形?��?��
		for (E edge : originGraph.getEdges()) {
			if (ngdMap.get(edge) - edgeThreshold>0) {
				toRemoveEdges.add(edge);
			}
		}
		for (E edge : toRemoveEdges) {
			originGraph.removeEdge(edge);
		}
		/**
		 * copy originGraph to tempGraph
		 */
		Graph<V, E> tempGraph = new UndirectedSparseGraph<V, E>();
		Set<V> nodesCollector = new HashSet<V>();
		Set<E> edgesCollector = new HashSet<E>();
		Map<E, Pair<V>> endpoints = new HashMap<E, Pair<V>>();
		for (V node : originGraph.getVertices()) {
			nodesCollector.add(node);
		}
		for (E edge : originGraph.getEdges()) {
			edgesCollector.add(edge);
			endpoints.put(edge, originGraph.getEndpoints(edge));
		}
		for (V n : nodesCollector) {
			tempGraph.addVertex(n);
		}
		for (E e : edgesCollector) {
			tempGraph.addEdge(e, endpoints.get(e));
		}
		/***/

		setCoreMap(getKcore(tempGraph)); // ?��?��??��?�k-core?��?��?��?��?大�?�k
		List<Entry<?, Integer>> sortedKcore = sort(getCoreMap()); // ??��??
		int maxK;
		maxK = sortedKcore.get(0).getValue(); // ??��?��?大k
		/**
		 * ?��??��?�K-core??大�?��?�形
		 */
		for (V node : originGraph.getVertices()) {
			if (getCoreMap().get(node) != maxK) {
				toRemoveVerteices.add(node);
			}
		}
		for (V term : toRemoveVerteices) {
			originGraph.removeVertex(term);
		}
		/***/

		return originGraph;
	}

	private List<Entry<?, Integer>> sort(Map<V, Integer> unsortingMap) {
		List<Entry<? extends Object, Integer>> sortingList = new ArrayList<Entry<? extends Object, Integer>>(
				unsortingMap.entrySet());
		Collections.sort(sortingList, new Comparator<Map.Entry<?, Integer>>() {
			public int compare(Map.Entry<?, Integer> entry1,
					Map.Entry<?, Integer> entry2) {
				return entry2.getValue().compareTo(entry1.getValue());
			}
		});
		return sortingList;
	}

	private Map<V, Integer> getKcore(Graph<V, E> inputGraph) {
		int k = 0;

		Map<V, Integer> outputMap = new HashMap<V, Integer>();
		Set<V> toRemoveVertexs = new HashSet<V>();
		while (inputGraph.getVertexCount() > 0) { // ?��?��k?��算�?��?��?�inputGraph?���?
			/**
			 * ?��?��degree<k??�node
			 */
			boolean statusChange = true;
			while (statusChange) { // ??�刪?��點�?��?�新算degree??�刪
				statusChange = false;
				for (V node : inputGraph.getVertices()) { // ??��?�入toRemoveVertexs
					if (inputGraph.degree(node) < k) {
						toRemoveVertexs.add(node);
						statusChange = true;
					}
				}
				for (V node : toRemoveVertexs) { // ??�由toRemoveVertexs?��??�graph
													// node
					inputGraph.removeVertex(node);
				}
			}
			/**
			 * ?��餘node,K-core?�至少大?��k
			 */
			for (V node : inputGraph.getVertices()) {
				outputMap.put(node, k);
			}
			k++;
		}
		return outputMap;
	}

}