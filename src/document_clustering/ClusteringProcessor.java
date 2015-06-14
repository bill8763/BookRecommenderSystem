package document_clustering;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.frieda.graduation.Experiment;

import tw.edu.ncu.im.Util.HttpIndexSearcher;
import DatasetPocess.fileList;

public class ClusteringProcessor {
	public static void main(String[] args) throws Exception {
		HttpIndexSearcher searcher = new HttpIndexSearcher();
		searcher.url = "http://140.115.82.105/searchweb/";
		/** 由此資料夾內的各文件特徵字詞來分群 */
		String path = "D:/dataset/mainWords/";
		/** 輸出資料夾不存在則先建立 */
		String ouputDir = "D:/DataSet/Processing/Concept/";
		File outputDir = new File(ouputDir);
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
		/** 算每篇文件相似度 */
		Avg_link_all docLink = new Avg_link_all();
		for (int i = 0; i < list.size(); i++) {
			for (int j = i + 1; j < list.size(); j++) {
				docLink.two_article(path,list.get(i),list.get(j),
						ouputDir + "BetaSimilarityResult.txt", searcher);
			}
		}
		/** 過濾 */
		BetaSimilarity_Filtering.main(0.2, ouputDir
				+ "BetaSimilarityResult.txt", ouputDir
				+ "BetaSimilarityMatrix.txt");
		/** 只保留最大邊 */
		for (int i = 0; i < list.size(); i++) {
			Max_Similarity_Calculate.Max_Similarity_Calculate_test(
					list.get(i), ouputDir + "MaxSimilarityMatrix.txt",
					ouputDir + "BetaSimilarityResult.txt");
		}
		/** 找出主星 */
		Star_Cover.Star_Cover_test(ouputDir + "MaxSimilarityMatrix.txt",
				ouputDir + "StarCoverGraph.txt");
		/** 篩選主星 */
		Experiment e = new Experiment();
		e.excute();
		/**產生概念分群狀態*/
		Concept_Postprocess.Concept_Postprocess_test(ouputDir
				+ "ConceptList.txt", ouputDir + "StarCoverGraph.txt", ouputDir
				+ "ConceptState.txt");
	}
}
