package tempSavedFiled;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.uci.ics.jung.graph.Graph;
import tw.edu.ncu.im.Preprocess.PreprocessComponent;
import tw.edu.ncu.im.Util.NgdEdgeSorter;

/**
 * 
 * @author chiang
 *
 * @param <V>
 * @param <E>
 */
public class NgdEdgeFilter<V, E> extends PreprocessDecorator<V, E> {
	private Map<E, Double> ngdMap = new HashMap<E, Double>();
	Double rankThresholdPercent;

	public Map<E, Double> getNgdMap() {
		return ngdMap;
	}

	public void setNgdMap(Map<E, Double> ngdMap) {
		this.ngdMap = ngdMap;
	}

	/**
	 * @param _component
	 * @param _edgeDistance
	 *            ?”¾edge??‡distance
	 * @param rankThreshold
	 *            è¦é?æ¿¾?‰©xï¼…ç?„NGD ex:0.3 70%??„éƒ½??ƒé?æ¿¾
	 */
	public NgdEdgeFilter(PreprocessComponent<V, E> _component, Map<E, Double> _edgeDistance, double _rankThreshold) {
		super(_component);
		this.setNgdMap(_edgeDistance);
		this.rankThresholdPercent = _rankThreshold;
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public Graph<V, E> execute(File doc) {
		Graph<V, E> originGraph = this.originComponent.execute(doc);
		if(getNgdMap().size()!=originGraph.getEdgeCount()){
			throw new IllegalStateException("the map should sync with the graph");
		}
		int rankThreshold = (int) (rankThresholdPercent * originGraph.getEdgeCount());
		
		List<Entry<?, Double>> sortedEdge = NgdEdgeSorter.sort(getNgdMap());
		
		for (int index = getNgdMap().size(); index > rankThreshold; index=index-1) {
			Object edgeUnderThreshold = sortedEdge.get(index-1).getKey();
			getNgdMap().remove((E) edgeUnderThreshold);
			originGraph.removeEdge((E) edgeUnderThreshold);
		}
		return originGraph;
	}

}
