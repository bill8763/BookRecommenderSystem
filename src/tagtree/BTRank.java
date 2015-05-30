package tagtree;
/*
�u�@�G�p��U�Ӧr����BTrank��
�����GPR(A)=(1-d)/n + d*{PR(T1)/I(T1)+PR(T2)/I(T2)+...+PR(Tn)/I(Tn)}      
�ӷ��GD:\\DataTemp\\Processing\\TagTree\\" + no + "_File\\BTRankPreResult.txt	
      D:\\DataTemp\\Processing\\TagTree\\" + no + "_File\\BTRankPreValueResult.txt	
�ت��GD:\\DataTemp\\Processing\\TagTree\\" + no + "_File\\BTRankResult.txt
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
	public static void main(int no,String dirPath) throws Exception 
	{							
		Hashtable<String,Integer> H_TermID = new Hashtable<String,Integer>(); 						// �r���P�N��<�r��,�N��>
		Hashtable<Integer,ArrayList<String>> H_IdOutTerm = new Hashtable<Integer,ArrayList<String>>();			// �r���P��s�X���r��<�N��,�s�X���r��>
		ArrayList<String> A_OutTerm;											// �s�X���r���M��
		ArrayList<String> A_Term = new ArrayList<String>();								// �Ҧ����r���M��				
		
		Hashtable<String,Hashtable<String,Double>> H_TermOutValue = new Hashtable<String,Hashtable<String,Double>>();	// �r���P��s�X���r������(CP/NGD)<�r��,<�s�X���r��,��>>
		Hashtable<String,Double> H_Value;										// �s�X���r������<�s�X���r��,��>
		
		int total = 0;													// �����r����(�N��)
		int outdegree = 0;												// �s�X�ƶq
		int indegree =0;												// �s�J�ƶq
		
		
		// Ū��(�������Ӧr���s����Ǧr��)
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
					if(arr[j].equals(arr[0]))			// �ۤv�s�ۤv����
				 		outdegree--;
				 	else
				 		A_OutTerm.add(arr[j]);			// �O���b�s�X�r��
				}
				
				if (arr[0] != null) 
				{
					H_TermID.put(arr[0],total);
					H_IdOutTerm.put(total,A_OutTerm);
					A_Term.add(arr[0]);
					
					H_TermOutValue.put(arr[0],H_Value);
					
					//System.out.print("�r��" + arr[0] + "���s�X�׬�" + H_IdOutTerm.get(total).size() +"�A�s���F�G");
					//for(int j = 0; j < H_IdOutTerm.get(total).size(); j++)
						//System.out.print(H_IdOutTerm.get(total).get(j)+" ");
					
				}
				//System.out.println();
			}
		}
				
		
		// Ū��(�������Ӧr���s����Ǧr������(CP/NGD))
		File fr2 = new File(dirPath+"TagTree\\" + no + "_File\\BTRankPreValueResult.txt");
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
		
				
		// �p��BTRank
		if (total > 0) 
		{
			
			float[] BTRank = new float[total + 1]; 				// �s��Ҧ��r����PR��
			float[] BTOut  = new float[total + 1]; 				// �s�X�r�����p���`�M
			float fatherRank = 1f;							// ��e�r����PR��
			float alpha = 0.85f;							// �����Y��d
			int father = 0;								// �Ȧs���СA��e�r��
			int son = 0; 								// �Ȧs���СA�s�Jfather�r��
			int iterator = 100;							// �|�N����
			
			for (int i = 1; i <= total; ++i) 					// �]�m��l�Ȭ�1.0f
			{
				BTRank[i] = 1.0f;
				BTOut[i] = 0.0f;
			}

			// �i���|�N���ƹB��
			for (int i = 0; i < iterator; i++) 
			{ 
				father = 1;
				
				// Ū�Xdocid�Moutdegree�Msons
				while (father!=total+1) 
				{ 	
					indegree=0;
								
					// PR(Ti)�G�s�X�̪�PR��
					// C(Ti) �G�ӳs�X�̪�Indegree
										
					for(int j=1;j<=H_IdOutTerm.size();j++)			// �p��father��Indegree
					{
						if(H_IdOutTerm.get(j).contains(A_Term.get(father-1)))	
							indegree++;	
					}
											
					fatherRank = BTRank[father] / indegree;		// PR(Ti) / C(Ti)
					//System.out.println(A_Term.get(father-1)+" "+indegree+" "+fatherRank);
											
					for(int k=1 ; k<=H_IdOutTerm.size() ; k++)		// ��X���ֳs�Vfather
					{
						if(k==father)
							continue;
						if(H_IdOutTerm.get(k).contains(A_Term.get(father-1)))
						{	
							son = k;
							//BTOut[son] += fatherRank;
							
							// �[�J(CP/NGD)�p��BTrank
							BTOut[son] += fatherRank * Double.parseDouble(H_TermOutValue.get(A_Term.get(son-1)).get(A_Term.get(father-1)).toString());	
						}
					}	
					
					father++;
				}

				// �ǳƤU���|�N�B�⪺��l��
				for (int l = 1; l <= total; l++) 	
				{
					// PR����1
					//BTOut[l] = 0.15f + alpha * BTOut[l];
					
					// PR����2
					BTOut[l] = 0.15f / total + alpha * BTOut[l]; 

					// �C�����йB��᪺�u��pr��
					BTRank[l] = BTOut[l]; 

					BTOut[l] = 0.0f;
				}
			}
			
			
			// ��X
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(dirPath+"TagTree\\" + no + "_File\\BTRankResult.txt")));
					
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
		//main(Integer.parseInt(args[0]));	
	}
}