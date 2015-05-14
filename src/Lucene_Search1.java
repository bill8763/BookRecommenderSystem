


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.text.html.HTMLEditorKit;



public class Lucene_Search1 extends HTMLEditorKit.ParserCallback {
	class ParserGetter extends HTMLEditorKit {
		// purely to make this methods public
		public HTMLEditorKit.Parser getParser() {
			return super.getParser();
		}
	}

	// 記錄是否將資料印出
	private boolean inHeader = false;
	private static String _sn = "";

	public Lucene_Search1() {
	}

	public void doit(int no) throws IOException {
		
		File f = new File("D:/K-core/Search1/" + no + "_" + "Lucene_output1.txt");
		f.delete();
		// try {
		ParserGetter kit = new ParserGetter();
		HTMLEditorKit.Parser parser = kit.getParser();
		HTMLEditorKit.ParserCallback callback = new Lucene_Search1();
		FileReader FileStream = new FileReader("D:/K-core/POS_filter/" + no + "_"
				+ "filter_output1.txt");
		BufferedReader BufferedStream = new BufferedReader(FileStream);
		String line;
		int l = 1;
		// ArrayList<String> stop_list = Stop_Loader.loadList("stop_list.txt");
		while ((line = BufferedStream.readLine()) != null) {
			
			_sn = String.valueOf(no);
			IndexStatus qq = new IndexStatus();
			try {
				qq.indexed(line, _sn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(int no) throws IOException{
		new Lucene_Search1().doit(no);
	}

	public static void main(String args[]) throws IOException {
		long StartTime = System.currentTimeMillis(); // 取出目前時間
		main(1);
		System.out.println(System.currentTimeMillis() - StartTime);
	}
}