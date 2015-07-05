package explicit_profile;

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

public class user_strength {
	double simMin = 0.4;

	@SuppressWarnings("resource")
	public void user_concept_strength(final String user_id,final int concept_id,final int topic_id) throws IOException {	
		
		BufferedReader br;
		BufferedReader br2;
		BufferedReader br3;
		String no;
		String line = "";
		ArrayList<String> linkList = new ArrayList<String>();
		ArrayList<String> strengthList = new ArrayList<String>();
		Map<String, Double> strengthmap = new HashMap<String, Double>();
		Map<String, Double> mainwordmap = new HashMap<String, Double>();
				
		no = user_id+"_"+concept_id +"_"+ topic_id;	

		br = new BufferedReader(new FileReader("D:/dataset/User_K-core/Rank/"+no + "_" + "Rank.txt"));//Ū�X�g�L�ƧǪ�NGD(�L�o�᪺�r����XNGD�����G)
		br2 = new BufferedReader(new FileReader("D:/dataset/User_K-core/Stem/"+no + "_" + "stem.txt"));//Ū�X�r��(�g�L�o�᪺�r��)
		br3 = new BufferedReader(new FileReader("D:/dataset/User_K-core/Main_word/"+no + "_" + "main_word.txt")); //Ū�Xmain_word
		
		while ((line = br.readLine()) != null) {
			linkList.add(line);
		}
		simMin=Double.parseDouble(linkList.get(linkList.size()/4).split(",")[2]);//NGD�e1/4���̤p��
		Double maxstrength = 0.0;	
		while ((line = br2.readLine()) != null) {
			String key = line.split(",")[0]; //�r��
			double strength= getStrength(key,linkList); 
			strengthList.add(key+","+ strength); //�O���`�I�j��
			strengthmap.put(key,strength);
			if(strength>maxstrength)
				maxstrength =strength;
	
		//		degreeMap.put(key, weight);//�����s����
		}
		while ((line = br3.readLine()) != null) {  //��Xmainword�v��
			String mainwordkey = line.split(",")[0];
			if(strengthmap.get(mainwordkey)!=null){
				mainwordmap.put(mainwordkey, strengthmap.get(mainwordkey));
			}
		}
		//�Ƨǳ���	
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
		
		File file=new File("D:/dataset/User_K-core/Strength/"+no + "_" + "strength.txt");
		file.delete();
		BufferedWriter bw = new BufferedWriter(new FileWriter("D:/dataset/User_K-core/Strength/"+no + "_" + "strength.txt"));
			

		while (iterator.hasNext()) {
			Map.Entry<String, Double> entry = iterator.next();
			System.out.println(entry.getKey() + "," + (double)entry.getValue()/maxstrength); //���W��
			bw.write(entry.getKey() + "," + (double)entry.getValue()/maxstrength);
//			rel_loader.rel_map.put(entry.getKey(), entry.getValue());
			bw.newLine();
		}
		bw.flush();
		bw.close();
			
        //�����r��weight		
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
					bw.flush(); // �M�Žw�İ�
					

				} catch (IOException f) {
					// TODO Auto-generated catch block
					f.printStackTrace();
				}
		}
		bw.close(); // ����BufferedWriter����
*/
	}
	
	public double getStrength (String node, ArrayList<String> linkList) { 
		//int degree = 0;
		double strength=0;
		for (String t : linkList) {//���g�L�Ƨǫ᪺NGD��
			if ((t.split(",")[0].equals(node) || t.split(",")[1].equals(node)) //node���r��
					&& Double.parseDouble(t.split(",")[2]) < simMin) { //�n�p����Э�
			//	degree++; 
				strength=strength+(1/(Double.parseDouble(t.split(",")[2])));  
			}//�u���p����e�Ȫ��~�|�إ߳s��
		}//�p��U�`�I(�r��)���s����(degree)
		//System.out.println(node+":"+weight);
		return strength;
	}
	

	public static void main(String[] args) throws IOException {
		user_strength u_test = new user_strength();
//		u_test.user_concept_strength(1, 3, 2);
	}
	
}
