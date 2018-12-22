package com.sk.SkAssignment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public class DataPuller {

	static void saveDataInFile() {
		try {
			JSONObject jsonObject = getJsonData();
			JSONArray detailData = jsonObject.getJSONArray("cards");

			String headers[] = { "id", "post_title", "url", "category", "no_reads" };
			File f = new File("/home/dpchn/Desktop/skdata/count.txt");

			String masterFileName = "/home/dpchn/Desktop/skdata/sk_master.csv";
			int firstNoOfRecord = 0;

			if (f.exists() && !f.isDirectory()) {

				firstNoOfRecord = UtilClass.getSizeOfFirstRecord();
				System.out.println("Previous data size " + firstNoOfRecord);
				Map<String, JSONObject> jsonMappedWithId = getLatestArticleFromJson(detailData, firstNoOfRecord);

				if (jsonMappedWithId.size() > 0) {
					CSVWriter latestFileWriter = UtilClass.getFileCSVWriter(getFileNameWithDateTime());
					latestFileWriter.writeNext(headers);
					for (String id : jsonMappedWithId.keySet()) {
						JSONObject data = (JSONObject) jsonMappedWithId.get(id);
						
						String title = data.optString("title").trim();
						String url = data.optString("permalink").trim();
						String read_count = data.optString("read_count").trim();
						String category = data.getJSONArray("category").get(0).toString().trim();
						String str[] = { id, title, url, read_count, category };
						latestFileWriter.writeNext(str);
					}
					latestFileWriter.close();
					System.out.println("New Data Saved successfully");
				} else {
					System.out.println("No new Record found");
				}
			} else {
				CSVWriter masterWriter = UtilClass.getFileCSVWriter(masterFileName);
				CSVWriter latestFileWriter = UtilClass.getFileCSVWriter(getFileNameWithDateTime());
				latestFileWriter.writeNext(headers);
				masterWriter.writeNext(headers);
				for (int i = 0; i < detailData.length(); i++) {
					JSONObject data = (JSONObject) detailData.get(i);
					String id = data.optString("ID").trim();
					String title = data.optString("title").trim();
					String url = data.optString("permalink").trim();
					String read_count = data.optString("read_count").trim();
					String category = data.getJSONArray("category").get(0).toString().trim();
					String dataToInsert[] = { id, title, url, category, read_count };
					latestFileWriter.writeNext(dataToInsert);
					masterWriter.writeNext(dataToInsert);
					firstNoOfRecord++;
				}
				System.out.println("New Data Saved");
				latestFileWriter.close();
				masterWriter.close();
				UtilClass.saveSizeOfFirstRecordNeed(firstNoOfRecord);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static JSONObject getJsonData() {
		String url = "https://login.sportskeeda.com/en/feed?page=1";
		HttpGet getRequest = new HttpGet(url);
		DefaultHttpClient client = new DefaultHttpClient();
		try {
			HttpResponse httpResponse = client.execute(getRequest);
			BufferedReader rd = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			JSONObject jsonObject = new JSONObject(result.toString());
			return jsonObject;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	static String getFileNameWithDateTime() {
		String file = "/home/dpchn/Desktop/skdata/sk_csv_";
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		String minutes = String.valueOf(date.getMinutes());
		if (minutes.length() == 1) {
			minutes = "0" + minutes;
		}
		file += cal.getTime().getHours() + ":" + minutes + "_" + date.getDate() + "_"
				+ cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + "_" + cal.get(Calendar.YEAR)
				+ ".csv";
		return file;
	}

	static Map<String, JSONObject> getJsondataMappedWithId(JSONArray jsonArray) {
		Map<String, JSONObject> map = new HashMap<String, JSONObject>();
		try {
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject data = (JSONObject) jsonArray.get(i);
				map.put(data.optString("ID"), data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * Method to extract latest data from Json and put in master file
	 */
	static Map<String, JSONObject> getLatestArticleFromJson(JSONArray dataArticle, int sizeOfDataNeedToSave) {
		Map<String, JSONObject> latestData = getJsondataMappedWithId(dataArticle);
		try {
			String masterFileName = "/home/dpchn/Desktop/skdata/sk_master.csv";
			File masterFile = new File(masterFileName);
			FileReader masterFileReader = new FileReader(masterFile);
			CSVReader masterReader = new CSVReader(masterFileReader);
			List<String[]> list = masterReader.readAll();

			for (String str[] : list) {
				String id = str[0];
				if (latestData.containsKey(id)) {
					latestData.remove(id);
				}
			}
			System.out.println("SIze after r " + latestData.size());
			masterReader.close();

			CSVWriter csvWriter = UtilClass.getFileCSVWriter(masterFileName);
			for (String id : latestData.keySet()) {
				JSONObject data = (JSONObject) latestData.get(id);
				String title = data.optString("title");
				String url = data.optString("permalink");
				String read_count = data.optString("read_count");
				String category = data.getJSONArray("category").get(0).toString();
				String str[] = { id, title, url, read_count, category };
				csvWriter.writeNext(str);
			}
			csvWriter.close();

			
			int totalNewRecord = sizeOfDataNeedToSave - latestData.size();
			System.out.println("total new records : " + totalNewRecord);
			if (totalNewRecord > 0) {
				int listSize = list.size() - 1;
				for (int i = 0; i < totalNewRecord; i++) {
					String str[] = list.get(listSize - i);
					JSONObject object = new JSONObject();
					object.put("ID", str[0]);
					object.put("title", str[1]);
					object.put("permalink", str[2]);
					object.put("read_count", str[3]);
					object.put("category", new JSONArray("["+str[4]+"]"));
					latestData.put(str[0], object);
				}
			} else {
				System.out.println("No new record found");
			}
			System.out.println("Size of rec to insert" + latestData.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return latestData;
	}
}
