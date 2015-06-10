package main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import database.DBconnect;
import document_clustering.Avg_link_all;
import processingUtil.CalculatePeriod;
import tw.edu.ncu.im.Util.HttpIndexSearcher;

public class ConceptInterest {
	static String path = "D:/dataset/";
	static String articlePath = "D:/dataset/A14OJS0VWMOSWO_mainWords/";

	public static void main(String[] args) throws Exception {

		String userID = "A14OJS0VWMOSWO";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

		String startDateString = "2001/01/21";
		Date startDate = dateFormat.parse(startDateString);
		long startTime = (long) startDate.getTime();

		String endDateString = "2001/01/22";
		Date endDate = dateFormat.parse(endDateString);
		long endTime = (long) endDate.getTime();

		while (startTime < endTime) {
			ConceptInterestCalculate(userID, Long.toString(startTime), 1.0);
			
			startTime = startTime + 24 * 60 * 60 * 1000;
		}

	}

	static void ConceptInterestCalculate(String user, String processingStemp,
			double phi) throws Exception {
		HttpIndexSearcher searcher = new HttpIndexSearcher();
		searcher.url = "http://140.115.82.105/searchweb/";
		String concept = "";
		String topic = "";
		/** 取得user有涉及的概念 */
		Set<String> conceptSet = findUserRatingInformation.getConcept(user);
		/** 計算每個概念的興趣 */
		for (String conceptAndTopic : conceptSet) {
			concept = conceptAndTopic.split(",")[0];
			topic = conceptAndTopic.split(",")[1];
			/** 取得user於該概念的文章 */
			List<String> articleList = findUserRatingInformation
					.getArticleList(user, concept, topic);
			/** 興趣 */
			Double articleInterest = 0.0;
			for (String article : articleList) {
				/** 計算文章與該概念的相似度 */
				Avg_link_all docLink = new Avg_link_all();
				Double sim = docLink.conceptArticleSim(path, concept, topic,
						articlePath + article, searcher);
				/** 文章評價日期 */
				String ratingStemp = findUserRatingInformation.getRatingStemp(
						user, article);
				/** 天數差距 */
				int dayDistance = CalculatePeriod.periodCalculator(
						processingStemp, ratingStemp);
				System.out.println(dayDistance);
				System.out.println(Math.pow(dayDistance, 1 / phi));
				System.out.println(sim);
				articleInterest += sim * 1 / Math.pow(dayDistance, 1 / phi);
			}
			articleInterest = articleInterest/articleList.size();
		
			/** 插入 */
	
				PreparedStatement insertProfile = null;
				/** TO DO:資料庫鍵完在確認一次欄位 */
				insertProfile = DBconnect.getConn().prepareStatement(
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
			
			
		}
		/** 算完概念的興趣 */

	}
}
