package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import DatasetPocess.fileList;

public class validation {
	public static void main(String[] args) throws Exception{
		String userID = "exp3";
		/**real user
		 * A14OJS0VWMOSWO
		 * AFVQZQ8PW0L
		 * A1D2C0WDCSHUWZ
		 * AHD101501WCN1
		 * A1K1JW1C5CUSUZ
		 * */
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

		String startDateString = "2015/07/31";
		Date startDate = dateFormat.parse(startDateString);
		long startTime = (long) startDate.getTime();

		String endDateString = "2015/08/14";
		Date endDate = dateFormat.parse(endDateString);
		long endTime = (long) endDate.getTime();

		String toRecommendDocDir ="D:/dataset/recommendation/";
		while (startTime < endTime) {
//			recommValidation(userID, toRecommendDocDir,Long.toString(startTime));
			
			startTime = startTime + 24 * 60 * 60 * 1000;
		}
	}
	public static void recommValidation(String user, String toRecommendDocPath, String processingStemp,String correctPath,String outputPath) throws Exception{
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
		Timestamp time = new Timestamp(Long.parseLong(processingStemp));
		String rateday = ft.format(time);
//		recommend.recommendDoc(user, toRecommendDocDir+rateday+"_"+user+".txt");
		String totalPath = "D:/dataset/mainWords/";
//		String correctPath =  "D:/dataset/ontology7_learning/nlpCorrect/";

		/**驗證*/
		List totleArticle =  fileList.getFileList(totalPath);
		List correctArticle = fileList.getFileList(correctPath);
		HashSet<String> recomArticle = new HashSet<>();
		FileReader FileStream1 = null;
		FileStream1 = new FileReader(toRecommendDocPath);
		BufferedReader BufferedStream1 = null;
		BufferedStream1 = new BufferedReader(FileStream1);
		String e2 = "";
		while ((e2 = BufferedStream1.readLine()) != null) {
			recomArticle.add(e2.split(":")[0]);
		}
		int truePositive=0;
		int falsePositive=0;
		BufferedStream1.close();
		for(String article:recomArticle){
			if(correctArticle.contains(article)){
				truePositive++;
			}
			else{
				falsePositive++;
			}
		}
		double precision = (double)truePositive / (double) (truePositive + falsePositive);
		double recall = (double) truePositive / (double) (correctArticle.size());
		double fMeasure = 2 * recall * precision / (recall + precision);
		if(recall==0.0&&precision==0.0){fMeasure=0;}
		BufferedWriter bw;
		bw = new BufferedWriter(new FileWriter(outputPath, true));
		bw.write(rateday+"_"+user+":"+fMeasure+","+precision+","+recall);
		bw.newLine();
		bw.flush();
		bw.close();
	}
}
