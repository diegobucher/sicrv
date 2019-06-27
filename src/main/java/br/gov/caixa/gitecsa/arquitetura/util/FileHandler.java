package br.gov.caixa.gitecsa.arquitetura.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileHandler {

	public static void writeFile(final byte[] file, final String path, final String fileName) throws FileNotFoundException, IOException {

		FileOutputStream fos = new FileOutputStream(new File(path + fileName));
		fos.write(file);
		fos.flush();
		fos.close();
	}

	public static byte[] readFile(final String path, final String fileName) throws FileNotFoundException, IOException {
		
		File file = new File(path + fileName);

		if (file.exists()) {
			return getFileBytes(file);
		}
		
		return null;
	}

	public static void deleteFile(final String path, final String fileName) throws FileNotFoundException, IOException {

		File file = new File(path + fileName);
		file.delete();
	}

	private static byte[] getFileBytes(File file) throws FileNotFoundException, IOException {
		int len = (int) file.length();
		byte[] sendBuf = new byte[len];
		
		FileInputStream inFile = null;		
		inFile = new FileInputStream(file);
		inFile.read(sendBuf, 0, len);
		inFile.close();

		return sendBuf;
	}
}
