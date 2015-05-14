

import java.io.IOException;
import java.util.Scanner;


public class Main_kcore {
	
	@SuppressWarnings("static-access")
	public void kcore(int start,int end) throws IOException{
	
		for( int i = start;i<=end;i++){
			Porter_Stemmer.Stemmer(i);
			new Qtag().tagging(i);
			Term_freq_count.counting(i);
			POS_filter.filter(i);
			new Lucene_Search1().doit(i);
			google_filter1.search_filter(i);
			new Lucene_Search2().doit(i);
			google_filter2.search_filter(i);
			Stem.stemming(i);
			NGD_calculate.NGD(i);
			Result_Rank.ranking(i);
			K_core kcore=new K_core();
			kcore.K_core_cal(i);            
			
		}
	}
	
	@SuppressWarnings("resource")
	public static void main(String args[]) throws Exception{
    	System.out.println("K-core Running");
		Scanner sc = new Scanner(System.in);
		System.out.print("Input the start number of file:");					
		int start = sc.nextInt();					// 開始編號
		System.out.print("Input the   end number of file:");					
		int end = sc.nextInt();	
		Main_kcore m_kcore = new Main_kcore();
		m_kcore.kcore(start, end);
		System.out.println("Preprocess finish!!!");
		Avg_link_all.main(new String[]{start+"", end+""});
		BetaSimilarity_Filtering.main(0.2);   //設定門檻值
		Max_Similarity_Calculate.main(new String[]{start+"", end+""});
										
    }                              
    

}
