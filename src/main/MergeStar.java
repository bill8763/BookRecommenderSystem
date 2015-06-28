package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class MergeStar {
	public static void main(String[] args) throws Exception, IOException {
		ArrayList mergeConcept = new ArrayList();
		String inputFile = "D:/dataset/Processing/Concept/StarCoverGraph.txt";
		String outputFile = "";
		File fr = new File(inputFile);
		if (fr.exists()) {
			FileReader FileStream = new FileReader(fr);
			BufferedReader BufferedStream = new BufferedReader(FileStream);
			String line = "";
			ArrayList<String> list = new ArrayList<String>();
			HashMap<String, String[]> conceptDoc = new HashMap<>();
			HashSet<String> set = new HashSet<>();
			while ((line = BufferedStream.readLine()) != null) {
				String concept = line.split(":")[0];
				String[] doc = line.split(":")[1].split(",");
				conceptDoc.put(concept, doc);
			}
			BufferedStream.close();
			for (String concept : conceptDoc.keySet()) {
				for (String concept2 : conceptDoc.keySet()) {
					if (!concept.equals(concept2)) {
						String[] docStrings = conceptDoc.get(concept);
						String[] docStrings2 = conceptDoc.get(concept2);
						for (int i = 0; i < docStrings.length; i++) {
							for (int j = 0; j < docStrings2.length; j++) {
								if (docStrings[i].equals(docStrings2[j])) {
									System.out.println(concept + "+" + concept2);
//									for(String s:set){
//										for(int k=0;k<s.split("+").length;k++){
//											if(concept.equals(s.split("+")[k])||concept2.equals(s.split("+")[k])){
//												String toAdd = s+"+"+concept+"+"+concept2;
//												set.remove(s);
//												set.add(toAdd);
//											}
//											else{
//												set.add(concept+"+"+concept2);
//											}
//										}
//									}
								}
							}
						}
					}
				}
			}
//			for(String print:set){
//				System.out.println(print);
//			}
			// BufferedWriter bw = new BufferedWriter(new
			// FileWriter(outputFile));
			// for(String s: list){
			// try
			// {
			// bw.write(s);
			// bw.newLine();
			// }
			// catch (IOException f)
			// {
			// f.printStackTrace();
			// }
			// }
			// bw.flush();
			// bw.close();
		}
	}
}
