package tagtree;
/*
工作：標號標籤樹，將字詞作階層的編號
來源：D:\\DataTemp\\Processing\\Document\\" + no + "_File\\FilterResult.txt	
      D:\\DataTemp\\Processing\\TagTree\\" + no + "_File\\TagTreeCheckResult.txt			
      D:\\DataTemp\\Processing\\TagTree\\" + no + "_File\\BTRankResult.txt	
目的：D:\\DataTemp\\Processing\\TagTree\\" + no + "_File\\TagTreeNumberResult.txt
*/

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

public class TagTree_Number
{
	public TagTree_Number(){}
	
	public static void TagTree_Number_test(int no) throws IOException
	{
		// 輸入
		//File fr1 = new File("D:\\DataTemp\\Processing\\Document\\" + no + "_File\\FilterResult.txt");	
		File fr1 = new File("D:\\DataTemp\\Processing\\TagTree\\" + no + "_File\\MergeOneGramResult.txt");	
		File fr2 = new File("D:\\DataTemp\\Processing\\TagTree\\" + no + "_File\\TagTreeCheckResult.txt");			
		File fr3 = new File("D:\\DataTemp\\Processing\\TagTree\\" + no + "_File\\BTRankResult.txt");	
		FileReader FileStream1 = new FileReader(fr1);
		FileReader FileStream2 = new FileReader(fr2);
		FileReader FileStream3 = new FileReader(fr3);
		BufferedReader BufferedStream1 = new BufferedReader(FileStream1);
		BufferedReader BufferedStream2 = new BufferedReader(FileStream2);
		BufferedReader BufferedStream3 = new BufferedReader(FileStream3);

		String line1 = "";
		String line2 = "";
		String line3 = "";
		ArrayList<String> list1 = new ArrayList<String>();
		ArrayList<String> list2 = new ArrayList<String>();
		ArrayList<String> list3 = new ArrayList<String>();				
		while ((line1 = BufferedStream1.readLine()) != null)
			list1.add(line1.split(",")[0]);
		while ((line2 = BufferedStream2.readLine()) != null)
			list2.add(line2);
		while ((line3 = BufferedStream3.readLine()) != null)
			list3.add(line3);
		
		BufferedStream1.close();
		BufferedStream2.close();
		BufferedStream3.close();
		
		
		// 處理
		Hashtable<String,ArrayList<String>> term_list = new Hashtable<String,ArrayList<String>>();	// 字詞與指向的字詞清單<字詞,字詞清單>
		Hashtable<String,String> term_num = new Hashtable<String,String>();				// 字詞與階層編號<字詞,編號>
		ArrayList<String> list;										// 指向的字詞清單
		String rootTerm = "";										// 記錄頭字詞
		double rootValue = 0.0;										// 記錄頭字詞的值
		
		for(String s1: list1)
		{
			list = new ArrayList<String>();
			term_list.put(s1,list);
			term_num.put(s1,"0");	
		}
		
		for(String s1: list2)
		{
			String father = s1.split("→")[0];
			String son[] = s1.split("→")[1].split(",");
			list = term_list.get(father);
			
			for(String s2: son)
				list.add(s2);
					
			term_list.put(father,list);
		}
		
		for(String s1: list3)
		{
			String curTerm = s1.split(",")[0];
			double curValue = Double.parseDouble(s1.split(",")[1]);
			if(curValue > rootValue)
			{
				rootTerm = curTerm;
				rootValue = curValue;	
			}	
		}
		
		
		// 找出編號
		term_num.put(rootTerm,"1");
		LinkedList<String> queue = new LinkedList<String>();
		queue.addLast(rootTerm);

		while(!queue.isEmpty())
		{
			int index = 1;
			String term = queue.removeFirst();
			String num = term_num.get(term);
			list = term_list.get(term);
			
			if(list.size()!=0)
			{
				for(String s1 : list)
				{
					term_num.put(s1,num+"."+index);
					queue.addLast(s1);
					index++;
				}		
			}	
		}
		
		
		// 排序
		Hashtable<String,String> num_term = new Hashtable<String,String>();		// 階層編號與字詞<編號,字詞>
		Set<String> set1 = term_num.keySet();
		Iterator iterator1 = set1.iterator();
		while(iterator1.hasNext())
		{
			String term = (String) iterator1.next();
			String num = term_num.get(term);
			if(!num.equals("0"))
				num_term.put(num,term);
		}
		
		Set<String> set2 = num_term.keySet();
		Iterator iterator2 = set2.iterator();
		LinkedList<String> sort = new LinkedList<String>();
		while(iterator2.hasNext())
		{
			String num = (String) iterator2.next();
			sort.add(num);
		}
		Collections.sort(sort);
		
		
		// 輸出	
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("D:\\DataTemp\\Processing\\TagTree\\" + no + "_File\\TagTreeNumberResult.txt")));			
		String output="";
		for(String s1: sort)
		{	
			String term = num_term.get(s1);
			output = term + ":" + s1;
			bw.write(output);
			bw.newLine();
		}
		bw.flush();
		bw.close();
	}
	
	public static void main(int no)throws IOException
	{
		TagTree_Number_test(no);
	}
	
	public static void main(String args[])throws IOException
	{
		main(Integer.parseInt(args[0]));
	}
} 