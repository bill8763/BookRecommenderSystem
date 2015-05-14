package prepocess;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.JOptionPane;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;


//import Google_Search1.ParserGetter;
public class Lucene_Search2 extends HTMLEditorKit.ParserCallback {
	class ParserGetter extends HTMLEditorKit {
		// purely to make this methods public
		public HTMLEditorKit.Parser getParser() {
			return super.getParser();
		}
	}

	// 記錄是否將資料印出
	private boolean inHeader = false;
	private static String _sn = "";
	String i1;
	int l = 1;

	public Lucene_Search2() {
	}

	public void doit(int no) throws IOException {
		// try {
		
		File f = new File("D:/K-core/Search2/" + no + "_" + "Lucene_output2.txt");
		f.delete();
		FileReader FileStream = new FileReader("D:/K-core/Pairs/" + no + "_"
				+ "pairs.txt");
		BufferedReader BufferedStream = new BufferedReader(FileStream);
		String line;

		while ((line = BufferedStream.readLine()) != null) {
			_sn = String.valueOf(no);
			IndexStatus2 qq = new IndexStatus2();
			try {
				qq.indexed("+"+line, _sn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(int no) throws IOException {
		new Lucene_Search2().doit(no);

	}

	public static void main(String args[]) throws IOException {

		long StartTime = System.currentTimeMillis(); // 取出目前時間
		main(1);
		System.out.println(System.currentTimeMillis() - StartTime);

	}
}