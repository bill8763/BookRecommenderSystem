package user_k_core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Result_Rank {
	public static void main(String args[])
	{
//		ranking(1,3,2);
	}
	@SuppressWarnings("resource")
	public static void ranking(String user_id,int concept_id,int topic_id, String path) {
		try {
			
			BufferedReader br = new BufferedReader(new FileReader(path+"NGD/"+user_id +"_" +concept_id+ "_" + topic_id +
					"_nNGD.txt"));
			Map<String, Double> map_Data = new HashMap<String, Double>();
			String line = "";
			while ((line = br.readLine()) != null) {
				String key = line.split(";")[0];
				String dupKey=key.split(",")[1]+","+key.split(",")[0];
				Double value = Double.parseDouble(line.split(";")[1]);
				System.out.println(value);
				if(!map_Data.containsKey(dupKey)&&!key.split(",")[1].equals(key.split(",")[0]))
				map_Data.put(key, value);
			}

			List<Map.Entry<String, Double>> list_Data = new ArrayList<Map.Entry<String, Double>>(
					map_Data.entrySet());
			Iterator<Map.Entry<String, Double>> iterator = list_Data.iterator();

			Collections.sort(list_Data,
					new Comparator<Map.Entry<String, Double>>() {
						public int compare(Map.Entry<String, Double> o1,
								Map.Entry<String, Double> o2) {
							return (int) ((o1.getValue() - o2.getValue()) * 1000.0);
						}
					});
			File test = new File(path+"Rank/");
			if (!test.exists()) {
				test.mkdirs();
			}
			File file=new File(path+"Rank/"+user_id +"_" +concept_id + "_" + topic_id +"_Rank.txt");
			file.delete();
			BufferedWriter bw = new BufferedWriter(new FileWriter(path+"Rank/"+user_id +"_" +concept_id + "_" + topic_id + "_Rank.txt"));
			//bw.write("");
			//bw.newLine();

			while (iterator.hasNext()) {
				Map.Entry<String, Double> entry = iterator.next();
				System.out.println(entry.getKey() + "," + entry.getValue());
				bw.write(entry.getKey() + "," + entry.getValue());
//				rel_loader.rel_map.put(entry.getKey(), entry.getValue());
				bw.newLine();
			}
			bw.flush();
			bw.close();
//			rel_loader.updateBase();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

