package user_k_core;

import java.util.Scanner;



public class Main_kcore {
	
	public void kcore(int user_id,int concept_id,int topic_id) throws Exception{

			new Lucene_Search2().doit(user_id,concept_id,topic_id);
			//google_filter2.search_filter(i);
			Stem.stemming(user_id,concept_id,topic_id);
			NGD_calculate.NGD(user_id,concept_id,topic_id);
			Result_Rank.ranking(user_id,concept_id,topic_id);
			K_core kcore = new K_core();
			kcore.K_core_cal(user_id,concept_id,topic_id);
			
	}
	
	@SuppressWarnings("resource")
	public static void main(String args[]) throws Exception{
    	System.out.println("K-core Running");
		Scanner sc = new Scanner(System.in);
		System.out.print("user_id");					
		int user_id = sc.nextInt();
		System.out.print("concept_id");					
		int concept_id = sc.nextInt();					// �}�l�s��
		System.out.print("topic_id:");					
		int topic_id = sc.nextInt();	
		Main_kcore m_kcore = new Main_kcore();
		m_kcore.kcore(user_id,concept_id, topic_id);
		System.out.println("finish!!!");
    }
    

}
