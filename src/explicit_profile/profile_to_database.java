package explicit_profile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class profile_to_database extends database.DBconnect{
	public profile_to_database() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		super();
		// TODO Auto-generated constructor stub
	}

	public void EI_to_database(int user_id) throws IOException, SQLException{
		FileReader FileStream1;
		FileStream1 = new FileReader("Relevance_Factor/"+user_id + "_RF.txt");
		@SuppressWarnings("resource")
		BufferedReader BufferedStream1 = new BufferedReader(FileStream1);
		String line1 = "";

		PreparedStatement select_ei = null;
		select_ei = getConn().prepareStatement("select * from profile where user_id = ? and concept_id = ? and topic_id = ?");
		
		PreparedStatement insert_ei = null;
		insert_ei = getConn().prepareStatement("insert into profile"+ "(user_id,concept_id,topic_id,explicit_profile,implicit_profile)"
				 +"values ( ?, ?, ?, ?, ? )");
		
		PreparedStatement update_ei = null;
		update_ei = getConn().prepareStatement("update profile set explicit_profile = ?,implicit_profile = ? where user_id = ? and concept_id = ? and topic_id = ?");
		
		while ((line1 = BufferedStream1.readLine()) != null) {
			int concept_id = Integer.parseInt(line1.split(";")[0].split(",")[0]);
			int topic_id = Integer.parseInt(line1.split(";")[0].split(",")[1]);
			double ei = Double.parseDouble(line1.split(";")[1]);
			select_ei.setInt(1, user_id);
			select_ei.setInt(2, concept_id);
			select_ei.setInt(3, topic_id);
			ResultSet eics = select_ei.executeQuery();
			if(eics.next()==true){
				update_ei.setDouble(1, ei);
				update_ei.setDouble(2, 0);
				update_ei.setInt(3, user_id);
				update_ei.setInt(4, concept_id);
				update_ei.setInt(5, topic_id);
				update_ei.executeUpdate(); 
				
			}
			else{
				insert_ei.setInt(1, user_id);
				insert_ei.setInt(2, concept_id);
				insert_ei.setInt(3, topic_id);
				insert_ei.setDouble(4, ei);
				insert_ei.setDouble(5, 0);
				insert_ei.executeUpdate();
			}
		}
		
	}
	public static void main(String[] args) throws Exception {
		profile_to_database ei_test = new profile_to_database();
		ei_test.EI_to_database(1);
	}

}
