package DatasetPocess;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
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
		String URL = "http://www.amazon.com/s/ref=nb_sb_noss?url=search-alias%3Dstripbooks&field-keywords=0812916883";
//		String URL="http://www.amazon.com/dp/0974343897";
		String output = "D:/dataset/6_Abstract.txt";
		getBookCategory(URL);
		//		getBookDescription(URL, output);
	}

	/** 抓取書籍類別 */
	static String getBookCategory(String URL) throws IOException {
		String category = "";
		try {
			Document Doc = Jsoup.connect(URL).get(); // Jsoup jar
			Elements title = Doc.select("title"); // 抓title
			Elements cate = Doc.getElementsByClass("refinementLink");			
//			Elements cate = Doc.getElementsByClass("zg_hrsr_ladder");	
//			Element categorydiv=Doc.getElementById("wayfinding-breadcrumbs_container");
//			Elements span = Doc.select("span");			
//			Elements cc = Doc.select("div");
//			Elements scriptElements = Doc.getElementsByTag("script");
//			Elements content = Doc.select("li");
//		.getElementById("wayfinding-breadcrumbs_feature_div");// tag
																				// id
			if (title.get(0).equals("404 - Document Not Found")) {
				category = "";
			} else {
				System.out.println("Title is " + title.get(0).text()); // �o��title
				System.out.println("done");
				category = cate.toString();
				System.out.println(category);
			}
		} catch (org.jsoup.HttpStatusException e) {
			System.out.println("Reconnecting...");
			getBookCategory(URL);
		} catch (java.net.SocketTimeoutException s) {
			System.out.println("Time out! Reconnecting...");
			getBookCategory(URL);
		} catch (java.net.UnknownHostException u) {
			System.out.println("Reconnecting...");
			getBookCategory(URL);
		}
		String output =category.split("</span>")[0].replaceAll("<span class=\"refinementLink\">", "").replaceAll("&amp","");
		System.out.print(output);
		return output;
	}

	/** 抓取書籍簡介 */
	static void getBookDescription(String URL, String output)
			throws IOException {
		File file = new File(output);
		/** 存在表示已經找過了，不用再找一次 */
		if (!file.exists()) {
			try {
				Document Doc = Jsoup.connect(URL).get(); // �ϥ�Jsoup jar
				Elements title = Doc.select("title"); // �n�ѪR��tag������title
				if (title.get(0).text().equals("404 - Document Not Found")) {
					BufferedWriter bw = new BufferedWriter(new FileWriter(
							output, false));
					bw.write("");
					bw.close();
					output = "";
				} else {
					Element content = Doc
							.getElementById("bookDescription_feature_div");// tag
																			// id
					System.out.println("Title is " + title.get(0).text()); // �o��title
					if (content == null) {
						BufferedWriter bw = new BufferedWriter(new FileWriter(
								output, false));
						bw.write("");
						bw.close();
					} else {
						System.out.println("content:" + content.text()); // �o��
																			// tag
																			// id�����e
						BufferedWriter bw = new BufferedWriter(new FileWriter(
								output, false));
						bw.write(content.text().replace(" Read more Read less",
								""));// ���N���̫�h�����r
						bw.close();
					}
				}
				System.out.println("done");
			} catch (org.jsoup.HttpStatusException e) {
				System.out.println("Reconnecting...");
				getBookDescription(URL, output);
			} catch (java.net.SocketTimeoutException s) {
				System.out.println("Time out! Reconnecting...");
				getBookDescription(URL, output);
			} catch (java.net.UnknownHostException u) {
				System.out.println("Reconnecting...");
				getBookDescription(URL, output);
			}
		}
	}
}
