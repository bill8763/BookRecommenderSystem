package user_k_core;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

import main.findUserRatingInformation;
import tw.edu.ncu.im.Util.HttpIndexSearcher;
import database.DBconnect;



public class Main_kcore {
	
		static String path = "D:/dataset/User_K-core/";

		public void kcore(String user_id,int concept_id,int topic_id) throws Exception{
			File test = new File(path);
			if (!test.exists()) {
				test.mkdirs();
			}
			HttpIndexSearcher searcher = new HttpIndexSearcher();
			searcher.url = "http://140.115.82.105/searchweb/";
			new prepocess();
			prepocess.mainwordPath = "D:/dataset/userMainWords/";
			prepocess.path = path;
			prepocess.getUserConceptTerms(user_id,concept_id, topic_id);
			new Lucene_Search2().doit(user_id,concept_id,topic_id,path,searcher);
			//google_filter2.search_filter(i);
			Stem.stemming(user_id,concept_id,topic_id, path);
			NGD_calculate.NGD(user_id,concept_id,topic_id, path);
			Result_Rank.ranking(user_id,concept_id,topic_id, path);
			K_core kcore = new K_core(path);
			kcore.K_core_cal(user_id,concept_id,topic_id);
			
	}
	
	@SuppressWarnings("resource")
	public static void main(String args[]) throws Exception{
//    	System.out.println("K-core Running");
//		Scanner sc = new Scanner(System.in);
//		System.out.print("user_id");					
//		int user_id = sc.nextInt();
//		System.out.print("concept_id");					
//		int concept_id = sc.nextInt();					// �}�l�s��
//		System.out.print("topic_id:");					
//		int topic_id = sc.nextInt();	
//		Main_kcore m_kcore = new Main_kcore();
//		m_kcore.kcore(user_id,concept_id, topic_id);
//		System.out.println("finish!!!");
		Main_kcore m_kcore = new Main_kcore();
		PreparedStatement selectUser = null;
		new DBconnect();
		selectUser = DBconnect.getConn().prepareStatement(
				"select * from behavior ");
		ResultSet userSet = selectUser.executeQuery();
		String temp = "";
		Set<String> userset = new HashSet();
		while (userSet.next()) {
			String user = userSet.getString("user_id");
			userset.add(user);
		}
		for(String user:userset){
			Set<String> conceptSet =findUserRatingInformation.getConcept(user);		
			for (String conceptAndTopic : conceptSet) {
				String tempConcept = conceptAndTopic.split(",")[0];
				String tempTopic = conceptAndTopic.split(",")[1];
				m_kcore.kcore(user,Integer.parseInt(tempConcept),Integer.parseInt(tempTopic));
			}

		}
		}	
    }

