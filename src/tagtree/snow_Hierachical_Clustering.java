package tagtree;
/*
工作：依照標籤將群集作子分群
      	1.標籤補齊
	2.標籤合併
	3.文件分群
來源：
目的：
*/

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Hashtable;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;

public class snow_Hierachical_Clustering
{
	public snow_Hierachical_Clustering(){}
	public static String mainwordPath = "";
	public static void Hierarchical_Clustering_test(int no,int type,String dirPath)throws IOException
	{
		Hashtable<String,ArrayList<String>> H_DocTerm = new Hashtable<String,ArrayList<String>>();	// 記錄文件對應字詞	
		Hashtable<String,ArrayList<String>> H_TermDoc = new Hashtable<String,ArrayList<String>>();	// 記錄字詞對應文件
		Hashtable<String,String> H_TermLevel = new Hashtable<String,String>();				// 記錄字詞對應階層
		Hashtable<String,String> H_LevelTerm = new Hashtable<String,String>();				// 記錄階層對應字詞
		Hashtable<String,ArrayList<String>> H_TermChild = new Hashtable<String,ArrayList<String>>();	// 記錄字詞對應兒子
		
		ArrayList<String> A_Term;									// 記錄對應字詞
		ArrayList<String> A_Doc;									// 記錄對應文件	
		ArrayList<String> A_Child;									// 記錄所有兒子
		
		ArrayList<String> term = new ArrayList<String>();						// 記錄所有字詞
		ArrayList<String> doc = new ArrayList<String>();						// 記錄所有文件
		
		// 標籤合併專用
		Hashtable<String,Boolean> H_Done = new Hashtable<String,Boolean>();				// 記錄是否已合併過
		Hashtable<String,ArrayList<String>> H_Merge = new Hashtable<String,ArrayList<String>>();	// 記錄新合併的標籤
		
		
		// 讀檔					
		FileReader FileStream1 = new FileReader(dirPath+"Concept\\ConceptList.txt");
		FileReader FileStream2 = new FileReader(dirPath+"TagTree\\" + no + "_File\\TagTreeNumberResult.txt");
		FileReader FileStream3 = new FileReader(dirPath+"TagTree\\" + no + "_File\\TagTreeCheckResult.txt");
		BufferedReader BufferedStream1 = new BufferedReader(FileStream1);
		BufferedReader BufferedStream2 = new BufferedReader(FileStream2);
		BufferedReader BufferedStream3 = new BufferedReader(FileStream3);
		String line1 = "";
		String line2 = "";
		String line3 = "";
		
		while((line1=BufferedStream1.readLine()) != null)
		{
			if(((line1.split(":")[0])).equals(String.valueOf(no)))
			{
				String arr[] = (line1.split(":")[1]).split(",");
				for(String d: arr)
				{
					doc.add(d);
					A_Term = new ArrayList<String>();
					H_DocTerm.put(d,A_Term);
				}				
				break;	
			}		
		}
		
		while((line2=BufferedStream2.readLine()) != null)
		{
			String t = line2.split(":")[0];
			String l = line2.split(":")[1];
			term.add(t);
			H_TermLevel.put(t,l);
			H_LevelTerm.put(l,t);
			A_Doc = new ArrayList<String>();
			H_TermDoc.put(t,A_Doc);
			
			// 合併專用
			H_Done.put(t,false);
		}
		
		while((line3=BufferedStream3.readLine()) != null)
		{
			String t = line3.split("-->")[0];
			String arr[] = line3.split("-->")[1].split(",");
			A_Child = new ArrayList<String>();
			
			for(String s: arr)
			{
				A_Child.add(s);
			}
			
			H_TermChild.put(t,A_Child);
		}
				
		
		// 處理文件對應字詞
		for(String d: doc)
		{
			A_Term = H_DocTerm.get(d);
			
			FileReader FileStream;
			if(type == 1)
				FileStream = new FileReader(dirPath+"Document\\" + d + "_File\\FilterResult.txt");
			else
				FileStream = new FileReader(mainwordPath+d);
			BufferedReader BufferedStream = new BufferedReader(FileStream);
			String line="";
			while((line=BufferedStream.readLine()) != null)
			{
				String t = line.split(",")[0].replace("+" , " ");
				if(term.contains(t) && !A_Term.contains(t))
					A_Term.add(t);
			}
			
			H_DocTerm.put(d,A_Term);
			BufferedStream.close();
		}
		
		
		// 處理字詞對應文件
		for(String t: term)
		{
			A_Doc = H_TermDoc.get(t);
			
			for(String d: doc)
			{
				A_Term = H_DocTerm.get(d);
				
				if(A_Term.contains(t) && !A_Doc.contains(d))
					A_Doc.add(d);	
			}
			
			H_TermDoc.put(t,A_Doc);	
		}
		
		
		// Step 1:標籤補齊
		for(String d: doc)								// 對於所有的文件
		{
			A_Term = H_DocTerm.get(d);						
			
			for(String t: A_Term)							// 對於該文件擁有的字詞
			{
				String father = findFather(t,H_TermLevel,H_LevelTerm);		// 找到上一層字詞
				if(father.equals(""))						// 如果上一層為空，表root
					continue;
								
				while(!father.equals("") && !H_DocTerm.get(d).contains(father))	// 若上一層字詞非空，且該文件沒有此字詞 --> 將此字詞加入並繼續往上一層補齊
				{
					A_Doc = H_TermDoc.get(father);				// 更新該字詞對應文件
					if(!A_Doc.contains(d))
					{	
						A_Doc.add(d);
						H_TermDoc.put(father,A_Doc);
					}
					father = findFather(father,H_TermLevel,H_LevelTerm);	// 下一個上一層字詞
				}
			}	
		}
		
		
		// Step 2:標籤合併
		LinkedList<String> queue = new LinkedList<String>();				// 處理順序
		queue.addLast(H_LevelTerm.get("1"));
		boolean happen = false;								// 判斷有無合併發生
		
		while(queue.size()!=0)
		{
			String curTerm = queue.pollFirst();
			
			if(H_Done.get(curTerm) || !H_TermChild.containsKey(curTerm))		// 表示已被合併過或最底層
				continue;
			else
			{
				A_Child = H_TermChild.get(curTerm);				// 該字的所有Child
				
				for(int i=0; i<A_Child.size(); i++)
				{
					happen = false;
					String first = A_Child.get(i);				// 第一個Child
					queue.addLast(first);
					
					A_Doc = H_TermDoc.get(first);				// 第一個Child擁有的文件
					String merge = first;					// 合併字詞
					
					for(int j=i+1; j<A_Child.size(); j++)
					{
						String second = A_Child.get(j);
						
						// 擁有的文件數一樣且該字詞未被合併過
						if(!H_Done.get(second) && sameDoc(first,second,H_TermDoc))
						{						
							merge = merge + "&" + second;
							happen = true;
							H_Done.put(second,true);
						}
					}
					
					if(happen)
						H_Merge.put(merge,A_Doc);
				}	
			}
		}
		
		
		// 新增合併的標籤，刪除舊的標籤
		if(H_Merge.size() != 0)
		{
			Set<String> set = H_Merge.keySet();
			Iterator iterator = set.iterator();
			while(iterator.hasNext())
			{
				String merge = (String)iterator.next();
				A_Doc = H_Merge.get(merge);
				H_TermDoc.put(merge,A_Doc);
				term.add(merge);
				
				String arr[] = merge.split("&");
				for(String s: arr)
				{
					H_TermDoc.remove(s);
					term.remove(s);	
				}				
			}	
		}
		
		
		// 輸出
		String output="";
		File dir = new File(dirPath+"Cluster");
		if(!dir.exists()){
			dir.mkdir();
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(dirPath+"Cluster\\HierarchicalClusteringResult.txt",true));
		
		Set<String> set = H_TermDoc.keySet();
		Iterator iterator = set.iterator();
		
		// 全部在此概念的文件
		output = no+"_concept:"+doc.get(0);
		for(int i=1; i<doc.size(); i++)
		{
			output = output + "," + doc.get(i);		
		}
		try 
		{
			bw.write(output);
			bw.newLine();	
		}
		catch (IOException f) 
		{
			f.printStackTrace();
		}
		
		// 階層分群的結果
		while(iterator.hasNext())
		{
			try{
				String tag = (String)iterator.next();
				A_Doc = H_TermDoc.get(tag);
				output = tag+":"+A_Doc.get(0);
				
				for(int i=1; i<A_Doc.size(); i++)
				{
					output = output +","+A_Doc.get(i);	
				}
				//System.out.println(output);

				bw.write(output);
				bw.newLine();	
			}
			catch (Exception f) 
			{
				f.printStackTrace();
				continue;
			}
		}
		bw.flush();
		bw.close();
		
				
		// 列印
		//printOut(H_DocTerm,doc);
		//printOut(H_TermDoc,term);
		//printOut(H_TermLevel);
		//printOut(H_LevelTerm);
		//printOut2(H_TermChild);	
		/*
		Set<String> set = H_Merge.keySet();
		Iterator iterator = set.iterator();
		while(iterator.hasNext())
		{
			String merge = (String)iterator.next();
			System.out.print(merge+" :");
			A_Doc = H_Merge.get(merge);
			
			for(String d: A_Doc)
				System.out.print(d+" ");
		}
		*/	
	}
	
	
	
	// 尋找字詞的父親
	public static String findFather(String term , Hashtable<String,String> H_TermLevel, Hashtable<String,String> H_LevelTerm)
	{
		String term_level = H_TermLevel.get(term);
		String arr[] = term_level.split("\\.");
		String father_level ="";
		String father ="";
		
		if(arr.length < 2)
			return father;
		else
		{
			father_level = arr[0];
			for(int i=1; i<arr.length-1; i++)
				father_level = father_level+"."+arr[i];
							
			father = H_LevelTerm.get(father_level);
			return father;
		} 
	}
	
	
	// 判斷兩字詞擁有的文件是否相同
	public static boolean sameDoc(String first,String second,Hashtable<String,ArrayList<String>> h)
	{
		ArrayList<String> first_doc = h.get(first);
		ArrayList<String> second_doc = h.get(second);
		boolean result=true;
		
		if(first_doc.size()!=second_doc.size())
			result=false;
			
		for(int i=0; i<first_doc.size(); i++)
		{
			String doc = first_doc.get(i);
			
			if(!second_doc.contains(doc))
				result=false;			
		}
		return result;
	}
		
		
	// 列印 H_DocTerm 、 H_TermDoc
	public static void printOut(Hashtable<String,ArrayList<String>> h , ArrayList<String> a)
	{
		for(String s: a)
		{
			System.out.print(s+"  :");
			
			ArrayList<String> a2 = h.get(s);
			for(String s2: a2)
				System.out.print(s2+" ");
			
			System.out.println();
		}	
	}
	
	// 列印 H_TermLevel 、 H_LevelTerm
	public static void printOut(Hashtable<String,String> h)
	{
		Set<String> set = h.keySet();
		Iterator iterator = set.iterator();
		while(iterator.hasNext())
		{
			String s = (String)iterator.next();
			System.out.println(s+" :"+h.get(s));
		}
	}
	
	// 列印 H_TermChild
	public static void printOut2(Hashtable<String,ArrayList<String>> h)
	{
		Set<String> set = h.keySet();
		Iterator iterator = set.iterator();
		while(iterator.hasNext())
		{
			
			String s = (String)iterator.next();
			System.out.print(s+" -->");
			ArrayList<String> a = h.get(s);
			
			for(String c: a)
				System.out.print(c+" ");
				
			System.out.println(); 
		}
	}
	
	public static void main(int no,int type,String _mainwordPath, String dirPath)throws IOException
	{
		mainwordPath=_mainwordPath;
		Hierarchical_Clustering_test(no,type,dirPath);
	}
	
	
	public static void main(String args[])throws IOException
	{
		//main(Integer.parseInt(args[0]),Integer.parseInt(args[0]));
	}
}