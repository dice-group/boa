package de.uni_leipzig.simba.boa.backend.configuration.command.impl.scripts;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;


public class PosDistributionCommand implements Command {

	@Override
	public void execute() {

		Map<String,Integer> posDistribution = new HashMap<String,Integer>();
//		PatternDao pDao = (PatternDao) DaoFactory.getInstance().createDAO(PatternDao.class);

//		for (Pattern p : pDao.findAllPatterns()) {
//			
//			String pos = p.getPosTaggedString();
//			if (posDistribution.containsKey(pos)) posDistribution.put(pos, posDistribution.get(pos) + 1);
//			else posDistribution.put(pos, 1);
//		}
		
		System.out.print("Enter path to file: ");
		Scanner scanner = new Scanner(System.in);
		String path = scanner.next();
		
		BufferedWriter out;
		
		try {
			out = new BufferedWriter(new FileWriter(path));
			for (Map.Entry<String,Integer> entry : posDistribution.entrySet()) {
				
				out.write(entry.getValue() + "\t|||" + entry.getKey() + Constants.NEW_LINE_SEPARATOR);
			}
			out.close();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
