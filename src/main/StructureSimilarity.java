package main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import database.DBconnect;
/**
 * 寫的方法不好，會造成大量連線，一直跳出警示訊息
 * 之後可以改為先將父節點資訊存下來減少連線數
 * @author chiang
 *
 */
public class StructureSimilarity {
	public static void main(String[] args) throws Exception{
		ArrayList<String> conceptList = new ArrayList<>();
		new DBconnect();
		PreparedStatement select_concept = null;
		select_concept = DBconnect.getConn().prepareStatement("select concept_id  from ontology");
		ResultSet concepts = select_concept.executeQuery();
		while(concepts.next()){
			conceptList.add(concepts.getString("concept_id"));
		}
		select_concept.close();
		for(int i=0;i<conceptList.size();i++){
		calculateStructureSim(conceptList.get(i));
		}
//		calculateStructureSim("3");
	}

	private static void calculateStructureSim(String concept) throws Exception {
		HashMap<String,Integer> heightMap = new HashMap<>();
		/**資料庫取得每點層數*/
		new DBconnect();
		PreparedStatement select_topic = null;
		select_topic = DBconnect.getConn().prepareStatement("select *  from ontology where concept_id = ?");
		select_topic.setString(1, concept); 
		ResultSet topics = select_topic.executeQuery();
		int maxHeight=0;
		while(topics.next()){
			String level = topics.getString("level_number");
			int height=0;
			if(level.contains(".")){
				String[] temp=level.split("\\.");
				height=temp.length;
				if(height>maxHeight){
					maxHeight=height;
				}
				heightMap.put(topics.getString("topic_id"),height);
			}
			else{
				heightMap.put("1",1);
			}
		}
		topics.close();
		select_topic.close();
		/**每對概念計算相似度計算*/
		HashMap<Set<String>,Double> topicsSimMap = new HashMap<>();
		for(String topic : heightMap.keySet()){
			for(String topic2 : heightMap.keySet()){
				HashSet<String> topicSet = new HashSet<>();
				topicSet.add(topic);
				topicSet.add(topic2);	
				if(!topic.equals(topic2) && topicsSimMap.get(topicSet)==null){
					/**先找共同祖先的高度*/
					String node1=topic;
					String node2=topic2;
					String commenParent=null;
					boolean find=false;
					while(!find){
						node2=topic2;
						while(!find){
							if(node1.equals(node2)){
								commenParent=node1;
								find=true;
							}
							node2=findParent(concept,node2);			
							if(node2.equals("0")){break;}
						}
						node1=findParent(concept,node1);							
					}
					double structureSim= (double) (2* heightMap.get(commenParent)) / (double)(heightMap.get(topic)+heightMap.get(topic2) );
					topicsSimMap.put(topicSet, structureSim);
					/** 插入 */
					PreparedStatement insertProfile = null;
					insertProfile = DBconnect
							.getConn()
							.prepareStatement(
									"insert into structure_similarity (concept_id,topic_id,topic2_id,similarity) "
											+ "values (?,?,?,?)"
											+ "ON DUPLICATE KEY UPDATE `similarity`=values(similarity)");
					insertProfile.setString(1, concept);
					insertProfile.setString(2,topic);
					insertProfile.setString(3, topic2);
					insertProfile.setDouble(4, structureSim);
					insertProfile.executeUpdate();
					insertProfile.close();
				}
			}
		}
		/**輸入資料庫*/
	}

	private static String findParent(String concept, String topic) throws Exception {
		new DBconnect();
		PreparedStatement find_parent = null;
		find_parent = DBconnect.getConn().prepareStatement("select *  from ontology where concept_id = ? and topic_id=?");
		find_parent.setString(1,concept);
		find_parent.setString(2,topic);	
		ResultSet parent = find_parent.executeQuery();
		String output=null;
		while(parent.next()){
			output=parent.getString("parent_id");}
		parent.close();
		find_parent.close();
		return output ;
	}
}
