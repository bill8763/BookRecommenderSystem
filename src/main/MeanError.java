package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import tw.edu.ncu.im.Util.HttpIndexSearcher;
import database.DBconnect;
import document_clustering.Avg_link_all;

public class MeanError {
	static String userID = "A1K1JW1C5CUSUZ";
	/**
	 * A14OJS0VWMOSWO
	 * AFVQZQ8PW0L
	 * A1D2C0WDCSHUWZ
	 * AHD101501WCN1
	 * A1K1JW1C5CUSUZ
	 * */
	static double maxInterest=0.08703609516873179;
	static String path = "D:/dataset/userRatingTimeOrder/" + userID + ".txt";
	static String dir = "D:/dataset/";
	/**評估結果時間區間*/
	static String timeStart = "2008/05/01";
	static String timeEnd = "2008/07/01"; /**需要設定成結束日+1*/
	static String articlePath = "D:/dataset/mainWords/";

	public static void main(String[] args) throws Exception {
		System.out.println(getMSE(userID, timeStart, timeEnd));
	}

	public static Double getMSE(String user, String testStart, String testEnd)
			throws Exception {
		/** 於測試期間的書籍當作評估標準 */
		ArrayList<String> ratingRow = new ArrayList<>();
		userID = user;
		path = "D:/dataset/userRatingTimeOrder/" + userID + ".txt";
		timeStart = testStart;
		timeEnd = testEnd;
		new DBconnect();
		HttpIndexSearcher searcher = new HttpIndexSearcher();
		HttpIndexSearcher.url = "http://140.115.82.105/searchweb/";
		
		/** parse time */
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date startDate = dateFormat.parse(timeStart);
		long startTime = (long) startDate.getTime() / 1000;
		Date endDate = dateFormat.parse(timeEnd);
		long endTime = (long) endDate.getTime() / 1000;
		/** 先取得此段時間的所有書籍與rating */
		BufferedReader BufferedStream = new BufferedReader(new FileReader(path));
		String line = "";
		while ((line = BufferedStream.readLine()) != null) {
			if (Long.parseLong(line.split("  ")[2]) >= startTime
					&& Long.parseLong(line.split("  ")[2]) <= endTime) {
				ratingRow.add(line);
			}
		}
		BufferedStream.close();
		double sumOfError=0;
		int count=0;
		/** 取得user有涉及的概念 */
		Set<String> conceptSet = findUserRatingInformation.getConcept(user);
		
		/** 取得這段時間內有評價的書籍所屬概念*/
		for (String row : ratingRow) {
			double conceptInterest=0;
			String concept="";
			String topic="";
			
			PreparedStatement selectConcept = null;
			selectConcept = DBconnect.getConn().prepareStatement(
					"select * from concept_article "
							+ "where article_id = ? ");
			selectConcept.setString(1, row.split("  ")[0]);
			ResultSet testArticle = selectConcept.executeQuery();

			if (testArticle.getRow()!=0) {
				concept=testArticle.getString("concept_id");
				topic=testArticle.getString("topic_id");
			}
			else if (!new File(articlePath + row.split("  ")[0]+".txt").exists()){
				continue;
			}
			else{
				/** 計算每個概念的相似度 */
				double maxSim=0.0;
				for (String conceptAndTopic : conceptSet) {
					String tempConcept = conceptAndTopic.split(",")[0];
					String tempTopic = conceptAndTopic.split(",")[1];
					Avg_link_all docLink = new Avg_link_all();
					Double sim = docLink.conceptArticleSim(dir, tempConcept,
							tempTopic, articlePath + row.split("  ")[0]+".txt" ,  searcher);
					if(sim>maxSim){
						maxSim=sim;
						concept=tempConcept;
						topic=tempTopic;
					}
				}
					System.out.print(maxSim+"\t");				
			}
			PreparedStatement selectProfile = null;
			selectProfile = DBconnect.getConn().prepareStatement(
					"select * from profile "
							+ "where concept_id = ? and topic_id = ? ");
			selectProfile.setString(1, concept);
			selectProfile.setString(2, topic);
			ResultSet interestResult = selectProfile.executeQuery();
			interestResult.next();			
			conceptInterest = Double.parseDouble(interestResult.getString("interest"));
			
			/** 5.0可能是80%~100% 
			 * so 取平均值 5.0 --> 0.9*/
			double error = Math.abs( (Double.parseDouble(row.split("  ")[1])-0.5)/5 - (conceptInterest/maxInterest) );
			sumOfError = sumOfError + error ;
			count++;
			System.out.println(conceptInterest+" "+sumOfError +" -->"+sumOfError/count);
		}
		double MAE = sumOfError/count;
		return MAE;
	}
}
