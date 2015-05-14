

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class BetaSimilarity_Filtering
{
	public BetaSimilarity_Filtering(){}
	
	public static void BetaSimilarity_Filtering_test(double thr) throws IOException
	{
		// 輸入	
		File fr = new File("D:/DataTemp/Processing/Concept/BetaSimilarityResult.txt");
		if(fr.exists())
		{
			FileReader FileStream = new FileReader(fr);
			BufferedReader BufferedStream = new BufferedReader(FileStream);
			String line = "";
			ArrayList<String> list = new ArrayList<String>();
			
			while ((line = BufferedStream.readLine()) != null)
			{
				String pair = line.split(":")[0];
				double sim = Double.parseDouble(line.split(":")[1]);
			
				if(sim < thr)
					sim = 0;
			
				list.add(pair + ":" + sim);
				
				String[] info = line.split("[,|:]");
		           String value1 = info[0];
		           String value2 = info[1];
		           double relevenceValue = Double.valueOf(info[2]);
		           System.out.println("test"+value1+value2+relevenceValue);
			}
			
			
			BufferedStream.close();
			
			
			// 輸出
			BufferedWriter bw = new BufferedWriter(new FileWriter("D:/DataTemp/Processing/Concept/BetaSimilarityMatrix.txt"));
			for(String s: list)
			{
				try 
				{
					bw.write(s);
					bw.newLine();
				}
				catch (IOException f) 
				{
					f.printStackTrace();
				}
			}
			bw.flush();
			bw.close();			
		}
	}
	public static void main(double thr)throws IOException
	{
		BetaSimilarity_Filtering_test(thr);
	}
	
	public static void main(String args[])throws IOException
	{
		main(Double.valueOf(0.2));   //設定門檻值
	}
}