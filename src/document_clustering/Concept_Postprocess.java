package document_clustering;

/*
 �u�@�G�������s��B�z�A�P�_�U�s�����ΡC
 C:�إ߸s��
 D:�R���s��
 Y:�s������
 N:�s������
 �ӷ��GD:\\DataTemp\\Processing\\Concept\\ConceptList.txt
 D:\\DataTemp\\Processing\\Concept\\StarCoverGraph.txt
 �ت��GD:\\DataTemp\\Processing\\Concept\\ConceptList.txt
 D:\\DataTemp\\Processing\\Concept\\ConceptState.txt
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;
import java.util.Hashtable;
import java.util.Iterator;

public class Concept_Postprocess {
	public Concept_Postprocess() {
	}

	@SuppressWarnings("rawtypes")
	public static void Concept_Postprocess_test(String conceptListPath,
			String starPath, String conceptStatePath) throws IOException {
		// ��J
		File fr1 = new File(conceptListPath);
		File fr2 = new File(starPath);

		if (!fr1.exists()) // �N���Ĥ@�g���
		{
			FileReader FileStream2 = new FileReader(fr2);
			BufferedReader BufferedStream2 = new BufferedReader(FileStream2);
			String line2 = "";

			BufferedWriter bw1 = new BufferedWriter(new FileWriter(
					conceptListPath));
			BufferedWriter bw2 = new BufferedWriter(new FileWriter(
					conceptStatePath));
			while ((line2 = BufferedStream2.readLine()) != null) {
				try {
					bw1.write(line2);
					bw1.newLine();
					bw2.write(line2.split(":")[0] + ",C:" + line2.split(":")[1]);
					bw2.newLine();
				} catch (IOException f) {
					f.printStackTrace();
				}
			}
			bw1.flush();
			bw1.close();
			bw2.flush();
			bw2.close();
			BufferedStream2.close();
		} else {
			FileReader FileStream1 = new FileReader(fr1);
			BufferedReader BufferedStream1 = new BufferedReader(FileStream1);
			String line1 = "";
			ArrayList<String> list1 = new ArrayList<String>();
			while ((line1 = BufferedStream1.readLine()) != null)
				list1.add(line1);

			FileReader FileStream2 = new FileReader(fr2);
			BufferedReader BufferedStream2 = new BufferedReader(FileStream2);
			String line2 = "";
			ArrayList<String> list2 = new ArrayList<String>();
			while ((line2 = BufferedStream2.readLine()) != null)
				list2.add(line2);

			BufferedStream1.close();
			BufferedStream2.close();

			// �B�z
			Hashtable<String, LinkedList<String>> oldConceptList = new Hashtable<String, LinkedList<String>>(); // �ª������s�����M��<�s��,���>
			Hashtable<String, LinkedList<String>> modConceptList = new Hashtable<String, LinkedList<String>>(); // �諸�����s�����M��<�s��,���>
			Hashtable<String, LinkedList<String>> newConceptList = new Hashtable<String, LinkedList<String>>(); // �s�������s�����M��<�s��,���>
			Hashtable<String, String> conceptState = new Hashtable<String, String>(); // �����s�������A
			Hashtable<String, String> addDocument = new Hashtable<String, String>(); // �s�W�����<�s��,���>

			LinkedList<String> oldList = new LinkedList<String>(); // �ª����M��<���>
			LinkedList<String> modList = new LinkedList<String>(); // �諸���M��<���>

			for (String str1 : list1) {
				String arr1[] = str1.split(":")[1].split(",");
				for (int i = 0; i < arr1.length; i++)
					oldList.add(arr1[i]);

				oldConceptList.put(str1.split(":")[0], oldList);
				newConceptList.put(str1.split(":")[0], oldList);
				conceptState.put(str1.split(":")[0], "N");
			}

			for (String str2 : list2) {
				String arr2[] = str2.split(":")[1].split(",");
				for (int i = 0; i < arr2.length; i++)
					modList.add(arr2[i]);

				modConceptList.put(str2.split(":")[0], modList);
				newConceptList.put(str2.split(":")[0], modList);
			}

			// �ˬd�s���ܤ�(N:���� Y:���� C:�s�W D:�R��)
			Set<String> set1 = oldConceptList.keySet();
			Set<String> set2 = modConceptList.keySet();
			Iterator iterator1 = set1.iterator();
			Iterator iterator2 = set2.iterator();

			while (iterator1.hasNext()) {
				String old = (String) iterator1.next();
				if (!modConceptList.containsKey(old)) {
					conceptState.put(old, "D");
				}
			}
			while (iterator2.hasNext()) {
				String mod = (String) iterator2.next();
				if (oldConceptList.containsKey(mod)) {
					boolean change = false;
					oldList = oldConceptList.get(mod);
					modList = modConceptList.get(mod);
					String addList = "";
					for (String str3 : modList) {
						if (!oldList.contains(str3)) {
							change = true;
							addList = addList + str3 + ",";
						}
					}
					if (change) {
						conceptState.put(mod, "Y");
						addDocument.put(mod,
								addList.substring(0, addList.length() - 1));
					}
				} else {
					modList = modConceptList.get(mod);
					String addList = "";
					for (String str4 : modList)
						addList = addList + str4 + ",";

					conceptState.put(mod, "C");
					addDocument.put(mod,
							addList.substring(0, addList.length() - 1));
				}
			}

			// ��X
			BufferedWriter bw1 = new BufferedWriter(new FileWriter(
					conceptListPath));
			for (String str3 : list2) {
				try {
					bw1.write(str3);
					bw1.newLine();
				} catch (IOException f) {
					f.printStackTrace();
				}
			}
			bw1.flush();
			bw1.close();

			BufferedWriter bw2 = new BufferedWriter(new FileWriter(
					conceptStatePath));
			Set<String> set3 = conceptState.keySet();
			Iterator iterator3 = set3.iterator();
			while (iterator3.hasNext()) {
				String concept = (String) iterator3.next();
				String state = conceptState.get(concept);
				String output = concept + "," + state;
				if (addDocument.containsKey(concept))
					output = output + ":" + addDocument.get(concept);
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
	}

	@SuppressWarnings("rawtypes")
	public static void printConceptList(Hashtable<String, LinkedList<String>> h) {
		Set<String> set = h.keySet();
		Iterator iterator = set.iterator();
		while (iterator.hasNext()) {
			String c = (String) iterator.next();
			LinkedList<String> l = h.get(c);
			System.out.print("Concept:" + c + ",");
			for (String s : l) {
				System.out.print(s + " ");
			}
			System.out.println();
		}
	}

	@SuppressWarnings("rawtypes")
	public static void printConceptState(Hashtable<String, String> h) {
		Set<String> set = h.keySet();
		Iterator iterator = set.iterator();
		while (iterator.hasNext()) {
			String c = (String) iterator.next();
			System.out.println("Concept:" + c + "," + h.get(c));
		}
	}

	public static void main() throws IOException {
		// Concept_Postprocess_test();
	}

	public static void main(String args[]) throws IOException {
		main();
	}
}