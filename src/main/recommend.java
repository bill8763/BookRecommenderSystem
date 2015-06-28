package main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import database.DBconnect;

public class recommend {
	public static void main(String[] args) throws Exception {
		recommendDoc("simulate1", null);
	}

	public static void recommendDoc(String user, String outputfile)
			throws Exception {
		/** 挑選profile內值最高的，先看有幾個 */
		new DBconnect();
		PreparedStatement selectProfileCount = null;
		selectProfileCount = DBconnect
				.getConn()
				.prepareStatement(
						"select count(*) from profile where user_id = ? ORDER BY interest DESC");
		selectProfileCount.setString(1, user);
		ResultSet userInterestCount = selectProfileCount.executeQuery();
		userInterestCount.next();
		int interestNum = userInterestCount.getInt(1);
		userInterestCount.close();
		selectProfileCount.close();
		
		PreparedStatement selectProfile = null;
		selectProfile = DBconnect
				.getConn()
				.prepareStatement(
						"select * from profile where user_id = ? ORDER BY interest DESC");
		selectProfile.setString(1, user);
		ResultSet userInterest = selectProfile.executeQuery();
		ArrayList article = new ArrayList();
		HashMap<String,Double> conceptInterestMap = new HashMap<>();
		double interest;
		String concept;
		String topic;
		int recnum = 3;
		if (interestNum < recnum) {
			recnum = interestNum;
		}
		for (int i = 0; i < recnum; i++) {
			userInterest.next();
			interest = userInterest.getDouble("interest");
			concept = userInterest.getString("concept_id");
			topic = userInterest.getString("topic_id");
			System.out.println(interest + "," + concept + "," + topic);
			conceptInterestMap.put(concept+","+topic,interest);
		}
		/**取概念內文件計算推薦的值*/
		for (String concept_topic:conceptInterestMap.keySet()){
			concept = concept_topic.split(",")[0];
			topic = concept_topic.split(",")[1];
			
		}
	}
}
