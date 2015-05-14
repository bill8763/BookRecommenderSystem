package user_k_core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import javax.swing.text.html.HTMLEditorKit;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;



//import Google_Search1.ParserGetter;
public class Lucene_Search2 extends HTMLEditorKit.ParserCallback {
	private BufferedReader bufferedStream;

	@SuppressWarnings("serial")
	class ParserGetter extends HTMLEditorKit {
		// purely to make this methods public
		public HTMLEditorKit.Parser getParser() {
			return super.getParser();
		}
	}


	public Lucene_Search2() {
	}

	public void doit(int user_id, int concept_id,int topic_id) throws Exception {
		// try {
		
		ServerUtil.initialize();
		// initialize query
		SolrQuery query = new SolrQuery();
		//show info of all files
		
		File f = new File("D:/DataTemp/User_K-core/Number_of_pair/" +user_id +"_" +concept_id + "_" + topic_id+ "_number_of_pair.txt");
		f.delete();
		FileReader FileStream = new FileReader("D:/DataTemp/User_K-core/Number_of_term/"+user_id +"_" +concept_id+ "_" +topic_id +"_number_of_term.txt");
		bufferedStream = new BufferedReader(FileStream);
		String line;
		ArrayList<String> term = new ArrayList<String>();
		while ((line = bufferedStream.readLine()) != null) {
			term.add(line.split(",")[0]);
		}
		Object[] obj = term.toArray();
		BufferedWriter bw = new BufferedWriter(new FileWriter("D:/DataTemp/User_K-core/Number_of_pair/" + user_id +"_" +concept_id + "_" + topic_id+ "_number_of_pair.txt", false));
		for (int i = 0; i < obj.length; i++) {
			for (int j = i + 1; j < obj.length; j++) {
				System.out.println(obj[i] + "+" + obj[j]);
				String queryword = "+"+ obj[i] + "+" + obj[j]; 
				query.setQuery(queryword);
				QueryResponse rsp = ServerUtil.execQuery(query);
				SolrDocumentList docs = rsp.getResults();
				
				bw.write(obj[i] + "+" + obj[j]+","+docs.getNumFound());
				bw.newLine();
				bw.flush(); // 清空緩衝區
			}
		}

		bw.close();
	}

	public static void main(int user_id,int concept_id,int topic_id) throws Exception {
		new Lucene_Search2().doit(user_id,concept_id,topic_id);

	}

	public static void main(String args[]) throws Exception {

		long StartTime = System.currentTimeMillis(); // 取出目前時間
		main(1,3,2);
		System.out.println(System.currentTimeMillis() - StartTime);

	}
}