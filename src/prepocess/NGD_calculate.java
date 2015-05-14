package prepocess;

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
	public static void NGD(int no) {
		try {
			FileReader FileStream1;

			FileStream1 = new FileReader("D:/K-core/Number_of_pair/" + no + "_"
					+ "number_of_pair.txt");

			BufferedReader BufferedStream1 = new BufferedReader(FileStream1);
			String e1 = "";

			ArrayList<String> pairlist = new ArrayList<String>();
			while ((e1 = BufferedStream1.readLine()) != null) {

				pairlist.add(e1);
			}

			FileReader FileStream = new FileReader("D:/K-core/Stem/" + no + "_"
					+ "stem.txt");
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
					// double logX = Math.log10(x);
					// double logY = Math.log10(y);
					// double logM = Math.log10(m);
					// double logN = 9.906;
					// // double logN = 10;
					// double NGD = (Math.max(logX, logY) - logM)
					// / (logN - Math.min(logX, logY));
					double NGD = NGD_cal(x, y, m);
					System.out.println(key1 + "," + key2 + ";" + NGD);
					set.add(key1 + "," + key2 + ";" + NGD);
				}
			}
			Object[] objs = set.toArray();
			File file = new File("D:/K-core/NGD/" + no + "_" + "nNGD.txt");
			file.delete();
			BufferedWriter bw;
			bw = new BufferedWriter(new FileWriter("D:/K-core/NGD/" + no + "_"
					+ "nNGD.txt", false));
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
		NGD(500);
	}

	public static void main(int no, int z, int type, double pNGD)
			throws IOException {
		// TODO Auto-generated method stub
		// NGD�p��(���Ҿ�s��,�L,�ϥΫ��A,NGD ���e��)(0=�e�B�z,1=�������s,2=���Ҿ�,3=����)
		ArrayList<String> list1 = new ArrayList<String>();
		ArrayList<String> list2 = new ArrayList<String>();
		ArrayList<String> oneName = new ArrayList<String>(); // ��r���W��
		ArrayList<String> twoName = new ArrayList<String>(); // ���r���W��
		ArrayList<Double> oneNum = new ArrayList<Double>(); // ��r����
		ArrayList<Double> twoNum = new ArrayList<Double>(); // ���r����

		// Ū��
		// type==2, ������
		File fr1 = new File("D:/DataTemp\\Processing\\TagTree\\" + no
				+ "_File\\MergeOneGramResult.txt");
		File fr2 = new File("D:/DataTemp\\Processing\\TagTree\\" + no
				+ "_File\\MergeTwoGramResult.txt");

		// type==3, ����
		if (type == 3) {
			fr1 = new File("D:/DataTemp\\Processing\\Document\\" + no
					+ "_File\\FilterResult.txt");
			fr2 = new File("D:/DataTemp\\Processing\\Document\\" + no
					+ "_File\\FilterResult2.txt");
		}

		FileReader FileStream1 = new FileReader(fr1);
		FileReader FileStream2 = new FileReader(fr2);
		BufferedReader BufferedStream1 = new BufferedReader(FileStream1);
		BufferedReader BufferedStream2 = new BufferedReader(FileStream2);
		String line1 = "";
		String line2 = "";

		while ((line1 = BufferedStream1.readLine()) != null)
			list1.add(line1);

		while ((line2 = BufferedStream2.readLine()) != null)
			list2.add(line2);

		Object[] datas1 = list1.toArray();
		Object[] datas2 = list2.toArray();
		BufferedStream1.close();
		BufferedStream2.close();

		// ���r���B�z
		for (int i = 0; i < datas2.length; i++) {
			String key = ((String) datas2[i]).split(",")[0];
			double value = 0;
			value = Double.parseDouble(((String) datas2[i]).split(",")[1]);
			twoName.add(key);
			twoNum.add(value);
		}

		// ��r���B�z
		for (int i = 0; i < datas1.length; i++) {
			String outkey = "";
			String key = ((String) datas1[i]).split(",")[0];
			for (int j = i + 1; j < datas1.length; j++) {
				double value = 0.0;
				String key2 = ((String) datas1[j]).split(",")[0];
				for (int k = 0; k < datas2.length; k++) {
					if (twoName.get(k).equals(
							'"' + key + '"' + " " + '"' + key2 + '"')) {
						outkey = key + "," + key2;
						if ((Double) twoNum.get(k) == 0)
							value = 1.0;
						else {
							value = NGD_cal(
									Double.parseDouble(((String) datas1[i])
											.split(",")[1]),
									Double.parseDouble(((String) datas1[j])
											.split(",")[1]),
									(Double) twoNum.get(k));
						}
						oneName.add(outkey);
					}
				}

				oneNum.add(value);
				// System.out.println(oneName.get(i) +"  "+ oneNum.get(i));

			}
		}

		// ��X
		Object[] objs = oneName.toArray();
		Object[] objsnum = oneNum.toArray();

		String objs_out = "";
		BufferedWriter bw = new BufferedWriter(new FileWriter(
				"D:/DataTemp\\Processing\\TagTree\\" + no
						+ "_File\\NGDResult.txt"));
		for (int i = 0; i < oneNum.size(); i++) {
			objs_out = (String) objs[i] + "," + objsnum[i];
			try {
				bw.write(objs_out);
				bw.newLine();
			} catch (IOException f) {
				f.printStackTrace();
			}
		}
		bw.flush();
		bw.close();
	}

}
