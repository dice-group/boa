package de.uni_leipzig.simba.boa.backend.configuration.command.impl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.apache.lucene.queryParser.ParseException;

import com.hp.hpl.jena.db.RDFRDBException;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternMappingDao;
import de.uni_leipzig.simba.boa.backend.entity.context.Context;
import de.uni_leipzig.simba.boa.backend.entity.context.LeftContext;
import de.uni_leipzig.simba.boa.backend.entity.context.RightContext;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.nlp.NamedEntityRecognizer;
import de.uni_leipzig.simba.boa.backend.rdf.Model;
import de.uni_leipzig.simba.boa.backend.rdf.store.Store;
import de.uni_leipzig.simba.boa.backend.rdf.uri.UriRetrieval;
import de.uni_leipzig.simba.boa.backend.rdf.uri.impl.DbpediaUriRetrieval;
import de.uni_leipzig.simba.boa.backend.search.PatternSearcher;

/**
 * 
 * @author Daniel Gerber
 */
public class CreateKnowledgeCommand implements Command {

	private final NLPediaLogger logger = new NLPediaLogger(CreateKnowledgeCommand.class);
	private final PatternMappingDao patternMappingDao = (PatternMappingDao) DaoFactory.getInstance().createDAO(PatternMappingDao.class);

	private final NamedEntityRecognizer ner = new NamedEntityRecognizer();
	private final Store store = new Store();
	private final Property rdfType;
	private final Property rdfsLabel;
	private final List<PatternMapping> patternMappingList = patternMappingDao.findAllPatternMappings();
	private Model model;
	private List<String> statementList = new ArrayList<String>();

	public CreateKnowledgeCommand() {

		if (store.isModelAvailable(NLPediaSettings.getInstance().getSetting("rdfModel")))
			store.removeModel(NLPediaSettings.getInstance().getSetting("rdfModel"));
		this.model = store.createModelIfNotExists(NLPediaSettings.getInstance().getSetting("rdfModel"));
		this.rdfType = this.model.createProperty(Constants.RDF_TYPE);
		this.rdfsLabel = this.model.createProperty(Constants.RDFS_LABEL);
	}

	/**
	 * 
	 */
	@Override
	public void execute() {

		try {

			BufferedWriter out = new BufferedWriter(new FileWriter(NLPediaSettings.getInstance().getSetting("rdfModelFile")));

			PatternSearcher patternSearcher = new PatternSearcher(NLPediaSettings.getInstance().getSetting("sentenceIndexDirectory"));

			for (PatternMapping mapping : this.patternMappingList) {

				System.out.println("Querying pattern: " + mapping.getProperty().getUri());

				List<Pattern> patternList = this.getTopNPattern(mapping, 5);

				System.out.println(patternList.size() + " patterns found!");

				for (Pattern pattern : patternList) {

					System.out.println("Querying pattern: " + pattern.getNaturalLanguageRepresentation() + " [ID:" + pattern.getId() + ", conf:" + pattern.getConfidence() + "]");

					String domainUri = pattern.getPatternMapping().getProperty().getRdfsDomain();
					String rangeUri = pattern.getPatternMapping().getProperty().getRdfsRange();

					String patternWithOutVariables = pattern.getNaturalLanguageRepresentation().substring(0, pattern.getNaturalLanguageRepresentation().length() - 3).substring(3).trim();

					this.logger.debug("Querying index for pattern \"" + patternWithOutVariables + "\".");
					Set<String> sentences = patternSearcher.getExactMatchSentences(patternWithOutVariables, Integer.valueOf(NLPediaSettings.getInstance().getSetting("maxNumberOfDocuments")));
					this.logger.debug("Pattern \"" + patternWithOutVariables + "\" returned " + sentences.size() + " results.");

					for (String foundString : sentences) {

						String nerTagged = this.ner.recognizeEntitiesInString(foundString);

						try {

							Context leftContext = new LeftContext(nerTagged, foundString, patternWithOutVariables);
							Context rightContext = new RightContext(nerTagged, foundString, patternWithOutVariables);

							boolean beginsWithDomain = pattern.getNaturalLanguageRepresentation().startsWith("?D?") ? true : false;

							if (beginsWithDomain) {

								if (leftContext.containsSuitableEntity(domainUri) && rightContext.containsSuitableEntity(rangeUri)) {

									String leftLabel = leftContext.getSuitableEntity(domainUri);
									String rightLabel = rightContext.getSuitableEntity(rangeUri);

									UriRetrieval uriRetrieval = new DbpediaUriRetrieval();
									String leftUri = uriRetrieval.getUri(leftLabel);
									String rightUri = uriRetrieval.getUri(rightLabel);

									// boolean leftResourceHasCorrectType =
									// DbpediaUtil.getInstance().askIsResourceOfType(leftUri,
									// domainUri);
									// boolean rightResourceHasCorrectType =
									// DbpediaUtil.getInstance().askIsResourceOfType(rightUri,
									// rangeUri);
									//
									// if ( rightResourceHasCorrectType &&
									// leftResourceHasCorrectType ) {
									//
									//
									// }

									RDFNode leftResource = model.createResource(leftUri);
									RDFNode rightResource = model.createResource(rightUri);

									Statement link = model.createStatement(leftResource, model.createProperty(pattern.getPatternMapping().getProperty().getUri()), rightResource);
									// Statement labelLeft =
									// model.createStatement((Resource)leftResource,
									// this.rdfsLabel, leftLabel);
									// Statement labelRight =
									// model.createStatement((Resource)rightResource,
									// this.rdfsLabel, rightLabel);
									// Statement typeLeft =
									// model.createStatement(leftResource,
									// this.rdfType,
									// model.createResource(pattern.getPatternMapping().getRdfsDomain()));
									// Statement typeRight =
									// model.createStatement(rightResource,
									// this.rdfType,
									// model.createResource(pattern.getPatternMapping().getRdfsRange()));

									model.addStatement(link);
									// model.addStatement(labelLeft);
									// model.addStatement(labelRight);
									// model.addStatement(typeLeft);
									// model.addStatement(typeRight);

									if (!statementList.contains(link.toString())) {

										statementList.add(link.toString());
										out.append("FoundString: " + foundString + Constants.NEW_LINE_SEPARATOR);
										out.append("LeftLabel(Domain):\t" + leftLabel + Constants.NEW_LINE_SEPARATOR);
										out.append("Pattern: " + pattern.getNaturalLanguageRepresentation() + Constants.NEW_LINE_SEPARATOR);
										out.append("RightLabel(Range):\t" + rightLabel + Constants.NEW_LINE_SEPARATOR);
										out.append("Statement created: " + link.toString() + Constants.NEW_LINE_SEPARATOR);
										out.append(Constants.NEW_LINE_SEPARATOR);
										System.out.println("Statement created: " + link);
									}
								}
							}
							else {

								if (leftContext.containsSuitableEntity(rangeUri) && rightContext.containsSuitableEntity(domainUri)) {

									String leftLabel = leftContext.getSuitableEntity(rangeUri);
									String rightLabel = rightContext.getSuitableEntity(domainUri);

									UriRetrieval uriRetrieval = new DbpediaUriRetrieval();
									String leftUri = uriRetrieval.getUri(leftLabel);
									String rightUri = uriRetrieval.getUri(rightLabel);

									// boolean leftResourceHasCorrectType =
									// DbpediaUtil.getInstance().askIsResourceOfType(leftUri,
									// rangeUri);
									// boolean rightResourceHasCorrectType =
									// DbpediaUtil.getInstance().askIsResourceOfType(rightUri,
									// domainUri);

									RDFNode leftResource = model.createResource(leftUri);
									RDFNode rightResource = model.createResource(rightUri);

									Statement link = model.createStatement(rightResource, model.createProperty(pattern.getPatternMapping().getProperty().getUri()), leftResource);
									// Statement labelLeft =
									// model.createStatement((Resource)leftResource,
									// this.rdfsLabel, leftLabel);
									// Statement labelRight =
									// model.createStatement((Resource)rightResource,
									// this.rdfsLabel, rightLabel);
									// Statement typeLeft =
									// model.createStatement(leftResource,
									// this.rdfType,
									// model.createResource(pattern.getPatternMapping().getRdfsDomain()));
									// Statement typeRight =
									// model.createStatement(rightResource,
									// this.rdfType,
									// model.createResource(pattern.getPatternMapping().getRdfsRange()));

									model.addStatement(link);
									// model.addStatement(labelLeft);
									// model.addStatement(labelRight);
									// model.addStatement(typeLeft);
									// model.addStatement(typeRight);

									if (!statementList.contains(link.toString())) {

										statementList.add(link.toString());
										out.append("FoundString: " + foundString + Constants.NEW_LINE_SEPARATOR);
										out.append("LeftLabel(Range):\t" + leftLabel + Constants.NEW_LINE_SEPARATOR);
										out.append("Pattern: " + pattern.getNaturalLanguageRepresentation() + Constants.NEW_LINE_SEPARATOR);
										out.append("RightLabel(Domain):\t" + rightLabel + Constants.NEW_LINE_SEPARATOR);
										out.append("Statement created: " + link.toString() + Constants.NEW_LINE_SEPARATOR);
										out.append(Constants.NEW_LINE_SEPARATOR);
										System.out.println("Statement created: " + link);
									}
								}
							}
						}
						catch (RDFRDBException rdfrdbe) {

							this.logger.error("Could not create statement!", rdfrdbe);
						}
						catch (IndexOutOfBoundsException ioob) {

							// System.out.println(foundString);
							// System.out.println(nerTagged);
							// System.out.println(patternWithOutVariables);
							//
							// ioob.printStackTrace();
							this.logger.error("Could not create context for string " + foundString + ". NER tagged: " + nerTagged + " pattern: " + patternWithOutVariables);
						}
					}
				}
			}
			out.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private List<Pattern> getTopNPattern(PatternMapping mapping, int topN) {

		List<Pattern> patternList = new ArrayList<Pattern>();

		for (Pattern p : mapping.getPatterns()) {

			if (p.isUseForPatternEvaluation() && p.getNumberOfOccurrences() > 19) {

				patternList.add(p);
			}
		}

		Collections.sort(patternList, new Comparator<Pattern>() {

			@Override
			public int compare(Pattern pattern1, Pattern pattern2) {

				return pattern2.getConfidence().compareTo(pattern1.getConfidence());
			}
		});

		return patternList.size() > topN ? patternList.subList(0, topN) : patternList;
	}
}
