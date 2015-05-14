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
		if (!file.exists()) {
			try {
				Document Doc = Jsoup.connect(URL).get(); // 使用Jsoup jar 去解析網頁
				Elements title = Doc.select("title"); // 要解析的tag元素為title
				Element content = Doc
						.getElementById("bookDescription_feature_div");// tag id
				System.out.println("Title is " + title.get(0).text()); // 得到title
																		// tag的內容
				System.out.println("content:" + content.text()); // 得到 tag id的內容

				BufferedWriter bw = new BufferedWriter(new FileWriter(output,
						false));
				bw.write(content.text().replace(" Read more Read less", ""));// 取代掉最後多取的字
				bw.close();
				System.out.println("done");
			} catch (org.jsoup.HttpStatusException e) {
				System.out.println("Reconnecting...");
				UrlCrawler(URL, output);
			} catch (java.net.SocketTimeoutException s) {
				System.out.println("Time out! Reconnecting...");
				UrlCrawler(URL, output);
			}
		}
	}
}
