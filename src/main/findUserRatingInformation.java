package main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sun.tools.javac.util.Pair;

import database.DBconnect;

public class findUserRatingInformation {
	public static void main(String args[]) {
		
	}

/**
 *取得 使用者於概念內評價的文章
 * @param user
 * @param concept Domain
 * @param topic Node
 * @return
 * @throws SQLException
 */
	public static List<String> getArticleList(String user, String concept, String topic)
			throws SQLException {
		List<String> outputList = new ArrayList<String>();
		PreparedStatement select_UserActicle = null;
		select_UserActicle = DBconnect
				.getConn()
				.prepareStatement(
						"select * from behavior,concept_article "
						+ "where behavior.user_id = ? "
						+ "and behavior.article_id=concept_article.article_id "
						+ "and concept_id = ? "
						+ "and  topic_id = ?");
		select_UserActicle.setString(1, user);
		select_UserActicle.setString(2, concept);
		select_UserActicle.setString(3, topic);
		ResultSet Uarticlers = select_UserActicle.executeQuery();
		while (Uarticlers.next()) {
			outputList.add(Uarticlers.getString("article_id"));
		}
		System.out.println(outputList);
		return outputList;
	}
/**
 * 取得使用者評價文章的時間
 * @param user
 * @param article
 * @return
 * @throws SQLException
 * @throws ParseException 
 */
	public static String getRatingStemp(String user, String article) throws SQLException, ParseException{
		PreparedStatement selectRatingTime = null;
		selectRatingTime = DBconnect
				.getConn()
				.prepareStatement(
						"select * from behavior where user_id = ? and article_id = ?");
		selectRatingTime.setString(1, user);
		selectRatingTime.setString(2, article);
		ResultSet ratingTime = selectRatingTime.executeQuery();
		String temp="";
		while (ratingTime.next()){
			temp = ratingTime.getDate("ratingTime").toString();
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = dateFormat.parse(temp);
		long time = (long) date.getTime();
		temp = Long.toString(time);
		System.out.println(temp);
		return temp;
	}
/**
 * 取得使用者有涉及的概念Set: concept_id,topic_id
 * @param user
 * @return
 * @throws SQLException 
 * @throws ClassNotFoundException 
 * @throws IllegalAccessException 
 * @throws InstantiationException 
 */
	public static Set<String> getConcept(String user) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		Set<String> outputSet = new HashSet<String>();
		PreparedStatement selectConcept = null;
		new DBconnect();
		selectConcept = DBconnect
				.getConn()
				.prepareStatement(
						"select * from behavior,concept_article "
						+ "where behavior.user_id = ? "
						+ "and behavior.article_id=concept_article.article_id ");
		selectConcept.setString(1, user);

		ResultSet Uarticlers = selectConcept.executeQuery();
		while (Uarticlers.next()) {
			outputSet.add(Uarticlers.getString("concept_id")+","+Uarticlers.getString("topic_id"));
		}
		System.out.println(outputSet);
		return outputSet;
	}

}
