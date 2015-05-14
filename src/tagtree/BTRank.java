package tagtree;
/*
工作：計算各個字詞的BTrank值
公式：PR(A)=(1-d)/n + d*{PR(T1)/I(T1)+PR(T2)/I(T2)+...+PR(Tn)/I(Tn)}      
來源：D:\\DataTemp\\Processing\\TagTree\\" + no + "_File\\BTRankPreResult.txt	
      D:\\DataTemp\\Processing\\TagTree\\" + no + "_File\\BTRankPreValueResult.txt	
目的：D:\\DataTemp\\Processing\\TagTree\\" + no + "_File\\BTRankResult.txt
*/


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.ArrayList;

public class BTRank 
{
	public static void main(int no) throws Exception 
	{							
		Hashtable<String,Integer> H_TermID = new Hashtable<String,Integer>(); 						// 字詞與代號<字詞,代號>
		Hashtable<Integer,ArrayList<String>> H_IdOutTerm = new Hashtable<Integer,ArrayList<String>>();			// 字詞與其連出的字詞<代號,連出的字詞>
		ArrayList<String> A_OutTerm;											// 連出的字詞清單
		ArrayList<String> A_Term = new ArrayList<String>();								// 所有的字詞清單				
		
		Hashtable<String,Hashtable<String,Double>> H_TermOutValue = new Hashtable<String,Hashtable<String,Double>>();	// 字詞與其連出的字詞之值(CP/NGD)<字詞,<連出的字詞,值>>
		Hashtable<String,Double> H_Value;										// 連出的字詞之值<連出的字詞,值>
		
		int total = 0;													// 紀錄字詞數(代號)
		int outdegree = 0;												// 連出數量
		int indegree =0;												// 連入數量
		
		
		// 讀檔(紀錄哪個字詞連到哪些字詞)
		File fr1 = new File("D:\\DataTemp\\Processing\\TagTree\\" + no + "_File\\BTRankPreResult.txt");
		BufferedReader BufferedStream1 = new BufferedReader(new FileReader(fr1));
		String line1 = "";
		String[] arr;	
		
		while((line1=BufferedStream1.readLine()) != null) 
		{
			A_OutTerm = new ArrayList<String>();
			H_Value = new Hashtable<String,Double>();
			
			++total;
			arr = line1.split(",");
			
			if (arr.length > 0) 
			{
				outdegree = arr.length - 1;
				for(int j = 1; j < arr.length; j++) 
				{
					if(arr[j].equals(arr[0]))			// 自己連自己不算
				 		outdegree--;
				 	else
				 		A_OutTerm.add(arr[j]);			// 記錄在連出字詞
				}
				
				if (arr[0] != null) 
				{
					H_TermID.put(arr[0],total);
					H_IdOutTerm.put(total,A_OutTerm);
					A_Term.add(arr[0]);
					
					H_TermOutValue.put(arr[0],H_Value);
					
					//System.out.print("字詞" + arr[0] + "的連出度為" + H_IdOutTerm.get(total).size() +"，連結了：");
					//for(int j = 0; j < H_IdOutTerm.get(total).size(); j++)
						//System.out.print(H_IdOutTerm.get(total).get(j)+" ");
					
				}
				//System.out.println();
			}
		}
				
		
		// 讀檔(紀錄哪個字詞連到哪些字詞的值(CP/NGD))
		File fr2 = new File("D:/DataTemp\\Processing\\TagTree\\" + no + "_File\\BTRankPreValueResult.txt");
		BufferedReader BufferedStream2 = new BufferedReader(new FileReader(fr2));
		String line2="";
		
		while((line2=BufferedStream2.readLine()) != null)
		{
			arr=line2.split(",");
			H_Value = H_TermOutValue.get(arr[0]);
			H_Value.put(arr[1],Double.parseDouble(arr[2]));
			H_TermOutValue.put(arr[0],H_Value);
		}
		/*
		for(int i=0 ; i<H_TermOutValue.size() ;i++)
		{
			System.out.println(A_Term.get(i)+"="+H_TermOutValue.get(A_Term.get(i)).size());
		}
		*/
		
		BufferedStream1.close();
		BufferedStream2.close();
		
				
		// 計算BTRank
		if (total > 0) 
		{
			
			float[] BTRank = new float[total + 1]; 				// 存放所有字詞的PR值
			float[] BTOut  = new float[total + 1]; 				// 連出字詞的計算總和
			float fatherRank = 1f;							// 當前字詞的PR值
			float alpha = 0.85f;							// 阻尼係數d
			int father = 0;								// 暫存指標，當前字詞
			int son = 0; 								// 暫存指標，連入father字詞
			int iterator = 100;							// 疊代次數
			
			for (int i = 1; i <= total; ++i) 					// 設置初始值為1.0f
			{
				BTRank[i] = 1.0f;
				BTOut[i] = 0.0f;
			}

			// 進行疊代次數運算
			for (int i = 0; i < iterator; i++) 
			{ 
				father = 1;
				
				// 讀出docid和outdegree和sons
				while (father!=total+1) 
				{ 	
					indegree=0;
								
					// PR(Ti)：連出者的PR值
					// C(Ti) ：該連出者的Indegree
										
					for(int j=1;j<=H_IdOutTerm.size();j++)			// 計算father的Indegree
					{
						if(H_IdOutTerm.get(j).contains(A_Term.get(father-1)))	
							indegree++;	
					}
											
					fatherRank = BTRank[father] / indegree;		// PR(Ti) / C(Ti)
					//System.out.println(A_Term.get(father-1)+" "+indegree+" "+fatherRank);
											
					for(int k=1 ; k<=H_IdOutTerm.size() ; k++)		// 找出有誰連向father
					{
						if(k==father)
							continue;
						if(H_IdOutTerm.get(k).contains(A_Term.get(father-1)))
						{	
							son = k;
							//BTOut[son] += fatherRank;
							
							// 加入(CP/NGD)計算BTrank
							BTOut[son] += fatherRank * Double.parseDouble(H_TermOutValue.get(A_Term.get(son-1)).get(A_Term.get(father-1)).toString());	
						}
					}	
					
					father++;
				}

				// 準備下次疊代運算的初始值
				for (int l = 1; l <= total; l++) 	
				{
					// PR公式1
					//BTOut[l] = 0.15f + alpha * BTOut[l];
					
					// PR公式2
					BTOut[l] = 0.15f / total + alpha * BTOut[l]; 

					// 每次反覆運算後的真正pr值
					BTRank[l] = BTOut[l]; 

					BTOut[l] = 0.0f;
				}
			}
			
			
			// 輸出
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("D:/DataTemp\\Processing\\TagTree\\" + no + "_File\\BTRankResult.txt")));
					
			for (int i = 1; i <= total; i++) 
			{
				bw.write(A_Term.get(i-1)+","+String.valueOf(BTRank[i]));
				bw.newLine();
			}
			bw.flush();
			bw.close();
			BTRank = null;
			BTOut = null;
		}
	}
	
	public static void main(String args[]) throws Exception
	{
		main(Integer.parseInt(args[0]));	
	}
}