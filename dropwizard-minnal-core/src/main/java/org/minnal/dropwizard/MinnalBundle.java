/**
 * 
 */
package org.minnal.dropwizard;

import io.dropwizard.Bundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import org.activejpa.enhancer.ActiveJpaAgentLoader;
import org.glassfish.jersey.server.ResourceConfig;
import org.minnal.instrument.ApplicationEnhancer;
import org.minnal.instrument.NamingStrategy;
import org.minnal.instrument.UnderscoreNamingStrategy;
import org.minnal.instrument.filter.ResponseTransformationFilter;
import org.minnal.instrument.util.MinnalModule;

import com.google.common.collect.Lists;

/**
 * Minnal bundle that instruments all the entities defined to auto generate APIs
 * 
 * @author ganeshs
 *
 */
public class MinnalBundle implements Bundle {
	
	private String[] packagesToScan;
	
	private NamingStrategy namingStrategy;
	
	/**
	 * @param packagesToScan
	 * @param namingStrategy
	 */
	public MinnalBundle(String[] packagesToScan, NamingStrategy namingStrategy) {
		this.packagesToScan = packagesToScan;
		this.namingStrategy = namingStrategy;
	}

	/**
	 * @param packagesToScan
	 */
	public MinnalBundle(String[] packagesToScan) {
		this(packagesToScan, new UnderscoreNamingStrategy());
	}

	public void initialize(Bootstrap<?> bootstrap) {
		getActiveJpaAgentLoader().loadAgent();
	}
	
	protected ActiveJpaAgentLoader getActiveJpaAgentLoader() {
		return ActiveJpaAgentLoader.instance();
	}

	public void run(Environment environment) {
		ResourceConfig config = environment.jersey().getResourceConfig();
		config.register(new ResponseTransformationFilter(Lists.<String>newArrayList(), new UnderscoreNamingStrategy()));
		environment.getObjectMapper().registerModule(new MinnalModule());
		createApplicationEnhancer(environment).enhance();
	}
	
	protected ApplicationEnhancer createApplicationEnhancer(Environment environment) {
		return new DropwizardApplicationEnhancer(environment, packagesToScan, namingStrategy);
	}

}
