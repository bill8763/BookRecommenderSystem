package document_clustering;


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
	
	public static void BetaSimilarity_Filtering_test(double thr,String inputFile,String ouputFile) throws IOException
	{
		// ��J	
		File fr = new File(inputFile);
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
			
			
			// ��X
			BufferedWriter bw = new BufferedWriter(new FileWriter(ouputFile));
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
	public static void main(double thr,String input,String output)throws IOException
	{
		BetaSimilarity_Filtering_test(thr,input,output);
	}
	
	public static void main(String args[])throws IOException
	{
		//main(Double.valueOf(0.2));   //�]�w���e��
	}
}