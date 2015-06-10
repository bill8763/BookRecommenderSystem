package tagtree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import tw.edu.ncu.im.Util.HttpIndexSearcher;

public class sonw_test {
	private static BufferedReader bufferedStream;

	public void test(double TCP, double PNGD, int type) throws Exception {
		// double PNGD = 7.0; // NGD 門檻值 //學長預選2
		// double TCP = 0.9; // CP 門檻值 //學長預選0.9
		HttpIndexSearcher searcher = new HttpIndexSearcher();
		searcher.url = "http://140.115.82.105/searchweb/";
		String dirPath = "D:/dataset/Processing/";
		String mainwordPath = "D:/dataset/A14OJS0VWMOSWO_mainWords/";
		String numOfPairPath = "D:/dataset/A14OJS0VWMOSWO_numOfPair/";
		ArrayList<String> tagTreeList = getTagTreeList(dirPath
				+ "Concept/ConceptState.txt"); // 標籤樹清單
		TagTree_Preprocess.main(dirPath, mainwordPath, numOfPairPath, searcher); // 標籤樹前處理

		for (String tree : tagTreeList) {
			int treeNum = Integer.parseInt(tree);

			// b.NGD
			// System.out.println("NGD_Caculate Running");
			NGD_calculate.main(treeNum, 0, 2, PNGD,dirPath); // NGD計算(標籤樹編號,無,使用型態,NGD
														// 門檻值)(0=前處理,1=概念分群,2=標籤樹,3=實驗)

			// c.Conditional probability
			// System.out.println("CP_Caculate Running");
			CP_Calculate.main(treeNum, 2, TCP,dirPath); // CP計算(標籤樹編號,使用型態,門檻值)(0=前處理,1=概念分群,2=標籤樹,3=實驗)

			// d.BTRank
			// System.out.println("PageRank_Preprocess Running");
			BTRank_Preprocess.main(treeNum, 2,dirPath); // Pagerank前處理(標籤樹編號,使用型態)(0=前處理,1=概念分群,2=標籤樹,3=實驗)

			// System.out.println("PageRank Running");
			BTRank.main(treeNum,dirPath); // Pagerank計算(標籤樹編號)

			// e.TagTree
			// System.out.println("TagTree_Build Running");
			TagTree_Build.main(treeNum,dirPath); // 建置標籤樹(標籤樹編號)

			// System.out.println("TagTree_Check Running");
			TagTree_Check.main(treeNum, 2,dirPath); // 檢查標籤樹(標籤樹編號,使用型態)(0=前處理,1=概念分群,2=標籤樹,3=實驗)

			// System.out.println("TagTree_Number Running");
			TagTree_Number.main(treeNum,dirPath); // 標號標籤樹(標籤樹編號)
		}

		// 4.階層分群
		File f = new File(
				dirPath+"Cluster\\HierarchicalClusteringResult.txt");
		if (f.exists())
			f.delete();

		for (String tree : tagTreeList) {
			int treeNum = Integer.parseInt(tree);
			// System.out.println("Hierarchical_Clustering Running");
			snow_Hierachical_Clustering.main(treeNum, type,mainwordPath,dirPath);
		}

	}

	public static ArrayList<String> getTagTreeList(String conceptStatePath)
			throws IOException {
		File file = new File(conceptStatePath);
		FileReader FileStream = new FileReader(file);
		bufferedStream = new BufferedReader(FileStream);
		String line = "";

		ArrayList<String> tagTreeList = new ArrayList<String>(); // 標籤樹清單
		while ((line = bufferedStream.readLine()) != null) {
			String state = line.split(":")[0].split(",")[1];
			String tree = line.split(",")[0];
			if (state.equals("C") || state.equals("Y"))
				tagTreeList.add(tree);
		}

		return tagTreeList;
	}

}