package database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import DatasetPocess.fileList;

public class behaviorTransformer {
	static String userID = "A1K1JW1C5CUSUZ";
	//A14OJS0VWMOSWO
	static String path = "D:/dataset/userRatingTimeOrder/" + userID + ".txt";
	static String timeStart = "2008/01/01";
	static String timeEnd = "2008/06/01";/**需要設定成結束日+1*/

	public static void main(String[] args) throws IOException, ParseException,
			SQLException, InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		userBehaviorTransform();
	}

	public static void userBehaviorTransform() throws IOException,
			ParseException, SQLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {

		BufferedReader BufferedStream = new BufferedReader(new FileReader(path));
		String line = "";
		/** DB */
		new DBconnect();
		/** time parse */
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date startDate = dateFormat.parse(timeStart);
		long startTime = (long) startDate.getTime() / 1000;
		Date endDate = dateFormat.parse(timeEnd);
		long endTime = (long) endDate.getTime() / 1000;
		
		String mainWordsPath = "D:/dataset/mainWords/";
		java.util.List<String> mainWordsList = fileList.getFileList(mainWordsPath);
		

		while ((line = BufferedStream.readLine()) != null) {
			if (Long.parseLong(line.split("  ")[2]) >= startTime
					&& Long.parseLong(line.split("  ")[2]) <= endTime 
					&& mainWordsList.contains(line.split("  ")[0]+".txt")) {
				PreparedStatement insertBehavior = null;
				insertBehavior = DBconnect.getConn().prepareStatement(
						"INSERT INTO `behavior`(`user_id`, `article_id`, `ratingTime`)"
								+ "VALUES (?,?,?)");
				insertBehavior.setString(1, userID);
				insertBehavior.setString(2, line.split("  ")[0]+".txt");
				insertBehavior.setString(3, line.split("  ")[2]);
				insertBehavior.setTimestamp(3,
						new Timestamp(Long.parseLong(line.split("  ")[2])*1000));
				insertBehavior.executeUpdate();
			}
		}
		BufferedStream.close();
	}
}
