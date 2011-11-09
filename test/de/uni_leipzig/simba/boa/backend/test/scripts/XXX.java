package de.uni_leipzig.simba.boa.backend.test.scripts;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.danielgerber.file.FileUtil;
import de.uni_leipzig.simba.boa.backend.Constants;


public class XXX {

	public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException, IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream("/Users/gerb/en_qa_uniq.txt"))));
		
		int maxLabel = 0;
		int maxLabels = 0;
		
		// 0_URI1 ||| 1_LABEL1 ||| 2_LABELS1 ||| 3_PROP ||| 4_URI2 ||| 5_LABEL2 ||| 6_LABELS2 ||| 7_RANGE ||| 8_DOMAIN
		
		String line;
		while ((line = br.readLine()) != null) {

			String[] l = line.split(" \\|\\|\\| ");
			
			maxLabel = Math.max(l[1].length(), maxLabel);
			maxLabel = Math.max(l[5].length(), maxLabel);
			
			maxLabels = Math.max(l[2].length(), maxLabels);
			maxLabels = Math.max(l[6].length(), maxLabels);
		}
		System.out.println("labels: " + maxLabel);
		System.out.println("surface forms: " + maxLabels);
	}
}
