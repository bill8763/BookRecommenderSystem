package tagtree;
/*
工作：建置標籤樹
      1.每回合找到最大BT值的字詞
      2.將其具最大BT值的字詞，確定沒有其它字詞有連向它所指向的字詞
來源：D:\\DataTemp\\Processing\\TagTree\\" + no + "_File\\BTRankResult.txt	
      D:\\DataTemp\\Processing\\TagTree\\" + no + "_File\\BTRankPreResult.txt	
目的：D:\\DataTemp\\Processing\\TagTree\\" + no + "_File\\TagTreeBuildResult.txt
*/

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class TagTree_Build
{
	public TagTree_Build(){}
	
	public static void main(int no) throws IOException
	{
		Hashtable<String,ArrayList<String>> H_TermOut = new Hashtable<String,ArrayList<String>>();	// 字詞與其連出的字詞<字詞,連出的字詞>
		Hashtable<String,Boolean> H_Choose = new Hashtable<String,Boolean>();				// 判斷字詞是否已確定階層<字詞,布林>
		
		ArrayList<String> A_Term = new ArrayList<String>();						// 所有字詞清單
		ArrayList<Double> A_BTValue = new ArrayList<Double>();						// 所有字詞的BT值清單
		ArrayList<String> A_TermOut ;									// 連出的字詞清單
		ArrayList<String> A_OriTermOut ;								// 最大BT值其連出的字詞清單
		ArrayList<String> A_NewTermOut ;								// 最大BT值其更新連出的字詞清單
		
		
		// 讀檔	
		File fr1 = new File("D:/DataTemp\\Processing\\TagTree\\" + no + "_File\\BTRankResult.txt");		
		File fr2 = new File("D:/DataTemp\\Processing\\TagTree\\" + no + "_File\\BTRankPreResult.txt");					
		BufferedReader BufferedStream1 = new BufferedReader(new FileReader(fr1));
		BufferedReader BufferedStream2 = new BufferedReader(new FileReader(fr2));
		String line1 = "";
		String line2 = "";
		String arr1 [];
		String arr2 [];
		
		while((line1=BufferedStream1.readLine()) != null)
		{
			arr1 = line1.split(",");
			A_Term.add(arr1[0]);
			A_BTValue.add(Double.parseDouble(arr1[1]));
			H_Choose.put(arr1[0],false);
		}
		
		while((line2=BufferedStream2.readLine()) != null)
		{
			arr2 = line2.split(",");
			if(arr2.length > 1)
			{
				A_TermOut = new ArrayList<String>();
				for(int i=1 ; i<arr2.length ; i++)
					A_TermOut.add(arr2[i]);
				
				H_TermOut.put(arr2[0],A_TermOut);
			}
		}
		
		BufferedStream1.close();
		BufferedStream2.close();
		
		
		// 檢查
		double max = 0.0;								// 最大BT值
		int index = 0;									// 下標
		String term = "";								// 最大BT值的字詞
		String out = "";								// 最大BT值的字詞其連出的某一字詞
		
		while(H_Choose.contains(false) && max >=0.0)
		{
			max = -1.0;
			index = 0;
			term = "";
			out = "";
			
			A_OriTermOut = new ArrayList<String>();
			A_NewTermOut = new ArrayList<String>();
			
			// 找到最大的BT值
			for(int i=0 ; i< A_BTValue.size() ; i++)
			{
				if( A_BTValue.get(i) > max )
				{
					max = A_BTValue.get(i);	
					index = i;		
				}
			}
			
			// 將其具最大BT值的字詞，確定沒有其它字詞有連向它所指向的字詞
			if(max > 0.0)
			{
				A_BTValue.set(index,-1.0);
				term = A_Term.get(index);
				A_OriTermOut = H_TermOut.get(term);
				
				if(A_OriTermOut != null)
				{
					for(int j=0 ; j<A_OriTermOut.size() ; j++)
					{
						out = A_OriTermOut.get(j);
						if(H_Choose.get(out) == false)	// 代表該字詞還未被確定階層
						{
							H_Choose.put(out,true);
							A_NewTermOut.add(out);
						}
					}
					H_TermOut.put(term,A_NewTermOut);		// 重新更新正確的階層
				}
			}
		}
		
		/*
		// 列印
		for(int i=0 ; i<A_Term.size() ; i++)
		{
			if(H_TermOut.get(A_Term.get(i))!=null && H_TermOut.get(A_Term.get(i)).size() > 0)
			{
				System.out.print(A_Term.get(i)+"→");
				for(int j=0 ; j<H_TermOut.get(A_Term.get(i)).size()-1 ;j++ )
					System.out.print(H_TermOut.get(A_Term.get(i)).get(j)+",");
					
				System.out.print(H_TermOut.get(A_Term.get(i)).get(H_TermOut.get(A_Term.get(i)).size()-1));
				System.out.println();
			}	
		}
		*/
		
		// 輸出
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("D:/DataTemp\\Processing\\TagTree\\" + no + "_File\\TagTreeBuildResult.txt")));			
		String output="";
		for(int i=0 ; i<A_Term.size() ; i++)
		{
			if(H_TermOut.get(A_Term.get(i))!=null && H_TermOut.get(A_Term.get(i)).size() > 0)
			{
				output = A_Term.get(i) + "→";
				for(int j=0 ; j<H_TermOut.get(A_Term.get(i)).size()-1 ;j++ )
					output = output + H_TermOut.get(A_Term.get(i)).get(j) + ",";
					
				output = output + H_TermOut.get(A_Term.get(i)).get(H_TermOut.get(A_Term.get(i)).size()-1);
				bw.write(output);
				bw.newLine();	
			}
		}
		bw.flush();
		bw.close();
	}
	
	public static void main(String args[]) throws IOException
	{
		main(Integer.parseInt(args[0]));
	}
}