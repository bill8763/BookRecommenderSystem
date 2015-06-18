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
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;



public class K_core {

	/**
	 * @param args
	 * @throws FileNotFoundException
	 */
	double simMin = 0.4;
	double avgSim=0;
	int max_core=0;
	BufferedReader br;

	String no;
	String line = "";
	ArrayList<String> linkList = new ArrayList<String>();
	BufferedReader br2;

	Map<String, Integer> coreMap = new HashMap<String, Integer>();
	Map<String, Integer> degreeMap = new HashMap<String, Integer>();
	Map<String, String> hitMap = new HashMap<String, String>();
	List<Map.Entry<String, Integer>> sort_data;

	static String path="";
	public K_core(String _path) {
		path=_path;
	}
	
	public void K_core_cal(String user_id,int concept_id,int topic_id) throws FileNotFoundException {
		String no = "";
		no = user_id+"_"+concept_id + "_"+ topic_id;
		
		this.no = no;
		br = new BufferedReader(new FileReader(path+"Rank/"+no + "_" + "Rank.txt"));//Ū�X�g�L�ƧǪ�NGD
		br2 = new BufferedReader(new FileReader(path+"stem/"+no + "_" + "stem.txt"));//Ū�X�r��
		try {
			
			while ((line = br.readLine()) != null) {
				linkList.add(line);
			}
			simMin=Double.parseDouble(linkList.get(linkList.size()/4).split(",")[2]);
			
			while ((line = br2.readLine()) != null) {
				String key = line.split(",")[0];
				String hits = line.split(",")[1];
				int degree = getDegree(key);
				coreMap.put(key, degree);//����K-core��
				degreeMap.put(key, degree);//�����s����
				hitMap.put(key,hits);//�����j�M���G
			}
			
			sort_data = new ArrayList<Map.Entry<String, Integer>>(coreMap
					.entrySet());
			
			Collections.sort(sort_data,
					new Comparator<Map.Entry<String, Integer>>() {
						public int compare(Map.Entry<String, Integer> o1,
								Map.Entry<String, Integer> o2) {
							return (int) ((o1.getValue() - o2.getValue()));
						}
					});
			//���Ӫ�ldegree�ȱƧ�
			getK_code_value();
			//�p��kcore��
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	private int getDegree(String node) {
		int degree = 0;
		double weight=0;
		for (String t : linkList) {
			if ((t.split(",")[0].equals(node) || t.split(",")[1].equals(node))
					&& Double.parseDouble(t.split(",")[2]) < simMin 
					&& Double.parseDouble(t.split(",")[2]) != 1) {
				degree++;
				weight=weight+Double.parseDouble(t.split(",")[2]);
			}//�u���p����e�Ȫ��~�|�إ߳s��
		}//�p��U�`�I(�r��)���s����(degree)
		System.out.println(node+":"+degree);
		return degree;
	}
	
	

	void getK_code_value() {
		List<Map.Entry<String, Integer>> outputList = new ArrayList<Map.Entry<String, Integer>>();
		while (!sort_data.isEmpty()) {
			System.out.println(sort_data.get(0));
			Map.Entry<String, Integer> node = sort_data.get(0);
			coreMap.remove(node.getKey());
			outputList.add(node);
			int temp=0;
			for (String t : linkList) {
				if ((t.split(",")[0].equals(node.getKey()))
						&& Double.parseDouble(t.split(",")[2]) < simMin
						&& Double.parseDouble(t.split(",")[2]) != 1) {
					if (coreMap.containsKey(t.split(",")[1])) {
						if (node.getValue() < coreMap.get(t.split(",")[1])) {
							temp = coreMap.get(t.split(",")[1]) - 1;
							coreMap.put(t.split(",")[1], temp);//�N�ثe�ƭȤj��ۤv���F�~-1
							System.out.println(t.split(",")[1] + " value-1");
						}
					}
				} else if ((t.split(",")[1].equals(node.getKey()))
						&& Double.parseDouble(t.split(",")[2]) < simMin
						&& Double.parseDouble(t.split(",")[2]) != 1) {
					// degree++;
					if (coreMap.containsKey(t.split(",")[0])) {
						if (node.getValue() < coreMap.get(t.split(",")[0])) {		
							temp = coreMap.get(t.split(",")[0]) - 1;
							coreMap.put(t.split(",")[0], temp);
							System.out.println(t.split(",")[0] + " value-1");
						}
					}
				}//�˵��O�_���j��ۤv�s���ת��F�~(�Хh��K-core�������X)
				
				sort_data = new ArrayList<Map.Entry<String, Integer>>(coreMap
						.entrySet());
				Collections.sort(sort_data,
						new Comparator<Map.Entry<String, Integer>>() {
							public int compare(Map.Entry<String, Integer> o1,
									Map.Entry<String, Integer> o2) {
								return (int) ((o1.getValue() - o2.getValue()));
							}
						});
			}
			System.out.println(node + ": finished");
			if(sort_data.size()==1)
			{
				max_core=sort_data.get(0).getValue();
				System.out.println("max_core:"+sort_data.get(0).getValue());
			}
		}

		System.out.println(outputList);
		System.out.println("Size:" + outputList.size());
		System.out.println("Threshold:" + simMin);
		
		BufferedWriter bw;
		BufferedWriter bw2;
		String query="";
		try {
			File test = new File(path+"K_core/");
			if (!test.exists()) {
				test.mkdirs();
			}
			File test2 = new File(path+"Main_word/");
			if (!test2.exists()) {
				test2.mkdirs();
			}
			bw = new BufferedWriter(new FileWriter(path+"K_core/"+no + "_" + "k_core.txt"));//�g�Jk-core�B�⵲�G
			bw2 = new BufferedWriter(new FileWriter(path+"Main_word/"+no + "_" + "main_word.txt"));//�g�J�֤߯S�x
			
			for (Entry<String, Integer> core : outputList) {

				bw.write(core.toString()+"="+degreeMap.get(core.toString().split("=")[0]));//MANETS=5=8(�r��=Kcore��=degree��)
			
				if(Integer.parseInt(core.toString().split("=")[1])>=( max_core)  )
				{
					query=query+"+\""+core.toString().split("=")[0]+"\"";
					//double coreV=Double.parseDouble(core.toString().split("=")[1]);
					//double hit=Double.parseDouble(hitMap.get(core.toString().split("=")[0]));
					//double score=coreV*(Math.log10(hit)/9.906);
					bw2.write(core.toString().split("=")[0].toLowerCase()+","+hitMap.get(core.toString().split("=")[0])+","+core.toString().split("=")[1]+","+degreeMap.get(core.toString().split("=")[0]));
					bw2.newLine();
					bw2.flush(); 
					
				}//�u��k-core�ȳ̤j���@�s���֤߯S�x
				
				bw.newLine();
				bw.flush();
				//����H�βM�Žw�İ�
				
			}
			bw.close(); 
			bw2.close(); // ����BufferedWriter����
			System.out.println(query);
			System.out.println(avgSim);
		} catch (IOException f) {
			// TODO Auto-generated catch block
			f.printStackTrace();
		}
		

	}

	@SuppressWarnings("resource")
	public static void main(String[] args) throws FileNotFoundException {
//		Scanner input = new Scanner(System.in);
//		System.out.println("Enter document index:");
//		int user_id = input.nextInt();
//		int concept_id = input.nextInt();
//		int topic_id = input.nextInt();
//		K_core kcore = new K_core();
//		System.out.println("Enter threshold:");
//		kcore.simMin = input.nextDouble(); 
//		kcore.K_core_cal(user_id,concept_id,topic_id);//�p��K-core��
		

		
	
	}
}
