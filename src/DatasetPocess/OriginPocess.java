package DatasetPocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class OriginPocess {
	public static void main(String args[]) throws IOException {
		String path = "D:/dataset/Books.txt";
		String output = "D:/dataset/PocessBook.txt";
		FomatPocess(path, output);
	}

	private static void FomatPocess(String path, String output)
			throws IOException {
		// TODO Auto-generated method stub
		// Open the input file for reading
		FileReader FileStream = new FileReader(path);
		BufferedReader in = new BufferedReader(FileStream);
		String line = "";
		BufferedWriter bw = new BufferedWriter(new FileWriter(output, false));
		HashSet<String> set = new HashSet<String>();
		int count = 0; // 一個評論有10項特徵
		String review = "";
		System.out.println(count);
		while ((line = in.readLine()) != null) {
			count++;
			String value = "";
			if (count == 1 || count == 4 || count == 5 || count == 7
					|| count == 8) {
				try {
					value = line.split(": ")[1];
				} catch (ArrayIndexOutOfBoundsException e) {
					value = "null";
				}
				review = review + value + "  ";
				System.out.print(count + " : " + value + " , ");
			}
			if (count == 2) {
				try {
					value = line.split("title: ")[1];
				} catch (ArrayIndexOutOfBoundsException e) {
					value = "null";
				}
				review = review + value + "  ";
				System.out.print(count + " : " + value + " , ");
			}
			if (count == 11) {
				System.out.println("next//");
				count = 0;
				//set.add(review);
				bw.write(review);
				bw.newLine();
				bw.flush(); // 清空緩衝區
				review = "";
			}
		}
		Object[] objs = set.toArray();
/*		for (int j = 0; j < objs.length; j++) {
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
		}*/
	}
}
