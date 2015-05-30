package tagtree;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NGD_calculate {

	static double NGD_cal(double x, double y, double m) {
		double logX = Math.log10(x);
		double logY = Math.log10(y);

		double logM = Math.log10(m);
		double logN = 6.4966;
		double NGD = 0;

		NGD = (Math.max(logX, logY) - logM) / (logN - Math.min(logX, logY));

		return NGD;
	}

	public static void main(int no, int z, int type, double pNGD, String dirPath)
			throws IOException {
		// TODO Auto-generated method stub
		// NGD�p��(���Ҿ�s��,�L,�ϥΫ��A,NGD
		// ���e��)(0=�e�B�z,1=�������s,2=���Ҿ�,3=����)
		Map<String, Long> termSearchMap = new HashMap<String, Long>();

		// Ū��
		// type==2, ������
		File fr1 = new File(dirPath + "TagTree\\" + no
				+ "_File\\MergeOneGramResult.txt");
		File fr2 = new File(dirPath + "TagTree\\" + no
				+ "_File\\MergeTwoGramResult.txt");

		// type==3, ����
		if (type == 3) {
			fr1 = new File(dirPath + "Document\\" + no
					+ "_File\\FilterResult.txt");
			fr2 = new File(dirPath + "Document\\" + no
					+ "_File\\FilterResult2.txt");
		}

		FileReader FileStream1 = new FileReader(fr1);
		FileReader FileStream2 = new FileReader(fr2);
		BufferedReader BufferedStream1 = new BufferedReader(FileStream1);
		BufferedReader BufferedStream2 = new BufferedReader(FileStream2);
		BufferedWriter bw = new BufferedWriter(new FileWriter(dirPath
				+ "TagTree\\" + no + "_File\\NGDResult.txt"));
		String line1 = "";
		String line2 = "";

		while ((line1 = BufferedStream1.readLine()) != null) {
			termSearchMap.put(line1.split(",")[0],
					Long.parseLong(line1.split(",")[1]));
		}
		BufferedStream1.close();

		while ((line2 = BufferedStream2.readLine()) != null) {
			String key = line2.split(",")[0].replace("\" \"", ",").replace(
					"\"", "");
			Long mergeTwoResult = Long.parseLong(line2.split(",")[1]);
			double value = NGD_cal(
					(double) termSearchMap.get(key.split(",")[0]),
					(double) termSearchMap.get(key.split(",")[1]),
					mergeTwoResult);
			bw.write(key + "," + value);
			bw.newLine();
			bw.flush();
		}
		BufferedStream2.close();
		bw.close();

	}
}
