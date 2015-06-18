package implicit_profile;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ontology.DBconnect;

public class II_profile extends DBconnect{
	public II_profile() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		super();
		// TODO Auto-generated constructor stub
	}

	public void II_concept(int user_id) throws SQLException, IOException{ //如果是EI的鄰居，並且沒有EI值的，就要去計算II
		//-----------------------找出EI鄰居(user未看過)的concept---------------------
		PreparedStatement  select_EI = null;
		select_EI = conn.prepareStatement("select * from profile where user_id = ?");
		select_EI.setInt(1, user_id); 
		ResultSet EI_rs = select_EI.executeQuery();
		
		Map<String, Double> EI_map = new HashMap<String, Double>();	
		ArrayList<String> EI_list = new ArrayList<String>();
		Set<String> II_set = new HashSet<String>(); //準備要計算的
		
		
		while(EI_rs.next()){
			if(EI_rs.getDouble("explicit_profile")!=0){
				String key = EI_rs.getString("concept_id") + ","+EI_rs.getString("topic_id");
				Double value = EI_rs.getDouble("explicit_profile");
				EI_map.put(key, value);
				EI_list.add(key);	
			}
		}
		for(int i=0;i<EI_list.size();i++){
			int concept_id = Integer.parseInt(EI_list.get(i).split(",")[0]);
			int topic_id = Integer.parseInt(EI_list.get(i).split(",")[1]);
			Set<String> temp_set = new HashSet<String>();
			
			if(find_parent(concept_id,topic_id)!=0){
				temp_set.add(concept_id+","+find_parent(concept_id,topic_id)); //parent
			}
			ArrayList<String> temp_child = find_child(concept_id,topic_id); //child
			ArrayList<String> temp_siblings = find_siblings(concept_id,topic_id); //siblings
			
			for(int j=0;j<temp_child.size();j++){
				temp_set.add(concept_id+","+temp_child.get(j));
			}
			for(int j=0;j<temp_siblings.size();j++){
				temp_set.add(concept_id+","+temp_siblings.get(j));
			}
			
			Object[] data = temp_set.toArray();
			for(int j=0;j<data.length;j++){
				if(EI_map.get(data[j])==null){ //如果沒有EI值，則計算II值
					II_set.add((String) data[j]);
				}
			}
			
			
		}
		
		
		System.out.println(EI_map); //可以利用EI_map找到EI值
		System.out.println(II_set); //欲計算II的concept
		
		//-------------------------計算這些concept的II值-----------------------------
		
		PreparedStatement select_similarity = null;  //查詢相似度
		select_similarity = conn.prepareStatement("select similarity from topic_similarity where concept_id = ? and topic_id = ? and topic2_id = ?");
		
		Object[] II_data = II_set.toArray();
		//計算II值
		BufferedWriter bw = new BufferedWriter(new FileWriter("Implicit_profile/"+user_id + "_II.txt"));
		for(int i=0; i<II_data.length; i++ ){	
			int II_concept_id = Integer.parseInt(((String) II_data[i]).split(",")[0]);
			int II_topic_id = Integer.parseInt(((String) II_data[i]).split(",")[1]);
			ArrayList<String> temp_list = new ArrayList<String>();
			
			if(find_parent(II_concept_id,II_topic_id)!=0){
				temp_list.add(II_concept_id+","+find_parent(II_concept_id,II_topic_id)); //parent
			}
			ArrayList<String> temp_child = find_child(II_concept_id,II_topic_id); //child
			ArrayList<String> temp_siblings = find_siblings(II_concept_id,II_topic_id); //siblings
			
			for(int j=0;j<temp_child.size();j++){
				temp_list.add(II_concept_id+","+temp_child.get(j));
			}
			for(int j=0;j<temp_siblings.size();j++){
				temp_list.add(II_concept_id+","+temp_siblings.get(j));
			}
			double total_II = 0;
			for(int j=0;j<temp_list.size();j++){
				int EI_concept_id = Integer.parseInt(temp_list.get(j).split(",")[0]);
				int EI_topic_id = Integer.parseInt(temp_list.get(j).split(",")[1]);
				if(II_topic_id<EI_topic_id){
					select_similarity.setInt(1, II_concept_id);
					select_similarity.setInt(2, II_topic_id);
					select_similarity.setInt(3, EI_topic_id);
				}
				else{
					select_similarity.setInt(1, II_concept_id);
					select_similarity.setInt(2, EI_topic_id);
					select_similarity.setInt(3, II_topic_id);
				}
				ResultSet sim_rs = select_similarity.executeQuery();
				sim_rs.next();
				double sim = sim_rs.getDouble("similarity");
				String key = EI_concept_id+","+EI_topic_id;
				double EI = 0;
				if(EI_map.get(key)!=null){
					EI = EI_map.get(key);
				}
				else{
					EI = 0;
				}	
				total_II = total_II + sim*EI;
			}
			bw.write(II_concept_id+","+II_topic_id+";"+total_II);
			bw.newLine();
			System.out.println(total_II);
		}
		
		bw.flush();
		bw.close();	
		conn.close();
		
	}
	public int find_parent(int concept_id,int topic_id) throws SQLException{ 
		//只會找上一層(只有一個)
		
		PreparedStatement  select_parent = null;
		select_parent = conn.prepareStatement("select parent_id from ontology where concept_id = ? and topic_id = ?");
		select_parent.setInt(1, concept_id);
		select_parent.setInt(2, topic_id);
		ResultSet parent_rs = select_parent.executeQuery();
		parent_rs.next();
		int parent_id = parent_rs.getInt("parent_id");
		return parent_id;
		
	}
	public ArrayList<String> find_child(int concept_id,int topic_id) throws SQLException{ 
		//如果parent_id 為此topic的id 就為child
		
		ArrayList<String> child_list = new ArrayList<String>();
		
		PreparedStatement  select_child = null;
		select_child = conn.prepareStatement("select topic_id from ontology where concept_id = ? and parent_id = ? ");
		
		select_child.setInt(1, concept_id);
		select_child.setInt(2, topic_id);
		ResultSet child_rs = select_child.executeQuery();
		
		while(child_rs.next()){
			child_list.add(child_rs.getString("topic_id"));
		}
		
		return child_list;
	}
	
	public ArrayList<String> find_siblings(int concept_id,int topic_id) throws SQLException{ 
		//(同階層的topic他們的parent_id是相同的)先找出parent_id，再找出有parent_id 的這些topic
		
		ArrayList<String> siblings_list = new ArrayList<String>();
		
		int parent_id = find_parent(concept_id,topic_id);
		
		PreparedStatement select_siblings = null;
		select_siblings = conn.prepareStatement("select topic_id from ontology where concept_id = ? and parent_id = ?");
		select_siblings.setInt(1, concept_id);
		select_siblings.setInt(2, parent_id);
		ResultSet siblings_rs = select_siblings.executeQuery();
		
		while(siblings_rs.next()){
			String siblings_id = siblings_rs.getString("topic_id");
			if(!siblings_id.equals(String.valueOf(topic_id))){
				siblings_list.add(siblings_id);
			}
			
		}
		
		return siblings_list;
		
	}
	
	
	public static void main(String[] args) throws Exception {
		II_profile II_test = new II_profile();
		II_test.II_concept(1);
		
	}
}
