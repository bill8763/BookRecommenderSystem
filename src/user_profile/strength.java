package user_profile;

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
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class  strength{

	double simMin = 0.4;
	
	@SuppressWarnings("resource")
	public void concept_strength(int concept_id,int topic_id) throws IOException {	
		
		
		BufferedReader br;
		BufferedReader br2;
		BufferedReader br3;
		String no;
		String line = "";
		ArrayList<String> linkList = new ArrayList<String>();
		ArrayList<String> strengthList = new ArrayList<String>();
		Map<String, Double> strengthmap = new HashMap<String, Double>();
		Map<String, Double> mainwordmap = new HashMap<String, Double>();
				
		no = concept_id +"_"+ topic_id;	

		br = new BufferedReader(new FileReader("D:/DataTemp/Concept_K-core/Rank/"+no + "_" + "Rank.txt"));//讀出經過排序的NGD(過濾後的字詞算出NGD的結果)
		br2 = new BufferedReader(new FileReader("D:/DataTemp/Concept_K-core/Stem/"+no + "_" + "stem.txt"));//讀出字詞(經過濾後的字詞)
		br3 = new BufferedReader(new FileReader("D:/DataTemp/Concept_K-core/Main_word/"+no + "_" + "main_word.txt")); //讀出main_word
		
		while ((line = br.readLine()) != null) {
			linkList.add(line);
		}
		simMin=Double.parseDouble(linkList.get(linkList.size()/4).split(",")[2]);//NGD前1/4為最小值
		Double maxstrength = 0.0;	
		while ((line = br2.readLine()) != null) {
			String key = line.split(",")[0]; //字詞
			double strength= getStrength(key,linkList); 
			strengthList.add(key+","+ strength); //記錄節點強度
			strengthmap.put(key,strength);
			if(strength>maxstrength)
				maxstrength =strength;
	
		//		degreeMap.put(key, weight);//紀錄連結度
		}
		while ((line = br3.readLine()) != null) {  //找出mainword權重
			String mainwordkey = line.split(",")[0];
			if(strengthmap.get(mainwordkey)!=null){
				mainwordmap.put(mainwordkey, strengthmap.get(mainwordkey));
			}
		}
		//排序部份	
		List<Map.Entry<String, Double>> list_Data = new ArrayList<Map.Entry<String, Double>>(
				mainwordmap.entrySet());
		Iterator<Map.Entry<String, Double>> iterator = list_Data.iterator();

		Collections.sort(list_Data,
				new Comparator<Map.Entry<String, Double>>() {
					public int compare(Map.Entry<String, Double> o1,
							Map.Entry<String, Double> o2) {
						return (int) ((o2.getValue() - o1.getValue()) * 1000.0);
					}
				});
		
		File file=new File("D:/DataTemp/Concept_K-core/Strength/"+no + "_" + "strength.txt");
		file.delete();
		BufferedWriter bw = new BufferedWriter(new FileWriter("D:/DataTemp/Concept_K-core/Strength/"+no + "_" + "strength.txt"));
			

		while (iterator.hasNext()) {
			Map.Entry<String, Double> entry = iterator.next();
			System.out.println(entry.getKey() + "," + (double)entry.getValue()/maxstrength); //正規化
			bw.write(entry.getKey() + "," + (double)entry.getValue()/maxstrength);
//			rel_loader.rel_map.put(entry.getKey(), entry.getValue());
			bw.newLine();
		}
		bw.flush();
		bw.close();
			
        //全部字的weight		
		/*			
		Object[] objs = weightList.toArray();
		BufferedWriter bw;
		bw = new BufferedWriter(new FileWriter("Concept_K-core/Weight/"+no + "_" + "weight.txt", false));

		

		bw.flush();
		bw.close();
		
		for (int j = 0; j < objs.length; j++) {

			System.out.println(objs[j]);
			String objs_out = (String) objs[j];

				try {
					
					bw.write(objs_out);
					bw.newLine();
					bw.flush(); // 清空緩衝區
					

				} catch (IOException f) {
					// TODO Auto-generated catch block
					f.printStackTrace();
				}
		}
		bw.close(); // 關閉BufferedWriter物件
*/
	}
	
	public double getStrength (String node, ArrayList<String> linkList) { 
		//int degree = 0;
		double strength=0;
		for (String t : linkList) {//為經過排序後的NGD值
			if ((t.split(",")[0].equals(node) || t.split(",")[1].equals(node)) //node為字詞
					&& Double.parseDouble(t.split(",")[2]) < simMin) { //要小於門標值
			//	degree++; 
				double ngd = 0;
				if(Double.parseDouble(t.split(",")[2])>1)
					ngd =1;
				else
					ngd = Double.parseDouble(t.split(",")[2]);
				strength=strength+(1-ngd);   
			}//只有小於門檻值的才會建立連結
		}//計算各節點(字詞)的連結度(degree)
		//System.out.println(node+":"+weight);
		return strength;
	}
	

	public static void main(String[] args) throws IOException {
		strength w_test = new strength();
		w_test.concept_strength(3, 2);
	}
	

}
