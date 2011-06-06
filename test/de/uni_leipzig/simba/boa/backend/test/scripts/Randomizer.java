package de.uni_leipzig.simba.boa.backend.test.scripts;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 *
 * @author ngonga
 */
public class Randomizer {
    public static void randomize(int n, String input, String output)
    {
        try
        {
             BufferedReader reader = new BufferedReader(new FileReader(input));
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(output)));
            
            String s, buffer="";
            ArrayList<String> entries = new ArrayList<String>();
            TreeSet<Integer> keys = new TreeSet<Integer>();
            s = reader.readLine();
            while(s!=null)
            {
                s.trim();
                if(s.equals("\n") || s.length() < 5)
                {
                    //System.out.println("Added "+buffer); 
                    entries.add(buffer+"\n");
                    buffer="";                    
                }
                else
                {
                    buffer = buffer + s + "\n";
                }
                //System.out.println(s);
                s = reader.readLine();
            }
            entries.add(buffer);
            //System.out.println(entries.size());
            int k;
            if(entries.size() > n)
            {
                while(keys.size() < n)
                {
                    k = (int)(Math.random()*entries.size());
                    if(!keys.contains(k))
                    keys.add(k);                    
                }                
            }
            
            //System.out.println("Keys = "+keys.size());
            
            int counter = 1;
            for(Integer i: keys)
            {
                writer.println(" *** "+counter+" *** ");
                writer.println(entries.get(i));
                counter++;
            }
            reader.close();
            writer.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public static void main(String args[])
    {
    	randomize(100, "/Users/gerb/ROBOT/top1n20/en_wiki_loc.rdf", "/Users/gerb/ROBOT/top1n20/en_wiki_loc_100.rdf");
        randomize(100, "/Users/gerb/ROBOT/top1n20/en_wiki_per.rdf", "/Users/gerb/ROBOT/top1n20/en_wiki_per_100.rdf");
        randomize(100, "/Users/gerb/ROBOT/top1n20/en_wiki_org.rdf", "/Users/gerb/ROBOT/top1n20/en_wiki_org_100.rdf");
    	
        randomize(100, "/Users/gerb/ROBOT/top1n20/en_news_loc.rdf", "/Users/gerb/ROBOT/top1n20/en_news_loc_100.rdf");
        randomize(100, "/Users/gerb/ROBOT/top1n20/en_news_per.rdf", "/Users/gerb/ROBOT/top1n20/en_news_per_100.rdf");
        randomize(100, "/Users/gerb/ROBOT/top1n20/en_news_org.rdf", "/Users/gerb/ROBOT/top1n20/en_news_org_100.rdf");
        
        randomize(100, "/Users/gerb/ROBOT/top2n20/en_wiki_loc.rdf", "/Users/gerb/ROBOT/top2n20/en_wiki_loc_100.rdf");
        randomize(100, "/Users/gerb/ROBOT/top2n20/en_wiki_per.rdf", "/Users/gerb/ROBOT/top2n20/en_wiki_per_100.rdf");
        randomize(100, "/Users/gerb/ROBOT/top2n20/en_wiki_org.rdf", "/Users/gerb/ROBOT/top2n20/en_wiki_org_100.rdf");
        
        randomize(100, "/Users/gerb/ROBOT/top2n20/en_news_loc.rdf", "/Users/gerb/ROBOT/top2n20/en_news_loc_100.rdf");
        randomize(100, "/Users/gerb/ROBOT/top2n20/en_news_per.rdf", "/Users/gerb/ROBOT/top2n20/en_news_per_100.rdf");
        randomize(100, "/Users/gerb/ROBOT/top2n20/en_news_org.rdf", "/Users/gerb/ROBOT/top2n20/en_news_org_100.rdf");
    }
}
