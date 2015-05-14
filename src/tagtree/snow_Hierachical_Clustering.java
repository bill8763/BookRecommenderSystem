package tagtree;

/*
�u�@�G�̷Ӽ��ұN�s���@�l���s
      	1.���Ҹɻ�
	2.���ҦX��
	3.�����s
�ӷ��G
�ت��G
*/

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Hashtable;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;

public class snow_Hierachical_Clustering
{
	public snow_Hierachical_Clustering(){}
	
	public static void Hierarchical_Clustering_test(int no,int type)throws IOException
	{
		Hashtable<String,ArrayList<String>> H_DocTerm = new Hashtable<String,ArrayList<String>>();	// �O���������r��	
		Hashtable<String,ArrayList<String>> H_TermDoc = new Hashtable<String,ArrayList<String>>();	// �O���r���������
		Hashtable<String,String> H_TermLevel = new Hashtable<String,String>();				// �O���r���������h
		Hashtable<String,String> H_LevelTerm = new Hashtable<String,String>();				// �O�����h�����r��
		Hashtable<String,ArrayList<String>> H_TermChild = new Hashtable<String,ArrayList<String>>();	// �O���r��������l
		
		ArrayList<String> A_Term;									// �O�������r��
		ArrayList<String> A_Doc;									// �O���������	
		ArrayList<String> A_Child;									// �O���Ҧ���l
		
		ArrayList<String> term = new ArrayList<String>();						// �O���Ҧ��r��
		ArrayList<String> doc = new ArrayList<String>();						// �O���Ҧ����
		
		// ���ҦX�ֱM��
		Hashtable<String,Boolean> H_Done = new Hashtable<String,Boolean>();				// �O���O�_�w�X�ֹL
		Hashtable<String,ArrayList<String>> H_Merge = new Hashtable<String,ArrayList<String>>();	// �O���s�X�֪�����
		
		
		// Ū��					
		FileReader FileStream1 = new FileReader("D:/DataTemp\\Processing\\Concept\\ConceptList.txt");
		FileReader FileStream2 = new FileReader("D:/DataTemp\\Processing\\TagTree\\" + no + "_File\\TagTreeNumberResult.txt");
		FileReader FileStream3 = new FileReader("D:/DataTemp\\Processing\\TagTree\\" + no + "_File\\TagTreeCheckResult.txt");
		BufferedReader BufferedStream1 = new BufferedReader(FileStream1);
		BufferedReader BufferedStream2 = new BufferedReader(FileStream2);
		BufferedReader BufferedStream3 = new BufferedReader(FileStream3);
		String line1 = "";
		String line2 = "";
		String line3 = "";
		
		while((line1=BufferedStream1.readLine()) != null)
		{
			if(((line1.split(":")[0])).equals(String.valueOf(no)))
			{
				String arr[] = (line1.split(":")[1]).split(",");
				for(String d: arr)
				{
					doc.add(d);
					A_Term = new ArrayList<String>();
					H_DocTerm.put(d,A_Term);
				}				
				break;	
			}		
		}
		
		while((line2=BufferedStream2.readLine()) != null)
		{
			String t = line2.split(":")[0];
			String l = line2.split(":")[1];
			term.add(t);
			H_TermLevel.put(t,l);
			H_LevelTerm.put(l,t);
			A_Doc = new ArrayList<String>();
			H_TermDoc.put(t,A_Doc);
			
			// �X�ֱM��
			H_Done.put(t,false);
		}
		
		while((line3=BufferedStream3.readLine()) != null)
		{
			String t = line3.split("��")[0];
			String arr[] = line3.split("��")[1].split(",");
			A_Child = new ArrayList<String>();
			
			for(String s: arr)
			{
				A_Child.add(s);
			}
			
			H_TermChild.put(t,A_Child);
		}
				
		
		// �B�z�������r��
		for(String d: doc)
		{
			A_Term = H_DocTerm.get(d);
			
			FileReader FileStream;
			if(type == 1)
				FileStream = new FileReader("D:/DataTemp\\Processing\\Document\\" + d + "_File\\FilterResult.txt");
			else
				FileStream = new FileReader("D:/K-core/Main_word/"+d+"_main_word.txt");
			BufferedReader BufferedStream = new BufferedReader(FileStream);
			String line="";
			while((line=BufferedStream.readLine()) != null)
			{
				String t = line.split(",")[0].replace("+" , " ");
				if(term.contains(t) && !A_Term.contains(t))
					A_Term.add(t);
			}
			
			H_DocTerm.put(d,A_Term);
			BufferedStream.close();
		}
		
		
		// �B�z�r���������
		for(String t: term)
		{
			A_Doc = H_TermDoc.get(t);
			
			for(String d: doc)
			{
				A_Term = H_DocTerm.get(d);
				
				if(A_Term.contains(t) && !A_Doc.contains(d))
					A_Doc.add(d);	
			}
			
			H_TermDoc.put(t,A_Doc);	
		}
		
		
		// Step 1:���Ҹɻ�
		for(String d: doc)								// ���Ҧ������
		{
			A_Term = H_DocTerm.get(d);						
			
			for(String t: A_Term)							// ���Ӥ��֦����r��
			{
				String father = findFather(t,H_TermLevel,H_LevelTerm);		// ���W�@�h�r��
				if(father.equals(""))						// �p�G�W�@�h���šA��root
					continue;
								
				while(!father.equals("") && !H_DocTerm.get(d).contains(father))	// �Y�W�@�h�r���D�šA�B�Ӥ��S�����r�� �� �N���r���[�J���~�򩹤W�@�h�ɻ�
				{
					A_Doc = H_TermDoc.get(father);				// ��s�Ӧr���������
					if(!A_Doc.contains(d))
					{	
						A_Doc.add(d);
						H_TermDoc.put(father,A_Doc);
					}
					father = findFather(father,H_TermLevel,H_LevelTerm);	// �U�@�ӤW�@�h�r��
				}
			}	
		}
		
		
		// Step 2:���ҦX��
		LinkedList<String> queue = new LinkedList<String>();				// �B�z����
		queue.addLast(H_LevelTerm.get("1"));
		boolean happen = false;								// �P�_���L�X�ֵo��
		
		while(queue.size()!=0)
		{
			String curTerm = queue.pollFirst();
			
			if(H_Done.get(curTerm) || !H_TermChild.containsKey(curTerm))		// ��ܤw�Q�X�ֹL�γ̩��h
				continue;
			else
			{
				A_Child = H_TermChild.get(curTerm);				// �Ӧr���Ҧ�Child
				
				for(int i=0; i<A_Child.size(); i++)
				{
					happen = false;
					String first = A_Child.get(i);				// �Ĥ@��Child
					queue.addLast(first);
					
					A_Doc = H_TermDoc.get(first);				// �Ĥ@��Child�֦������
					String merge = first;					// �X�֦r��
					
					for(int j=i+1; j<A_Child.size(); j++)
					{
						String second = A_Child.get(j);
						
						// �֦������Ƥ@�˥B�Ӧr�����Q�X�ֹL
						if(!H_Done.get(second) && sameDoc(first,second,H_TermDoc))
						{						
							merge = merge + "&" + second;
							happen = true;
							H_Done.put(second,true);
						}
					}
					
					if(happen)
						H_Merge.put(merge,A_Doc);
				}	
			}
		}
		
		
		// �s�W�X�֪����ҡA�R���ª�����
		if(H_Merge.size() != 0)
		{
			Set<String> set = H_Merge.keySet();
			Iterator iterator = set.iterator();
			while(iterator.hasNext())
			{
				String merge = (String)iterator.next();
				A_Doc = H_Merge.get(merge);
				H_TermDoc.put(merge,A_Doc);
				term.add(merge);
				
				String arr[] = merge.split("&");
				for(String s: arr)
				{
					H_TermDoc.remove(s);
					term.remove(s);	
				}				
			}	
		}
		
		
		// ��X
		String output="";
		BufferedWriter bw = new BufferedWriter(new FileWriter("D:/DataTemp\\Processing\\Cluster\\HierarchicalClusteringResult.txt",true));;
		
		Set<String> set = H_TermDoc.keySet();
		Iterator iterator = set.iterator();
		
		// �����b�����������
		output = no+"_concept:"+doc.get(0);
		for(int i=1; i<doc.size(); i++)
		{
			output = output + "," + doc.get(i);		
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
		
		// ���h���s�����G
		while(iterator.hasNext())
		{
			try{
				String tag = (String)iterator.next();
				A_Doc = H_TermDoc.get(tag);
				output = tag+":"+A_Doc.get(0);
				
				for(int i=1; i<A_Doc.size(); i++)
				{
					output = output +","+A_Doc.get(i);	
				}
				//System.out.println(output);

				bw.write(output);
				bw.newLine();	
			}
			catch (Exception f) 
			{
				f.printStackTrace();
				continue;
			}
		}
		bw.flush();
		bw.close();
		
				
		// �C�L
		//printOut(H_DocTerm,doc);
		//printOut(H_TermDoc,term);
		//printOut(H_TermLevel);
		//printOut(H_LevelTerm);
		//printOut2(H_TermChild);	
		/*
		Set<String> set = H_Merge.keySet();
		Iterator iterator = set.iterator();
		while(iterator.hasNext())
		{
			String merge = (String)iterator.next();
			System.out.print(merge+" :");
			A_Doc = H_Merge.get(merge);
			
			for(String d: A_Doc)
				System.out.print(d+" ");
		}
		*/	
	}
	
	
	
	// �M��r��������
	public static String findFather(String term , Hashtable<String,String> H_TermLevel, Hashtable<String,String> H_LevelTerm)
	{
		String term_level = H_TermLevel.get(term);
		String arr[] = term_level.split("\\.");
		String father_level ="";
		String father ="";
		
		if(arr.length < 2)
			return father;
		else
		{
			father_level = arr[0];
			for(int i=1; i<arr.length-1; i++)
				father_level = father_level+"."+arr[i];
							
			father = H_LevelTerm.get(father_level);
			return father;
		} 
	}
	
	
	// �P�_��r���֦������O�_�ۦP
	public static boolean sameDoc(String first,String second,Hashtable<String,ArrayList<String>> h)
	{
		ArrayList<String> first_doc = h.get(first);
		ArrayList<String> second_doc = h.get(second);
		boolean result=true;
		
		if(first_doc.size()!=second_doc.size())
			result=false;
			
		for(int i=0; i<first_doc.size(); i++)
		{
			String doc = first_doc.get(i);
			
			if(!second_doc.contains(doc))
				result=false;			
		}
		return result;
	}
		
		
	// �C�L H_DocTerm �B H_TermDoc
	public static void printOut(Hashtable<String,ArrayList<String>> h , ArrayList<String> a)
	{
		for(String s: a)
		{
			System.out.print(s+"  :");
			
			ArrayList<String> a2 = h.get(s);
			for(String s2: a2)
				System.out.print(s2+" ");
			
			System.out.println();
		}	
	}
	
	// �C�L H_TermLevel �B H_LevelTerm
	public static void printOut(Hashtable<String,String> h)
	{
		Set<String> set = h.keySet();
		Iterator iterator = set.iterator();
		while(iterator.hasNext())
		{
			String s = (String)iterator.next();
			System.out.println(s+" :"+h.get(s));
		}
	}
	
	// �C�L H_TermChild
	public static void printOut2(Hashtable<String,ArrayList<String>> h)
	{
		Set<String> set = h.keySet();
		Iterator iterator = set.iterator();
		while(iterator.hasNext())
		{
			
			String s = (String)iterator.next();
			System.out.print(s+" ��");
			ArrayList<String> a = h.get(s);
			
			for(String c: a)
				System.out.print(c+" ");
				
			System.out.println(); 
		}
	}
	
	public static void main(int no,int type)throws IOException
	{
		Hierarchical_Clustering_test(no,type);
	}
	
	
	public static void main(String args[])throws IOException
	{
		main(Integer.parseInt(args[0]),Integer.parseInt(args[0]));
	}
}