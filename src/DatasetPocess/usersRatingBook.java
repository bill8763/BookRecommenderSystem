package DatasetPocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class usersRatingBook {
	public static HashMap<String, String> usersBooks=new HashMap<String,String>();
	private static BufferedWriter bw;
	private static BufferedReader in;
	public static void main(String args[]) throws IOException {
		String input = "D:/dataset/PocessBook.txt";
		String outputDir = "D:/dataset/userRatingBook0430/";
		userRaingBook(input, outputDir);
	}

	private static void userRaingBook(String input, String outputDir) throws IOException {
		FileReader FileStream = new FileReader(input);
		in = new BufferedReader(FileStream);
		
		File Dir = new File(outputDir);
		if (!Dir.exists()) {
			Dir.mkdirs();
			try {
				Dir.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String line="";
		String book=" ";
		String user= "";
		String rate=" ";
		String time=" ";
		String temp="";
		int count=0, theLine=2866052;
		/**
		 * 分斷跑時
		 * 從@theLine的下一行開始讀
		 */
		while (count<theLine)   {
			in.readLine();
			System.out.println(++count+"readline..");
		 }
		System.out.println("start");
		while ((line = in.readLine())!= null) {	
			book=line.split("  ")[0];
			user=line.split("  ")[2];
			rate=line.split("  ")[4];
			time=line.split("  ")[5];
			temp=book+"  "+rate+"  "+time+"\n";
			bw = new BufferedWriter(new FileWriter(outputDir+user+".txt", true));
			bw.write(temp);
			bw.flush();
			System.out.println(++count);
		}
		/**
		 *  hashmap太大，記憶體會不足，只能直接IO讀存
		 *  
		while ((line = in.readLine())!= null) {
			book=line.split("  ")[0];
			user=line.split("  ")[2];
			rate=line.split("  ")[4];
			time=line.split("  ")[5];
			temp="";
			if(!usersBooks.containsKey(user)){
				usersBooks.put(user, book+"  "+rate+"  "+time+"\n");
			}
			else{
				temp=usersBooks.get(user)+book+"  "+rate+"  "+time+"\n";
				usersBooks.put(user, temp);
			}
				System.out.println(line.split("  ")[0]+"  ");
		}

		for(Object key : usersBooks.keySet()){
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputDir+key+".txt", false));
			bw.write(usersBooks.get(key));
			bw.flush();
		}
		*/
	}
}
