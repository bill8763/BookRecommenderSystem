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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
		int recnum = 5;
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
				return entry2.getValue().compareTo(entry1.getValue());
			}
		});
		return sortingList;
		//TODO return the LinkedHashMap is a better idea
	}

	public static void extendedRecommendDoc(String user, String outputfile) throws Exception  {
		// TODO Auto-generated method stub
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
		HashMap<String,Double> extendedConceptMap = new HashMap<>();	
		HashSet<String> toExtendedConceptSet = new HashSet<>();
		HashMap<Set<String>,Double> conceptSimilarityMap = new HashMap<>();
		Set<String> userConceptSet = findUserRatingInformation.getConcept(user);
		double interest;
		String concept;
		String topic;
		int recnum = 10;
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
		System.out.println("conceptInterestMap::::::::::::::"+conceptInterestMap.keySet().size());
		/**除profile高的之外的其他概念*/
			selectProfile = DBconnect
					.getConn()
					.prepareStatement(
							"select * from profile where user_id = ? and concept_id = ? ");
			selectProfile.setString(1, user);
		for (String concept_topic:conceptInterestMap.keySet()){
			concept = concept_topic.split(",")[0];
			topic = concept_topic.split(",")[1];
			selectProfile.setString(2,concept);
			ResultSet profileResult = selectProfile.executeQuery();
			while(profileResult.next()){
				toExtendedConceptSet.add(concept+","+profileResult.getString("topic_id"));				
			}
		}
		System.out.println("toExtendedConceptSet::::::::::::::"+toExtendedConceptSet.size());
		toExtendedConceptSet.removeAll(conceptInterestMap.keySet());
		System.out.println("toExtendedConceptSet::::::::::::::"+toExtendedConceptSet.size());
		/**每個概念的structure sim*/
		PreparedStatement selectSimilarity = DBconnect
				.getConn()
				.prepareStatement(
						"select * from structure_similarity "
						+ "where  concept_id = ? and  topic_id = ? and topic2_id = ?");
		
		for(String concept_topic:userConceptSet){
			concept = concept_topic.split(",")[0];
			topic = concept_topic.split(",")[1];
			selectSimilarity.setString(1, concept);					
			selectSimilarity.setString(2, topic);	
			
			for(String concept_topic2:userConceptSet){
				String concept2 = concept_topic2.split(",")[0];
				String topic2 = concept_topic2.split(",")[1];
				if(concept_topic2.split(",")[0].equals(concept)&&!topic2.equals(topic)){
					selectSimilarity.setString(3, topic2);
					HashSet<String> topicsSet = new HashSet<>();
					topicsSet.add(concept_topic);
					topicsSet.add(concept_topic2);
					ResultSet structureSim = selectSimilarity.executeQuery();
					if(structureSim.next()){
					conceptSimilarityMap.put(topicsSet, structureSim.getDouble("similarity"));
					}
				}
			}
		}
		
		for(String concept_topic:toExtendedConceptSet){
			concept = concept_topic.split(",")[0];
			topic = concept_topic.split(",")[1];
			Double sumOfSimInter=0.0;
			for(String concept_topic2:conceptInterestMap.keySet()){
				String concept2 = concept_topic2.split(",")[0];
				String topic2 = concept_topic2.split(",")[1];
				HashSet<String> topicsSet = new HashSet<>();
				topicsSet.add(concept_topic);
				topicsSet.add(concept_topic2);
				double conceptInterest = conceptInterestMap.get(concept_topic2);
				double structSim = conceptSimilarityMap.get(topicsSet);
				sumOfSimInter+=conceptInterest*structSim;				
			}
			extendedConceptMap.put(concept_topic, sumOfSimInter);
		}
		
		/**取做延伸概念的概念內內文件 計算推薦的值*/
		for (String concept_topic:toExtendedConceptSet){
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
				double docRecommend =  extendedConceptMap.get(concept_topic) * docConSim;
				articleRecommendMap.put(tempArticle, docRecommend);
			}
		}
			List<Entry<?, Double>> sortedArticle = sort(articleRecommendMap);
			int recommendNum=10;
			if(sortedArticle.size()<recommendNum){
				recommendNum=sortedArticle.size();
			}
			System.out.println(recommendNum+"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			BufferedWriter bw;
			for(int i=0;i<recommendNum;i++){
				bw = new BufferedWriter(new FileWriter(outputfile, true));
				bw.write(sortedArticle.get(i).getKey().toString()+":"+sortedArticle.get(i).getValue());
				bw.newLine();
				bw.flush();
				bw.close();
			}
	}
	public static void EIRecommendDoc(String user, String outputfile)
			throws Exception {
		/** 挑選profile內值最高的，先看有幾個 */
		new DBconnect();	
		PreparedStatement selectProfile = null;
		selectProfile = DBconnect
				.getConn()
				.prepareStatement(
						"select * from ei_profile where user_id = ? ");
		selectProfile.setString(1, user);
		ResultSet userInterest = selectProfile.executeQuery();
		HashMap<String,Double> articleRecommendMap = new HashMap<>();
		HashMap<String,Double> conceptInterestMap = new HashMap<>();
		double interest;
		String concept;
		String topic;
		while (userInterest.next()) {
			interest = userInterest.getDouble("Explicit_profile");
			if(interest>0.5){
				concept = userInterest.getString("concept_id");
				topic = userInterest.getString("topic_id");
				System.out.println(interest + "," + concept + "," + topic);
				conceptInterestMap.put(concept+","+topic,interest);
			}
		}
		/**取概念內文件計算推薦的值*/
			int recNum=15;
			int count=0;		
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
				if(count<recNum){
				String tempArticle= articleInConcept.getString("article_id");
				double docConSim = articleInConcept.getDouble("similarity");
				double docRecommend =  conceptInterestMap.get(concept_topic) * docConSim;
				articleRecommendMap.put(tempArticle, docRecommend);
				count++;
				}
			}
		}

			BufferedWriter bw;
			for(String article:articleRecommendMap.keySet()){
				bw = new BufferedWriter(new FileWriter(outputfile, true));
				bw.write(article+":");
				bw.newLine();
				bw.flush();
				bw.close();
			}	
	}
}
