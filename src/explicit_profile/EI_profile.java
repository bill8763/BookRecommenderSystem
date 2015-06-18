package explicit_profile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class EI_profile extends database.DBconnect{

	public EI_profile() throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public void EI(String user_id,double phi) throws Exception{
		//�p��RF
		relevance_factor test = new relevance_factor();
		test.user_article_concept(user_id); //��Juser_id�A���X����v��
		//�p��UIA
		user_in_article uia_test = new user_in_article();
		uia_test.UIA(user_id, phi);
		
		FileReader FileStream1;
		FileStream1 = new FileReader("Relevance_Factor/"+user_id + "_RF.txt");
		@SuppressWarnings("resource")
		BufferedReader BufferedStream1 = new BufferedReader(FileStream1);
		
		FileReader FileStream2;
		FileStream2 = new FileReader("Explicit_profile/"+user_id + "_UIA.txt");
		@SuppressWarnings("resource")
		BufferedReader BufferedStream2 = new BufferedReader(FileStream2);
		
		String line1 = "";
		String line2 = "";
		
		Map<Integer, Double> UIA_map = new HashMap<Integer, Double>();	
		
		PreparedStatement select_article = null;
		select_article = getConn().prepareStatement("select * from concept_article where concept_id = ? and topic_id = ?");
		
		while((line2 = BufferedStream2.readLine()) != null){
			int key= Integer.parseInt(line2.split(";")[0]);
			double value = Double.parseDouble(line2.split(";")[1]);
			UIA_map.put(key, value);
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter("Explicit_profile/"+user_id + "_EI.txt"));
		
		while((line1 = BufferedStream1.readLine()) != null){
			int concept_id = Integer.parseInt(line1.split(";")[0].split(",")[0]);
			int topic_id = Integer.parseInt(line1.split(";")[0].split(",")[1]);
			double rf = Double.parseDouble(line1.split(";")[1]);
			select_article.setInt(1, concept_id);
			select_article.setInt(2, topic_id);
			ResultSet article_rs = select_article.executeQuery();
			double all_UIA = 0;
			while(article_rs.next()){
				int key = article_rs.getInt("article_id");
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

	
	
	public void norm_EI(int user_id) throws IOException{
		FileReader FileStream1;
		FileStream1 = new FileReader("Explicit_profile/"+user_id + "_EI.txt");
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
		BufferedWriter bw = new BufferedWriter(new FileWriter("Explicit_profile/"+user_id + "_norm_EI.txt"));
		
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
	
	public static void main(String[] args) throws Exception {
		EI_profile EI_test = new EI_profile();
		
		EI_test.EI("A14OJS0VWMOSWO", 1); 
		EI_test.norm_EI(1); //�����W��
	}

}
