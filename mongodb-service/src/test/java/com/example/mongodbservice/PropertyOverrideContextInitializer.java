package com.example.mongodbservice;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.support.TestPropertySourceUtils;

public class PropertyOverrideContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                applicationContext, "de.flapdoodle.mongodb.embedded.version=5.0.5");
        TestPropertySourceUtils.addPropertiesFilesToEnvironment(applicationContext, PropertiesLocation.VALUE);
    }
}

