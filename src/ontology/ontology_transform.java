package ontology;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ontology_transform extends DBconnect{
	
	public ontology_transform() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		super();
		// TODO Auto-generated constructor stub
	}
	public void ontology_to_database(int no) throws IOException, SQLException{ //輸入tagtree編號(為concept之id)
		FileReader FileStream;
		FileStream = new FileReader("D:/DataTemp/Processing/TagTree/" + no + "_File/TagTreeNumberResult.txt");
		@SuppressWarnings("resource")
		BufferedReader BufferedStream = new BufferedReader(FileStream);
		String line = "";
		int i = 1;
		ArrayList<String> hierarchy_list = new ArrayList<String>();
		
		//還需加 如果資料庫有的話@_@` 就不執行以下
		
		while ((line = BufferedStream.readLine()) != null) {
			String topic = i + ","+ line; //i為topic之id
			hierarchy_list.add(topic);
			//寫入資料庫
			PreparedStatement insert_ontology = null; 
			insert_ontology = conn.prepareStatement("insert into ontology" + "(concept_id,topic_id,topic_name,level_number,parent_id)"
	  				   +"values ( ?, ?, ?, ?, ? )");
			
			insert_ontology.setInt(1, no); 
			insert_ontology.setInt(2, i); 
			insert_ontology.setString(3, line.split(":")[0]);
			insert_ontology.setString(4, line.split(":")[1]);
			insert_ontology.setInt(5, 0);
			insert_ontology.executeUpdate(); 
			 
			i++;
			
		}
	
		for(int j=0;j<hierarchy_list.size();j++){
			String parent_number = find_parent(hierarchy_list.get(j).split(":")[1]);
			String topic_id = hierarchy_list.get(j).split(",")[0];
			System.out.println(parent_number);
			if(!parent_number.isEmpty()){
				String p_topicsql = "select * from ontology where level_number = \'" + parent_number + "\' and concept_id = \'"+no+"\'";
				ResultSet p_topicrs = stmt.executeQuery(p_topicsql);
				p_topicrs.next();
				String parent_id = p_topicrs.getString("topic_id");
				String parent_update = "UPDATE ontology set parent_id =  \'"+ parent_id +"\' where concept_id = \'"+no+"\' and topic_id = \'"+topic_id+"\'";
				stmt.executeUpdate(parent_update);
			}
		}
		conn.close();
	//跑for~> 同concept 尋找父topic 利用下面一段程式碼
		
	}
	public String find_parent(String level_number){ //找尋父topic
		
		String temp[]=level_number.split("\\.");
		String parent_number = "";
		for(int i=0;i<temp.length-1;i++){
			if(i!=temp.length-2)
				parent_number = parent_number+temp[i]+"."; 
			else
				parent_number = parent_number+temp[i];
		}
		
		System.out.println("find_parent.. "+parent_number);
		return parent_number;
		
		
	}
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException, SQLException {
		ontology_transform test = new ontology_transform();
		
			test.ontology_to_database(4);
		
	}


}
