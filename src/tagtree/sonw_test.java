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
		
//		double PNGD = 7.0;						// NGD ���e��	 //�Ǫ��w��2
//		double TCP = 0.9;						// CP  ���e��            //�Ǫ��w��0.9
		
		TagTree_Preprocess.main();				// ���Ҿ�e�B�z
		ArrayList<String> tagTreeList = getTagTreeList();	// ���Ҿ�M��
		for(String tree: tagTreeList)
		{
			int treeNum = Integer.parseInt(tree);
			
			// b.NGD 
//			System.out.println("NGD_Caculate Running");	
			NGD_calculate.main(treeNum,0,2,PNGD);		// NGD�p��(���Ҿ�s��,�L,�ϥΫ��A,NGD ���e��)(0=�e�B�z,1=�������s,2=���Ҿ�,3=����)
			
			// c.Conditional probability
//			System.out.println("CP_Caculate Running");	
			CP_Calculate.main(treeNum,2,TCP);		// CP�p��(���Ҿ�s��,�ϥΫ��A,���e��)(0=�e�B�z,1=�������s,2=���Ҿ�,3=����)

			// d.BTRank
//			System.out.println("PageRank_Preprocess Running");
			BTRank_Preprocess.main(treeNum,2);		// Pagerank�e�B�z(���Ҿ�s��,�ϥΫ��A)(0=�e�B�z,1=�������s,2=���Ҿ�,3=����)
				
//			System.out.println("PageRank Running");		
			BTRank.main(treeNum);				// Pagerank�p��(���Ҿ�s��)
			
			// e.TagTree
//			System.out.println("TagTree_Build Running");
			TagTree_Build.main(treeNum);			// �ظm���Ҿ�(���Ҿ�s��)		
		
//			System.out.println("TagTree_Check Running");	
			TagTree_Check.main(treeNum,2);			// �ˬd���Ҿ�(���Ҿ�s��,�ϥΫ��A)(0=�e�B�z,1=�������s,2=���Ҿ�,3=����)	
		
//			System.out.println("TagTree_Number Running");	
			TagTree_Number.main(treeNum);			// �и����Ҿ�(���Ҿ�s��)
		}
		
		
		// 4.���h���s
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
		
		ArrayList<String> tagTreeList = new ArrayList<String>();	// ���Ҿ�M��
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
