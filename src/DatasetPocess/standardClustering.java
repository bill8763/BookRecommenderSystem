package DatasetPocess;

import java.awt.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class standardClustering {
	public static void main(String[] args) throws Exception {
		HashMap<String, Set<String>> categoryMap = new HashMap<>();
		String output = "D:/dataset/processing_0.7/standardClustering.txt";
		String URL = "http://www.amazon.com/s/ref=nb_sb_noss?url=search-alias%3Dstripbooks&field-keywords=";
		String fileListPath = "D:/dataset/processing_0.7/concept/ConceptList.txt";
		BufferedReader br = new BufferedReader(new FileReader(fileListPath));
		String line = "";

		ArrayList<String> allFile = new ArrayList<>();

		while ((line = br.readLine()) != null) {
			String lineFile[] = line.split(":")[1].split(",");
			for (String f : lineFile) {
				allFile.add(f);
			}
		}

		for (String i : allFile) {
			String category = UrlCrawler.getBookCategory(URL
					+ i.replaceAll(".txt", ""));
			Set categorySet = new HashSet();
			if(categoryMap.get(category)!=null){
				categorySet = categoryMap.get(category);
			}
			categorySet.add(i);
			categoryMap.put(category, categorySet);
			System.out.println(category + categorySet);
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(output, false));
		for (String i : categoryMap.keySet()) {
			bw.write(i +":"+ categoryMap.get(i));
			bw.newLine();
			bw.flush();
		}
		bw.close();
	}
}
