package de.uni_leipzig.simba.boa.backend.configuration.command.impl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.danielgerber.file.BufferedFileWriter;
import de.danielgerber.file.BufferedFileWriter.WRITER_WRITE_MODE;
import de.danielgerber.file.FileUtil;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternMappingDao;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureHelper;
import de.uni_leipzig.simba.boa.backend.persistance.serialization.SerializationManager;

public class CreateMachineLearningCommand implements Command {

	static List<Integer> doNotUseThose = new ArrayList<Integer>();
	
	@Override
	public void execute() {

		List<Pair> pairs = new ArrayList<Pair>();
		
		// collect all the patterns
		for ( PatternMapping mapping : SerializationManager.getInstance().deserializePatternMappings(NLPediaSettings.BOA_DATA_DIRECTORY + "patternmappings/") ) {
			
			// get x patterns for each mapping and add it with the mapping URI to the map 
			List<Pattern> patterns = new ArrayList<Pattern>(mapping.getPatterns()); 
			Collections.shuffle(patterns);//PatternUtil.getTopNPattern(mapping, PatternSelectionStrategy.ALL, 5, null);
			
			if ( patterns.size() <= 200 ) {
				
				for (Pattern p : patterns) pairs.add(new Pair(mapping, p));
			}
			else {

				for ( int i = 0; i < 100 && i < patterns.size() ; i++) {
					
					if ( !doNotUseThose.contains( new Integer(patterns.get(i).getId())) ) {
						
						pairs.add(new Pair(mapping, patterns.get(i)));
					}
				}
			}
			
			System.out.println(String.format("Added %s patterns for mapping %s", patterns.size(), mapping.getProperty().getUri()));
		}
		System.out.println(String.format("Found %s patterns at all", pairs.size()));
		Collections.shuffle(pairs);
		
		BufferedFileWriter writer = FileUtil.openWriter("/Users/gerb/machine_learning_input.txt", "UTF-8", WRITER_WRITE_MODE.OVERRIDE);
		
//		int i = 0;
//		for ( Pair pair : pairs ) {
//			
//			if ( i++ == 1000 ) break;
//			writer.write(FeatureHelper.createNetworkTrainingFileLine(pair.getMapping(), pair.getPattern()));
//		}
//		writer.close();
	}
	
	private class Pair {
		
		private PatternMapping mapping;
		private Pattern pattern;
		
		private Pair (PatternMapping mapping, Pattern pattern) {
			
			this.setMapping(mapping);
			this.setPattern(pattern);
		}

		/**
		 * @return the mapping
		 */
		public PatternMapping getMapping() {

			return mapping;
		}

		/**
		 * @param mapping the mapping to set
		 */
		public void setMapping(PatternMapping mapping) {

			this.mapping = mapping;
		}

		/**
		 * @return the pattern
		 */
		public Pattern getPattern() {

			return pattern;
		}

		/**
		 * @param pattern the pattern to set
		 */
		public void setPattern(Pattern pattern) {

			this.pattern = pattern;
		}
	}
	
	static {
		
		doNotUseThose.addAll(Arrays.asList(648, 649, 650, 651, 652, 653, 654, 655, 656, 657, 658, 659, 660, 661, 662, 663, 664, 665, 666, 667, 668, 669, 670, 671, 672, 673, 674, 675, 676, 677, 678, 679, 680, 681, 682, 683, 684, 685, 686, 687, 688, 689, 690, 691, 692, 693, 694, 695, 696, 697, 698, 699, 700, 701, 702, 703, 704, 705, 706, 707, 708, 709, 710, 711, 712, 713, 714, 715, 716, 717, 718, 719, 720, 721, 722, 723, 724, 725, 726, 727, 728, 729, 730, 731, 732, 733, 734, 735, 736, 737, 738, 739, 740, 741, 742, 743, 744, 745, 746, 747, 748, 749, 750, 751, 752, 753, 754, 755, 756, 757, 758, 759, 760, 761, 762, 763, 764, 765, 766, 767, 768, 769, 770, 771, 772, 773, 774, 775, 776, 777, 778, 779, 780, 781, 782, 783, 784, 785, 786, 787, 788, 789, 790, 791, 792, 793, 794, 795, 796, 797, 798, 799, 800, 801, 802, 803, 804, 805, 806, 807, 808, 809, 810, 811));
	}
	
	public static void main(String[] args) {

		NLPediaSetup s = new NLPediaSetup(true);
		CreateMachineLearningCommand c = new CreateMachineLearningCommand();
		c.execute();
	}
}
