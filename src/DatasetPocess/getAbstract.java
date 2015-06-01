package DatasetPocess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class getAbstract {
	public static void main(String args[]) throws IOException {
		/***/
		// String input = "D:/dataset/BookIdTitle.txt";
		String input = "D:/dataset/BookIdTitle.txt";
		String dir = "D:/dataset/BookAbstract";
		/**處理BookIdTitle*/
		//getAbstract(input, dir);
		String timeStart ="978480000";
		String timeEnd = "1008460800";
		input = "D:/dataset/userRatingTimeOrder/A14OJS0VWMOSWO.txt";
		dir = "D:/dataset/A14OJS0VWMOSWO_Abstract";
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
			String timeStart, String timeEnd) throws IOException {
		FileReader FileStream = new FileReader(input);
		BufferedReader in = new BufferedReader(FileStream);
		String line = "";
		while ((line = in.readLine()) != null) {
			if (Long.valueOf(line.split("  ")[2]) >= Long
					.valueOf(timeStart) && Long.valueOf(line.split("  ")[2]) <= Long
					.valueOf(timeEnd)) {
				String BookId = line.split("  ")[0];
				UrlCrawler.getBookDescription("http://www.amazon.com/dp/" + BookId, dir
						+ "/" + BookId + ".txt");
				System.out.println(dir + "/" + BookId + ".txt is done!");
			}
		}

	}
}
