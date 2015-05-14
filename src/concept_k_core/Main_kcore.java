package concept_k_core;

import java.util.Scanner;



public class Main_kcore {
	
	public void kcore(int concept_id,int topic_id) throws Exception{

			new Lucene_Search2().doit(concept_id,topic_id);
			//google_filter2.search_filter(i);
			Stem.stemming(concept_id,topic_id);
			NGD_calculate.NGD(concept_id,topic_id);
			Result_Rank.ranking(concept_id,topic_id);
			K_core kcore = new K_core();
			kcore.K_core_cal(concept_id,topic_id);
			
	}
	
	@SuppressWarnings("resource")
	public static void main(String args[]) throws Exception{
    	System.out.println("K-core Running");
		Scanner sc = new Scanner(System.in);
		System.out.print("concept_id");					
		int concept_id = sc.nextInt();					// ¶}©l½s¸¹
		System.out.print("topic_id:");					
		int topic_id = sc.nextInt();	
		Main_kcore m_kcore = new Main_kcore();
		m_kcore.kcore(concept_id, topic_id);
		System.out.println("finish!!!");
    }
    

}
