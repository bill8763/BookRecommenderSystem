

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;


public class Max_Similarity_Calculate
{
	public Max_Similarity_Calculate(){}
	
	
	@SuppressWarnings("rawtypes")
	public static void Max_Similarity_Calculate_test(int no1)throws IOException
	{
		// 块
		File fr1 = new File("D:/DataTemp/Processing/Concept/MaxSimilarityMatrix.txt");
		File fr2 = new File("D:/DataTemp/Processing/Concept/BetaSimilarityMatrix.txt");
		
		if(!fr1.exists())	// 材絞ゅン
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter("D:/DataTemp/Processing/Concept/MaxSimilarityMatrix.txt"));	
			String output = no1+","+no1+":0.0";
			try 
			{
				bw.write(output);
				bw.newLine();
				bw.flush();
				bw.close();
			} 
			catch (IOException f) 
			{
				f.printStackTrace();
			}
		}
		else
		{
			FileReader FileStream1 = new FileReader(fr1);
			BufferedReader BufferedStream1 = new BufferedReader(FileStream1);
			String line1 = "";
			ArrayList<String> list1 = new ArrayList<String>();
			while((line1=BufferedStream1.readLine())!=null)
				list1.add(line1);
			
			FileReader FileStream2 = new FileReader(fr2);
			BufferedReader BufferedStream2 = new BufferedReader(FileStream2);
			String line2 = "";
			ArrayList<String> list2 = new ArrayList<String>();
			while((line2=BufferedStream2.readLine())!=null)
				list2.add(line2);
			
			BufferedStream1.close();
			BufferedStream2.close();
			
			// 矪瞶
			Hashtable<String,String> oldDocumentMax = new Hashtable<String,String>();		// <侣ゅン籔ㄤ程ゅン,程>
			Hashtable<String,String> newDocumentMax = new Hashtable<String,String>();		// <穝ゅン籔ㄤ程ゅン,程>
			Hashtable<String,String> newDocumentValue = new Hashtable<String,String>();		// <穝ゅン籔ㄤウ侣ゅン,>
			for(String str1:list1)
			{
				oldDocumentMax.put(str1.split(":")[0],str1.split(":")[1]);
				newDocumentMax.put(str1.split(":")[0],str1.split(":")[1]);
			}
			for(String str2:list2)
			{
				if(Integer.parseInt(str2.split(",")[0]) == no1 && Double.parseDouble(str2.split(":")[1]) != 0.0)
					newDocumentValue.put(str2.split(":")[0],str2.split(":")[1]);
			}
		
			
			// т穝ゅンㄤ程ゅン newDocumentMax
			Set<String> newSet = newDocumentValue.keySet();
			Iterator iterator1 = newSet.iterator();
			double maxValue = 0.0;
			double nowValue = 0.0;
			String maxDocument = "";
			String nowDocument = "";
       		 	while(iterator1.hasNext())
       		 	{
        			nowDocument = (String)iterator1.next();
        			nowValue = Double.parseDouble(newDocumentValue.get(nowDocument));
        			
        			if(nowValue > maxValue )
        			{
        				maxValue = nowValue;
        				maxDocument = nowDocument;	
        			}
        		}
        		if(newDocumentValue.size()==0)
      				newDocumentMax.put(no1 +","+no1,"0.0");
        		else
        			newDocumentMax.put(maxDocument,Double.toString(maxValue));
        		
        	
        		// 穝侣ゅンㄤ程ゅン
	        	iterator1 = newSet.iterator();
       		 	Set<String> oldSet = oldDocumentMax.keySet();
       		 	Iterator iterator2;
       		 	while(iterator1.hasNext())
        		{
        			String newDocument = (String) iterator1.next();	
        			String newValue = (String) newDocumentValue.get(newDocument);
        			iterator2 = oldSet.iterator();
        			while(iterator2.hasNext())
        			{
        				String oldDocument = (String) iterator2.next();
        				//System.out.println(oldDocument);
        				String oldValue = (String) oldDocumentMax.get(oldDocument);
        				//System.out.println(oldValue);
        				if(newDocument.split(",")[1].equals(oldDocument.split(",")[0]))
        				{
        					if(Double.parseDouble(newValue) < Double.parseDouble(oldValue))
        					{
        						newDocumentMax.remove(oldDocument);
        						newDocumentMax.put(oldDocument.split(",")[0]+","+newDocument.split(",")[0],newValue);	
        					}	
        				}	
        			}
        		}
        		
        	
        		// 块
        		oldSet = newDocumentMax.keySet();
			iterator2 = oldSet.iterator();
			BufferedWriter bw = new BufferedWriter(new FileWriter("D:/DataTemp/Processing/Concept/MaxSimilarityMatrix.txt"));
			while(iterator2.hasNext())
			{
				String outDocument = (String)iterator2.next();
				String outMaxValue = newDocumentMax.get(outDocument);
				String output = outDocument+":"+outMaxValue;
				
				try 
				{
					bw.write(output);
					bw.newLine();
				} 
				catch (IOException f) 
				{
					f.printStackTrace();
				}
			}
			bw.flush();
			bw.close();
		}		
	
	}
	
	public static void main(int no1)throws IOException
	{
		Max_Similarity_Calculate_test(no1);
		
	}
	
	public static void main(String args[])throws IOException
	{
		//main(Integer.parseInt(args[0]));
		//int start = Integer.valueOf(args[0]);
		//int end = Integer.valueOf(args[1]);
		int start =3001;
		int end = 3010;
		for(int i=start;i<=end;i++){
			Max_Similarity_Calculate_test(i);
		}      
	}
}