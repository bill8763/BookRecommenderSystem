package main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.sun.tools.javac.util.Pair;

import database.DBconnect;
import processingUtil.CalculatePeriod;

public class ConceptInterest {
	public static void main(String[] args) throws SQLException, ParseException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException {

		String userID = "A14OJS0VWMOSWO";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

		String startDateString = "2001/01/01";
		Date startDate = dateFormat.parse(startDateString);
		long startTime = (long) startDate.getTime();

		String endDateString = "2001/02/01";
		Date endDate = dateFormat.parse(endDateString);
		long endTime = (long) endDate.getTime();

		while (startTime < endTime) {
			ConceptInterestCalculate(userID, Long.toString(startTime), 1.0);
			startTime = startTime + 24 * 60 * 60 * 1000;
		}

	}

	static void ConceptInterestCalculate(String user, String processingStemp,
			double phi) throws SQLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException, ParseException {
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
				Double sim = ConceptsSimilarity.articleSimilarity(article,
						concept, topic);
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
			/** 判斷有無資料，來選擇更新或插入 */
			PreparedStatement selectProfile = null;
			selectProfile = DBconnect
					.getConn()
					.prepareStatement(
							"select count(*) from profile "
									+ "where user_id = ? and concpet_id=? and topic_id=? ");
			selectProfile.setString(1, user);
			selectProfile.setInt(2, Integer.parseInt(concept));
			selectProfile.setInt(3, Integer.parseInt(topic));
			ResultSet existState = selectProfile.executeQuery();
			/** 已存在則更新 */
			if (existState.equals(0)) {
				PreparedStatement updateProfile = null;
				/** TO DO:資料庫鍵完在確認一次欄位 */
				updateProfile = DBconnect
						.getConn()
						.prepareStatement(
								"update interest from profile "
										+ "where user_id = ? and concpet_id=? and topic_id=? and process_time=?");
				updateProfile.setString(1, user);
				updateProfile.setInt(2, Integer.parseInt(concept));
				updateProfile.setInt(3, Integer.parseInt(topic));
				updateProfile.setTimestamp(4,
						new Timestamp(Long.parseLong(processingStemp)));
				updateProfile.executeUpdate();
			}
			/** 不存在則插入 */
			else {
				PreparedStatement insertProfile = null;
				/** TO DO:資料庫鍵完在確認一次欄位 */
				insertProfile = DBconnect.getConn().prepareStatement(
						"insert into profile (user_id,concpet_id,topic_id,interest,process_time) "
								+ "values (?,?,?,?,?)");
				insertProfile.setString(1, user);
				insertProfile.setInt(2, Integer.parseInt(concept));
				insertProfile.setInt(3, Integer.parseInt(topic));
				insertProfile.setDouble(4, articleInterest);
				insertProfile.setTimestamp(5,
						new Timestamp(Long.parseLong(processingStemp)));
				insertProfile.executeUpdate();
			}
		}
		/** 算完概念的興趣 */

	}
}
