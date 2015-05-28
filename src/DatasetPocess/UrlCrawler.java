package DatasetPocess;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 
 * @author chiang
 *
 */
public class UrlCrawler {
	public static void main(String args[]) throws IOException {
		String URL = "http://www.amazon.com/dp/0974343897";
		String output = "D:/dataset/6_Abstract.txt";
		UrlCrawler(URL, output);
	}

	static void UrlCrawler(String URL, String output) throws IOException {
		File file = new File(output);
		/** 存在表示已經找過了，不用再找一次 */
		if (!file.exists()) {
			try {
				Document Doc = Jsoup.connect(URL).get(); // �ϥ�Jsoup jar
															// �h�ѪR����
				Elements title = Doc.select("title"); // �n�ѪR��tag������title
				Element content = Doc
						.getElementById("bookDescription_feature_div");// tag id
				System.out.println("Title is " + title.get(0).text()); // �o��title
				if (content == null) {
					BufferedWriter bw = new BufferedWriter(new FileWriter(
							output, false));
					bw.write("");
					bw.close();
				} else {
					System.out.println("content:" + content.text()); // �o�� tag
																		// id�����e
					BufferedWriter bw = new BufferedWriter(new FileWriter(
							output, false));
					bw.write(content.text().replace(" Read more Read less", ""));// ���N���̫�h�����r
					bw.close();
				}
				System.out.println("done");
			} catch (org.jsoup.HttpStatusException e) {
				System.out.println("Reconnecting...");
				UrlCrawler(URL, output);
			} catch (java.net.SocketTimeoutException s) {
				System.out.println("Time out! Reconnecting...");
				UrlCrawler(URL, output);
			} catch (java.net.UnknownHostException u) {
				System.out.println("Reconnecting...");
				UrlCrawler(URL, output);
			}
		}
	}
}
