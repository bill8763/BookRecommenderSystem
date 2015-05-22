


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JTextArea;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;



public class IndexStatus2 {
	public static void indexed(String textArea, String no) throws Exception{
		System.out.println("process: " + textArea);
		long runstartTime = System.currentTimeMillis(); // ���X�ثe�ɶ�
		ServerUtil.initialize();
		// initialize query
		SolrQuery query = new SolrQuery();
		//show info of all files
		query.setQuery(textArea);
		QueryResponse rsp = ServerUtil.execQuery(query);
		SolrDocumentList docs = rsp.getResults();
		//System.out.println(docs.getNumFound());
		
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter("D:/K-core/Search2/" + no + "_"
					+ "Lucene_output2.txt", true));
			if (docs.getNumFound() != 0) {
				String test = "���� " + docs.getNumFound() + " �����G"; 
				bw.write(String.valueOf(test));
				bw.newLine();
				bw.flush(); // �M�Žw�İ�
				bw.close(); // ����BufferedWriter����
				// �إ߹B�νw�İϿ�X��Ʀ�data.txt�ɪ�BufferedWriter����
				// �A�å�bw����ѦҤޥ�
				// �N�r��g�J�ɮ�
			} else if (docs.getNumFound() == 0) {
				bw.write("���� 0 �����G\r\n");
				bw.close(); // ����BufferedWriter����
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("finish: " + textArea);
		System.out.println("����r��O�ɶ�" + (System.currentTimeMillis() - runstartTime));
		
		//textArea.append("\n\n=== Show indexed docs ===");
		//textArea.append("\n 1. Docs indexed:" + docs.getNumFound());
	}
}