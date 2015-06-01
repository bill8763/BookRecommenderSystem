package concept_k_core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import javax.swing.text.html.HTMLEditorKit;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import tw.edu.ncu.im.Util.HttpIndexSearcher;
import tw.edu.ncu.im.Util.IndexSearchable;

//import Google_Search1.ParserGetter;
public class Lucene_Search2  {
	private  static BufferedReader bufferedStream;



	public Lucene_Search2() {

	}

	public static void doit(int concept_id, int topic_id, String path, HttpIndexSearcher searcher)
			throws Exception {
		File test = new File(path+"Number_of_pair/");
		if (!test.exists()) {
			test.mkdirs();
		}
		File f = new File(path + "Number_of_pair/" + concept_id + "_"
				+ topic_id + "_number_of_pair.txt");
		f.delete();
		FileReader FileStream = new FileReader(path + "Number_of_term/"
				+ concept_id + "_" + topic_id + "_number_of_term.txt");
		bufferedStream = new BufferedReader(FileStream);
		String line;
		ArrayList<String> term = new ArrayList<String>();
		while ((line = bufferedStream.readLine()) != null) {
			term.add(line.split(",")[0]);
		}
		Object[] obj = term.toArray();
		BufferedWriter bw = new BufferedWriter(new FileWriter(path
				+ "Number_of_pair/" + concept_id + "_" + topic_id
				+ "_number_of_pair.txt", false));
				long SearchResult = 0;		
		for (int i = 0; i < obj.length; i++) {
			for (int j = i + 1; j < obj.length; j++) {
					String temp1 = obj[i].toString().replace("\"", "");
					String temp2 = obj[j].toString().replace("\"", "");
				try {
					SearchResult = searcher
							.searchMultipleTerm(new String[] {temp1,temp2});
				} catch (SolrServerException e) {// Retry
					try {
						SearchResult = searcher
								.searchMultipleTerm(new String[] {temp1,temp2});
					} catch (SolrServerException e1) {
						e1.printStackTrace();
					}
				}
					bw.write(obj[i] + "+" + obj[j] + "," + SearchResult);
					bw.newLine();
					bw.flush();				
			}
		}
			bw.close();		
	}

	public static void main(int concept_id, int topic_id) throws Exception {
		// new Lucene_Search2().doit(concept_id,topic_id);

	}

	public static void main(String args[]) throws Exception {

		long StartTime = System.currentTimeMillis(); // ���X�ثe�ɶ�
		main(3, 2);
		System.out.println(System.currentTimeMillis() - StartTime);

	}
}