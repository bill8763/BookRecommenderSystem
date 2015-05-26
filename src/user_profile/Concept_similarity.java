package user_profile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ontology.DBconnect;

public class Concept_similarity extends DBconnect{
	public Concept_similarity() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		super();
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("resource")
	public void concept_weight(int concept_id,int topic_id) throws Exception{
		
		//�M��D�D�U���峹
		PreparedStatement select_acticle = null;
		select_acticle = getConn().prepareStatement("select * from concept_article where concept_id = ? and topic_id = ?");
		select_acticle.setInt(1, concept_id); 
		select_acticle.setInt(2, topic_id); 
		ResultSet articlers = select_acticle.executeQuery();
		ArrayList<String> article_list = new ArrayList<String>();
		while(articlers.next()){
			article_list.add(articlers.getString("article_id"));
		}
		System.out.println(article_list); 
		
		//�p��h�峹�@�P�S�x
		Set<String> term_set = new HashSet<String>(); //�s�J(term,�j�M��)
		Map<String, Integer> term_map = new HashMap<String, Integer>(); //�p��term�b�h�ֽg���X�{
		
		for(int i=0;i<article_list.size();i++){
			FileReader FileStream;
			FileStream = new FileReader("D:/K-core/Main_word/"+article_list.get(i)+"_main_word.txt");
			BufferedReader BufferedStream = new BufferedReader(FileStream);
			String line = "";
			
			while ((line = BufferedStream.readLine()) != null) {
			//	line = line.replace("+"," "); 
				line = "\""+ line.split(",")[0]+"\""+","+line.split(",")[1];
				term_set.add(line); //(term,�j�M��)
				if(term_map.get(line.split(",")[0])!=null){
					int terms = term_map.get(line.split(",")[0]);
					int f_terms = terms + 1;
					term_map.put(line.split(",")[0],f_terms);
				}
				else{
					term_map.put(line.split(",")[0],0);
				}
				
			}
		}
		System.out.println(term_set); 
		System.out.println(term_map); 
		BufferedWriter bw;
		bw = new BufferedWriter(new FileWriter("D:/DataTemp/Concept_K-core/Number_of_term/"+concept_id+ "_" +topic_id +"_number_of_term.txt", false));
		for(String term: term_set){
			bw.write(term);
			bw.newLine();
			bw.flush(); // �M�Žw�İ�
		}
		
		//k-core�B�z
		new concept_k_core.Lucene_Search2().doit(concept_id,topic_id);
		concept_k_core.Stem.stemming(concept_id,topic_id);
		concept_k_core.NGD_calculate.NGD(concept_id,topic_id);
		concept_k_core.Result_Rank.ranking(concept_id,topic_id);
		concept_k_core.K_core kcore= new concept_k_core.K_core();
		kcore.K_core_cal(concept_id,topic_id);
		
		//�p��strength
		strength s_test = new strength();
		s_test.concept_strength(concept_id, topic_id);
		
		//�p���v��strength*0.5+frequency*0.5
		FileReader FileStream2;	
		Map<String, Double> weight_map = new HashMap<String,Double>(); //�p��term�b�h�ֽg���X�{
		FileStream2 = new FileReader("D:/DataTemp/Concept_K-core/Strength/"+concept_id + "_" + topic_id + "_strength.txt");
		BufferedReader BufferedStream2 = new BufferedReader(FileStream2);
		String line2 = "";
		while ((line2 = BufferedStream2.readLine()) != null) {
			
			String key = "\""+ line2.split(",")[0]+"\"";
			double weight = 0.0;
			if(term_map.get(key)!=null){
				weight = term_map.get(key)*0.5+Double.parseDouble(line2.split(",")[1])*0.5; //�p��̫��v��
				weight_map.put(key, weight);
			}	
		}
		//�Ƨ�
		List<Map.Entry<String, Double>> list_Data = new ArrayList<Map.Entry<String, Double>>(
				weight_map.entrySet());
		Iterator<Map.Entry<String, Double>> iterator = list_Data.iterator();

		Collections.sort(list_Data,
				new Comparator<Map.Entry<String, Double>>() {
					public int compare(Map.Entry<String, Double> o1,
							Map.Entry<String, Double> o2) {
						return (int) ((o2.getValue() - o1.getValue()) * 1000.0);
					}
				});
		
		File file=new File("D:/DataTemp/Concept_K-core/Weight/"+concept_id + "_" + topic_id +"_weight.txt");
		file.delete();
		BufferedWriter bw2 = new BufferedWriter(new FileWriter("D:/DataTemp/Concept_K-core/Weight/"+concept_id + "_" + topic_id+ "_weight.txt"));
			

		while (iterator.hasNext()) {
			Map.Entry<String, Double> entry = iterator.next();
			System.out.println(entry.getKey() + "," + entry.getValue()); //���W��
			bw2.write(entry.getKey() + "," + entry.getValue());
//			rel_loader.rel_map.put(entry.getKey(), entry.getValue());
			bw2.newLine();
		}
		bw2.flush();
		bw2.close();
	}
	
	@SuppressWarnings("resource")
	public void user_weight(int user_id, int concept_id, int topic_id, String article) throws Exception{
		String [] user_article = article.split(",");
		Set<String> term_set = new HashSet<String>(); //�s�J(term,�j�M��)
		Map<String, Integer> term_map = new HashMap<String, Integer>(); //�p��term�b�h�ֽg���X�{
		//�p��峹�@�P�S�x
		for(int i=0;i<user_article.length;i++){
			FileReader FileStream;
			FileStream = new FileReader("D:/K-core/Main_word/"+user_article[i]+"_main_word.txt");
			BufferedReader BufferedStream = new BufferedReader(FileStream);
			String line = "";
			
			while ((line = BufferedStream.readLine()) != null) {
			//	line = line.replace("+"," "); 
				line = "\""+ line.split(",")[0]+"\""+","+line.split(",")[1];
				term_set.add(line); //(term,�j�M��)
				if(term_map.get(line.split(",")[0])!=null){
					int terms = term_map.get(line.split(",")[0]);
					int f_terms = terms + 1;
					term_map.put(line.split(",")[0],f_terms);
				}
				else{
					term_map.put(line.split(",")[0],0);
				}
				
			}
		}
		
		System.out.println(term_set); 
		System.out.println(term_map); 
		BufferedWriter bw;
		bw = new BufferedWriter(new FileWriter("D:/DataTemp/User_K-core/Number_of_term/"+user_id+"_"+concept_id+ "_" +topic_id +"_number_of_term.txt", false));
		for(String term: term_set){
			bw.write(term);
			bw.newLine();
			bw.flush(); // �M�Žw�İ�
		}
		
		//�p��k-core
		new user_k_core.Lucene_Search2().doit(user_id,concept_id,topic_id);
		//google_filter2.search_filter(i);
		user_k_core.Stem.stemming(user_id,concept_id,topic_id);
		user_k_core.NGD_calculate.NGD(user_id,concept_id,topic_id);
		user_k_core.Result_Rank.ranking(user_id,concept_id,topic_id);
		user_k_core.K_core kcore = new user_k_core.K_core();
		kcore.K_core_cal(user_id,concept_id,topic_id);
		
	}
	
	public void user_article_concept(int user_id) throws Exception{
		//�d��user�ݹL���峹
		PreparedStatement select_UserActicle = null;
		select_UserActicle = getConn().prepareStatement("select * from behavior where user_id = ?");
		select_UserActicle.setInt(1, user_id); 
		ResultSet Uarticlers = select_UserActicle.executeQuery();
		ArrayList<String> article_list = new ArrayList<String>();
		while(Uarticlers.next()){
			article_list.add(Uarticlers.getString("article_id"));
		}
		System.out.println(article_list); 
		
		//�d�ߤ峹��concept
		Map<String, String> user_concept_map = new HashMap<String, String>(); //�p��term�b�h�ֽg���X�{
		PreparedStatement selsect_UserConcept = null;
		selsect_UserConcept = getConn().prepareStatement("select * from concept_article where article_id = ?");
		
		for(int i=0 ;i<article_list.size();i++){
			String article_id = article_list.get(i);
			selsect_UserConcept.setString(1, article_id);
			ResultSet Uconceptrs = selsect_UserConcept.executeQuery();
			while(Uconceptrs.next()){
				String concept_id = Uconceptrs.getString("concept_id");
				String topic_id = Uconceptrs.getString("topic_id");
				String key = concept_id+","+topic_id;
				if(user_concept_map.get(key)!=null){
					String value = user_concept_map.get(key) + "," +article_id;
					user_concept_map.put(key, value);
				}
				else{
					user_concept_map.put(key, article_id);
				}	
			}	
		}
		
		System.out.println(user_concept_map); 
		
		List<Map.Entry<String, String>> list_Data = new ArrayList<Map.Entry<String,String>>(
				user_concept_map.entrySet());
		Iterator<Map.Entry<String, String>> iterator = list_Data.iterator();
		
		
		BufferedWriter bw = new BufferedWriter(new FileWriter("D:/DataTemp/Concept_similarity/"+user_id + "_ConSim.txt"));
		
		
		while (iterator.hasNext()) {
			Map.Entry<String, String> entry = iterator.next();
			int concept_id =Integer.parseInt(entry.getKey().split(",")[0]) ;
			int topic_id = Integer.parseInt(entry.getKey().split(",")[1]);
			String articles = entry.getValue();
			System.out.println(entry.getKey() + "," + entry.getValue());
			
			//�p��concept�v���Puser_concept�v��
			File file1= new  File( "D:/DataTemp/Concept_K-core//Weight//"+concept_id+"_"+topic_id+"_weight.txt" ); 
			if(!file1.exists()){
				concept_weight(concept_id,topic_id);
			}
			File file2= new  File( "D:/DataTemp/User_K-core//Weight//"+concept_id+"_"+topic_id+"_weight.txt" ); 
			if(!file2.exists()){
				user_weight(user_id,concept_id,topic_id,articles);
			}
			
			
			//�p��RF
			double avg_link = RF(user_id,concept_id,topic_id);
			bw.write(concept_id+","+topic_id+";"+avg_link);
			bw.newLine();
		}
		bw.flush();
		bw.close();	
		
	}
	
	public double RF(int user_id,int concept_id,int topic_id) throws IOException{
		FileReader FileStream1;
		FileReader FileStream2;
		FileReader FileStream5;
		
		FileStream1 = new FileReader("D:/DataTemp/User_K-core/Main_word/"+user_id+"_"+concept_id + "_" + topic_id+ "_main_word.txt");
		@SuppressWarnings("resource")
		BufferedReader BufferedStream1 = new BufferedReader(FileStream1);
		FileStream2 = new FileReader("D:/DataTemp/Concept_K-core/Weight/"+concept_id + "_" + topic_id +"_weight.txt");
		@SuppressWarnings("resource")
		BufferedReader BufferedStream2 = new BufferedReader(FileStream2);

		
		String line1 = "";
		String line2 = "";
		int user_count = 0;
		int concept_count = 0;
		int N_weight = 10; //�]�w���X�ӯS�x
		ArrayList<String> concept_list = new ArrayList<String>();
		ArrayList<String> user_list = new ArrayList<String>();
		
		while ((line1 = BufferedStream1.readLine()) != null) {
			user_list.add(line1);
			user_count++;
		}
		while ((line2 = BufferedStream2.readLine()) != null) {
			if(concept_count<N_weight)
				concept_list.add(line2);
			else
				break;
			concept_count++;
		}
		
	
		FileStream5 = new FileReader("D:/DataTemp/Concept_K-core/NGD/"+concept_id + "_" + topic_id+ "_nNGD.txt");
		@SuppressWarnings("resource")
		BufferedReader BufferedStream5 = new BufferedReader(FileStream5);	
		String line5 = "";
	
		Map<String, Double> ngd_map = new HashMap<String, Double>();		
		
		while ((line5 = BufferedStream5.readLine()) != null) {
			String key = line5.split(";")[0];
			Double value = Double.parseDouble(line5.split(";")[1]);
			ngd_map.put(key, value);
		}
		double total_ngd = 0;
		int pair = 0;
		
		for(int i=0 ;i<user_list.size();i++){
			for(int j=0; j<concept_list.size();j++){
				String key1 = user_list.get(i).split(",")[0].replace("\"", "")+","+concept_list.get(j).split(",")[0].replace("\"", "");
				String key2 = concept_list.get(j).split(",")[0].replace("\"", "")+","+ user_list.get(i).split(",")[0].replace("\"", "");
				
				if(ngd_map.get(key1)!= null){
					if( ngd_map.get(key1).isInfinite()){
						total_ngd = total_ngd+1;
					}
					else{
						total_ngd = total_ngd+ngd_map.get(key1);
					}
					
					pair++;
				}
				else if( ngd_map.get(key2)!=null){
					if( ngd_map.get(key2).isInfinite()){
						total_ngd = total_ngd+1;
					}
					else{
						total_ngd = total_ngd+ngd_map.get(key2);
					}
					pair++;
				}
				else if(key1.equals(key2)){
					total_ngd = total_ngd+0;
					pair++;
				}
			}
		}
		double avg_link = 1-(total_ngd/pair);
		System.out.println(avg_link);
		return avg_link;
	}
	
	public static void main(String[] args) throws Exception {
		Concept_similarity test = new Concept_similarity();
		test.user_article_concept(2); //��Juser_id�A���X����v��
	//	test.concept_weight(3, 2);
	//  test.user_weight(1, 3, 2, "3032,3080,3082");
	//	test.RF(1, 3, 74);

	}
}
