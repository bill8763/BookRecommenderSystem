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
		String URL = "http://www.amazon.com/Business-Security-T-A-Brown/dp/0974343897";
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
			Element categorydiv=Doc.getElementById("wayfinding-breadcrumbs_container");
			Elements cate = Doc.getElementsByClass("a-list-item");
			Elements cc = Doc.select("div");
			 Elements scriptElements = Doc.getElementsByTag("script");

			 for (Element element :scriptElements ){                
			        for (DataNode node : element.dataNodes()) {
			            System.out.println(node.getWholeData());
			        }
			        System.out.println("-------------------");            
			  }
			if (title.get(0).equals("404 - Document Not Found")) {
				category = "";
			} else {
				Elements content = Doc.select("li");
						//.getElementById("wayfinding-breadcrumbs_feature_div");// tag
																				// id
				System.out.println("Title is " + title.get(0).text()); // �o��title

				System.out.println("done");
				category = categorydiv.toString();
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
		return category;
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
