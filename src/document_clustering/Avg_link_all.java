package document_clustering;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

public class Avg_link_all {

	/**
	 * @param args
	 */
	public void two_article(int no1, int no2) throws Exception{
		
		ServerUtil.initialize();
		// initialize query
		SolrQuery query = new SolrQuery();
		//show info of all files
		
		Set<String> termset1 = new HashSet<String>();
		Set<String> termset2 = new HashSet<String>();
		
		FileReader FileStream1= null;
		double allNGD = 0;
		int count = 0;
		double sim = 0;
		//-----------判斷檔案是否存在----------------------------
		File a = new File("D:/K-core/Main_word/" + no1 + "_"+ "main_word.txt");
		BufferedWriter bw2 = null;
		if(a.exists())
			FileStream1 = new FileReader("D:/K-core/Main_word/" + no1 + "_"+ "main_word.txt");
		else{
			bw2 = new BufferedWriter(new FileWriter("D:/K-core/Main_word/" + no1 + "_"+ "main_word.txt", false));
			bw2.flush(); 
			bw2.close();
		}
		//---------------------------------------------------
		BufferedReader BufferedStream1 = null;
		BufferedStream1= new BufferedReader(FileStream1);
		String e2 = "";

		while ((e2 = BufferedStream1.readLine()) != null) {

			termset1.add(e2);
		}
		BufferedStream1.close();
		//-----------判斷檔案是否存在----------------------------	
		FileReader FileStream2 = null;
		File b = new File("D:/K-core/Main_word/" + no2 + "_"+ "main_word.txt");
		BufferedWriter bw3;
		
		if( b.exists())
			FileStream2 = new FileReader("D:/K-core/Main_word/" + no2 + "_"+ "main_word.txt");
		else{
			bw3 = new BufferedWriter(new FileWriter("D:/K-core/Main_word/" + no2 + "_"+ "main_word.txt", false));
			bw3.flush(); 
			bw3.close();
		}
		//---------------------------------------------------
		BufferedReader BufferedStream2 = null; 
		BufferedStream2 = new BufferedReader(FileStream2);
		String e3 = "";
		
		while ((e3 = BufferedStream2.readLine()) != null) {
			termset2.add(e3);
		}
		BufferedStream2.close();
		
	//	BufferedWriter bw;
	//	bw = new BufferedWriter(new FileWriter("Avg_link/Main_pair/"+no1 + "_" +no2 +"_pair_term.txt", false));
		for (String term: termset1){
			for(String term2: termset2){
				if(!term.split(",")[0].equals(term2.split(",")[0])){
					System.out.println(term.split(",")[0]+","+term2.split(",")[0]);
					String queryword = "+\""+term.split(",")[0]+"\"+\""+term2.split(",")[0]+"\"";
					query.setQuery(queryword);
					QueryResponse rsp = ServerUtil.execQuery(query);
					SolrDocumentList docs = rsp.getResults();
					
					double x = Double.parseDouble(String.valueOf(term.split(",")[1]));
					double y = Double.parseDouble(String.valueOf(term2.split(",")[1]));
					double m = docs.getNumFound();
					double NGD = NGD_cal(x,y,m);
					
					if (NGD < 1){
						allNGD = allNGD + NGD;
						count = count+1;
					}
					else if(NGD>=1){
						NGD =1;
						allNGD = allNGD + NGD;
						count = count+1;
					}
					
				
				}
			}
		}
		if(count==0)
			sim=0;
		else
			sim = 1-((double)1/(count))*allNGD;
		
		String output = no1 +","+ no2 +":"+sim;
		BufferedWriter bw;
		
		try 
		{
			bw = new BufferedWriter(new FileWriter("D:/DataTemp/Processing/Concept/BetaSimilarityResult.txt",true));
			bw.write(output);
			bw.newLine();
			bw.flush();
			bw.close();
		} 
		catch (IOException f) 
		{
			f.printStackTrace();
		}	
		
		

	}
	static double NGD_cal(double x, double y, double m) {
		double logX = Math.log10(x);
		double logY = Math.log10(y);
		double logM = Math.log10(m);
		
			
		double logN =  6.4966;
        
		double NGD = (Math.max(logX, logY) - logM)
				/ (logN - Math.min(logX, logY));
		return NGD;
	}
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Avg_link_all pair = new Avg_link_all();
		//pair.two_article(3001,3002);

		//int start = Integer.valueOf(args[0]);
		//int end = Integer.valueOf(args[1]);
		int start = 3001;
		int end = 3010;
		for(int i=start;i<=end;i++){
			for(int j=i+1;j<=end;j++){
				pair.two_article(i,j);
				
			}
		}              //十篇文件?!

	}
	

}
