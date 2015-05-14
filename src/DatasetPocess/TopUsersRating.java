package DatasetPocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class TopUsersRating {
	public static HashMap<String, String> usersBooks = new HashMap<String, String>();
	private static BufferedWriter bw;
	private static BufferedReader in;

	public static void main(String args[]) throws IOException {
		String input = "C:/Users/chiang/Downloads/dataset/PocessBook.txt";
		String outputDir = "D:/dataset/topUserRatingBook0502/";
		userRaingBook(input, outputDir);
	}

	private static void userRaingBook(String input, String outputDir)
			throws IOException {
		FileReader FileStream = new FileReader(input);
		in = new BufferedReader(FileStream);

		File Dir = new File(outputDir);
		if (!Dir.exists()) {
			Dir.mkdirs();
			try {
				Dir.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String line = "";
		String book = " ";
		String user = "";
		String rate = " ";
		String time = " ";
		String temp = "";
		int count = 0;
		String top25user[] = { "A14OJS0VWMOSWO", "A1D2C0WDCSHUWZ",
				"A1EKTLUL24HDG8", "A1G37DFO8MQW0M", "A1K1JW1C5CUSUZ",
				"A1L43KWWR05PCS", "A1M8PP7MLHNBQB", "A1MC6BFHWY6WC3",
				"A1N1YEMTI9DJ86", "A1S3C5OFU508P3", "A1X8VZWTOG8IS6",
				"A20EEWWSFMZ1PN", "A2EDZH51XHFA9B", "A2F6N60Z96CAJI",
				"A2L7N2U5Z316ZE", "A2NJO6YE954DBH", "A2OJW07GQRNJUT",
				"A2VE83MZF98ITY", "A30KEXFT9SILL6", "A319KYEIAZ3SON",
				"A3M174IC0VXOS2", "A3MV1KKHX51FYT", "A3QVAKVRAH657N",
				"AFVQZQ8PW0L", "AHD101501WCN1" };

		while ((line = in.readLine()) != null) {
			book = line.split("  ")[0];
			user = line.split("  ")[2];
			rate = line.split("  ")[4];
			time = line.split("  ")[5];
			temp = "";
			for (int i = 0; i < top25user.length; i++) {
				if (user.equals(top25user[i])) {
					if (!usersBooks.containsKey(user)) {
						usersBooks.put(user, book + "  " + rate + "  " + time
								+ "\n");
					} else {
						temp = usersBooks.get(user) + book + "  " + rate + "  "
								+ time + "\n";
						usersBooks.put(user, temp);
					}
				}
			}
			System.out.println(++count);
		}

		for (Object key : usersBooks.keySet()) {
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputDir
					+ key + ".txt", false));
			bw.write(usersBooks.get(key));
			bw.flush();
		}

	}
}
