package main;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import database.DBconnect;
import document_clustering.Avg_link_all;
import processingUtil.CalculatePeriod;
import tw.edu.ncu.im.Util.HttpIndexSearcher;

public class ConceptInterestLongTerm {
	static String path = "D:/dataset/";
	static String articlePath = "D:/dataset/mainWords/";

	public static void main(String[] args) throws Exception {

		String userID = "A1K1JW1C5CUSUZ";
		/**
		 * A14OJS0VWMOSWO
		 * AFVQZQ8PW0L
		 * A1D2C0WDCSHUWZ
		 * AHD101501WCN1
		 * A1K1JW1C5CUSUZ
		 * */
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

		String startDateString = "2008/04/30";
		Date startDate = dateFormat.parse(startDateString);
		long startTime = (long) startDate.getTime();

		String endDateString = "2008/05/01";
		Date endDate = dateFormat.parse(endDateString);
		long endTime = (long) endDate.getTime();

		while (startTime < endTime) {
			ConceptInterestCalculate_Long(userID, Long.toString(startTime),
					1.0, 3.0);

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
		HashMap<String, String> articleToConceptMap = new HashMap<>();
		HashMap<String, Integer> conceptFrequenceMap = new HashMap<>();
		HashMap<String, Set<String>> conceptFrequenceDayMap = new HashMap<>();
		HashMap<String, Double> longFeatureMap = new HashMap<>();
		HashMap<String, Double> articleCoceptSimMap = new HashMap<>();
		/** 取得所有使用者的評價記錄 */
		List<String> userArticleList = findUserRatingInformation
				.getUserArticle(user, processingStemp, articlePath);
		
		/** 取得所有概念 */
		Set<String> conceptSet = findUserRatingInformation.getAllConcept();

		/** 找出每篇文章所屬概念 */
		for (String article : userArticleList) {
				/** 計算每個概念的相似度 */
				double maxSim = 0.0;
				for (String conceptAndTopic : conceptSet) {
					String tempConcept = conceptAndTopic.split(",")[0];
					String tempTopic = conceptAndTopic.split(",")[1];
					Avg_link_all docLink = new Avg_link_all();
					Double sim = docLink.conceptArticleSim(path, tempConcept,
							tempTopic, articlePath + article, searcher);
					articleCoceptSimMap.put(conceptAndTopic + "," + article,
							sim);
					if (sim >= maxSim) {
						maxSim = sim;
						concept = tempConcept;
						topic = tempTopic;
					}
				}
				/** DB中是否有文章與概念對應 */
				PreparedStatement selectCount = null;
				new DBconnect();
				selectCount = DBconnect.getConn().prepareStatement(
						"select count(*) AS countnum from concept_article "
								+ "where article_id = ? ");
				selectCount.setString(1, article);
				ResultSet hasConcept = selectCount.executeQuery();
				hasConcept.next();
				/** 有的話加入map */
				if (hasConcept.getInt("countnum") != 0) {
					PreparedStatement selectConcept = null;
					selectConcept = DBconnect.getConn().prepareStatement(
							"select * from concept_article "
									+ "where article_id = ? ");
					selectConcept.setString(1, article);

					ResultSet Uarticlers = selectConcept.executeQuery();
					while (Uarticlers.next()) {
						articleToConceptMap.put(article,
								Uarticlers.getString("concept_id") + ","
										+ Uarticlers.getString("topic_id"));
					}
				} else {
					System.out.println(concept);
					/** insert & put */
					PreparedStatement insertConcept = null;
					insertConcept = DBconnect.getConn().prepareStatement(
							"insert into concept_article(concept_id,topic_id,article_id) "
									+ "VALUES (?,?,?) ");
					insertConcept.setString(1, concept);
					insertConcept.setString(2, topic);
					insertConcept.setString(3, article);
					insertConcept.executeUpdate();

					articleToConceptMap.put(article, concept + "," + topic);
					System.out.print(maxSim + "\t");
				}
				hasConcept.close();
				selectCount.close();
		}

		/** 計算每個概念出現使用者有閱讀文章的頻率+天數 */
		for (String article : userArticleList) {
			String conceptAndTopic = articleToConceptMap.get(article);
			if (conceptFrequenceMap.get(conceptAndTopic) == null) {
				conceptFrequenceMap.put(conceptAndTopic, 1);
			} else {
				conceptFrequenceMap.put(conceptAndTopic,
						conceptFrequenceMap.get(conceptAndTopic) + 1);
			}
			String ratetime = findUserRatingInformation.getRatingStemp(user,
					article);
			SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
			Timestamp time = new Timestamp(Long.parseLong(ratetime));
			String rateday = ft.format(time);
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
		/** 計算平均與標準差 */
		Double sumOfFreq = 0.0;
		int count = 0;
		for (String conceptAndTopic : conceptSet) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Timestamp earliestDate = findUserRatingInformation.getEarliestTime(
					user, conceptAndTopic.split(",")[0],
					conceptAndTopic.split(",")[1]);
			long earliestTime  = earliestDate.getTime();
			int period = CalculatePeriod.periodCalculator(
					Long.toString(earliestTime), processingStemp);

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

		/** 取得所有概念 */
		Set<String> userConceptSet = findUserRatingInformation.getConcept(user);
		/** 計算每個概念的興趣 */
		for (String conceptAndTopic : userConceptSet) {
			concept = conceptAndTopic.split(",")[0];
			topic = conceptAndTopic.split(",")[1];
			/** 取得user於該概念的文章 */
			List<String> articleList = findUserRatingInformation
					.getArticleList(user, concept, topic);
			/** 興趣 */
			Double articleInterest = 0.0;
			for (String article : articleList) {
				/** 取得文章與該概念的相似度 */
				Double sim = articleCoceptSimMap.get(conceptAndTopic + ","
						+ article);
				/** 文章評價日期 */
				String ratingStemp = findUserRatingInformation.getRatingStemp(
						user, article);
				/** 天數差距 */
				int dayDistance = CalculatePeriod.periodCalculator(
						processingStemp, ratingStemp);
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

	}

}
