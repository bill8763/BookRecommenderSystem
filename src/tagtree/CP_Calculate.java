package tagtree;
/*
�u�@�G�p����r������Conditional Probability��
�����Gp(x|y)=P(x^y)/P(y) 	
�ӷ��G	2.D:\\DataTemp\\Processing\\TagTree\\" + no + "_File\\MergeOneGramResult.txt			(������)
       	  D:\\DataTemp\\Processing\\TagTree\\" + no + "_File\\MergeTwoGramResult.txt			(������)
	3.D:\\DataTemp\\Processing\\Document\\" + no + "_File\\FilterResult.txt				(����)
	  D:\\DataTemp\\Processing\\Document\\" + no + "_File\\FilterResult2.txt			(����)
�ت��GD:\\DataTemp\\Processing\\TagTree\\" + no + "_File\\CPResult.txt			
*/

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class CP_Calculate
{
	public CP_Calculate() {}
	
	public static void CP_Calculate_test(int no,int type,double thr) throws IOException
	{	
		ArrayList<String> list1 = new ArrayList<String>();	
		ArrayList<String> list2 = new ArrayList<String>();	
		ArrayList<String> oneName = new ArrayList<String>();	// ��r���W��
		ArrayList<String> twoName = new ArrayList<String>();	// ���r���W��
		ArrayList<Double> oneNum = new ArrayList<Double>();	// ��r����
		ArrayList<Double> twoNum = new ArrayList<Double>();	// ���r����
		
		
		// Ū��
		// type==2, ������
		File fr1 = new File("D:/DataTemp\\Processing\\TagTree\\" + no + "_File\\MergeOneGramResult.txt");
		File fr2 = new File("D:/DataTemp\\Processing\\TagTree\\" + no + "_File\\MergeTwoGramResult.txt");
		
		// type==3, ����
		if(type==3)
		{
			fr1 = new File("D:/DataTemp\\Processing\\Document\\" + no + "_File\\FilterResult.txt");
			fr2 = new File("D:/DataTemp\\Processing\\Document\\" + no + "_File\\FilterResult2.txt");
		}
		
		FileReader FileStream1 = new FileReader(fr1);
		FileReader FileStream2 = new FileReader(fr2);
		BufferedReader BufferedStream1 = new BufferedReader(FileStream1);
		BufferedReader BufferedStream2 = new BufferedReader(FileStream2);		
		String line1 = "";
		String line2 = "";
				
		while ((line1 = BufferedStream1.readLine()) != null)
			list1.add(line1);
		
		while ((line2 = BufferedStream2.readLine()) != null)
			list2.add(line2);	
				
		Object[] datas1 = list1.toArray();
		Object[] datas2 = list2.toArray();
		BufferedStream1.close();
		BufferedStream2.close();
		
		
		// ��r���B�z
		// p(x) = g(x) /g(N)
		final double total = 25;			// �N��Google ��Ʈw�`��	
		for (int i = 0; i < datas1.length; i++) 
		{
			String key = ((String)datas1[i]).split(",")[0];
			double value = (double)Math.log(Long.parseLong(((String)datas1[i]).split(",")[1])) / total;
			//double value = (double)Long.parseLong(((String)datas1[i]).split(",")[1]) / total;

			oneName.add(key);
			oneNum.add(value);
			//System.out.println(oneName.get(i) +"  "+ oneNum.get(i));
		}
		
		
		// ���r���B�z
		// p(x,y) = g(x,y) / g(N) 	
		for (int i = 0; i < datas2.length; i++) 
		{
			String key = ((String)datas2[i]).split(",")[0];
			double value = 0;
			if(((String)datas2[i]).split(",")[1].contains(".")){
				value = (double) Math.log(Long.parseLong(((String)datas2[i]).split(",")[1].split("\\.")[0])) / total;
			}
			else
				value = (double) Math.log(Long.parseLong(((String)datas2[i]).split(",")[1])) / total;
			//double value = (double) Long.parseLong(((String)datas2[i]).split(",")[1]) / total;

			twoName.add(key);
			twoNum.add(value);
			//System.out.println(twoName.get(i) +"  "+ twoNum.get(i));
		}
		
		
		// �p��CP
		// p(x|y)=P(x^y)/P(y) 		
		double preCP[][] = new double [oneNum.size()][oneNum.size()];
	
		
		for(int i=0 ; i<oneNum.size() ; i++)
		{
			for(int j=0 ; j<oneNum.size() ; j++)
			{
				if(i>=j)	
					continue;
				else
				{
					
					String two_key1 = "\""+oneName.get(i)+"\""+" "+"\""+oneName.get(j)+"\"";
					String two_key2 = "\""+oneName.get(j)+"\""+" "+"\""+oneName.get(i)+"\"";
					for(int k =0;k<twoName.size();k++){
						if(two_key1.equals(twoName.get(k))||two_key2.equals(twoName.get(k))){
							preCP[i][j] = twoNum.get(k) / oneNum.get(j);
							preCP[j][i] = twoNum.get(k) / oneNum.get(i);
							//System.out.println(oneName.get(i)+" "+oneName.get(j));
							//System.out.println(twoName.get(k));
						}
					}
					
					//System.out.println(oneName.get(i)+" & " + oneName.get(j) + " " +preCP[i][j]);
					
					if(preCP[i][j]== Double.NEGATIVE_INFINITY)
						preCP[i][j]=0;
					if(preCP[j][i]== Double.NEGATIVE_INFINITY)
						preCP[j][i]=0;
					
				
				}
			}	
		}
				
		
		// ���
		for(int i=0 ; i<oneNum.size() ; i++)
		{
			for(int j=0 ; j<oneNum.size() ; j++)
			{
				if(i>=j)	
					continue;
				
				if(preCP[i][j]>=preCP[j][i])
					preCP[j][i]=0.0;
					
				else if(preCP[i][j]<preCP[j][i])
					preCP[i][j]=0.0;
			}
		}
		
			
		// ���e�ȳ]�w
		double threshold = thr;					// CP���e��
		double CPMax = 0.0;					// CP�̤j��
		double CPMin = 1.0;					// CP�̤p��
		double CPMid = 0.0;					// CP������
		double CPAvg = 0.0;					// CP������
		
		for(int i=0 ; i<oneNum.size() ; i++)
		{
			for(int j=0 ; j<oneNum.size() ; j++)
			{
				if(i==j)	
					continue;
					
				if(preCP[i][j]>CPMax)
					CPMax=preCP[i][j];
				else if(preCP[i][j]<CPMin && preCP[i][j]!=0)
					CPMin=preCP[i][j];
					
				CPAvg+=preCP[i][j];					
			}
		}
		CPMid = (CPMax+CPMin)/2;
		CPAvg = CPAvg/ ((oneNum.size()*(oneNum.size()-1))/2);
		
		
		// �L�o
		if(thr == 1.0)
			threshold = CPMid;
		if(thr == 2.0)
			threshold = CPAvg;
		
		if(threshold > 0.0)
		{	
			for(int i=0 ; i<oneNum.size() ; i++)
			{
				for(int j=0 ; j<oneNum.size() ; j++)
				{
					if(i==j)	
						continue;
					
					if(preCP[i][j]<threshold)
						preCP[i][j]=0.0;
				}
			}
		}
		
		
		// ��X
		Object[] objs = oneName.toArray();
		String objs_out = "";
		BufferedWriter 	bw = new BufferedWriter(new FileWriter("D:/DataTemp\\Processing\\TagTree\\" + no + "_File\\CPResult.txt"));
		for(int i=0 ; i<oneNum.size() ; i++)
		{
			for(int j=0 ; j<oneNum.size() ; j++)
			{
				if(i==j)	
					continue;
				
				objs_out = (String) objs[i] + "," + objs[j] + "," + preCP[i][j];
				
				try 
				{
					bw.write(objs_out);
					bw.newLine();
				}
				catch (IOException f) 
				{
					f.printStackTrace();
				}
			}
		}
		bw.flush();
		bw.close();
	}
	
	public static void main(int no,int type,double thr)throws IOException
	{
		CP_Calculate_test(no,type,thr);
	}
	
	public static void main(String args[])throws IOException
	{
		for(int i=1;i<=6;i++){
			main(i,2,0.3);
		}
	//	main(Integer.parseInt(args[0]),Integer.parseInt(args[1]),Double.parseDouble(args[2]));
	}
}