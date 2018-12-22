package com.sk.SkAssignment;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import com.opencsv.CSVWriter;

public class UtilClass {
	static void saveSizeOfFirstRecordNeed(int count) {
		String countFile = "/home/dpchn/Desktop/skdata/count.txt";
		File f = new File(countFile);
		int size = 0;
		try {
			if (f.exists() && !f.isDirectory()) {
				File file2 = new File(countFile);
				FileReader fileReader = new FileReader(file2);
				size = fileReader.read();
				fileReader.close();
			} else {
				FileWriter fileWriter = new FileWriter(f);
				fileWriter.write(count);
				fileWriter.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static int getSizeOfFirstRecord() {
		String countFile = "/home/dpchn/Desktop/skdata/count.txt";
		int size = 0;
		try {
			File file2 = new File(countFile);
			FileReader fileReader = new FileReader(file2);
			size = fileReader.read();
			fileReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return size;
	}
	
	static CSVWriter getFileCSVWriter(String fileName){
		CSVWriter masterWriter = null;
		try {
			File file = new File(fileName);
			FileWriter masterFileWriter  = new FileWriter(file, true);
			masterWriter = new CSVWriter(masterFileWriter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return masterWriter;
	}
}
