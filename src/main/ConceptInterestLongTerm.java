package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import DatasetPocess.fileList;
import database.DBconnect;
import document_clustering.Avg_link_all;
import processingUtil.CalculatePeriod;
import tw.edu.ncu.im.Util.HttpIndexSearcher;

public class ConceptInterestLongTerm {
	static String path = "D:/dataset/";
	static String articlePath = "D:/dataset/userMainWords/";
	static String toRecommendDocDir = "D:/dataset/recommendation/";
	static HashMap<String, String> articleToConceptMap = new HashMap<>();
	public static void main(String[] args) throws Exception {

		String userID = "exp5";
		/**real user
		 * A14OJS0VWMOSWO
		 * AFVQZQ8PW0L
		 * A1D2C0WDCSHUWZ
		 * AHD101501WCN1
		 * A1K1JW1C5CUSUZ
		 * */
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

		String startDateString = "2015/07/03";
		Date startDate = dateFormat.parse(startDateString);
		long startTime = (long) startDate.getTime();

		String endDateString = "2015/07/08";
		Date endDate = dateFormat.parse(endDateString);
		long endTime = (long) endDate.getTime();

		while (startTime < endTime) {
			ConceptInterestCalculate_Long(userID, Long.toString(startTime),
					1.0, 1.0);
			
			startTime = startTime + 24 * 60 * 60 * 1000;
		}

	}

	static void ConceptInterestCalculate_Long(String user,
			String processingStemp, double phiForShort, double phiForLong)
			throws Exception {
		HttpIndexSearcher searcher = new HttpIndexSearcher();
		searcher.url = "http://140.115.82.105/searchweb/";
		String concept = "";
		String topic = "";

		HashMap<String, Integer> conceptFrequenceMap = new HashMap<>();
		HashMap<String, Set<String>> conceptFrequenceDayMap = new HashMap<>();
		HashMap<String, Double> longFeatureMap = new HashMap<>();
		HashMap<String, Double> articleCoceptSimMap = new HashMap<>();
		List<String> userArticleList = findUserRatingInformation
				.getUserArticle(user, processingStemp, articlePath);/** 取得所有使用者的評價文件 */
		HashSet<String> recomArticle = new HashSet<>();
		/**當日是否有閱讀文章*/
		boolean newArticleFlag=false;
		ArrayList<String> newArticle = new ArrayList<>(); /**存當日新增文章*/
		HashSet<String> toUpdateConcept = new HashSet<>();
		for(String article:userArticleList){
			String ratingStemp = findUserRatingInformation.getRatingStemp(
					user, article);/** 文章評價日期 */
			int dayDistance = CalculatePeriod.periodCalculator(
					processingStemp, ratingStemp);/** 天數差距 */
			if(dayDistance==0){
				newArticle.add(article);
				newArticleFlag=true;
				}
		}
		
		if(newArticleFlag){
			/** 取得所有概念 */
			Set<String> conceptSet = findUserRatingInformation.getAllConcept();	
			/** 找出當日新增文章所屬概念 */
			double maxSim = 0.0;
			for (String article : newArticle) {
					/** 計算每個概念的相似度 */
					maxSim=0.0;
					for (String conceptAndTopic : conceptSet) {
						String tempConcept = conceptAndTopic.split(",")[0];
						String tempTopic = conceptAndTopic.split(",")[1];
						Avg_link_all docLink = new Avg_link_all();
						Double sim = docLink.conceptArticleSim(path, tempConcept,
								tempTopic, articlePath + article, searcher);
						articleCoceptSimMap.put(conceptAndTopic + "," + article,
								sim);
						/**大於門檻值(0.3)歸為該概念*/		
						if(sim>0.3){
							concept = tempConcept;
							topic = tempTopic;							
							if(articleToConceptMap.get(article)==null){
								articleToConceptMap.put(article, concept + "," + topic);
								toUpdateConcept.add(concept + "," + topic);						
							}
							else{
								String temp = articleToConceptMap.get(article);
								articleToConceptMap.remove(article);
								articleToConceptMap.put(article,temp+"&"+concept+","+topic);
							}
							/** insert & put */
							PreparedStatement insertConcept = null;
							insertConcept = DBconnect.getConn().prepareStatement(
									"insert into concept_userarticle(concept_id,topic_id,article_id,similarity) "
											+ "Values (?,?,?,?) ON DUPLICATE KEY UPDATE similarity=?");
							insertConcept.setString(1, concept);
							insertConcept.setString(2, topic);
							insertConcept.setString(3, article);
							insertConcept.setDouble(4, maxSim);
							insertConcept.setDouble(5, maxSim);
							insertConcept.executeUpdate();
							insertConcept.close();
							toUpdateConcept.add(concept + "," + topic);
						}
						if (sim >= maxSim) {
							maxSim = sim;
							concept = tempConcept;
							topic = tempTopic;
						}
					}
					if(maxSim<0.3){
						/** insert & put */
						PreparedStatement insertConcept = null;
						insertConcept = DBconnect.getConn().prepareStatement(
								"insert into concept_userarticle(concept_id,topic_id,article_id,similarity) "
										+ "Values (?,?,?,?) ON DUPLICATE KEY UPDATE similarity=?");
						insertConcept.setString(1, concept);
						insertConcept.setString(2, topic);
						insertConcept.setString(3, article);
						insertConcept.setDouble(4, maxSim);
						insertConcept.setDouble(5, maxSim);
						insertConcept.executeUpdate();
						toUpdateConcept.add(concept + "," + topic);
						articleToConceptMap.put(article, concept + "," + topic);
						System.out.print(maxSim + "\t");
						insertConcept.close();
					}
					else{
						System.out.println("bigger than 0.3");
					}
			}
			Set<String> userConceptSet = findUserRatingInformation.getConcept(user);	
	
			/** 計算每個概念出現使用者有閱讀文章的頻率+天數 */
			for (String article : userArticleList) {
				String conceptAndTopic = articleToConceptMap.get(article);
				String ratetime = findUserRatingInformation.getRatingStemp(user,
						article);
				SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
				Timestamp time = new Timestamp(Long.parseLong(ratetime));
				String rateday = ft.format(time);
				
				if(conceptAndTopic.contains("&")){ /**文件歸類到多個概念*/
					for(int i=0;i<conceptAndTopic.split("&").length;i++){
						if (conceptFrequenceMap.get(conceptAndTopic.split("&")[i]) == null) {
							conceptFrequenceMap.put(conceptAndTopic.split("&")[i], 1);
						} else {
							conceptFrequenceMap.put(conceptAndTopic.split("&")[i],
									conceptFrequenceMap.get(conceptAndTopic.split("&")[i]) + 1);
						}
						
						if (conceptFrequenceDayMap.get(conceptAndTopic.split("&")[i]) == null) {
							HashSet<String> tempSet = new HashSet<>();
							tempSet.add(rateday);
							conceptFrequenceDayMap.put(conceptAndTopic.split("&")[i], tempSet);
						} else {
							Set<String> tempSet = conceptFrequenceDayMap
									.get(conceptAndTopic.split("&")[i]);
							tempSet.add(rateday);
							conceptFrequenceDayMap.put(conceptAndTopic.split("&")[i], tempSet);
						}
					}
				}
				else{/**文件屬於一個概念*/
					if (conceptFrequenceMap.get(conceptAndTopic) == null) {
						conceptFrequenceMap.put(conceptAndTopic, 1);
					} else {
						conceptFrequenceMap.put(conceptAndTopic,
								conceptFrequenceMap.get(conceptAndTopic) + 1);
					}
					
					if (conceptFrequenceDayMap.get(conceptAndTopic) == null) {
						HashSet<String> tempSet = new HashSet<>();
						tempSet.add(rateday);
						conceptFrequenceDayMap.put(conceptAndTopic, tempSet);
					} else {
						Set<String> tempSet = conceptFrequenceDayMap
								.get(conceptAndTopic);
						tempSet.add(rateday);
						conceptFrequenceDayMap.put(conceptAndTopic, tempSet);
					}
				}			
			}
			
			/** 計算平均與標準差 */
			Double sumOfFreq = 0.0;
			int count = 0;
			for (String conceptAndTopic : userConceptSet) {
				Timestamp earliestDate = findUserRatingInformation.getEarliestTime(
						user, conceptAndTopic.split(",")[0],
						conceptAndTopic.split(",")[1]);
				long earliestTime  = earliestDate.getTime();
				int period = CalculatePeriod.periodCalculator(
						Long.toString(earliestTime), processingStemp);
				if(period==0)period=1;
				if (conceptFrequenceMap.get(conceptAndTopic) != null) {
					double longFeature = conceptFrequenceMap.get(conceptAndTopic)
							* conceptFrequenceDayMap.get(conceptAndTopic).size()
							* period;
					longFeatureMap.put(conceptAndTopic, longFeature);
					sumOfFreq += longFeature;
					count++;
				}
			}
			Double mean = sumOfFreq / count;
			Double var = 0.0;
			for (String conceptAndTopic : conceptSet) {
				if (conceptFrequenceMap.get(conceptAndTopic) != null) {
					var += Math.pow(longFeatureMap.get(conceptAndTopic) - mean, 2);
				}
			}
			/** 標準差 */
			double sd = Math.sqrt(var / count);
			double threshold = mean + sd;
	
			/**TODO:長變短 短變長判斷*/
			for (String conceptAndTopic : userConceptSet) {
				concept = conceptAndTopic.split(",")[0];
				topic = conceptAndTopic.split(",")[1];
				if(longFeatureMap.containsKey(conceptAndTopic)){
					if (longFeatureMap.get(conceptAndTopic) > threshold) {
						

					}
				}
			}

			/** 計算每個概念的興趣 */
			for (String conceptAndTopic : toUpdateConcept) {
				concept = conceptAndTopic.split(",")[0];
				topic = conceptAndTopic.split(",")[1];
				/** 取得user於該概念的文章 */
				List<String> articleList = findUserRatingInformation
						.getArticleList(user, concept, topic);
				/** 興趣 */
				Double articleInterest = 0.0;
				for (String article : articleList) {
					/** 取得文章與該概念的相似度 */
					PreparedStatement selectSim = null;
					new DBconnect();
					selectSim = DBconnect.getConn().prepareStatement(
							"select *  from concept_userarticle "
									+ "where concept_id =? and topic_id=? and article_id = ? ");
					selectSim.setString(1, concept);
					selectSim.setString(2, topic);
					selectSim.setString(3, article);
					ResultSet hasConcept = selectSim.executeQuery();
					hasConcept.next();
					Double sim =hasConcept.getDouble("similarity");
					/** 文章評價日期 */
					String ratingStemp = findUserRatingInformation.getRatingStemp(
							user, article);
					/** 天數差距 */
					int dayDistance = CalculatePeriod.periodCalculator(
							processingStemp, ratingStemp);
					if(dayDistance==0){dayDistance=1;}
					System.out.println(dayDistance);
					System.out.println(Math.pow(dayDistance, 1 / phiForShort));
					System.out.println(sim);
					if(longFeatureMap.containsKey(conceptAndTopic)){
						if (longFeatureMap.get(conceptAndTopic) > threshold) {
							articleInterest += sim * 1
									/ Math.pow(dayDistance, 1 / phiForLong);
						} else {
							articleInterest += sim * 1
									/ Math.pow(dayDistance, 1 / phiForShort);
						}
					}
				}
				articleInterest = articleInterest / articleList.size();
				System.out.println(articleInterest+","+ articleList.size());
				/** 插入 */
	
				PreparedStatement insertProfile = null;
				/** TO DO:資料庫鍵完在確認一次欄位 */
				insertProfile = DBconnect
						.getConn()
						.prepareStatement(
								"insert into profile (user_id,concept_id,topic_id,interest,process_time) "
										+ "values (?,?,?,?,?)"
										+ "ON DUPLICATE KEY UPDATE interest=?,process_time=?");
				insertProfile.setString(1, user);
				insertProfile.setInt(2, Integer.parseInt(concept));
				insertProfile.setInt(3, Integer.parseInt(topic));
				insertProfile.setDouble(4, articleInterest);
				insertProfile.setTimestamp(5,
						new Timestamp(Long.parseLong(processingStemp)));
				insertProfile.setDouble(6, articleInterest);
				insertProfile.setTimestamp(7,
						new Timestamp(Long.parseLong(processingStemp)));
				insertProfile.executeUpdate();
				insertProfile.close();
			}
			/** 算完概念的興趣 */
		
			/**推薦*/
			File outputDir = new File(toRecommendDocDir);
			if (!outputDir.exists()) {
				outputDir.mkdirs();
				try {
					outputDir.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			File outputDir2 = new File(toRecommendDocDir+"extended/");
			if (!outputDir2.exists()) {
				outputDir2.mkdirs();
				try {
					outputDir2.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
			Timestamp time = new Timestamp(Long.parseLong(processingStemp));
			String rateday = ft.format(time);
			recommend.recommendDoc(user, toRecommendDocDir+rateday+"_"+user+".txt");
			recommend.extendedRecommendDoc(user, toRecommendDocDir+"extended/"+rateday+"_"+user+".txt");
			/**驗證推薦*/
			String correctPath =  "D:/dataset/exp5/";
			String outputPath = toRecommendDocDir+"extended/"+"exp5_extended.txt";
			validation.recommValidation(user, toRecommendDocDir+"extended/"+rateday+"_"+user+".txt", processingStemp,correctPath,outputPath);
			/**驗證推薦*/
//			String correctPath =  "D:/dataset/exp3/5/";
//			String outputPath = toRecommendDocDir+"exp3_5.txt";
//			validation.recommValidation(user, toRecommendDocDir+rateday+"_"+user+".txt", processingStemp,correctPath,outputPath);
//			correctPath="D:/dataset/exp3/6/";
//			outputPath = toRecommendDocDir+"exp3_6.txt";
//			validation.recommValidation(user, toRecommendDocDir+rateday+"_"+user+".txt", processingStemp,correctPath,outputPath);
//			correctPath="D:/dataset/exp3/7/";
//			outputPath = toRecommendDocDir+"exp3_7.txt";
//			validation.recommValidation(user, toRecommendDocDir+rateday+"_"+user+".txt", processingStemp,correctPath,outputPath);
//			correctPath="D:/dataset/exp3/10/";
//			outputPath = toRecommendDocDir+"exp3_10.txt";
//			validation.recommValidation(user, toRecommendDocDir+rateday+"_"+user+".txt", processingStemp,correctPath,outputPath);
		}
	}
}
