import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



public class recomm extends DBconnect{

	public recomm() throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		super();
		// TODO Auto-generated constructor stub
	}
	public void user_article_concept(int user_id) throws Exception{
		//查詢user看過的文章
		PreparedStatement select_UserActicle = null;
		select_UserActicle = conn.prepareStatement("select * from behavior where user_id = ?");
		select_UserActicle.setInt(1, user_id); 
		ResultSet Uarticlers = select_UserActicle.executeQuery();
		ArrayList<String> article_list = new ArrayList<String>();
		while(Uarticlers.next()){
			article_list.add(Uarticlers.getString("article_id"));
		}
		System.out.println(article_list); 
		
		//查詢文章的concept
		Map<String, String> user_concept_map = new HashMap<String, String>(); //計算term在多少篇文件出現
		PreparedStatement selsect_UserConcept = null;
		selsect_UserConcept = conn.prepareStatement("select * from concept_article where article_id = ?");
		
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
		
		
		BufferedWriter bw = new BufferedWriter(new FileWriter("D:/DataTemp/Relevance_Factor/"+user_id + "_RF.txt"));
		
		
		while (iterator.hasNext()) {
			Map.Entry<String, String> entry = iterator.next();
			int concept_id =Integer.parseInt(entry.getKey().split(",")[0]) ;
			int topic_id = Integer.parseInt(entry.getKey().split(",")[1]);
			String articles = entry.getValue();
			System.out.println(entry.getKey() + "," + entry.getValue());
			
			//計算concept權重與user_concept權重
			File file1= new  File( "D:/DataTemp/Concept_K-core//Weight//"+concept_id+"_"+topic_id+"_weight.txt" ); 
			if(!file1.exists()){
				concept_weight(concept_id,topic_id);
			}
			File file2= new  File( "D:/DataTemp/User_K-core//Weight//"+concept_id+"_"+topic_id+"_weight.txt" ); 
			if(!file2.exists()){
				user_weight(user_id,concept_id,topic_id,articles);
			}
			
			
			//計算RF
			double avg_link = RF(user_id,concept_id,topic_id);
			bw.write(concept_id+","+topic_id+";"+avg_link);
			bw.newLine();
		}
		bw.flush();
		bw.close();	
		
	}
	
}
