package explicit_profile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


public class Experimental_EI extends database.DBconnect{
	public Experimental_EI() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		super();
		// TODO Auto-generated constructor stub
	}
	public void ex_EI(int user_id,double max) throws IOException, SQLException{
		FileReader FileStream1;
		FileStream1 = new FileReader("D:/dataset/Explicit_profile/"+user_id + "_norm_EI.txt");
		//�i�H�ϥΥ��W�ƹL��@@`
		@SuppressWarnings("resource")
		BufferedReader BufferedStream1 = new BufferedReader(FileStream1);
	
		ArrayList<String> endrf_list = new ArrayList<String>();
		String line1 = "";
		
		while ((line1 = BufferedStream1.readLine()) != null) {
			double rf= Double.parseDouble(line1.split(";")[1]);
			
			if(rf>max){
				//System.out.println(line1);
				endrf_list.add(line1);
			}
		}
		
		HashSet<String> article_set = new HashSet<String>();
		
		for(int i=0;i<endrf_list.size();i++){
			int concept_id = Integer.parseInt(endrf_list.get(i).split(";")[0].split(",")[0]);
			int topic_id = Integer.parseInt(endrf_list.get(i).split(";")[0].split(",")[1]);
			PreparedStatement select_acticle = null;
			select_acticle = getConn().prepareStatement("select * from concept_article where concept_id = ? and topic_id = ?");
			select_acticle.setInt(1, concept_id); 
			select_acticle.setInt(2, topic_id); 
			ResultSet articlers = select_acticle.executeQuery();
			
			while(articlers.next()){
				article_set.add(articlers.getString("article_id"));
			}
		}
		//System.out.println(article_set);
		
		FileReader FileStream2;
		FileStream2 = new FileReader("D:/dataset/Explicit_profile/data_7.txt");
		@SuppressWarnings("resource")
		BufferedReader BufferedStream2 = new BufferedReader(FileStream2);
		FileReader FileStream3;
		FileStream3 = new FileReader("D:/dataset/Explicit_profile/ans_7.txt");
		@SuppressWarnings("resource")
		BufferedReader BufferedStream3 = new BufferedReader(FileStream3);
		
		Map<String, String> data_map = new HashMap<String, String>(); 
		Map<String, String> ans_map = new HashMap<String, String>();
		
		String line2 = "";
		String line3 = "";
		while((line2 = BufferedStream2.readLine()) != null){
			data_map.put(line2, line2);
		}
		while((line3 = BufferedStream3.readLine()) != null){
			ans_map.put(line3, line3);
		}
		
		int result = 0;
		int correct = ans_map.size();
		int tp = 0;
		Object[] datas = article_set.toArray();
		for(int i = 0;i<datas.length;i++){
			if(data_map.get(datas[i])==null){
				result++;
				if(ans_map.get(datas[i])!=null){
					tp++;
				}
			}
		}
		
		double precision = (double)tp/result;
		double recall = (double)tp/correct;
		
		double fmeasure = (2*precision*recall) / (precision+recall);
		
		System.out.println(" precision��.. " + precision);
		System.out.println(" recall��.. " + recall);
		System.out.println(" Fmeasure��.. " + fmeasure);
		
		
		
	}
	public static void main(String[] args) throws Exception {
		Experimental_EI test = new Experimental_EI();
		double[] rf = {0,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9};
		//double[] rf = {0.1};
		for(int i=0;i<rf.length;i++){
			System.out.println("�ثe���e�Ȭ��G" + rf[i]);
			test.ex_EI(1,rf[i]);
		}

	}
}
