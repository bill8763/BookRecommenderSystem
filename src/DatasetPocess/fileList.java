package DatasetPocess;

import java.io.*;
import java.util.*;

/**
 * 
 * �C�X��Ƨ����Ҧ��ɮ�
 * @author chiang 
 */
public class fileList {
	public static void main(String[] args) {

		File a = new File("D:/dataset/0430filtering");

		String[] filenames;
		String fullpath = a.getAbsolutePath();

		if (a.isDirectory()) {
			filenames = a.list();
			for (int i = 0; i < filenames.length; i++) {
				File tempFile = new File(fullpath + "\\" + filenames[i]);
				if (tempFile.isDirectory()) {
					System.out.println("�ؿ�:" + filenames[i]);
				} else
					System.out.print("\""+filenames[i].split(".txt")[0]+"\""+",");
			}
		} else
			System.out.println("[" + a + "]���O�ؿ�");

	}
}