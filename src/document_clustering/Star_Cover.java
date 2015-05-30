package document_clustering;
/*
�u�@�G���Ҧ����s�����X�C
�ӷ��GD:\\DataTemp\\Processing\\Concept\\MaxSimilarityMatrix.txt
�ت��GD:\\DataTemp\\Processing\\Concept\\StarCoverGraph.txt
*/

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;

public class Star_Cover
{
	public Star_Cover(){}
	
	@SuppressWarnings("rawtypes")
	public static void Star_Cover_test(String maxPath,String outputPath) throws IOException
	{
		
		File fr1 = new File(maxPath);
		FileReader FileStream1 = new FileReader(fr1);
		BufferedReader BufferedStream1 = new BufferedReader(FileStream1);
		String line1 = "";
		ArrayList<String> list1 = new ArrayList<String>();
		
		Hashtable<String,ArrayList<String>> adjacent = new Hashtable<String,ArrayList<String>>();	// �O���۾F�x�}
		ArrayList<String> neighbor;									// �O���۾F�x�}�����F�~
		Hashtable<String,String> type = new Hashtable<String,String>();					// �O�����A
		Hashtable<String,Integer> degree = new Hashtable<String,Integer>();				// �O������׼�
		HashSet<String> vertex = new HashSet<String>();							// �O���Ҧ��`�I
		
		
		// ��J
		while ((line1 = BufferedStream1.readLine()) != null)
			list1.add(line1);
		
		BufferedStream1.close();
			
		// �B�z	
		Object datas[] = list1.toArray();
		String key1 = "";
		String key2 = "";
		int count=0;
		
		for(int i=0; i<datas.length; i++) 
		{
			neighbor = new ArrayList<String>();
			key1 = ((String)datas[i]).split(",")[0];			
	
			adjacent.put(key1,neighbor);
			type.put(key1,"Sate");
			degree.put(key1,0);
			vertex.add(key1);
		}
		
		for(int i=0; i<datas.length; i++) 
		{
			key1 = ((String)datas[i]).split(",")[0];
			key2 = ((String)datas[i]).split(",")[1].split(":")[0];
			
			neighbor = adjacent.get(key1);
			if(!neighbor.contains(key2))
			{
				neighbor.add(key2);
				count = degree.get(key1);
				count++;
				degree.put(key1,count);
			}
			adjacent.put(key1,neighbor);
			
			neighbor = adjacent.get(key2);
			if(!neighbor.contains(key1))
			{
				neighbor.add(key1);
				count = degree.get(key2);
				count++;
				degree.put(key2,count);
			}
			adjacent.put(key2,neighbor);
		}
				
		
		// ���s
		// 1.����X�֦��̤j����ת��`�I�A�ó]�Ӹ`�I�����A��Star
		String winner="";
		String ver="";
		int max=0;
		int num=0;
		Iterator iter1 = vertex.iterator();
		while(iter1.hasNext())
		{
			ver = (String)iter1.next();
			num = degree.get(ver);
			if(num > max)
			{
				max=num;
				winner = ver;	
			}		
		}
		type.put(winner,"Star");
		
		
		// 2.��X�䥦���`�I�κA
		String typ ="";
		String nei ="";
		Boolean b = false;
		Iterator iter2 = vertex.iterator();
		while(iter2.hasNext())
		{
			ver = (String)iter2.next();
			typ = type.get(ver);
			b=false;
			if(typ.equals("Sate"))
			{
				neighbor = adjacent.get(ver);
				if(!neighbor.isEmpty())
				{
					for(int i=0; i< neighbor.size(); i++)
					{
						nei = neighbor.get(i);
						if(!type.get(nei).equals("Star"))
						{
							if(degree.get(ver)>=degree.get(nei))
								b = true;	
						}
						if(ver.equals(nei))
							b=true;	
					}	
				}	
			}
			if(b)
				type.put(ver,"Star");		
		}
		
		
		// 3.�M��U�����s�����s��
		Hashtable<String,String> conceptNumber = new Hashtable<String,String>();	// �O�������s�s��
		File fr2 = new File(outputPath);
		int conceptMax = 0;
		if(fr2.exists())
		{
			FileReader FileStream2 = new FileReader(fr2);
			BufferedReader BufferedStream2 = new BufferedReader(FileStream2);
			String line2 = "";
			ArrayList<String> list2 = new ArrayList<String>();
			
			while ((line2 = BufferedStream2.readLine()) != null)
				list2.add(line2);
				
			for(String str: list2)
			{
				conceptNumber.put(str.split(":")[1].split(",")[0],str.split(":")[0]);
				if(Integer.parseInt(str.split(":")[0])>conceptMax)
					conceptMax = Integer.parseInt(str.split(":")[0]);
			}
					
			BufferedStream2.close();
		}
		
		/*
		// �C�L
		String key3="";
		Iterator iter3 = vertex.iterator();
		while(iter3.hasNext())
		{
			key3=(String)iter3.next();
			neighbor = adjacent.get(key3);
			System.out.print("Vertex: " +key3+" Degree: "+degree.get(key3)+" Type:"+type.get(key3)+" Neighbors: ");
			for(int i=0; i<neighbor.size(); i++)
				System.out.print(neighbor.get(i)+" ");
			System.out.println();
		}
		System.out.println("Winner: "+winner+" Number:"+max);
		*/
				
		
		// ��X
		String conceptNo ="";
		String key4="";
		String output="";		
		Iterator iter4 = vertex.iterator();
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath));
        	while(iter4.hasNext())
            	{
            		key4=(String)iter4.next();            		
            		if(type.get(key4).equals("Star"))
            		{
            			if(conceptNumber.containsKey(key4))
            				conceptNo = conceptNumber.get(key4);
            			else
            				conceptNo = String.valueOf(++conceptMax);
            			
            			output = conceptNo+":"+key4;
            			neighbor = adjacent.get(key4);
            			for(int i=0; i<neighbor.size(); i++)
            			{
            				if(!key4.equals(neighbor.get(i)))
            					output = output +","+ neighbor.get(i);	
            				
            			}
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
            	}
            	bw.flush();
		bw.close();
		
		
		
	}
	
	public static void main()throws IOException
	{
		//Star_Cover_test();
	}
	
	public static void main(String args[])throws IOException
	{
		main();
	}
}