package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import tw.edu.ncu.im.Util.NgdEdgeSorter;
import database.DBconnect;

public class recommend {
	public static void main(String[] args) throws Exception {
		recommendDoc("test1", "D:/dataset/recommendation/test.txt");
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
		HashMap<String,Double> articleRecommendMap = new HashMap<>();
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
			
			PreparedStatement selectarticleInConcept = null;
			selectarticleInConcept = DBconnect
					.getConn()
					.prepareStatement(
							"select * from concept_article,concept_userarticle "
							+ "where concept_userarticle.concept_id = ? and concept_userarticle.topic_id=?"
							+ "and concept_article.article_id=concept_userarticle.article_id");
			selectarticleInConcept.setString(1, concept);
			selectarticleInConcept.setString(2, topic);	
			ResultSet articleInConcept = selectarticleInConcept.executeQuery();
			while(articleInConcept.next()){
				String tempArticle= articleInConcept.getString("article_id");
				double docConSim = articleInConcept.getDouble("similarity");
				double docRecommend =  conceptInterestMap.get(concept_topic) * docConSim;
				articleRecommendMap.put(tempArticle, docRecommend);
			}
		}
			List<Entry<?, Double>> sortedArticle = sort(articleRecommendMap);
			int recommendNum=10;
			if(sortedArticle.size()<recommendNum){
				recommendNum=sortedArticle.size();
			}
			BufferedWriter bw;
			for(int i=0;i<recommendNum;i++){
				bw = new BufferedWriter(new FileWriter(outputfile, true));
				bw.write(sortedArticle.get(i).getKey().toString()+":"+sortedArticle.get(i).getValue());
				bw.newLine();
				bw.flush();
				bw.close();
			}
			
	}
	
	public static List<Entry<?, Double>> sort(Map<? , Double> unsortingMap){
		List<Entry<? extends Object, Double>> sortingList = new ArrayList<Entry<? extends Object, Double>>(unsortingMap.entrySet());
		Collections.sort(sortingList, new Comparator<Map.Entry<?, Double>>() {
			public int compare(Map.Entry<?, Double> entry1,
					Map.Entry<?, Double> entry2) {
				return entry1.getValue().compareTo(entry2.getValue());
			}
		});
		return sortingList;
		//TODO return the LinkedHashMap is a better idea
	}
}
