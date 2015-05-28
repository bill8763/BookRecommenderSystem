package document_clustering;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import tagtree.ServerUtil;
import tw.edu.ncu.im.Util.IndexSearchable;

public class Avg_link_all {
	IndexSearchable searcher;

	/**
	 * @param args
	 */
	public void two_article(String filePath1, String filePath2,
			String outputFilePath, IndexSearchable searcher) throws Exception {
		this.searcher = searcher;
		Set<String> termset1 = new HashSet<String>();
		Set<String> termset2 = new HashSet<String>();

		FileReader FileStream1 = null;
		double allNGD = 0;
		int count = 0;
		double sim = 0;
		// -----------�P�_�ɮ׬O�_�s�b----------------------------
		File a = new File(filePath1);
		BufferedWriter bw2 = null;
		if (a.exists())
			FileStream1 = new FileReader(filePath1);
		else {
			bw2 = new BufferedWriter(new FileWriter(filePath1, false));
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
		File b = new File(filePath2);
		BufferedWriter bw3;

		if (b.exists())
			FileStream2 = new FileReader(filePath2);
		else {
			bw3 = new BufferedWriter(new FileWriter(filePath2, false));
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

			String output = filePath1 + "," + filePath2 + ":" + sim;
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
