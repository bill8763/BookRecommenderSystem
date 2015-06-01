package concept_k_core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class NGD_calculate {

	@SuppressWarnings("resource")
	public static void NGD(int concept_id,int topic_id, String path) {
		try {
			FileReader FileStream1;

			FileStream1 = new FileReader(path+"Number_of_pair/" + concept_id + "_" +topic_id
					+ "_number_of_pair.txt");

			BufferedReader BufferedStream1 = new BufferedReader(FileStream1);
			String e1 = "";

			ArrayList<String> pairlist = new ArrayList<String>();
			while ((e1 = BufferedStream1.readLine()) != null) {

				pairlist.add(e1);
			}

			FileReader FileStream = new FileReader(path+"Stem/" + concept_id + "_" +topic_id
					+ "_stem.txt");
			BufferedReader BufferedStream = new BufferedReader(FileStream);
			String e = "";

			ArrayList<String> termlist = new ArrayList<String>();
			while ((e = BufferedStream.readLine()) != null) {

				termlist.add(e);

			}

			Object[] datas = termlist.toArray();
			LinkedHashSet<String> set = new LinkedHashSet<String>();
			for (int i = 0; i < datas.length; i++) {
				String key1 = String.valueOf(datas[i]).split(",")[0];
				double x = Double.parseDouble(String.valueOf(datas[i]).split(
						",")[1]);
				for (int j = i + 1; j < datas.length; j++) {
					String key2 = String.valueOf(datas[j]).split(",")[0];
					double y = Double.parseDouble(String.valueOf(datas[j])
							.split(",")[1]);
					double m = 0;
					for (String o : pairlist) {
						if (o.contains("\"" + key1 + "\"+\"" + key2 + "\"")
								|| o.contains("\"" + key2 + "\"+\"" + key1
										+ "\""))
						// if(o.contains(key1+"+"+key2)||o.contains(key2+"+"+key1))
						{
							m = Double.parseDouble(o.split(",")[1]);
							// System.out.println("get m="+m);
							break;
						}
					}
					System.out.println("x=" + x + " y=" + y + " m=" + m);
//					double logX = Math.log10(x);
//					double logY = Math.log10(y);
//					double logM = Math.log10(m);
//					double logN = 9.906;
//					// double logN = 10;
//					double NGD = (Math.max(logX, logY) - logM)
//							/ (logN - Math.min(logX, logY));
					double NGD=NGD_cal(x,y,m);
					System.out.println(key1 + "," + key2 + ";" + NGD);
					set.add(key1 + "," + key2 + ";" + NGD);
				}
			}
			Object[] objs = set.toArray();
			File file = new File(path+"NGD/" + concept_id + "_" + topic_id +"_nNGD.txt");
			file.delete();
			File test = new File(path+"NGD/");
			if (!test.exists()) {
				test.mkdirs();
			}
			BufferedWriter bw;
			bw = new BufferedWriter(new FileWriter(path+"NGD/" + concept_id + "_" + topic_id +"_nNGD.txt", false));
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
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static double NGD_cal(double x, double y, double m) {
		double logX = Math.log10(x);
		double logY = Math.log10(y);
	
		double logM = Math.log10(m);
		double logN = 6.4966;
		double NGD = 0;
		
			NGD = (Math.max(logX, logY) - logM) / (logN - Math.min(logX, logY));
		
		return NGD;
	}

	public static void main(String args[]) throws IOException {
		//NGD(3,2);
	}

}
