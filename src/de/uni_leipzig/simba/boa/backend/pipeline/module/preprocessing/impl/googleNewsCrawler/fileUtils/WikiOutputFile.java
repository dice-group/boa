package de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.impl.googleNewsCrawler.fileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WikiOutputFile {

	private String outPutFile;
	public BufferedWriter bw;
	private boolean rewriteExistingFile;
	
	private int nrOfDokuments=0;
	private int nrOfNotEmptyDokuments=0;
	
	public WikiOutputFile(String outPutFile, boolean rewriteExistingFile)
			throws IOException {
		this.outPutFile=outPutFile;
		this.rewriteExistingFile=rewriteExistingFile;
		
		bw= new BufferedWriter(new FileWriter(new File(outPutFile),!rewriteExistingFile));
		
	}
	
	public synchronized void appendTextBlock(String uri, String text) throws IOException{		
		bw.append("<doc url=\"").append(uri).append("\">\n");
		bw.append(text);
		bw.append("\n</doc>\n");
		bw.flush();
		nrOfDokuments++;
		if(!text.isEmpty())
			nrOfNotEmptyDokuments++;
		
		if(nrOfDokuments%20==0)
			System.out.println(getStatisic());
	}
	
	public String getStatisic(){
		return "added: "+this.nrOfDokuments+"\tnot empty: "+this.nrOfNotEmptyDokuments;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
