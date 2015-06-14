package DatasetPocess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * get user abstract
 * 取得使用者某段時間有評價的書
 * 並到亞馬遜上抓取abstract
 * NOTE: 多次reconnecting 連不上時請重跑
 * 有找過的不會再找一次
 * 若還是一樣需要手動上網看問題
 * 若網站尚無資訊則手動將此behavior刪掉
 * @author chiang
 *
 */
public class getAbstract {
	public static void main(String args[]) throws IOException, ParseException {
		/***/
		// String input = "D:/dataset/BookIdTitle.txt";
		String input = "D:/dataset/BookIdTitle.txt";
		String dir = "D:/dataset/BookAbstract";
		/**處理BookIdTitle*/
		//getAbstract(input, dir);
		
		String startDateString = "2008/01/01";
		String endDateString = "2008/07/01"; /**比結束日多一天*/
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date startDate = dateFormat.parse(startDateString);
		long timeStart = (long) startDate.getTime()/1000;
		Date endDate = dateFormat.parse(endDateString);
		long timeEnd = (long) endDate.getTime()/1000;
		/**
		 * A14OJS0VWMOSWO
		 * AFVQZQ8PW0L
		 * A1D2C0WDCSHUWZ
		 * AHD101501WCN1
		 * A1X8VZWTOG8IS6 <--太多有聲書 來源太混亂
		 * A1K1JW1C5CUSUZ
		*/
		input = "D:/dataset/userRatingTimeOrder/A1K1JW1C5CUSUZ.txt";
		dir = "D:/dataset/A1K1JW1C5CUSUZ_Abstract/";
		/**若dir不存在，先建立*/
		File Dir = new File(dir);
		if (!Dir.exists()) {
			Dir.mkdirs();
			try {
				Dir.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		getUserAbstract(input, dir, timeStart, timeEnd);
	}

	/** 由BookIdTitle取得Abstract */
	private static void getAbstract(String input, String dir)
			throws IOException {
		FileReader FileStream = new FileReader(input);
		BufferedReader in = new BufferedReader(FileStream);
		String line = "";
		while ((line = in.readLine()) != null) {
			String BookId = line.split(" :: ")[0];
			UrlCrawler.getBookDescription("http://www.amazon.com/dp/" + BookId, dir
					+ "/" + BookId + ".txt");
			System.out.println(dir + "/" + BookId + ".txt is done!");
		}
	}

	/**
	 * 由user取得user讀過書籍的abstract
	 * 
	 * @param input
	 * @param dir
	 * @param timeStart
	 *            設定某段時間的閱讀書籍
	 * @param timeEnd
	 * @throws IOException
	 */
	private static void getUserAbstract(String input, String dir,
			long timeStart, long timeEnd) throws IOException {
		FileReader FileStream = new FileReader(input);
		BufferedReader in = new BufferedReader(FileStream);
		String line = "";
		while ((line = in.readLine()) != null) {
			if (Long.valueOf(line.split("  ")[2]) >= timeStart && Long.valueOf(line.split("  ")[2]) <= timeEnd) {
				String BookId = line.split("  ")[0];
				UrlCrawler.getBookDescription("http://www.amazon.com/dp/" + BookId, dir
						+ "/" + BookId + ".txt");
				System.out.println(dir + BookId + ".txt is done!");
			}
		}

	}
}
