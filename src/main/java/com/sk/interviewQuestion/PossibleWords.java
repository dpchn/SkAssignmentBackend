package com.sk.interviewQuestion;

import java.util.ArrayList;
import java.util.List;

public class PossibleWords {

	public static void main(String[] args) {
		String input = "277";
		List<String> list = getListOfGeneratedWord(input, 0, new ArrayList<String>(), "");
		System.out.println("No. of words : "+list.size());
		list.forEach(System.out::println);
	}

	static List<String> getListOfGeneratedWord(String input, int start, List<String> list, String word) {
		if (start == input.length()) {
			list.add(word);
			return list;
		}
		
		
		String characters = getKeyCharacterOFnumbwe(input.charAt(start));
		
		if(characters.length()==0) {
			return list;
		}
		
		for (int i = 0; i < characters.length(); i++) {
			getListOfGeneratedWord(input, start+1, list, word + characters.charAt(i));
			
		}
		return list;
	}

	static String getKeyCharacterOFnumbwe(char key) {
		switch (key) {
		case '1':
			return "";
		case '2':
			return "abc";
		case '3':
			return "def";
		case '4':
			return "ghi";
		case '5':
			return "jkl";
		case '6':
			return "mno";
		case '7':
			return "pqrs";
		case '8':
			return "tuv";
		case '9':
			return "wxyz";
		}
		return "";
	}
}
