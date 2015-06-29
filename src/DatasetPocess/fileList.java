package DatasetPocess;

import java.io.*;
import java.util.*;

/**
 * 
 * �C�X��Ƨ����Ҧ��ɮ�
 * 
 * @author chiang
 */
public class fileList {
	public static void main(String[] args) {

	}

	public static List<String> getFileList(String direction){
		List<String> outputList = new ArrayList<>();
		File a = new File(direction);

		String[] filenames;
		String fullpath = a.getAbsolutePath();

		if (a.isDirectory()) {
			filenames = a.list();
			outputList = Arrays.asList(a.list());
			/**
			 * �H�U�ݻݭn�L�X����榡
			 */
			for (int i = 0; i < filenames.length; i++) {
				File tempFile = new File(fullpath + "\\" + filenames[i]);
				if (tempFile.isDirectory()) {
					System.out.println("資料夾:" + filenames[i]);
				} else
					System.out.print("\""+filenames[i].split(".txt")[0]+"\""+",");
			}
		} else
			System.out.println("[" + a + "]不存在");
		return outputList;
	}
}