package de.uni_leipzig.simba.boa.frontend.data;

import java.io.Serializable;

import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;

import com.vaadin.data.util.BeanItemContainer;


@SuppressWarnings("serial")
public class PatternMappingContainer extends BeanItemContainer<PatternMapping> implements Serializable {

	public PatternMappingContainer() throws InstantiationException, IllegalAccessException {
		super(PatternMapping.class);
	}
	
	public static PatternMappingContainer createWithTestData() {
		
		PatternMappingContainer pmc = null;
		try {
			
			pmc = new PatternMappingContainer();
			
			PatternMapping patternMapping1 = new PatternMapping();
			patternMapping1.setUri("http://this.is.an.uri/pattern1");
			patternMapping1.setRdfsDomain("http://dbpedia.org/ontology/Airline");
			patternMapping1.setRdfsRange("http://dbpedia.org/ontology/SportsTeam");
			
			
			PatternMapping patternMapping2 = new PatternMapping();
			patternMapping2.setUri("http://this.is.an.uri/pattern2");
			patternMapping2.setRdfsDomain("http://dbpedia.org/ontology/EducationalInstitution");
			patternMapping2.setRdfsRange("http://dbpedia.org/ontology/Company");
			
			pmc.addItem(patternMapping1);
			pmc.addItem(patternMapping2);
		}
		catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pmc;
	}
}
