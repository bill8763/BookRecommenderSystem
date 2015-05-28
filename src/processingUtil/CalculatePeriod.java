package processingUtil;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * input: a pair of unix timestamps
 * @author chiang
 *
 */
public class CalculatePeriod {
	public static int periodCalculator(String timestamp1,String timestamp2) { //計算相差日期
		Long time1=Long.valueOf(timestamp1);
		Long time2=Long.valueOf(timestamp2);
	    long dateDiff = 0;
	    if(time1<time2) {
	        dateDiff = ((time2-time1)/(24*60*60));
	    }
	    else {
	        dateDiff = ((time1-time2)/(24*60*60));
	    }
	    return (int) dateDiff;
	}
	public static void main(String[] args){
		System.out.println(periodCalculator("949536000","949622400") );
	}
}
