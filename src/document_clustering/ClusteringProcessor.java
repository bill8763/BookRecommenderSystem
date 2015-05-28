package document_clustering;

import java.io.File;
import java.io.IOException;
import java.util.List;

import tw.edu.ncu.im.Util.HttpIndexSearcher;
import DatasetPocess.fileList;

public class ClusteringProcessor {
	public static void main(String[] args) throws Exception {
		HttpIndexSearcher searcher = new HttpIndexSearcher();
		searcher.url = "http://140.115.82.105/searchweb/";
		/** 由此資料夾內的各文件特徵字詞來分群 */		
		String path = "D:/dataset/A14OJS0VWMOSWO_mainWords/";
		/**輸出資料夾不存在則先建立*/
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
		Avg_link_all docLink = new Avg_link_all();
		for (int i = 0; i < list.size(); i++) {
			for (int j = i + 1; j < list.size(); j++) {
				docLink.two_article(
						path + list.get(i),
						path + list.get(j),
						ouputDir+"BetaSimilarityResult.txt",
						searcher);
			}
		}
		BetaSimilarity_Filtering.main(0.1,
				ouputDir+"BetaSimilarityResult.txt",
				ouputDir+"BetaSimilarityMatrix.txt");

	}
}
