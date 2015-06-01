package concept_k_core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Stem {

	/**
	 * 
	 */
	public Stem() {
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unchecked")
	public static void stemming(int concept_id,int topic_id, String path) throws IOException {
		FileReader FileStream = new FileReader(path+"Number_of_term/"+concept_id + "_"+topic_id + "_number_of_term.txt");
		@SuppressWarnings("resource")
		BufferedReader BufferedStream = new BufferedReader(FileStream);
		String e = "";

		@SuppressWarnings("rawtypes")
		ArrayList list = new ArrayList();
		while ((e = BufferedStream.readLine()) != null) {
			
				list.add(e);
				
		}
		// System.out.println( list.toString());

		Object[] datas = list.toArray();
		LinkedHashSet<String> set = new LinkedHashSet<String>();
		for (int i = 0; i < datas.length; i++) {
			String key = ((String) datas[i]).split(",")[0]; //first keyword
			String value = ((String) datas[i]).split(",")[1];
			
			Pattern p= Pattern.compile("[(),\"\\?!:;=><]");

	         Matcher m=p.matcher(key);

	         String first=m.replaceAll("");
	         
			 set.add (first + "," + value);
					 // i++;
 
			  
		}
		File test = new File(path+"Stem/");
		if (!test.exists()) {
			test.mkdirs();
		}
		Object[] objs = set.toArray();
		BufferedWriter bw;
		bw = new BufferedWriter(new FileWriter(path+"Stem/"+concept_id + "_" +topic_id + "_stem.txt", false));
		for (int j = 0; j < objs.length; j++) {
			String objs_out = (String) objs[j];

				try {
					
					bw.write(objs_out);
					bw.newLine();
					bw.flush(); // �M�Žw�İ�
					

				} catch (IOException f) {
					// TODO Auto-generated catch block
					f.printStackTrace();
				}

			
		}
		bw.close(); // ����BufferedWriter����
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	// TODO Auto-generated method stub
	public static void main(int concept_id,int topic_id) throws IOException {
		//stemming(concept_id,topic_id);
	}

	public static void main(String args[]) throws IOException {
		// TODO Auto-generated method stub
		//stemming(3,2);
	}

}
