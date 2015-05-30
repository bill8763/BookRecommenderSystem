package tagtree;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import tw.edu.ncu.im.Util.IndexSearchable;

public class TagTree_Preprocess {
	private static BufferedReader bufferedStream4;
	public static String mainwordPath = "";
	public static String numberOfPairPath = "";
	static IndexSearchable searcher;

	public static void TagTree_Preprocess_test(String dirPath) throws Exception {
		File fr1 = new File(dirPath + "ConceptState.txt");
		FileReader FileStream1 = new FileReader(fr1);
		BufferedReader BufferedStream1 = new BufferedReader(FileStream1);
		String line1 = "";
		ArrayList<String> list1 = new ArrayList<String>();
		while ((line1 = BufferedStream1.readLine()) != null)
			list1.add(line1);

		BufferedStream1.close();

		// �B�z
		String concept = "";
		String state = "";
		LinkedList<String> document;
		for (String str1 : list1) {
			concept = str1.split(":")[0].split(",")[0];
			state = str1.split(":")[0].split(",")[1];
			document = new LinkedList<String>();

			if (state.equals("C")) // �N��s�W�����s
			{
				String arr1[] = str1.split(":")[1].split(",");
				for (int i = 0; i < arr1.length; i++)
					document.add(arr1[i]);

				createConcept(dirPath, concept, document);
			} else if (state.equals("D")) // �N��R�������s
			{
				deleteConcept(dirPath, concept);
			}
			/*
			 * else if(state.equals("Y")) // �N���s�����s { String arr1[] =
			 * str1.split(":")[1].split(","); for(int i=0; i<arr1.length; i++)
			 * document.add(arr1[i]);
			 * 
			 * updateConcept(concept,document); }
			 */
		}

	}

	// �إ߸s��
	public static void createConcept(String conceptDirPath, String concept,
			LinkedList<String> document) throws Exception {

		System.out.println(document);
		// ��J
		File dir = new File(conceptDirPath + concept + "_File");
		dir.mkdir();

		Hashtable<String, String> oneGram = new Hashtable<String, String>(); // ��r��<�r��,�j�M���G��>
		Hashtable<String, String> twoGram = new Hashtable<String, String>(); // ���r��<�r��,�j�M���G��>
		Map<String, String> twoGram_map = new HashMap<String, String>(); // ���r������map
		ArrayList<String> oneTerm = new ArrayList<String>();

		int size = document.size();

		for (int i = 0; i < size; i++) {

			File fr1 = new File(mainwordPath + document.get(i));
			File fr2 = new File(numberOfPairPath + document.get(i));

			FileReader FileStream1 = new FileReader(fr1);
			FileReader FileStream2 = new FileReader(fr2);
			BufferedReader BufferedStream1 = new BufferedReader(FileStream1);
			BufferedReader BufferedStream2 = new BufferedReader(FileStream2);
			String line1 = "";
			String line2 = "";

			while ((line1 = BufferedStream1.readLine()) != null) {
				oneGram.put(line1.split(",")[0].replace("+", " "),
						line1.split(",")[1]);

			}
			Vector<String> v = new Vector<String>(oneGram.keySet());
			Collections.sort(v);
			Iterator o = v.iterator();
			String key = "";
			String value = "";
			String output = "";
			while (o.hasNext()) {
				key = (String) o.next();
				oneTerm.add(key);

			}
			while ((line2 = BufferedStream2.readLine()) != null) {
				line2 = line2.toLowerCase(); // ��p�g
				line2 = line2.replace("+", " "); // ��榡
				twoGram_map.put(line2.split(",")[0], line2.split(",")[1]);
			}
		}
		System.out.println(oneTerm);
		for (int j = 0; j < oneTerm.size(); j++) {
			for (int k = j + 1; k < oneTerm.size(); k++) {
				String term1 = oneTerm.get(j);
				String term2 = oneTerm.get(k);
				String index1 = "\"" + term1 + "\"" + " " + "\"" + term2 + "\"";
				// String index2 = "\""+term2+"\""+" "+"\""+term1+"\"";

				if (twoGram_map.get(index1) != null) {
					twoGram.put(index1, twoGram_map.get(index1));
				}

				else if (twoGram_map.get(index1) == null) {
					System.out.println(index1);

					long SearchResult=0;
					try {
						SearchResult = searcher
								.searchMultipleTerm(new String[] { term1, term2 });
					} catch (SolrServerException e) {// Retry
						try {
							SearchResult = searcher
									.searchMultipleTerm(new String[] { term1,
											term2 });
						} catch (SolrServerException e1) {
							e1.printStackTrace();
						}
					}
					String term_pair = Long.toString(SearchResult);
					twoGram.put(index1, term_pair);
				}
			}
		}

		// ��X
		outputHashtable(concept, oneGram, twoGram);
	}

	// �R���s��
	public static void deleteConcept(String dirPath, String concept) {
		File dir = new File(dirPath + concept + "_File");
		String fileList[] = dir.list();
		for (String str : fileList) {
			File file = new File(dirPath + concept + "_File\\" + str);
			file.delete();
		}
		dir.delete();
	}

	// ��s�s��
	public static void updateConcept(String concept, LinkedList<String> document)
			throws IOException {
		// ��J
		// �쥻�����M��
		File fr = new File("D:/DataTemp\\Processing\\Concept\\ConceptList.txt");
		FileReader FileStream = new FileReader(fr);
		BufferedReader BufferedStream = new BufferedReader(FileStream);
		String line = "";
		LinkedList<String> oldDocument = new LinkedList<String>();

		while ((line = BufferedStream.readLine()) != null) {
			if (line.split(":")[0].equals(concept)) {
				String arr[] = line.split(":")[1].split(",");
				for (int i = 0; i < arr.length; i++) {
					if (!document.contains(arr[i]))
						oldDocument.add(arr[i]);
				}
			}
		}
		Collections.sort(oldDocument);
		String min = oldDocument.getFirst();

		Hashtable<String, String> oneGram = new Hashtable<String, String>(); // ��r��<�r��,�j�M���G��>
		Hashtable<String, String> twoGram = new Hashtable<String, String>(); // ���r��<�r��,�j�M���G��>

		File fr1 = new File("D:/DataTemp\\Processing\\TagTree\\" + concept
				+ "_File\\MergeOneGramResult.txt");
		File fr2 = new File("D:/DataTemp\\Processing\\TagTree\\" + concept
				+ "_File\\MergeTwoGramResult.txt");
		FileReader FileStream1 = new FileReader(fr1);
		FileReader FileStream2 = new FileReader(fr2);
		BufferedReader BufferedStream1 = new BufferedReader(FileStream1);
		BufferedReader BufferedStream2 = new BufferedReader(FileStream2);
		String line1 = "";
		String line2 = "";

		// �B�z�쥻�����M��r��
		while ((line1 = BufferedStream1.readLine()) != null)

			oneGram.put(line1.split(",")[0], line1.split(",")[1]);

		while ((line2 = BufferedStream2.readLine()) != null) {
			if (!line2.split(",")[0].split(" ")[0].equals(line2.split(",")[0]
					.split(" ")[1]))
				twoGram.put(line2.split(",")[0], line2.split(",")[1]);
		}

		// �B�z��s�����M��r��
		for (String str1 : document) {
			File fr3 = new File("D:/DataTemp\\Processing\\document\\" + str1
					+ "_File\\FilterResult.txt");
			FileReader FileStream3 = new FileReader(fr3);
			BufferedReader BufferedStream3 = new BufferedReader(FileStream3);
			String line3 = "";

			while ((line3 = BufferedStream3.readLine()) != null)
				oneGram.put(line3.split(",")[0], line3.split(",")[1]);
		}

		for (String str2 : document) {
			for (String str3 : oldDocument) {
				File fr4 = new File("D:/DataTemp\\Processing\\document\\"
						+ str2 + "_File\\" + str3
						+ "_File\\MergeTwoGramResult.txt");
				if (Integer.parseInt(str2) < Integer.parseInt(str3))
					fr4 = new File("D:/DataTemp\\Processing\\document\\" + str3
							+ "_File\\" + str2
							+ "_File\\MergeTwoGramResult.txt");

				FileReader FileStream4 = new FileReader(fr4);
				bufferedStream4 = new BufferedReader(FileStream4);
				String line4 = "";

				while ((line4 = bufferedStream4.readLine()) != null) {
					if (!line4.split(",")[0].split(" ")[0].equals(line4
							.split(",")[0].split(" ")[1]))
						twoGram.put(line4.split(",")[0], line4.split(",")[1]);
				}
			}
		}

		// ��X
		outputHashtable(concept, oneGram, twoGram);
	}

	public static void printHashtable(Hashtable<String, String> h) {
		Vector<String> v = new Vector<String>(h.keySet());
		Collections.sort(v);
		Iterator<String> i = v.iterator();
		String key = "";
		String value = "";
		while (i.hasNext()) {
			key = (String) i.next();
			value = h.get(key);
			System.out.println(key + "," + value);
		}
	}

	// ��X
	public static void outputHashtable(String concept,
			Hashtable<String, String> oneGram, Hashtable<String, String> twoGram)
			throws IOException {
		BufferedWriter bw1 = new BufferedWriter(new FileWriter(
				"D:/DataTemp\\Processing\\TagTree\\" + concept
						+ "_File\\MergeOneGramResult.txt"));
		Vector<String> v = new Vector<String>(oneGram.keySet());
		Collections.sort(v);
		Iterator<String> i = v.iterator();
		String key = "";
		String value = "";
		String output = "";
		while (i.hasNext()) {
			key = (String) i.next();
			value = oneGram.get(key);
			output = key + "," + value;
			try {
				bw1.write(output);
				bw1.newLine();
			} catch (IOException f) {
				f.printStackTrace();
			}
		}
		bw1.flush();
		bw1.close();

		BufferedWriter bw2 = new BufferedWriter(new FileWriter(
				"D:/DataTemp\\Processing\\TagTree\\" + concept
						+ "_File\\MergeTwoGramResult.txt"));
		v = new Vector<String>(twoGram.keySet());
		Collections.sort(v);
		i = v.iterator();
		while (i.hasNext()) {
			key = (String) i.next();
			value = twoGram.get(key);
			output = key + "," + value;
			try {
				bw2.write(output);
				bw2.newLine();
			} catch (IOException f) {
				f.printStackTrace();
			}
		}
		bw2.flush();
		bw2.close();
	}

	public static void main(String dirPath, String _mainwordPath,
			String _numberOfPairPath, IndexSearchable _searcher)
			throws Exception {
		mainwordPath = _mainwordPath;
		numberOfPairPath = _numberOfPairPath;
		TagTree_Preprocess_test(dirPath);
		searcher = _searcher;
	}

	public static void main(String args[]) throws Exception {
		// main();
	}
}
