package ontology;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class article_to_ontology  extends DBconnect{
	
	public article_to_ontology() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		super();
		// TODO Auto-generated constructor stub
	}
	public void article_to_database() throws IOException, SQLException{
		FileReader FileStream;
		FileStream = new FileReader("D:/dataset/Processing/Cluster/HierarchicalClusteringResult.txt");
		@SuppressWarnings("resource")
		BufferedReader BufferedStream = new BufferedReader(FileStream);
		String line = "";
		ArrayList<String> concept_list = new ArrayList<String>();
		String temp = "";
		int concept_num = 0;
		while ((line = BufferedStream.readLine()) != null) {
			if(!line.contains("_concept")){
				temp = temp +line+"#";
			}
			if(line.contains("_concept")){
				if(Integer.parseInt(line.split("_")[0])!=1){
					concept_num = Integer.parseInt(line.split("_")[0])-1;
					concept_list.add(concept_num+"%"+temp);
					System.out.println(concept_num+"%"+temp);
					temp = "";
				}
			}
		}
		concept_list.add((concept_num+1)+"%"+temp); //�̫�@��concept
		System.out.println((concept_num+1)+"%"+temp);
//-------------------------��Ƽg�J����-------------------------------
		PreparedStatement select_topic = null; 
		PreparedStatement insert_article_to_topic = null;
		select_topic = getConn().prepareStatement("select * from ontology where concept_id = ? and topic_name = ?");
		insert_article_to_topic = getConn().prepareStatement("insert into concept_article" + "(concept_id,topic_id,article_id)"
	  				   +"values ( ?, ?, ? )");
		
		String concept_id = concept_list.get(1).split("%")[0]; //�Ȯɥ��] concept
		String[] concept_article = (concept_list.get(1).split("%")[1]).split("#"); //�Ȯɥ��] concept
		
		
		for(int i=0;i<concept_article.length;i++){

			if(concept_article[i].contains("&")){ //���h�D�D
				String[] topic_temp= concept_article[i].split(":")[0].split("&");
				for(int j=0;j<topic_temp.length;j++){
					select_topic.setString(1,concept_id);
					select_topic.setString(2,topic_temp[j]);
					ResultSet topicrs = select_topic.executeQuery();
					topicrs.next();
					String topic_id = topicrs.getString("topic_id"); //�۹�����topic_id
					if(concept_article[i].contains(",")){ //�D�D�U���h�g�峹
						String[] article_temp = concept_article[i].split(":")[1].split(",");
						for( int k=0;k<article_temp.length;k++){
							insert_article_to_topic.setString(1,concept_id);
							insert_article_to_topic.setString(2,topic_id);
							insert_article_to_topic.setString(3,article_temp[k]);
							insert_article_to_topic.executeUpdate(); 
						}
					}
					else{ //�D�D�U�u���@�g�峹
						insert_article_to_topic.setString(1,concept_id);
						insert_article_to_topic.setString(2,topic_id);
						insert_article_to_topic.setString(3,concept_article[i].split(":")[1]);
						insert_article_to_topic.executeUpdate(); 
					}
					
				}			
			}
			else{
				select_topic.setString(1,concept_id);
				select_topic.setString(2,concept_article[i].split(":")[0]);
				ResultSet topicrs = select_topic.executeQuery();
				topicrs.next();
				String topic_id = topicrs.getString("topic_id"); //�۹�����topic_id
				if(concept_article[i].contains(",")){ //�D�D�U���h�g�峹
					String[] article_temp = concept_article[i].split(":")[1].split(",");
					for( int k=0;k<article_temp.length;k++){
						insert_article_to_topic.setString(1,concept_id);
						insert_article_to_topic.setString(2,topic_id);
						insert_article_to_topic.setString(3,article_temp[k]);
						insert_article_to_topic.executeUpdate(); 
					}
				}
				else{ //�D�D�U�u���@�g�峹
					insert_article_to_topic.setString(1,concept_id);
					insert_article_to_topic.setString(2,topic_id);
					insert_article_to_topic.setString(3,concept_article[i].split(":")[1]);
					insert_article_to_topic.executeUpdate(); 
				}
			
			}
	
		}
		
	}
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException, SQLException {
		article_to_ontology test = new article_to_ontology();
		test.article_to_database();

	}

}
