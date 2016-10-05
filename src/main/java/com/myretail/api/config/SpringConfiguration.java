package com.myretail.api.config;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.myretail.api.model.ProductAttributes;
import com.myretail.core.datastore.DataAccessObject;
import com.myretail.core.datastore.DataStore;
import com.myretail.core.datastore.MapDataStore;
import com.myretail.core.datastore.MappedObjectDAO;

@Configuration
public class SpringConfiguration {

    @Bean
    public DataStore dataStore() {
        return new MapDataStore("data.json");
    }
    
    @Bean
    @Autowired
    public DataAccessObject productAttributesDAO(DataStore dataStore) {
        return new MappedObjectDAO("product-attributes", dataStore);
    }
    
}
