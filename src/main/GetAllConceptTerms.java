package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import database.DBconnect;
/**
 * 	將所有該概念內文章的核心字詞納入
 * @author chiang
 *
 */
public class GetAllConceptTerms extends DBconnect {
	public GetAllConceptTerms() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		super();
	}
	public static void main(String[] args) throws Exception{
		
		String mainwordsDirPath="D:/K-core/Main_word/";
		String conceptDirPath="D:/DataTemp/Concept_K-core/Number_of_term/";
		GetAllConceptTermsMethod(1,1,mainwordsDirPath,conceptDirPath);
	}

	@SuppressWarnings("resource")
	public static  void GetAllConceptTermsMethod(int concept_id, int topic_id,String mainwordsDir,String outputDir) throws Exception {

		// 尋找主題下的文章
		PreparedStatement select_acticle = null;
		select_acticle = DBconnect.getConn()
				.prepareStatement("select * from concept_article where concept_id = ? and topic_id = ?");
		select_acticle.setInt(1, concept_id);
		select_acticle.setInt(2, topic_id);
		ResultSet articlers = select_acticle.executeQuery();
		ArrayList<String> article_list = new ArrayList<String>();
		while (articlers.next()) {
			article_list.add(articlers.getString("article_id"));
		}
		System.out.println(article_list);

		// 計算多文章共同特徵
		Set<String> term_set = new HashSet<String>(); // 存入(term,搜尋數)
		Map<String, Integer> term_map = new HashMap<String, Integer>(); // 計算term在多少篇文件出現

		for (int i = 0; i < article_list.size(); i++) {
			FileReader FileStream;
			FileStream = new FileReader(mainwordsDir + article_list.get(i));
			@SuppressWarnings("resource")
			BufferedReader BufferedStream = new BufferedReader(FileStream);
			String line = "";

			while ((line = BufferedStream.readLine()) != null) {
				// line = line.replace("+"," ");
				line = "\"" + line.split(",")[0] + "\"" + ","
						+ line.split(",")[1];
				term_set.add(line); // (term,搜尋數)
				if (term_map.get(line.split(",")[0]) != null) {
					int terms = term_map.get(line.split(",")[0]);
					int f_terms = terms + 1;
					term_map.put(line.split(",")[0], f_terms);
				} else {
					term_map.put(line.split(",")[0], 0);
				}

			}
		}
		System.out.println(term_set);
		System.out.println(term_map);
		BufferedWriter bw;
		bw = new BufferedWriter(new FileWriter(outputDir + concept_id + "_"
				+ topic_id + "_number_of_term.txt", false));
		for (String term : term_set) {
			bw.write(term);
			bw.newLine();
			bw.flush(); // 清空緩衝區
		}
	}

}
