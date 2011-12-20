package de.uni_leipzig.simba.boa.evaluation.charts;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import com.googlecode.charts4j.AxisLabels;
import com.googlecode.charts4j.AxisLabelsFactory;
import com.googlecode.charts4j.AxisStyle;
import com.googlecode.charts4j.AxisTextAlignment;
import com.googlecode.charts4j.BarChart;
import com.googlecode.charts4j.BarChartPlot;
import com.googlecode.charts4j.Color;
import com.googlecode.charts4j.Data;
import com.googlecode.charts4j.DataUtil;
import com.googlecode.charts4j.Fills;
import com.googlecode.charts4j.GCharts;
import com.googlecode.charts4j.LinearGradientFill;
import com.googlecode.charts4j.Plots;
import com.sun.org.apache.bcel.internal.generic.NEW;

import de.danielgerber.format.OutputFormatter;
import de.danielgerber.math.MathUtil;
import de.uni_leipzig.simba.boa.backend.comparator.PatternComparator;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternDao;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternMappingDao;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.enums.Feature;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureHelper;
import de.uni_leipzig.simba.boa.backend.util.ListUtil;
import edu.stanford.nlp.util.StringUtils;

import static com.googlecode.charts4j.Color.*;

public class PatternMappingConfidenceDistribution  {

	public static NLPediaSetup s = new NLPediaSetup(false);
	
	public static void main(String[] args) {

		PatternMappingConfidenceDistribution pmcd = new PatternMappingConfidenceDistribution();
//		pmcd.example2();
//		pmcd.saveImage(pmcd.example1(), "/Users/gerb", "patternMappingConfidenceDistribution");
	}
	
	/**
	 * @param imageUrl the computed chart URL for the google chart api 
	 * @param path the path to save the image, wihtout trailing /
	 * @param name the name without the file ending
	 */
	public void saveImage(String imageUrl, String path, String name){
		
		BufferedImage image = null;
        try {
 
            URL url = new URL(imageUrl);
            image = ImageIO.read(url);
 
            ImageIO.write(image, "png", new File(path + "/" + name + ".png"));
 
        } catch (IOException e) {
        	
        	e.printStackTrace();
        }
	}
	
	

	public String example1() {
		
		Collection<Integer> input = getExampleData();
		int max = 0;
		for ( Integer i  : input ) max = Math.max(max, i); 
		Data data = DataUtil.scaleWithinRange(0, max, new ArrayList<Integer>(input));
		
		// Defining data plots.
		BarChartPlot patterns = Plots.newBarChartPlot(data, BLUEVIOLET, "");

		// Instantiating chart.
		BarChart chart = GCharts.newBarChart(patterns);

		// Defining axis info and styles
		AxisStyle axisStyle = AxisStyle.newAxisStyle(BLACK, 13, AxisTextAlignment.CENTER);
		// 50.0 is percent of axis where the labels stands
		AxisLabels numberOfPatterns = AxisLabelsFactory.newAxisLabels("Number of Patterns", 50.0);
		numberOfPatterns.setAxisStyle(axisStyle);
		AxisLabels confidence = AxisLabelsFactory.newAxisLabels("Confidence", 50.0);
		confidence.setAxisStyle(axisStyle);

		// Adding axis info to chart.
		chart.addXAxisLabels(AxisLabelsFactory.newAxisLabels("0.0", "0.1", "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9", "1.0"));
		chart.addXAxisLabels(confidence);
		System.out.println(input);
		
		chart.addYAxisLabels(AxisLabelsFactory.newNumericRangeAxisLabels(0, max));
		chart.addYAxisLabels(numberOfPatterns);

		chart.setSize(547, 547);
		chart.setTitle("Pattern Confidence Distribution", BLACK, 16);
		
		System.out.println(chart.toURLString());
		
		return chart.toURLString();
	}
	
	public static Collection<Integer> getExampleData(){

		List<Pattern> patternList = new ArrayList<Pattern>();
		Random r = new Random();
		for ( int i = 0; i < 100000 ; i++) {
			
			Pattern p = new Pattern();
			p.setConfidence(r.nextDouble());
			patternList.add(p);
		}
		
		Map<String,Integer> confidenceToOccurrence = new TreeMap<String,Integer>();
		confidenceToOccurrence.put("0.0", 0);
		confidenceToOccurrence.put("0.1", 0);
		confidenceToOccurrence.put("0.2", 0);
		confidenceToOccurrence.put("0.3", 0);
		confidenceToOccurrence.put("0.4", 0);
		confidenceToOccurrence.put("0.5", 0);
		confidenceToOccurrence.put("0.6", 0);
		confidenceToOccurrence.put("0.7", 0);
		confidenceToOccurrence.put("0.8", 0);
		confidenceToOccurrence.put("0.9", 0);
		confidenceToOccurrence.put("1.0", 0);
		
		DecimalFormat f = new DecimalFormat("0.0000");
		
		for (Pattern p : patternList ) {
			
			String index = String.valueOf(f.format(p.getConfidence()).substring(0,3));
			confidenceToOccurrence.put(index, confidenceToOccurrence.get(index) + 1);
		}
		return confidenceToOccurrence.values();
	}
}
