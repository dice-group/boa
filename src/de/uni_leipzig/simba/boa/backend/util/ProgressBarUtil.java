package de.uni_leipzig.simba.boa.backend.util;


public class ProgressBarUtil {

	/**
	 * Prints a progress bar on the sys.out stream
	 * by replace the last line. 
	 * 
	 * @param percent - the percent it should print between 0 -> 100
	 */
	public static void printProgBar(int percent){
		
        StringBuilder bar = new StringBuilder("[");

        for(int i = 0; i < 50; i++){
            if( i < (percent/2)){
                bar.append("=");
            }else if( i == (percent/2)){
                bar.append(">");
            }else{
                bar.append(" ");
            }
        }

        bar.append("]   " + percent + "%     ");
        System.out.print("\r" + bar.toString());
    }
}
