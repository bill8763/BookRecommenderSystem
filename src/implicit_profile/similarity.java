package implicit_profile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ontology.DBconnect;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import concept_k_core.ServerUtil;
import explicit_profile.relevance_factor;

public class similarity extends DBconnect{
	public similarity() throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		super();
		// TODO Auto-generated constructor stub
	}
	public void concept_matching(int concept_id) throws Exception{
		PreparedStatement select_topic = null;
		select_topic = conn.prepareStatement("select COUNT(*) size from ontology where concept_id = ?");
		select_topic.setInt(1, concept_id); 
		ResultSet topics = select_topic.executeQuery();
		topics.next();
		int topic_size = topics.getInt("size");
		System.out.println(topic_size);
		
		PreparedStatement insert_sim = null;
		insert_sim = conn.prepareStatement("insert into topic_similarity"+ "(concept_id,topic_id,topic2_id,similarity)"
				 +"values ( ?, ?, ?, ? )");
		for(int i=1;i<=topic_size;i++){
			for(int j=i+1;j<=topic_size;j++){
				double topic_sim = sim(concept_id,i,j); //計算主題相似度
				insert_sim.setInt(1,concept_id);
				insert_sim.setInt(2,i);
				insert_sim.setInt(3,j);
				insert_sim.setDouble(4, topic_sim);
				insert_sim.executeUpdate(); 
			}
		}
	
	}
	public double sim(int concept_id,int topic_id,int topic2_id) throws Exception{
		ServerUtil.initialize();
		// initialize query
		SolrQuery query = new SolrQuery();
		//show info of all files
		relevance_factor rf_test = new relevance_factor();
		File file1= new  File( "Concept_K-core//Main_word//"+concept_id+"_"+topic_id+"_main_word.txt" ); 
		File file2= new  File( "Concept_K-core//Main_word//"+concept_id+"_"+topic2_id+"_main_word.txt" ); 
		//判斷topic是否有建立過K-core，若沒有則建立
		if(!file1.exists()){
			rf_test.concept_weight(concept_id, topic_id);
		}
		if(!file2.exists()){
			rf_test.concept_weight(concept_id, topic2_id);
		}
		
		FileReader FileStream1 = new FileReader(file1);
		FileReader FileStream2 = new FileReader(file2);
		FileReader FileStream3 = new FileReader("TagTree//"+concept_id+"_File//NGDResult.txt");

		@SuppressWarnings("resource")
		BufferedReader BufferedStream1 = new BufferedReader(FileStream1);
		@SuppressWarnings("resource")
		BufferedReader BufferedStream2 = new BufferedReader(FileStream2);
		@SuppressWarnings("resource")
		BufferedReader BufferedStream3 = new BufferedReader(FileStream3);  
		
		ArrayList<String> mainword_list1 = new ArrayList<String>();
		ArrayList<String> mainword_list2 = new ArrayList<String>();
		Map<String, Double> ngd_map = new HashMap<String, Double>();	
		
		String line1 = "";
		String line2 = "";
		String line3 = "";
		while ((line1 = BufferedStream1.readLine()) != null) {
			mainword_list1.add(line1.split(",")[0]+","+line1.split(",")[1]);
		}
		while ((line2 = BufferedStream2.readLine()) != null) {
			mainword_list2.add(line2.split(",")[0]+","+line2.split(",")[1]);
		}
		while((line3 = BufferedStream3.readLine()) != null){
			String key = line3.split(",")[0]+"+"+line3.split(",")[1];
			double value = Double.parseDouble(line3.split(",")[2]);
			ngd_map.put(key, value);
		}
		double total_ngd = 0;
		int pair = 0;
		System.out.println(ngd_map);
		for(int i=0;i<mainword_list1.size();i++){
			for(int j=0;j<mainword_list2.size();j++){
				String key1 = mainword_list1.get(i).split(",")[0].replace("+", " ")+"+"+mainword_list2.get(j).split(",")[0].replace("+", " ");
				String key2 = mainword_list2.get(j).split(",")[0].replace("+", " ")+"+"+mainword_list1.get(i).split(",")[0].replace("+", " ");
				System.out.println(key1);
				System.out.println(key2);
				if(ngd_map.get(key1)!=null){
					System.out.println(ngd_map.get(key1));
					total_ngd = total_ngd+ngd_map.get(key1);
					pair = pair+1;
				}
				else if(ngd_map.get(key2)!=null){
					System.out.println(ngd_map.get(key2));
					total_ngd = total_ngd+ngd_map.get(key2);
					pair = pair+1;
				}
				else{
					String queryword = "+" + "\"" + mainword_list1.get(i).split(",")[0] +"\""+"+"+"\""+mainword_list2.get(j).split(",")[0]+"\"";
					query.setQuery(queryword);
					QueryResponse rsp = ServerUtil.execQuery(query);
					SolrDocumentList docs = rsp.getResults();
					double x = Double.parseDouble(mainword_list1.get(i).split(",")[1]);
					double y = Double.parseDouble(mainword_list2.get(j).split(",")[1]);
					double m = docs.getNumFound();
					double NGD = NGD_cal(x,y,m);
					if(Double.isInfinite(NGD))
						NGD = 1;
					total_ngd = total_ngd + NGD;
					pair = pair+1;
				}
					
			}
		}
		
		double avg_link = 1-(total_ngd/pair);
		System.out.println(avg_link);
		return avg_link;
	}
	
	static double NGD_cal(double x, double y, double m) {
		double logX = Math.log10(x);
		double logY = Math.log10(y);
	
		double logM = Math.log10(m);
		double logN = 6.4966;
		double NGD = 0;
		
			NGD = (Math.max(logX, logY) - logM) / (logN - Math.min(logX, logY));
		
		return NGD;
	}
	public static void main(String[] args) throws Exception {
		similarity sim_test = new similarity();
		for(int i=1;i<=38;i++){
			//sim_test.concept_matching(i);   //要算一陣子
		}
		
	}
	
}