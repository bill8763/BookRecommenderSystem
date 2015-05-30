package tagtree;
/*
�u�@�G�ظm���Ҿ�
      1.�C�^�X���̤jBT�Ȫ��r��
      2.�N���̤jBT�Ȫ��r���A�T�w�S���䥦�r�����s�V���ҫ��V���r��
�ӷ��GD:\\DataTemp\\Processing\\TagTree\\" + no + "_File\\BTRankResult.txt	
      D:\\DataTemp\\Processing\\TagTree\\" + no + "_File\\BTRankPreResult.txt	
�ت��GD:\\DataTemp\\Processing\\TagTree\\" + no + "_File\\TagTreeBuildResult.txt
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
	
	public static void main(int no, String dirPath) throws IOException
	{
		Hashtable<String,ArrayList<String>> H_TermOut = new Hashtable<String,ArrayList<String>>();	// �r���P��s�X���r��<�r��,�s�X���r��>
		Hashtable<String,Boolean> H_Choose = new Hashtable<String,Boolean>();				// �P�_�r���O�_�w�T�w���h<�r��,���L>
		
		ArrayList<String> A_Term = new ArrayList<String>();						// �Ҧ��r���M��
		ArrayList<Double> A_BTValue = new ArrayList<Double>();						// �Ҧ��r����BT�ȲM��
		ArrayList<String> A_TermOut ;									// �s�X���r���M��
		ArrayList<String> A_OriTermOut ;								// �̤jBT�Ȩ�s�X���r���M��
		ArrayList<String> A_NewTermOut ;								// �̤jBT�Ȩ��s�s�X���r���M��
		
		
		// Ū��	
		File fr1 = new File(dirPath+"TagTree\\" + no + "_File\\BTRankResult.txt");		
		File fr2 = new File(dirPath+"TagTree\\" + no + "_File\\BTRankPreResult.txt");					
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
		
		
		// �ˬd
		double max = 0.0;								// �̤jBT��
		int index = 0;									// �U��
		String term = "";								// �̤jBT�Ȫ��r��
		String out = "";								// �̤jBT�Ȫ��r����s�X���Y�@�r��
		
		while(H_Choose.contains(false) && max >=0.0)
		{
			max = -1.0;
			index = 0;
			term = "";
			out = "";
			
			A_OriTermOut = new ArrayList<String>();
			A_NewTermOut = new ArrayList<String>();
			
			// ���̤j��BT��
			for(int i=0 ; i< A_BTValue.size() ; i++)
			{
				if( A_BTValue.get(i) > max )
				{
					max = A_BTValue.get(i);	
					index = i;		
				}
			}
			
			// �N���̤jBT�Ȫ��r���A�T�w�S���䥦�r�����s�V���ҫ��V���r��
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
						if(H_Choose.get(out) == false)	// �N��Ӧr���٥��Q�T�w���h
						{
							H_Choose.put(out,true);
							A_NewTermOut.add(out);
						}
					}
					H_TermOut.put(term,A_NewTermOut);		// ���s��s���T�����h
				}
			}
		}
		
		/*
		// �C�L
		for(int i=0 ; i<A_Term.size() ; i++)
		{
			if(H_TermOut.get(A_Term.get(i))!=null && H_TermOut.get(A_Term.get(i)).size() > 0)
			{
				System.out.print(A_Term.get(i)+"��");
				for(int j=0 ; j<H_TermOut.get(A_Term.get(i)).size()-1 ;j++ )
					System.out.print(H_TermOut.get(A_Term.get(i)).get(j)+",");
					
				System.out.print(H_TermOut.get(A_Term.get(i)).get(H_TermOut.get(A_Term.get(i)).size()-1));
				System.out.println();
			}	
		}
		*/
		
		// ��X
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(dirPath+"TagTree\\" + no + "_File\\TagTreeBuildResult.txt")));			
		String output="";
		for(int i=0 ; i<A_Term.size() ; i++)
		{
			if(H_TermOut.get(A_Term.get(i))!=null && H_TermOut.get(A_Term.get(i)).size() > 0)
			{
				output = A_Term.get(i) + "��";
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
		//main(Integer.parseInt(args[0]));
	}
}