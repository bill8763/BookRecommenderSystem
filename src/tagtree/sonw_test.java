package tagtree;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;



public class sonw_test {
	private static BufferedReader bufferedStream;
	public void test(double TCP,double PNGD,int type) throws Exception
	{
		
//		double PNGD = 7.0;						// NGD 門檻值	 //學長預選2
//		double TCP = 0.9;						// CP  門檻值            //學長預選0.9
		
		TagTree_Preprocess.main();				// 標籤樹前處理
		ArrayList<String> tagTreeList = getTagTreeList();	// 標籤樹清單
		for(String tree: tagTreeList)
		{
			int treeNum = Integer.parseInt(tree);
			
			// b.NGD 
//			System.out.println("NGD_Caculate Running");	
			NGD_calculate.main(treeNum,0,2,PNGD);		// NGD計算(標籤樹編號,無,使用型態,NGD 門檻值)(0=前處理,1=概念分群,2=標籤樹,3=實驗)
			
			// c.Conditional probability
//			System.out.println("CP_Caculate Running");	
			CP_Calculate.main(treeNum,2,TCP);		// CP計算(標籤樹編號,使用型態,門檻值)(0=前處理,1=概念分群,2=標籤樹,3=實驗)

			// d.BTRank
//			System.out.println("PageRank_Preprocess Running");
			BTRank_Preprocess.main(treeNum,2);		// Pagerank前處理(標籤樹編號,使用型態)(0=前處理,1=概念分群,2=標籤樹,3=實驗)
				
//			System.out.println("PageRank Running");		
			BTRank.main(treeNum);				// Pagerank計算(標籤樹編號)
			
			// e.TagTree
//			System.out.println("TagTree_Build Running");
			TagTree_Build.main(treeNum);			// 建置標籤樹(標籤樹編號)		
		
//			System.out.println("TagTree_Check Running");	
			TagTree_Check.main(treeNum,2);			// 檢查標籤樹(標籤樹編號,使用型態)(0=前處理,1=概念分群,2=標籤樹,3=實驗)	
		
//			System.out.println("TagTree_Number Running");	
			TagTree_Number.main(treeNum);			// 標號標籤樹(標籤樹編號)
		}
		
		
		// 4.階層分群
		File f = new File("D:/DataTemp\\Processing\\Cluster\\HierarchicalClusteringResult.txt");
		if(f.exists())
			f.delete();
			
		for(String tree: tagTreeList)
		{
			int treeNum = Integer.parseInt(tree);
//			System.out.println("Hierarchical_Clustering Running");
			snow_Hierachical_Clustering.main(treeNum,type);				
		}
		
	}
	public static ArrayList<String> getTagTreeList() throws IOException 
	{		
		File file= new File("D:/DataTemp\\Processing\\Concept\\ConceptState.txt");
		FileReader FileStream = new FileReader(file);
		bufferedStream = new BufferedReader(FileStream);
		String line="";
		
		ArrayList<String> tagTreeList = new ArrayList<String>();	// 標籤樹清單
		while((line=bufferedStream.readLine()) != null)
		{
			String state = line.split(":")[0].split(",")[1];
			String tree = line.split(",")[0];
			if(state.equals("C") || state.equals("Y"))
				tagTreeList.add(tree);	
		}
		
		return tagTreeList;
	}
	

}
