package DatasetPocess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class getAbstract {
	public static void main(String args[]) throws IOException {
		String dir = "D:/dataset/BookAbstract";
		String input = "D:/dataset/BookIdTitle.txt";
		getAbstract(input,dir);
	}

	private static void getAbstract(String input, String dir) throws IOException {

		FileReader FileStream = new FileReader(input);
		BufferedReader in = new BufferedReader(FileStream);
		String line = "";
		while ((line = in.readLine()) != null) {
			String BookId = line.split(" :: ")[0];
			UrlCrawler.UrlCrawler("http://www.amazon.com/dp/"+BookId , dir+"/"+BookId+".txt");
			System.out.println(dir+"/"+BookId+".txt is done!");
		}
	}
}
