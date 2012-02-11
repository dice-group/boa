package de.uni_leipzig.simba.boa.backend.search.surfaceforms;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import weka.core.tokenizers.NGramTokenizer;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.sun.org.apache.bcel.internal.generic.RETURN;

import de.danielgerber.file.FileUtil;
import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.backgroundknowledge.BackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.backgroundknowledge.impl.DatatypePropertyBackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.backgroundknowledge.impl.ObjectPropertyBackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.rdf.ontology.ClassIndexer;

/**
 * This thing needs at least 4GB of RAM.
 * 
 * 
 * @author gerb
 */
public class SurfaceFormGenerator {

    private static NLPediaLogger logger = new NLPediaLogger(SurfaceFormGenerator.class);
    private Map<String,Set<String>> urisToLabels;
    private static SurfaceFormGenerator INSTANCE = null;
    
    private SurfaceFormGenerator() { 
        
        initializeSurfaceForms();
    }
    
    /**
     * @return
     */
    public static SurfaceFormGenerator getInstance() {
        
        if ( SurfaceFormGenerator.INSTANCE == null ) {
            
            SurfaceFormGenerator.INSTANCE = new SurfaceFormGenerator();
        }
        
        return SurfaceFormGenerator.INSTANCE;
    }
    
    

    /**
     * 
     */
    private void initializeSurfaceForms() {
        
        SurfaceFormGenerator.logger.info("Intializing surface forms...");
        
        List<String> surfaceForms    = FileUtil.readFileInList(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.BACKGROUND_KNOWLEDGE_PATH + NLPediaSettings.BOA_LANGUAGE + "_uri_surface_form.tsv", "UTF-8");
        this.urisToLabels = new HashMap<String,Set<String>>(); 
        
        // initialize the surface forms from dbpedia spotlight 
        for ( String line : surfaceForms ) {
            
            String[] lineParts = line.split("\t");
            String[] surfaceFormsPart = Arrays.copyOfRange(lineParts, 1, lineParts.length);
            Set<String> filteredSurfaceForms = new HashSet<String>();
            
            for ( String surfaceForm : surfaceFormsPart) {
                
                if ( surfaceForm.length() >= NLPediaSettings.getIntegerSetting("surfaceFormMinimumLength") ) filteredSurfaceForms.add(" " + surfaceForm + " ");
            }
            this.urisToLabels.put(lineParts[0], filteredSurfaceForms);
        }
        SurfaceFormGenerator.logger.info("Finished intializing surface forms! Found " + urisToLabels.size() + 
                " dbpedia spotlight surfaceforms");
    }
    
    /**
     * 
     * @param backgroundKnowledge
     * @return
     */
    public BackgroundKnowledge createSurfaceFormsForBackgroundKnowledge(BackgroundKnowledge backgroundKnowledge) {
        
        if ( backgroundKnowledge instanceof ObjectPropertyBackgroundKnowledge ) {
            
            return this.createSurfaceFormsForObjectProperty((ObjectPropertyBackgroundKnowledge) backgroundKnowledge);
        } 
        if ( backgroundKnowledge instanceof DatatypePropertyBackgroundKnowledge ) {
            
            return this.createSurfaceFormsForDatatypeProperty((DatatypePropertyBackgroundKnowledge) backgroundKnowledge);
        }
        throw new RuntimeException("background knowledge of wrong type found: "  + backgroundKnowledge.getClass()); 
    }
    
    /**
     * 
     * @param backgroundKnowledge
     * @return
     */
    private BackgroundKnowledge createSurfaceFormsForObjectProperty(BackgroundKnowledge objectPropertyBackgroundKnowledge) {

        String subjectUri    = objectPropertyBackgroundKnowledge.getSubjectUri();
        String objectUri    = objectPropertyBackgroundKnowledge.getObjectUri();
        
        Set<String> subjectSurfaceForms = new HashSet<String>();
        subjectSurfaceForms.add(objectPropertyBackgroundKnowledge.getSubjectLabel());
        
        // we found labels for the subject in the surface form file
        if ( this.urisToLabels.containsKey(subjectUri) ) {
            
            subjectSurfaceForms = urisToLabels.get(subjectUri);
        }
        logger.debug("Found " + subjectSurfaceForms.size() + " at all!");
        subjectSurfaceForms.removeAll(Arrays.asList("", null));
        objectPropertyBackgroundKnowledge.setSubjectSurfaceForms(subjectSurfaceForms);
        
        // ################################################################################
        // ################################################################################
        // ################################################################################
        
        Set<String> objectSurfaceForms = new HashSet<String>();
        objectSurfaceForms.add(objectPropertyBackgroundKnowledge.getObjectLabel());
        
        // we found labels for the object in the surface form file
        if ( this.urisToLabels.containsKey(objectUri) ) {
            
            objectSurfaceForms = urisToLabels.get(objectUri);
        }
        logger.debug("Found " + objectSurfaceForms.size() + " at all");
        
        objectSurfaceForms.removeAll(Arrays.asList("", null));
        objectPropertyBackgroundKnowledge.setObjectSurfaceForms(objectSurfaceForms);
        
        return objectPropertyBackgroundKnowledge;
    }
    
    /** 
     * @param backgroundKnowledge
     * @return
     */
    private BackgroundKnowledge createSurfaceFormsForDatatypeProperty(DatatypePropertyBackgroundKnowledge backgroundKnowledge) {

        BackgroundKnowledge dbk =  this.createSurfaceFormsForObjectProperty(backgroundKnowledge);
        
        if ( !((DatatypePropertyBackgroundKnowledge) dbk).getObjectDatatype().equals("NA")
                && !((DatatypePropertyBackgroundKnowledge) dbk).getObjectDatatype().isEmpty() ) {
            
            dbk.setObjectSurfaceForms(
                    this.createDatatypePropertyLabels(dbk.getObjectLabel(), ((DatatypePropertyBackgroundKnowledge) dbk).getObjectDatatype()));
        }
        
        return dbk;
    }
    
    private Set<String> createDatatypePropertyLabels(String objectLabel, String predicateType) {

        Set<String> labels = new HashSet<String>();
        
        // ######################################################################
        // ################   Double    
        // ######################################################################
        
        if ( predicateType.equals("http://www.w3.org/2001/XMLSchema#double") ) {
            
            labels.addAll(this.handleDouble(objectLabel));
        }
        
        // ######################################################################
        // ################   Non-Negative Integer    
        // ######################################################################
        
        else if ( predicateType.equals("http://www.w3.org/2001/XMLSchema#nonNegativeInteger") 
                || predicateType.equals("http://www.w3.org/2001/XMLSchema#int") ) {
            
            labels.addAll(handleNonNegativeInteger(objectLabel));
        }

        // ######################################################################
        // ################   String    
        // ######################################################################
        
        else if ( predicateType.equals("http://www.w3.org/2001/XMLSchema#string") ) {
            
            labels.addAll(handleString(objectLabel));
        }
        
        // ######################################################################
        // ################   Date    
        // ######################################################################
        
        else if ( predicateType.equals("http://www.w3.org/2001/XMLSchema#date") ) {
            
            labels.addAll(this.handleDate(objectLabel));
        }
        else {
            
            logger.error("There is something obiously wrong: " + objectLabel + "  " + predicateType);
            throw new RuntimeException("There is something obiously wrong: " + objectLabel + "  " + predicateType);
        }
        return labels;
    }
    
    private Set<String> handleDate(String objectLabel)  {
    
        Set<String> variations = new HashSet<String>(); 
        
        String d = getDateString(objectLabel, "yyyy-MM-dd", "d");
        String MM = getDateString(objectLabel, "yyyy-MM-dd", "MM");
        String MMM = getDateString(objectLabel, "yyyy-MM-dd", "MMM");
        String MMMM = getDateString(objectLabel, "yyyy-MM-dd", "MMMM");
        String yy = getDateString(objectLabel, "yyyy-MM-dd", "yy");
        String yyyy = getDateString(objectLabel, "yyyy-MM-dd", "yyyy");
        
        variations.add(getOrdinalFor(Integer.valueOf(d)) + " of " + MMMM + " " + yyyy);
        variations.add(d + " " + MMMM + " " + yyyy);
        variations.add(d + "." + MM + "." + yyyy);
        variations.add(d + "." + MM + "." + yy);
        variations.add(d + " " + MMM + " " + yyyy);
        variations.add(d + " " + MMMM  + " '" + yy);
        variations.add(d + " " + MMM  + " '" + yy);
        variations.add(MMM + " " + d  + " , " + yyyy);
        variations.add(MMMM + " " + yyyy);
        variations.add(MMM + " " + yyyy);
        variations.add(MMMM);
        variations.add(yyyy);
        
        return variations;
    }
    
    private Set<String> handleDouble(String objectLabel) {
        
        // make sure its a number
        Double d = new Double(objectLabel);
        Integer i = d.intValue();
        
        Set<String> variations = new HashSet<String>();
        variations.add(String.valueOf(d));
        variations.add(String.valueOf(i)); // rounded to integer
        variations.add(String.valueOf(((i/5)*5))); // rounded down to next 5: 104 -> 100
        variations.add(String.valueOf(((i/10)*10))); // rounded down to next 10
        variations.add(new DecimalFormat("#.0").format(d));
        variations.add(new DecimalFormat("#.00").format(d));
        
        return variations;
    }
    
    private Set<String> handleString(String objectLabel) {

        Set<String> variations = new HashSet<String>();
        
        // create some ngrams
        NGramTokenizer ngt = new NGramTokenizer();
        ngt.setDelimiters(" ");
        ngt.setNGramMaxSize(2);
        ngt.setNGramMinSize(2);
        ngt.tokenize(objectLabel);
        while (ngt.hasMoreElements())
            variations.add(String.valueOf(ngt.nextElement()));
        
        Set<String> surfaceForms = urisToLabels.get(objectLabel.replace(" ", "_"));
        // replace whitespace with _ and try to get surface forms from the "index"
        if ( surfaceForms != null ) variations.addAll(surfaceForms);
        
        // for some labels there are multiple ones coded in divided by comma  
        if (objectLabel.contains(",")) variations.addAll(Arrays.asList(objectLabel.split(",")));
        
        // sepcial cases
        if (objectLabel.contains("C++") ) variations.add("C + +");
        if (objectLabel.contains("C#") ) variations.add("C #");
        
        return variations;
    }

    /**
     * 
     * @param parseCandidate
     * @return
     */
    private Set<String> handleNonNegativeInteger(String parseCandidate) {

        // some non-negative integers contain decimals and other stuff, so remove it
        if ( parseCandidate.contains(".") ) parseCandidate = parseCandidate.replaceAll("\\.[0-9]+$", "");
        
        // try to parse it
        Integer i = Integer.valueOf(parseCandidate);
        
        Set<String> variations = new HashSet<String>();
            
        if ( i == 1 ) variations.add("first");
        if ( i == 2 ) variations.add("second");
        if ( i == 3 ) variations.add("third");
        if ( i == 4 ) variations.add("fourth");
        if ( i == 5 ) variations.add("fifth");
        variations.add(getOrdinalFor(i));
        variations.add(i.toString());
        if ( i >= 0 ) variations.add(EnglishNumberToWords.convert(Long.valueOf(i)));
        if ( i < 0 ) variations.add("-" + EnglishNumberToWords.convert(Math.abs(Long.valueOf(i))));
        if ( i < 0 ) variations.add("- " + EnglishNumberToWords.convert(Math.abs(Long.valueOf(i))));
        variations.add(String.valueOf(((i/5)*5))); // rounded down to next 5: 104 -> 100
        variations.add(String.valueOf(((i/10)*10))); // rounded down to next 10
        if ( i > 100 ) variations.add(String.valueOf(((i/100)*100))); // rounded down to next 100
        if ( i > 1000 )variations.add(String.valueOf(((i/1000)*1000))); // rounded down to next 1000
        if ( i > 1000000 ) variations.add(String.format("%.1f", i / 1000000.0));
        if ( i > 1000000 ) variations.add(String.format("%.0f", i / 1000000.0));
        if ( i > 1000000000 ) variations.add(String.format("%.1f", i / 1000000000.0));
        if ( i > 1000000000 ) variations.add(String.format("%.0f", i / 1000000000.0));
        
        return variations;
    }

    /**
     * Creates a date string from a given string and pattern.
     * 
     * @param dateString
     * @param fromPattern
     * @param toPattern
     * @return
     * @throws ParseException
     */
    private String getDateString(String dateString, String fromPattern, String toPattern) {

        try {
            
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(fromPattern);
            Date date = simpleDateFormat.parse(dateString);
            
            simpleDateFormat.applyPattern(toPattern);
            return simpleDateFormat.format(date);
        }
        catch (ParseException e) {
            
            logger.error("Parse Exception on date.", e);
            throw new RuntimeException("Parse Exception on date.", e);
        }
    }
    
    /**
     * Returns the ordnial value for a given integer:
     * 
     * 1 -> 1st, 123 -> 123rd
     * 
     * @param value
     * @return
     */
    public String getOrdinalFor(int value) {

        int hundredRemainder    = value % 100;
        int tenRemainder        = value % 10;
        if (hundredRemainder - tenRemainder == 10) return value + "th";

        switch (tenRemainder) {
        case 1:
            return value + "st";
        case 2:
            return value + "nd";
        case 3:
            return value + "rd";
        default:
            return value + "th";
        }
    }
}

//// we have no predicate type given, so let#s try something
//if ( predicateType.equals("null") ) {
//    
//    // test the string if it is empty, if yes then skip this triple 
//    String tempLabel = objectLabel.replaceAll("'|\"|,|-|\\.|/|~", "");
//    tempLabel = objectLabel.replace("none", "").replace("None", "").replace("see below", "");
//    if ( tempLabel.trim().isEmpty() ) throw new NumberFormatException();
//    
//    Set<String> ret = null;
//    
//    // try to parse as double
//    Double d = null;
//    try {
//        
//        d = Double.valueOf(objectLabel);
//    }
//    catch ( NumberFormatException nfe ) {}
//    if ( d != null ) {
//        
//        ret = createDatatypePropertyLabels(String.valueOf(d), "http://www.w3.org/2001/XMLSchema#double");
//    }
//    
//    // try to parse as an integer
//    Integer i = null;
//    try {
//        
//        i = Integer.valueOf(objectLabel);
//    }
//    catch (NumberFormatException nfe) {}
//    if ( i != null ) {
//        
//        ret = createDatatypePropertyLabels(String.valueOf(i), "http://www.w3.org/2001/XMLSchema#nonNegativeInteger");
//    }
//    
//    // not an integer nor a double -> it has to be string
//    if ( i == null && d == null ) {
//        
//        Set<String> surfaceForms = urisToLabels.get(objectLabel.replace(" ", "_"));
//        // replace whitespace with _ and try to get surface forms from the "index"
//        if ( surfaceForms != null ) {
//            
//            ret = StringUtils.join(surfaceForms, "_&_");
//        }
//        else {
//            ret = objectLabel;
//        }
//    }
//    labels.append(ret);
//}