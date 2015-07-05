package explicit_profile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import main.recommend;
import main.validation;
import database.DBconnect;


public class EI_profile extends database.DBconnect{

	public EI_profile() throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		super();
		// TODO Auto-generated constructor stub
	}
	public static void main(String[] args) throws Exception {
		EI_profile EI_test = new EI_profile();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		String startDateString = "2015/08/01";
		Date startDate = dateFormat.parse(startDateString);
		long startTime = (long) startDate.getTime();
		String endDateString = "2015/08/14";
		Date endDate = dateFormat.parse(endDateString);
		long endTime = (long) endDate.getTime();

		while (startTime < endTime) {
			SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
			Timestamp time = new Timestamp(startTime);
			String rateday = ft.format(time);
			EI_test.EI("exp3", 1,Long.toString(startTime)); 
			EI_test.norm_EI("exp3", Long.toString(startTime)); 
			profile_to_database ei_test = new profile_to_database();			
			ei_test.EI_to_database("exp3",rateday);
			String toRecommendDocDir="D:/dataset/Explicit_profile/recommendation/";
			recommend.EIRecommendDoc("exp3","D:/dataset/Explicit_profile/recommendation/"+rateday+"exp3.txt");
			String correctPath =  "D:/dataset/exp3/5/";
			String outputPath = toRecommendDocDir+"exp3_5.txt";
			validation.recommValidation("exp3", toRecommendDocDir+rateday+"exp3.txt", Long.toString(startTime),correctPath,outputPath);
			correctPath="D:/dataset/exp3/6/";
			outputPath = toRecommendDocDir+"exp3_6.txt";
			validation.recommValidation("exp3", toRecommendDocDir+rateday+"exp3.txt",  Long.toString(startTime),correctPath,outputPath);
			correctPath="D:/dataset/exp3/7/";
			outputPath = toRecommendDocDir+"exp3_7.txt";
			validation.recommValidation("exp3", toRecommendDocDir+rateday+"exp3.txt",  Long.toString(startTime),correctPath,outputPath);
			correctPath="D:/dataset/exp3/10/";
			outputPath = toRecommendDocDir+"exp3_10.txt";
			validation.recommValidation("exp3", toRecommendDocDir+rateday+"exp3.txt",  Long.toString(startTime),correctPath,outputPath);
			startTime = startTime + 24 * 60 * 60 * 1000;
		}
	}
	
	public void EI(String user_id,double phi,String processingTime) throws Exception{
		//�p��RF
		relevance_factor test = new relevance_factor();
		test.user_article_concept(user_id); //��Juser_id�A���X����v��
		//�p��UIA
		user_in_article uia_test = new user_in_article();
		uia_test.UIA(user_id, phi,processingTime);
		
		FileReader FileStream1;
		FileStream1 = new FileReader("D:/dataset/Relevance_Factor/"+user_id + "_RF.txt");
		@SuppressWarnings("resource")
		BufferedReader BufferedStream1 = new BufferedReader(FileStream1);
		
		FileReader FileStream2;
		FileStream2 = new FileReader("D:/dataset/Explicit_profile/"+user_id + "_UIA.txt");
		@SuppressWarnings("resource")
		BufferedReader BufferedStream2 = new BufferedReader(FileStream2);
		
		String line1 = "";
		String line2 = "";
		
		Map<String, Double> UIA_map = new HashMap<>();	
		new DBconnect();
		PreparedStatement select_article = null;
		select_article = getConn().prepareStatement("select * from concept_userarticle where concept_id = ? and topic_id = ?");
		
		while((line2 = BufferedStream2.readLine()) != null){
			String key= line2.split(";")[0];
			double value = Double.parseDouble(line2.split(";")[1]);
			UIA_map.put(key, value);
		}
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
		Timestamp time = new Timestamp(Long.parseLong(processingTime));
		String rateday = ft.format(time);
		BufferedWriter bw = new BufferedWriter(new FileWriter("D:/dataset/Explicit_profile/"+user_id +"_"+rateday+ "_EI.txt"));
		
		while((line1 = BufferedStream1.readLine()) != null){
			int concept_id = Integer.parseInt(line1.split(";")[0].split(",")[0]);
			int topic_id = Integer.parseInt(line1.split(";")[0].split(",")[1]);
			double rf = Double.parseDouble(line1.split(";")[1]);
			select_article.setString(1, Integer.toString(concept_id));
			select_article.setString(2, Integer.toString(topic_id));
			ResultSet article_rs = select_article.executeQuery();
			double all_UIA = 0;
			while(article_rs.next()){
				String key = article_rs.getString("article_id");
				if(UIA_map.get(key)!=null){
					all_UIA = all_UIA+UIA_map.get(key);
				}
			}
			
			double EI = (double)rf*all_UIA;
			//System.out.println(line1.split(";")[0]+";"+EI);
			bw.write(line1.split(";")[0]+";"+EI);
			bw.newLine();
		}
		bw.flush();
		bw.close();	
		getConn().close();
	}

	
	
	public void norm_EI(String user_id, String processingTime) throws IOException{
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
		Timestamp time = new Timestamp(Long.parseLong(processingTime));
		String rateday = ft.format(time);
		FileReader FileStream1;
		FileStream1 = new FileReader("D:/dataset/Explicit_profile/"+user_id +"_"+rateday+ "_EI.txt");
		@SuppressWarnings("resource")
		BufferedReader BufferedStream1 = new BufferedReader(FileStream1);
		String line1 = "";
		ArrayList<String> EI_list = new ArrayList<String>();
		//�p��total_EI
		double max_EI = 0;
		while((line1 = BufferedStream1.readLine()) != null){
			double EI = Double.parseDouble(line1.split(";")[1]);
			EI_list.add(line1);
			if(EI>max_EI)
				max_EI = EI;
		}
		//�p�⥿�W�ƫ�EI
		System.out.println("�̤j���G"+max_EI);
		BufferedWriter bw = new BufferedWriter(new FileWriter("D:/dataset/Explicit_profile/"+user_id +"_"+rateday+ "_norm_EI.txt"));
		
		for(int i=0;i<EI_list.size();i++){
			double EI = Double.parseDouble(EI_list.get(i).split(";")[1]);
			double fin_EI = (double)EI/max_EI;
			System.out.println(fin_EI);
			
			bw.write(EI_list.get(i).split(";")[0]+";"+fin_EI);
			bw.newLine();
		}
		bw.flush();
		bw.close();	
	
	}
	


}
