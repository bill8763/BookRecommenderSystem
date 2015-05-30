package tagtree;
/*
�u�@�GBTrank�e�B�z�C
      1.�p�� CP/NGD ��
      2.��쥿�T���r�������Y
�ӷ��G	2.D:\\DataTemp\\Processing\\TagTree\\" + no + "_File\\MergeOneGramResult.txt
      	  D:\\DataTemp\\Processing\\TagTree\\" + no + "_File\\NGDResult.txt
      	  D:\\DataTemp\\Processing\\TagTree\\" + no + "_File\\CPResult.txt
      	3.D:\\DataTemp\\Processing\\Document\\" + no + "_File\\FilterResult.txt
      	  D:\\DataTemp\\Processing\\TagTree\\" + no + "_File\\NGDResult.txt
      	  D:\\DataTemp\\Processing\\TagTree\\" + no + "_File\\CPResult.txt
�ت��GD:\\DataTemp\\Processing\\TagTree\\" + no + "_File\\BTRankPreValueResult.txt
      D:\\DataTemp\\Processing\\TagTree\\" + no + "_File\\BTRankPreResult.txt
*/

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.ArrayList;

public class BTRank_Preprocess
{
	public BTRank_Preprocess(){}
	
	public static void main(int no,int type,String dirPath) throws IOException
	{
		Hashtable<String,ArrayList<String>> H_TermNGD = new Hashtable<String,ArrayList<String>>();			// NGD���Y<�r��,�P�䥦�r����NGD��>
		Hashtable<String,ArrayList<String>> H_TermCP = new Hashtable<String,ArrayList<String>>();			// CP���Y<�r��,�P�䥦�r����CP���V>
		Hashtable<String,ArrayList<String>> H_TermOut = new Hashtable<String,ArrayList<String>>();			// NGD���Y��CP���V<�r��,�P�䥦�r�����Y���V>
		ArrayList<String> A_NGD;
		ArrayList<String> A_CP;
		ArrayList<String> A_Out;
		ArrayList<String> A_Term = new ArrayList<String>();								// �r���W��
		
		Hashtable<String,Hashtable<String,Double>> H_TermNGDValue = new Hashtable<String,Hashtable<String,Double>>();	// NGD���Y<�r��,<�r��,NGD��>>
		Hashtable<String,Hashtable<String,Double>> H_TermCPValue = new Hashtable<String,Hashtable<String,Double>>();	// CP���Y<�r��,<�r��,CP��>>
		Hashtable<String,Double> H_NGD;											// NGD���Y<�r��,NGD��>
		Hashtable<String,Double> H_CP;											// CP���Y<�r��,CP��>
		
		double  value1 = 0.0;
		double  value2 = 0.0;
		double	result = 0.0;
		
		
		// Ū��
		// type==2, ������
		File fr1 = new File(dirPath+"TagTree\\" + no + "_File\\MergeOneGramResult.txt");			
		File fr2 = new File(dirPath+"TagTree\\" + no + "_File\\NGDResult.txt");				
		File fr3 = new File(dirPath+"TagTree\\" + no + "_File\\CPResult.txt");	
		// type==3, ����
		if(type == 3)
			fr1 = new File(dirPath+"Document\\" + no + "_File\\FilterResult.txt");
		
		BufferedReader BufferedStream1 = new BufferedReader(new FileReader(fr1));
		BufferedReader BufferedStream2 = new BufferedReader(new FileReader(fr2));
		BufferedReader BufferedStream3 = new BufferedReader(new FileReader(fr3));
		String line1 = "";
		String line2 = "";
		String line3 = "";
		String arr1 [];
		String arr2 [];
		String arr3 [];
		
		
		// �B�z
		while((line1=BufferedStream1.readLine()) != null)
		{
			A_NGD = new ArrayList<String>();
			A_CP = new ArrayList<String>();
			A_Out = new ArrayList<String>();
			
			H_NGD = new Hashtable<String,Double>(); 
			H_CP = new Hashtable<String,Double>();
			
			arr1 = line1.split(","); //���
			A_Term.add(arr1[0]);
			H_TermNGD.put(arr1[0],A_NGD);
			H_TermCP.put(arr1[0],A_CP);
			H_TermOut.put(arr1[0],A_Out);
			
			H_TermNGDValue.put(arr1[0],H_NGD);
			H_TermCPValue.put(arr1[0],H_CP);
		}
		
		
		// NGD�B�z
		while((line2=BufferedStream2.readLine()) != null)
		{
			arr2=line2.split(",");
			if(arr2.length > 0)
			{
				value1 = Double.parseDouble(arr2[2]);
				if( value1 < 1.0)
				{
					A_NGD = H_TermNGD.get(arr2[0]);
					A_NGD.add(arr2[1]);
					H_TermNGD.put(arr2[0],A_NGD);
					
					H_NGD = H_TermNGDValue.get(arr2[0]);
					H_NGD.put(arr2[1],value1);
					H_TermNGDValue.put(arr2[0],H_NGD);
				}
			}
		}
		
		
		// CP�B�z
		while((line3=BufferedStream3.readLine()) != null)
		{
			arr3=line3.split(",");
			if(arr3.length > 0)
			{
				value2 = Double.parseDouble(arr3[2]);
				if( value2 > 0.0)
				{
					A_CP = H_TermCP.get(arr3[0]);
					A_CP.add(arr3[1]);
					H_TermCP.put(arr3[0],A_CP);
					
					H_CP = H_TermCPValue.get(arr3[0]);
					H_CP.put(arr3[1],value2);
					H_TermCPValue.put(arr3[0],H_CP);
				}
			}
		}
		
		BufferedStream1.close();
		BufferedStream2.close();
		BufferedStream3.close();
		
		
		// �P�_��Ӭ������r���䥿�T�����V���Y�A�ñN (CP/NGD) �ȿ�X
		String  first  = "";
		String  second = "";
		
		BufferedWriter bw1 = new BufferedWriter(new FileWriter(new File(dirPath+"TagTree\\" + no + "_File\\BTRankPreValueResult.txt")));
		String output1="";
		
		for(int i=0 ; i<A_Term.size() ; i++)
		{
			first = A_Term.get(i);
			for(int j=0 ; j<H_TermCP.get(first).size() ; j++)
			{
				second = (String) H_TermCP.get(first).get(j);
				if(H_TermNGD.get(first).contains(second))
				{
					A_Out = H_TermOut.get(first);
					A_Out.add(second);
					H_TermOut.put(first,A_Out);
					
					result = Double.parseDouble(H_TermCPValue.get(first).get(second).toString()) / Double.parseDouble(H_TermNGDValue.get(first).get(second).toString());
					output1 = first+","+second+","+result;
					bw1.write(output1);
					bw1.newLine();
				}
			}
		}
		bw1.flush();
		bw1.close();
		
		
		// ��X
		BufferedWriter bw2 = new BufferedWriter(new FileWriter(new File(dirPath+"TagTree\\" + no + "_File\\BTRankPreResult.txt")));
		String output="";
		for(int i=0 ; i<A_Term.size() ; i++)
		{
			output = A_Term.get(i);
			for(int j=0 ; j<H_TermOut.get(A_Term.get(i)).size() ;j++ )
				output = output+","+ H_TermOut.get(A_Term.get(i)).get(j);
			
			//System.out.println(A_Term.get(i)+","+H_TermOut.get(A_Term.get(i)).size());
			bw2.write(output);
			bw2.newLine();
		}
		bw2.flush();
		bw2.close();
	}
	
	public static void printHashtable(Hashtable<String,ArrayList> h, ArrayList<String> a)
	{
		for(int i=0 ; i<a.size() ; i++)
		{
			System.out.print(a.get(i)+"��");
			for(int j=0 ; j<h.get(a.get(i)).size() ;j++ )
				System.out.print(h.get(a.get(i)).get(j)+" ");
			System.out.println();
		}	
	}
	
/*	public static void main(String args[]) throws IOException
	{
		for(int i=1;i<=6;i++)main(i,2);
		main(Integer.parseInt(args[0]),Integer.parseInt(args[1]));
	}*/
}