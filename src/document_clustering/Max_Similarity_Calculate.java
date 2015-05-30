package document_clustering;


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
	public static void Max_Similarity_Calculate_test(String fileID,String maxPath, String betaPath)throws IOException
	{
		// ��J
		File maxFile = new File(maxPath);
		File betaFile = new File(betaPath);
		
		if(!maxFile.exists())	// �N���Ĥ@�g���
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(maxPath));	
			String output = fileID+","+fileID+":0.0";
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
			FileReader FileStream1 = new FileReader(maxFile);
			BufferedReader BufferedStream1 = new BufferedReader(FileStream1);
			String line1 = "";
			ArrayList<String> list1 = new ArrayList<String>();
			while((line1=BufferedStream1.readLine())!=null)
				list1.add(line1);
			
			FileReader FileStream2 = new FileReader(betaFile);
			BufferedReader BufferedStream2 = new BufferedReader(FileStream2);
			String line2 = "";
			ArrayList<String> list2 = new ArrayList<String>();
			while((line2=BufferedStream2.readLine())!=null)
				list2.add(line2);
			
			BufferedStream1.close();
			BufferedStream2.close();
			
			// �B�z
			Hashtable<String,String> oldDocumentMax = new Hashtable<String,String>();		// <�¤��P��̤j�ۦ��פ��,�̤j�ۦ��׭�>
			Hashtable<String,String> newDocumentMax = new Hashtable<String,String>();		// <�s���P��̤j�ۦ��פ��,�̤j�ۦ��׭�>
			Hashtable<String,String> newDocumentValue = new Hashtable<String,String>();		// <�s���P�䥦�¤��,�ۦ��׭�>
			for(String str1:list1)
			{
				oldDocumentMax.put(str1.split(":")[0],str1.split(":")[1]);
				newDocumentMax.put(str1.split(":")[0],str1.split(":")[1]);
			}
			for(String str2:list2)
			{
				if( str2.split(",")[0].equals(fileID) && Double.parseDouble(str2.split(":")[1]) != 0.0)
					newDocumentValue.put(str2.split(":")[0],str2.split(":")[1]);
			}
		
			
			// ���s����̤j�ۦ��פ��å[�J�� newDocumentMax
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
      				newDocumentMax.put(fileID +","+fileID,"0.0");
        		else
        			newDocumentMax.put(maxDocument,Double.toString(maxValue));
        		
        	
        		// ��s�¤���̤j�ۦ��פ��
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
        		
        	
        		// ��X
        		oldSet = newDocumentMax.keySet();
			iterator2 = oldSet.iterator();
			BufferedWriter bw = new BufferedWriter(new FileWriter(maxPath));
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
		//Max_Similarity_Calculate_test(no1);
		
	}
	
	public static void main(String args[])throws IOException
	{

	}
}