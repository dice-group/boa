package de.uni_leipzig.simba.boa.backend.configuration.command.impl.scripts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternDao;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.persistance.hibernate.HibernateFactory;


public class CreateLearndFromDistributionCommand implements Command {

	public CreateLearndFromDistributionCommand(){
		
	}
	
	@Override
	public void execute() {

		try {
			
			System.out.print("Please enter the file path:\t");
			
			Scanner scanner = new Scanner(System.in);
			String path = scanner.next();
			
			String[] databaseIds = new String[]{"en_wiki_loc", "en_wiki_per", "en_wiki_org", "en_news_loc", "en_news_per", "en_news_org"};
//			String[] databaseIds = new String[]{"en_wiki_loc"};
			
			PatternDao patternDao = (PatternDao) DaoFactory.getInstance().createDAO(PatternDao.class);

			Map<String,Map<Integer,Integer>> learnedFromDistribution = new HashMap<String,Map<Integer,Integer>>();
			
			for (String database : databaseIds) {
			
				HibernateFactory.changeConnection(database);
				learnedFromDistribution.put(database, new HashMap<Integer,Integer>());
				
				for ( Pattern pattern : patternDao.findAllPatterns() ) {
					
					Integer max = (int) pattern.retrieveMaxLearnedFrom();
					
					if ( learnedFromDistribution.get(database).containsKey(max) ) learnedFromDistribution.get(database).put(max, learnedFromDistribution.get(database).get(max) + 1);
					else learnedFromDistribution.get(database).put(max, 1);
				}
			}
			Map<Integer,Integer> enWikiLocDistribution = learnedFromDistribution.get(databaseIds[0]);
			Map<Integer,Integer> enWikiPerDistribution = learnedFromDistribution.get(databaseIds[1]);
			Map<Integer,Integer> enWikiOrgDistribution = learnedFromDistribution.get(databaseIds[2]);
			Map<Integer,Integer> enNewsLocDistribution = learnedFromDistribution.get(databaseIds[3]);
			Map<Integer,Integer> enNewsPerDistribution = learnedFromDistribution.get(databaseIds[4]);
			Map<Integer,Integer> enNewsOrgDistribution = learnedFromDistribution.get(databaseIds[5]);
			
			BufferedWriter out = new BufferedWriter(new FileWriter(path + "en_wiki_loc"));
			for (Entry<Integer,Integer> entry : enWikiLocDistribution.entrySet() ) {
				
				out.append(entry.getKey() + "\t" + entry.getValue() + Constants.NEW_LINE_SEPARATOR);
			}
			
			out = new BufferedWriter(new FileWriter(path + "en_wiki_per"));
			for (Entry<Integer,Integer> entry : enWikiPerDistribution.entrySet() ) {
				
				out.append(entry.getKey() + "\t" + entry.getValue());
			}
			
			out = new BufferedWriter(new FileWriter(path + "en_wiki_org"));
			for (Entry<Integer,Integer> entry : enWikiOrgDistribution.entrySet() ) {
				
				out.append(entry.getKey() + "\t" + entry.getValue());
			}
			
			out = new BufferedWriter(new FileWriter(path + "en_news_loc"));
			for (Entry<Integer,Integer> entry : enNewsLocDistribution.entrySet() ) {
				
				out.append(entry.getKey() + "\t" + entry.getValue());
			}
			
			out = new BufferedWriter(new FileWriter(path + "en_news_per"));
			for (Entry<Integer,Integer> entry : enNewsPerDistribution.entrySet() ) {
				
				out.append(entry.getKey() + "\t" + entry.getValue());
			}
			
			out = new BufferedWriter(new FileWriter(path + "en_news_org"));
			for (Entry<Integer,Integer> entry : enNewsOrgDistribution.entrySet() ) {
				
				out.append(entry.getKey() + "\t" + entry.getValue());
			}
			
			out.close();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
