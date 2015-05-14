package tagtree;
/*
工作：檢查標籤樹
      比較兩字詞與三字詞的 CP/NGD 值
      k→l→j & k→j
      l→k→j & l→j
來源：	2.D:\\DataTemp\\Processing\\TagTree\\" + no + "_File\\MergeOneGramResult.txt	
      	  D:\\DataTemp\\Processing\\TagTree\\" + no + "_File\\MergeTwoGramResult.txt
      	  D:\\DataTemp\\Processing\\TagTree\\" + no + "_File\\BTRankPreResult.txt
      	  D:\\DataTemp\\Processing\\TagTree\\" + no + "_File\\TaggingTreeResult.txt	
	3.D:\\DataTemp\\Processing\\Document\\" + no + "_File\\FilterResult.txt	
      	  D:\\DataTemp\\Processing\\Document\\" + no + "_File\\FilterResult2.txt
      	  D:\\DataTemp\\Processing\\TagTree\\" + no + "_File\\BTRankPreResult.txt
      	  D:\\DataTemp\\Processing\\TagTree\\" + no + "_File\\TagTreeBuildResult.txt	
目的：D:\\DataTemp\\Processing\\TagTree\\" + no + "_File\\TagTreeCheckResult.txt
*/

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList; 

public class TagTree_Check
{
	public TagTree_Check(){}
	
	public static void TagTree_Check_test(int no,int type) throws IOException
	{
		ArrayList<String> oneName = new ArrayList<String>();	// 單字詞名稱
		ArrayList<String> twoName = new ArrayList<String>();	// 雙字詞名稱
		ArrayList<Double> oneValue = new ArrayList<Double>();	// 單字詞值
		ArrayList<Double> twoValue = new ArrayList<Double>();	// 雙字詞值
		
		
		// 輸入
		// type==2, 標籤樹
		File fr1 = new File("D:/DataTemp\\Processing\\TagTree\\" + no + "_File\\MergeOneGramResult.txt");
		File fr2 = new File("D:/DataTemp\\Processing\\TagTree\\" + no + "_File\\MergeTwoGramResult.txt");
		File fr3 = new File("D:/DataTemp\\Processing\\TagTree\\" + no + "_File\\BTRankPreResult.txt");
		File fr4 = new File("D:/DataTemp\\Processing\\TagTree\\" + no + "_File\\TagTreeBuildResult.txt");
		
		// type==3, 實驗
		if(type == 3)
		{
			fr1 = new File("D:/DataTemp\\Processing\\Document\\" + no + "_File\\FilterResult.txt");
			fr2 = new File("D:/DataTemp\\Processing\\Document\\" + no + "_File\\FilterResult2.txt");	
		}	
		
		FileReader FileStream1 = new FileReader(fr1);
		FileReader FileStream2 = new FileReader(fr2);
		FileReader FileStream3 = new FileReader(fr3);
		FileReader FileStream4 = new FileReader(fr4);
		BufferedReader BufferedStream1 = new BufferedReader(FileStream1);
		BufferedReader BufferedStream2 = new BufferedReader(FileStream2);		
		BufferedReader BufferedStream3 = new BufferedReader(FileStream3);
		BufferedReader BufferedStream4 = new BufferedReader(FileStream4);
		String line = "";
		
		
		// f(x) = Math.log(f(x))
		while ((line = BufferedStream1.readLine()) != null)
		{
			oneName.add(line.split(",")[0]);
			oneValue.add(Math.log(Double.parseDouble(line.split(",")[1])));	
		}
		
		// f(x,y) = Math.log(f(x,y))
		while ((line = BufferedStream2.readLine()) != null)
		{
			twoName.add(line.split(",")[0]);
			twoValue.add(Math.log(Double.parseDouble(line.split(",")[1])));	
		}
		
		
		// CP處理
		// CP(x,y)=p(x|y)=f(x,y)/f(y) 			
		double CP[][] = new double [oneValue.size()][oneValue.size()];
		int p =0;
		for(int i=0 ; i<oneValue.size() ; i++)
		{
			for(int j=0 ; j<oneValue.size() ; j++)
			{
				if(i>=j)	
					continue;
				else
				{
					CP[i][j] = twoValue.get(p) / oneValue.get(j);
					CP[j][i] = twoValue.get(p) / oneValue.get(i);
					
					if(CP[i][j]== Double.NEGATIVE_INFINITY)
						CP[i][j]=0;
					if(CP[j][i]== Double.NEGATIVE_INFINITY)
						CP[j][i]=0;
					p++;
				}
			}	
		}
		
		
		// NGD處理
		// NGD(x,y) = {M[f(x),f(y)] - f(x,y)} / {f(N) - m[f(x),f(y)]}			
		double NGD[][] = new double [oneValue.size()][oneValue.size()];
		double total = 30.0;
		p =0;
		for(int i=0 ; i<oneValue.size() ; i++)
		{
			for(int j=0 ; j<oneValue.size() ; j++)
			{
				if(i>=j)	
					continue;
					
				NGD[i][j] = (Math.max(oneValue.get(i),oneValue.get(j))-twoValue.get(p)) / (total - Math.min(oneValue.get(i),oneValue.get(j)));	
				NGD[j][i] = NGD[i][j];
				p++;
			}	
		}
		
		
		// CN處理
		// CN(x,y) = CP(x,y) / NGD(x,y);
		double CN[][] = new double [oneValue.size()][oneValue.size()];
		for(int i=0 ; i<oneValue.size() ; i++)
		{
			for(int j=0 ; j<oneValue.size() ; j++)
			{
				CN[i][j] = CP[i][j] / NGD[i][j] ;
			}	
		}
		
		
		// 指向關係
		// PointTo(x,y)
		int PointTo[][] = new int [oneValue.size()][oneValue.size()];
		while((line = BufferedStream3.readLine()) != null)
		{
			String temp1[] = line.split(",");
			if(temp1.length > 1)
			{
				int first = oneName.indexOf(temp1[0]);
				for(int i=1; i<temp1.length ;i++)
				{
					int second = oneName.indexOf(temp1[i]);
					PointTo[first][second] = 2;
				}	
			}
		}
		
		
		// 標籤樹
		while((line = BufferedStream4.readLine()) != null)
		{
			String temp2[] = line.replaceAll("→",",").split(",");
			if(temp2.length > 1)
			{
				int first = oneName.indexOf(temp2[0]);
				for(int i=1; i<temp2.length ;i++)
				{
					int second = oneName.indexOf(temp2[i]);
					PointTo[first][second]-=1;
				}	
			}
		}
		
		
		// 檢查
		double threeTermValue = 0.0;			// 三字詞的值
		double twoTermValue = 0.0;			// 雙字詞的值
		boolean change = false;				// 紀錄單次是否有變化
		boolean changeAll = false;			// 記錄整體是否有變化
		for(int j=0 ; j<oneValue.size() ; j++)
		{
			ArrayList<Integer> sons = new ArrayList<Integer>();
			
			for(int i=0 ; i<oneValue.size() ; i++)
			{
				if(PointTo[i][j]>0)
					sons.add(i);	
			}
			
			if(sons.size()>1)
			{
				for(int k=0 ; k < sons.size()-1 ; k++)
				{
					for(int l=k+1 ; l < sons.size() ; l++)
					{
						if( PointTo[sons.get(k)][sons.get(l)] == 0 && PointTo[sons.get(l)][sons.get(k)] == 0)
							continue;
						else if ( (PointTo[sons.get(k)][j]+PointTo[sons.get(l)][j]) > 3 )
							continue;
						
						change = false;
						
						
						if( PointTo[sons.get(k)][sons.get(l)] > 0)	//k→l→j & k→j
						{
							
							//threeTermValue = (twoTermValue.get(k)/total) * CP[j][sons.get(l)] * CP[sons.get(l)][sons.get(k)];	// PLSA
							//twoTermValue = CP[j][sons.get(k)];
							
							threeTermValue = (double)(CN[sons.get(k)][sons.get(l)]+CN[sons.get(l)][j])/2;				// CN
							twoTermValue = CN[sons.get(k)][j];
							
							if(threeTermValue > twoTermValue && PointTo[sons.get(k)][sons.get(l)]==1)
							{
								PointTo[sons.get(l)][j]=1;
								PointTo[sons.get(k)][j]=2;
								change = true;
								changeAll = true;	
							}
							
							//System.out.println(oneName.get(sons.get(k))+"→"+oneName.get(sons.get(l))+"→"+oneName.get(j)+":"+threeTermValue);
							//System.out.println(oneName.get(sons.get(k))+"→"+oneName.get(j)+":"+twoTermValue);
							//System.out.println("Change:"+change);
						}
						else						//l→k→j & l→j
						{
							//threeTermValue = (twoTermValue.get(l)/total) * CP[j][sons.get(k)] * CP[sons.get(k)][sons.get(l)];	// PLSA
							//twoTermValue = CP[j][sons.get(l)];
							
							threeTermValue = (double)(CN[sons.get(l)][sons.get(k)]+CN[sons.get(k)][j])/2;				// CN
							twoTermValue = CN[sons.get(l)][j];
							
							if(threeTermValue > twoTermValue && PointTo[sons.get(l)][sons.get(k)]==1)
							{
								PointTo[sons.get(k)][j]=1;
								PointTo[sons.get(l)][j]=2;
								change = true;
								changeAll = true;	
							}
							
							//System.out.println(oneName.get(sons.get(l))+"→"+oneName.get(sons.get(k))+"→"+oneName.get(j)+":"+threeTermValue);
							//System.out.println(oneName.get(sons.get(l))+"→"+oneName.get(j)+":"+twoTermValue);
							//System.out.println("Change:"+change);
						}					
					}	
				}	
			}	
		}
		
		//System.out.println("ChangeAll:"+changeAll);
		
		// 輸出
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("D:/DataTemp\\Processing\\TagTree\\" + no + "_File\\TagTreeCheckResult.txt")));			
		String output="";
		for(int i=0 ; i<oneValue.size() ; i++)
		{
			output =  oneName.get(i)+ "→";
			for(int j=0 ; j<oneValue.size() ; j++)
			{
				if(PointTo[i][j]!=1)
					continue;
				else
				{
					output = output + oneName.get(j) + ",";
				}		
			}
			output = output.substring(0,output.length()-1);
			if(output.split("→").length > 1)
			{
				bw.write(output);
				bw.newLine();	
			}
		}
		bw.flush();
		bw.close();
		
		BufferedStream1.close();
		BufferedStream2.close();
		BufferedStream3.close();
		BufferedStream4.close();
		
	}
	
	public static void main(int no,int type)throws IOException
	{
		TagTree_Check_test(no,type);
	}
	
	public static void main(String args[])throws IOException
	{
		for(int i=1;i<=6;i++)
			main(i,2);
		//main(Integer.parseInt(args[0]),Integer.parseInt(args[1]));
	}
} 