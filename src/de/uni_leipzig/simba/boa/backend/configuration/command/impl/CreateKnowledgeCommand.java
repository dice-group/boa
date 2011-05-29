package de.uni_leipzig.simba.boa.backend.configuration.command.impl;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.lucene.queryParser.ParseException;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternDao;
import de.uni_leipzig.simba.boa.backend.entity.context.Context;
import de.uni_leipzig.simba.boa.backend.entity.context.LeftContext;
import de.uni_leipzig.simba.boa.backend.entity.context.RightContext;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.nlp.NamedEntityRecognizer;
import de.uni_leipzig.simba.boa.backend.rdf.Model;
import de.uni_leipzig.simba.boa.backend.rdf.store.Store;
import de.uni_leipzig.simba.boa.backend.rdf.uri.UriRetrieval;
import de.uni_leipzig.simba.boa.backend.rdf.uri.impl.DbpediaUriRetrieval;
import de.uni_leipzig.simba.boa.backend.search.PatternSearcher;
import de.uni_leipzig.simba.boa.backend.util.DbpediaUtil;

/**
 * 
 * @author Daniel Gerber
 */
public class CreateKnowledgeCommand implements Command {

	private final NLPediaLogger logger = new NLPediaLogger(CreateKnowledgeCommand.class);
	private final PatternDao patternDao = (PatternDao) DaoFactory.getInstance().createDAO(PatternDao.class);
	
	private final NamedEntityRecognizer ner = new NamedEntityRecognizer();
	private final Store store = new Store();
	private final Property rdfType;
	private final Property rdfsLabel;
	private final List<Pattern> patternList = patternDao.findAllPatterns();
	private Model model;
	
	public CreateKnowledgeCommand () {
		
		if ( store.isModelAvailable(NLPediaSettings.getInstance().getSetting("rdfModel")) ) store.removeModel(NLPediaSettings.getInstance().getSetting("rdfModel")); 
		this.model = store.createModelIfNotExists(NLPediaSettings.getInstance().getSetting("rdfModel"));
		this.rdfType = this.model.createProperty(Constants.RDF_TYPE);
		this.rdfsLabel = this.model.createProperty(Constants.RDFS_LABEL);
	}
	/**
	 * 
	 */
	@Override public void execute() {

		try {
			
			PatternSearcher patternSearcher = new PatternSearcher(NLPediaSettings.getInstance().getSetting("sentenceIndexDirectory"));
			
			System.out.println("Querying index for " + this.patternList.size() + " patterns!");
			
			for ( Pattern pattern : patternList ) {
				
				if ( pattern.getConfidence() >= new Double(NLPediaSettings.getInstance().getSetting("createKnowledgeThreshold")) ) {
					
					System.out.println("Querying pattern: " + pattern.getNaturalLanguageRepresentation() + " ["+pattern.getId()+"]");
					
					String domainUri	= pattern.getPatternMapping().getRdfsDomain();
					String rangeUri		= pattern.getPatternMapping().getRdfsRange();
					
					String patternWithOutVariables = pattern.getNaturalLanguageRepresentation().substring(0, pattern.getNaturalLanguageRepresentation().length() - 3).substring(3).trim();
					
					this.logger.debug("Querying index for pattern \"" + patternWithOutVariables + "\".");
					Set<String> sentences = patternSearcher.getSentencesWithString(patternWithOutVariables, Integer.valueOf(NLPediaSettings.getInstance().getSetting("maxNumberOfDocuments")));
					this.logger.debug("Pattern \"" + patternWithOutVariables + "\" returned " + sentences.size() + " results.");
					
					for ( String foundString : sentences ) {
						
						String nerTagged = this.ner.recognizeEntitiesInString(foundString);
						
						try {
	
							Context leftContext = new LeftContext(nerTagged, foundString, patternWithOutVariables);
							Context rightContext = new RightContext(nerTagged, foundString, patternWithOutVariables);
							
							boolean beginsWithDomain = pattern.getNaturalLanguageRepresentation().startsWith("?D?") ? true : false;
							
							if ( beginsWithDomain ) {
								
								if ( leftContext.containsSuitableEntity(domainUri) && rightContext.containsSuitableEntity(rangeUri) ) {
									
									String leftLabel = leftContext.getSuitableEntity(domainUri);
									String rightLabel = rightContext.getSuitableEntity(rangeUri);
									
									UriRetrieval uriRetrieval = new DbpediaUriRetrieval();
									String leftUri = uriRetrieval.getUri(leftLabel);
									String rightUri = uriRetrieval.getUri(rightLabel);

//									boolean leftResourceHasCorrectType	= DbpediaUtil.getInstance().askIsResourceOfType(leftUri, domainUri);
//									boolean rightResourceHasCorrectType	= DbpediaUtil.getInstance().askIsResourceOfType(rightUri, rangeUri);
//									
//									if ( rightResourceHasCorrectType && leftResourceHasCorrectType ) {
//										
//										
//									}
									
									RDFNode leftResource	= model.createResource(leftUri);
									RDFNode rightResource	= model.createResource(rightUri);
									
									Statement link			= model.createStatement(leftResource, model.createProperty(pattern.getPatternMapping().getUri()), rightResource);
//									Statement labelLeft		= model.createStatement((Resource)leftResource, this.rdfsLabel, leftLabel);
//									Statement labelRight	= model.createStatement((Resource)rightResource, this.rdfsLabel, rightLabel);
//									Statement typeLeft		= model.createStatement(leftResource, this.rdfType, model.createResource(pattern.getPatternMapping().getRdfsDomain()));
//									Statement typeRight		= model.createStatement(rightResource, this.rdfType, model.createResource(pattern.getPatternMapping().getRdfsRange()));
									
									model.addStatement(link);
//									model.addStatement(labelLeft);
//									model.addStatement(labelRight);
//									model.addStatement(typeLeft);
//									model.addStatement(typeRight);
									
									System.out.println("Statement created: " + link);
								}
							}
							else {
								
								if ( leftContext.containsSuitableEntity(rangeUri) && rightContext.containsSuitableEntity(domainUri) ) {
									
									String leftLabel = leftContext.getSuitableEntity(rangeUri);
									String rightLabel = rightContext.getSuitableEntity(domainUri);
									
									UriRetrieval uriRetrieval = new DbpediaUriRetrieval();
									String leftUri = uriRetrieval.getUri(leftLabel);
									String rightUri = uriRetrieval.getUri(rightLabel);
									
//									boolean leftResourceHasCorrectType	= DbpediaUtil.getInstance().askIsResourceOfType(leftUri, rangeUri);
//									boolean rightResourceHasCorrectType	= DbpediaUtil.getInstance().askIsResourceOfType(rightUri, domainUri);
									
									RDFNode leftResource	= model.createResource(leftUri);
									RDFNode rightResource	= model.createResource(rightUri);
									
									Statement link			= model.createStatement(leftResource, model.createProperty(pattern.getPatternMapping().getUri()), rightResource);
//									Statement labelLeft		= model.createStatement((Resource)leftResource, this.rdfsLabel, leftLabel);
//									Statement labelRight	= model.createStatement((Resource)rightResource, this.rdfsLabel, rightLabel);
//									Statement typeLeft		= model.createStatement(leftResource, this.rdfType, model.createResource(pattern.getPatternMapping().getRdfsDomain()));
//									Statement typeRight		= model.createStatement(rightResource, this.rdfType, model.createResource(pattern.getPatternMapping().getRdfsRange()));
									
									model.addStatement(link);
//									model.addStatement(labelLeft);
//									model.addStatement(labelRight);
//									model.addStatement(typeLeft);
//									model.addStatement(typeRight);
									
									System.out.println("Statement created: " + link);
								}
							}
						}
						catch ( IndexOutOfBoundsException ioob ) {
							
//							System.out.println(foundString);
//							System.out.println(nerTagged);
//							System.out.println(patternWithOutVariables);
//							
//							ioob.printStackTrace();
							this.logger.error("Could not create context for string " + foundString + ". NER tagged: " + nerTagged + " pattern: "  + patternWithOutVariables);
						}
					}
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
