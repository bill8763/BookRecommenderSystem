package document_clustering;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import tw.edu.ncu.im.Util.IndexSearchable;

public class Avg_link_all {
	 IndexSearchable searcher;

	/**
	 * @param args
	 */
	public void two_article(String fileDir, String fileID1, String fileID2,
			String outputFilePath, IndexSearchable searcher) throws Exception {
		this.searcher = searcher;
		Set<String> termset1 = new HashSet<String>();
		Set<String> termset2 = new HashSet<String>();

		FileReader FileStream1 = null;
		double allNGD = 0;
		int count = 0;
		double sim = 0;
		// -----------�P�_�ɮ׬O�_�s�b----------------------------
		File a = new File(fileDir + fileID1);
		BufferedWriter bw2 = null;
		if (a.exists())
			FileStream1 = new FileReader(fileDir + fileID1);
		else {
			bw2 = new BufferedWriter(new FileWriter(fileDir + fileID1, false));
			bw2.flush();
			bw2.close();
		}
		// ---------------------------------------------------
		BufferedReader BufferedStream1 = null;
		BufferedStream1 = new BufferedReader(FileStream1);
		String e2 = "";

		while ((e2 = BufferedStream1.readLine()) != null) {

			termset1.add(e2);
		}
		BufferedStream1.close();
		// -----------�P�_�ɮ׬O�_�s�b----------------------------
		FileReader FileStream2 = null;
		File b = new File(fileDir + fileID2);
		BufferedWriter bw3;

		if (b.exists())
			FileStream2 = new FileReader(fileDir + fileID2);
		else {
			bw3 = new BufferedWriter(new FileWriter(fileDir + fileID2, false));
			bw3.flush();
			bw3.close();
		}
		// ---------------------------------------------------
		BufferedReader BufferedStream2 = null;
		BufferedStream2 = new BufferedReader(FileStream2);
		String e3 = "";

		while ((e3 = BufferedStream2.readLine()) != null) {
			termset2.add(e3);
		}
		BufferedStream2.close();

		if (termset1 != null && termset2 != null) {
			for (String term : termset1) {
				for (String term2 : termset2) {
					if (!term.split(",")[0].equals(term2.split(",")[0])) {
						System.out.println(term.split(",")[0] + ","
								+ term2.split(",")[0]);
						long SearchResult = 0;
						try {
							SearchResult = this.searcher
									.searchMultipleTerm(new String[] {
											term.split(",")[0],
											term2.split(",")[0] });
						} catch (SolrServerException e) {// Retry
							try {
								SearchResult = this.searcher
										.searchMultipleTerm(new String[] {
												term.split(",")[0],
												term2.split(",")[0] });
							} catch (SolrServerException e1) {
								e1.printStackTrace();
							}
						}
						/** end catch */

						double x = Double.parseDouble(String.valueOf(term
								.split(",")[1]));
						double y = Double.parseDouble(String.valueOf(term2
								.split(",")[1]));
						double m = SearchResult;
						double NGD = NGD_cal(x, y, m);

						if (NGD < 1) {
							allNGD = allNGD + NGD;
							count = count + 1;
						} else if (NGD >= 1) {
							NGD = 1;
							allNGD = allNGD + NGD;
							count = count + 1;
						}

					}
				}
			}
			if (count == 0)
				sim = 0;
			else
				sim = 1 - ((double) 1 / (count)) * allNGD;

			String output = fileID1 + "," + fileID2 + ":" + sim;
			BufferedWriter bw;

			try {
				bw = new BufferedWriter(new FileWriter(outputFilePath, true));
				bw.write(output);
				bw.newLine();
				bw.flush();
				bw.close();
			} catch (IOException f) {
				f.printStackTrace();
			}
		}
	}

	public  Double conceptArticleSim(String dir, String concept_id,
			String topic_id, String articlePath, IndexSearchable searcher)
			throws Exception {
		this.searcher = searcher;
		Set<String> termset1 = new HashSet<String>();
		Set<String> termset2 = new HashSet<String>();
		Map<String, Double> ngd_map = new HashMap<String, Double>();

		FileReader FileStream1 = new FileReader(articlePath);
		BufferedReader BufferedStream1 = new BufferedReader(FileStream1);
		FileReader FileStream2 = new FileReader(dir
				+ "Concept_K-core/Main_word/" + concept_id + "_" + topic_id
				+ "_main_word.txt");
		BufferedReader BufferedStream2 = new BufferedReader(FileStream2);
		FileReader FileStream3 = new FileReader(dir + "Concept_K-core/NGD/"
				+ concept_id + "_" + topic_id + "_nNGD.txt");
		BufferedReader BufferedStream3 = new BufferedReader(FileStream3);

		double allNGD = 0;
		double sim = 0;
		double ngd = 0;
		int count = 0;
		int article_count = 0;
		int concept_count = 0;
		int N_weight = 10; // 設定取幾個特徵
		String line1, line2, line3;

		while ((line1 = BufferedStream1.readLine()) != null) {
			if (article_count < N_weight)
				termset1.add(line1);
			else
				break;
			article_count++;
		}
		while ((line2 = BufferedStream2.readLine()) != null) {
			if (concept_count < N_weight)
				termset2.add(line2);
			else
				break;
			concept_count++;
		}
		while ((line3 = BufferedStream3.readLine()) != null) {
			String key = line3.split(";")[0];
			Double value = Double.parseDouble(line3.split(";")[1]);
			ngd_map.put(key, value);
		}
		if (termset1 != null && termset2 != null) {
			for (String term : termset1) {
				for (String term2 : termset2) {
					if (!term.split(",")[0].equals(term2.split(",")[0])) {
						String key1 = term.split(",")[0] + term2.split(",")[0];
						String key2 = term2.split(",")[0] + term.split(",")[0];
						if (ngd_map.get(key1) != null) {
							if (ngd_map.get(key1).isInfinite()) {
								ngd = 1;
							} else {
								ngd = ngd_map.get(key1);
							}
						} else if (ngd_map.get(key2) != null) {
							if (ngd_map.get(key2).isInfinite()) {
								ngd = 1;
							} else {
								ngd = ngd_map.get(key2);
							}
						} else {
							long SearchResult = 0;
							try {
								SearchResult = this.searcher
										.searchMultipleTerm(new String[] {
												term.split(",")[0],
												term2.split(",")[0] });
							} catch (SolrServerException e) {// Retry
								try {
									SearchResult = this.searcher
											.searchMultipleTerm(new String[] {
													term.split(",")[0],
													term2.split(",")[0] });
								} catch (SolrServerException e1) {
									e1.printStackTrace();
								}
							}
							/** end catch */
							double x = Double.parseDouble(String.valueOf(term
									.split(",")[1]));
							double y = Double.parseDouble(String.valueOf(term2
									.split(",")[1]));
							double m = SearchResult;
							ngd = NGD_cal(x, y, m);
						}
						if (ngd < 1) {
							allNGD = allNGD + ngd;
							count = count + 1;
						} else if (ngd >= 1) {
							ngd = 1;
							allNGD = allNGD + ngd;
							count = count + 1;
						}
					}
				}
			}
			if (count == 0)
				sim = 0;
			else
				sim = 1 - ((double) 1 / (count)) * allNGD;
		}
		return sim;
	}

	static double NGD_cal(double x, double y, double m) {
		double logX = Math.log10(x);
		double logY = Math.log10(y);
		double logM = Math.log10(m);

		double logN = 6.4966;

		double NGD = (Math.max(logX, logY) - logM)
				/ (logN - Math.min(logX, logY));
		return NGD;
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Avg_link_all pair = new Avg_link_all();

	}

}
