package user_k_core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
	public static void stemming(int user_id,int concept_id,int topic_id) throws IOException {
		FileReader FileStream = new FileReader("D:/DataTemp/User_K-core/Number_of_term/"+user_id +"_" +concept_id + "_"+topic_id + "_number_of_term.txt");
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
		
		Object[] objs = set.toArray();
		BufferedWriter bw;
		bw = new BufferedWriter(new FileWriter("D:/DataTemp/User_K-core/Stem/"+user_id +"_" +concept_id + "_" +topic_id + "_stem.txt", false));
		for (int j = 0; j < objs.length; j++) {
			String objs_out = (String) objs[j];

				try {
					
					bw.write(objs_out);
					bw.newLine();
					bw.flush(); // 清空緩衝區
					

				} catch (IOException f) {
					// TODO Auto-generated catch block
					f.printStackTrace();
				}

			
		}
		bw.close(); // 關閉BufferedWriter物件
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	// TODO Auto-generated method stub
	public static void main(int user_id,int concept_id,int topic_id) throws IOException {
		stemming(user_id,concept_id,topic_id);
	}

	public static void main(String args[]) throws IOException {
		// TODO Auto-generated method stub
		stemming(1,3,2);
	}

}
