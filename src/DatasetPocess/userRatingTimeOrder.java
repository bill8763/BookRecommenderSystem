package DatasetPocess;

import java.awt.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class userRatingTimeOrder {
	static HashMap<String, String> users = new HashMap<>();
	static BufferedWriter bw;

	public static void main(String args[]) throws IOException {
		String inputDir = "D:/dataset/topUserRatingBook0502/";
		String outputDir = "D:/dataset/userRatingTimeOrder/";
		File Dir = new File(outputDir);
		if (!Dir.exists()) {
			Dir.mkdirs();
			try {
				Dir.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		getUsers(inputDir, outputDir);
		for (String user : users.keySet()) {
			sortByTime(inputDir + user + ".txt", outputDir + user + ".txt");

		}
	}

	private static void getUsers(String inputDir, String outputDir) {
		File a = new File(inputDir);
		String[] filenames;
		String fullpath = a.getAbsolutePath();
		/**
		 * 看input資料夾內有哪些檔案 將這些user存入hashmap
		 */
		if (a.isDirectory()) {
			filenames = a.list();
			for (int i = 0; i < filenames.length; i++) {
				File tempFile = new File(fullpath + "\\" + filenames[i]);
				if (tempFile.isDirectory()) {
					System.out.println("目錄:" + filenames[i]);
				} else
					users.put(filenames[i].split(".txt")[0],
							filenames[i].split(".txt")[0]);
				System.out.print("\"" + filenames[i].split(".txt")[0] + "\""
						+ ",");
			}
		} else
			System.out.println("[" + a + "]不是目錄");
	}

	private static void sortByTime(String user, String output)
			throws IOException {
		FileReader FileStream = new FileReader(user);
		BufferedReader in = new BufferedReader(FileStream);
		bw = new BufferedWriter(new FileWriter(output, false));
		TreeMap<String, Long> times = new TreeMap();

		String line = "";
		SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd");
		Timestamp time;
		while ((line = in.readLine()) != null) {
			times.put(line, Long.valueOf(line.split("  ")[2]));
		}
		/**
		 * 用list排序
		 */
		ArrayList<Entry<String, Long>> list = new ArrayList<Entry<String, Long>>(
				times.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Long>>() {
			public int compare(Map.Entry<String, Long> entry1,
					Map.Entry<String, Long> entry2) {
				return (int) ( entry1.getValue() - entry2.getValue());
			}
		});
		/**
		 * 輸出
		 */
		for (Entry<String, Long> entry : list) {
			time = new Timestamp(times.get(entry.getKey()) * 1000);
			System.out.println(times.get(entry.getKey()) + " " + ft.format(time));
			bw.write(entry.getKey() + "  " + ft.format(time) + "\n");
			bw.flush();
		}
	}

}
