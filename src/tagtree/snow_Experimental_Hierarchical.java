package tagtree;
import java.io.IOException;
import java.util.Scanner;


public class snow_Experimental_Hierarchical {
	
	public static void main(String args[]) throws Exception
	{
		System.out.println("Experimental_Hierarchical_Test Running");
		
		Scanner sc = new Scanner(System.in);
		System.out.print("Input the start number of file:");					
		int start = sc.nextInt();					// 開始編號
		//System.out.print("Input the   end number of file:");					
		int end = sc.nextInt();						// 結束編號	
		
		
		sonw_test sonw = new sonw_test();
		
		double PNGD[] = {7};
		double TCP[] = {0.2};
		//double PNGD[] = {8,9};
		//double TCP[] = {0.1,0.2,0.3};
		for(int i=0;i<PNGD.length;i++){
			for(int j=0;j<TCP.length;j++){
				sonw.test(TCP[j], PNGD[i],0); //改實驗路徑地方 1信夫
				System.out.println("TCP值："+TCP[j]);
				System.out.println("PNGD值："+PNGD[i]);
//				double fmeasure = Experimental_Cluster_FScore.Experimental_Cluster_FScore_test(1);		// 計算F-measure
//				double fcubed = Experimental_Cluster_FCubed.Experimental_Cluster_FCubed_test(start,end,1);	// 計算F-cubed		
			}
		}
		
//		double fmeasure = Experimental_Cluster_FScore.Experimental_Cluster_FScore_test(1);		// 計算F-measure
//		double fcubed = Experimental_Cluster_FCubed.Experimental_Cluster_FCubed_test(start,end,1);	// 計算F-cubed		
		
	}

}
