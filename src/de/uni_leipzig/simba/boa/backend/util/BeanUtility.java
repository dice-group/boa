package de.uni_leipzig.simba.boa.backend.util;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;

/**
 * BeanUtility provides functions regarding to beans using the Spring Framework.
 */
public class BeanUtility {
	
	/**
	 * @param file - a file object representing file where to load from
	 * @param beanName - the name of the bean
	 * @return the requested bean
	 * @throws IOException - an error occured while getting bean
	 */
	public static Object getBean(File file, String beanName) throws BeansException {
		
		FileSystemResource beanFile = new FileSystemResource(file.getAbsolutePath());
		XmlBeanFactory beanFactory = new XmlBeanFactory(beanFile);
		
		return beanFactory.getBean(beanName);
	}
	
	/**
	 * @param file - a file object representing the file where to load from
	 * @param type - the type of the beans to be loaded
	 * @return a map containing all found beans
	 * @throws IOException - if an error occurs
	 */
	@SuppressWarnings("unchecked")
	public static Map getBeansOfType(File file, Class type) throws BeansException {
		
		FileSystemResource beanFile = new FileSystemResource(file.getAbsolutePath());
		XmlBeanFactory beanFactory = new XmlBeanFactory(beanFile);
		
		return beanFactory.getBeansOfType(type);
	}
}
