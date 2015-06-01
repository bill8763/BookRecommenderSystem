package concept_k_core;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Scanner;

import tw.edu.ncu.im.Util.HttpIndexSearcher;
import database.DBconnect;

public class Main_kcore {
	static String path = "D:/dataset/Concept_K-core/";

	public void kcore(int concept_id, int topic_id) throws Exception {
		File test = new File(path);
		if (!test.exists()) {
			test.mkdirs();
		}
		HttpIndexSearcher searcher = new HttpIndexSearcher();
		searcher.url = "http://140.115.82.105/searchweb/";
		new prepocess();
		prepocess.mainwordPath = "D:/dataset/A14OJS0VWMOSWO_mainWords/";
		prepocess.path = path;
		prepocess.getConceptTerms(concept_id, topic_id);
		Lucene_Search2.doit(concept_id, topic_id, path,searcher);
		// google_filter2.search_filter(i);
		Stem.stemming(concept_id, topic_id, path);
		NGD_calculate.NGD(concept_id, topic_id, path);
		Result_Rank.ranking(concept_id, topic_id, path);
		K_core kcore = new K_core(path);
		kcore.K_core_cal(concept_id, topic_id);

	}

	@SuppressWarnings("resource")
	public static void main(String args[]) throws Exception {
		// System.out.println("K-core Running");
		// Scanner sc = new Scanner(System.in);
		// System.out.print("concept_id");
		// int concept_id = sc.nextInt(); // �}�l�s��
		// System.out.print("topic_id:");
		// int topic_id = sc.nextInt();
		// Main_kcore m_kcore = new Main_kcore();
		// m_kcore.kcore(concept_id, topic_id);
		// System.out.println("finish!!!");
		new DBconnect();
		PreparedStatement select_acticle = null;
		select_acticle = DBconnect.getConn().prepareStatement(
				"select * from concept_article");
		ResultSet articlers = select_acticle.executeQuery();
		Main_kcore m_kcore = new Main_kcore();
		while (articlers.next()) {
			m_kcore.kcore(Integer.parseInt(articlers.getString("concept_id")),
					Integer.parseInt(articlers.getString("topic_id")));
		}

	}

}
