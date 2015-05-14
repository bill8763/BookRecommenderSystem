package DatasetPocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.text.html.parser.Entity;

public class GetIdAndTitle {
	public static void main(String args[]) throws IOException {
		String input = "D:/dataset/PocessBook.txt";
		String output = "D:/dataset/BookIdTitle.txt";
		FiltingIdTitle(input, output);
	}

	private static void FiltingIdTitle(String input, String output) throws IOException {
		FileReader FileStream = new FileReader(input);
		BufferedReader in = new BufferedReader(FileStream);
		String line="";
		BufferedWriter bw = new BufferedWriter(new FileWriter(output, false));
		HashMap<String, String> Book=new HashMap<String,String>();
		while ((line = in.readLine())!= null) {
			//if(!line.split(",")[0].equals(Book.keySet())){
				Book.put(line.split("  ")[0],line.split("  ")[1]);
				System.out.println(line.split("  ")[0]);
		//	}
		//	System.out.print(" not the same ");
		}
		for (Object key : Book.keySet()) {
            System.out.println(key + " :: " + Book.get(key));
			try {
				bw.write(key + " :: " + Book.get(key));
				bw.newLine();
				bw.flush(); // ²MªÅ½w½Ä°Ï
			} catch (IOException f) {
				// TODO Auto-generated catch block
				f.printStackTrace();
			}
		}
	}
}
