package com.myretail.api.service;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.myretail.api.model.ProductAttributes;
import com.myretail.core.datastore.DataAccessObject;
import com.myretail.core.exception.InternalServerErrorException;
import com.myretail.core.exception.ResourceNotFoundException;
import com.myretail.core.resilience.CircuitCommand;

import rx.Observable;

@Service("productService")
public class ProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private DataAccessObject productAttributesDAO;
    
    public Observable<ProductAttributes> getProductAttributes(final BigInteger productId, final String ids) {
        return new CircuitCommand<Map<String, String>>("GetProductAttributes",
                () -> productAttributesDAO.getItemById(productId.toString())).observe()
                .onErrorReturn( error -> { throw new InternalServerErrorException("Error while fetching product attributes from DataStore", error); } )
                .map( allAttributeMap -> {
                    LOGGER.info( "allAttributeMap: ", allAttributeMap);
                    
                    if( allAttributeMap == null || allAttributeMap.isEmpty() ) {
                        throw new ResourceNotFoundException("No attributes found in datastore"); 
                    }
                    List<String> requestedAttributeIds = Arrays.asList(ids.split(","));
                    Map<String, String> requestedAttributeMaps = allAttributeMap.entrySet().stream()
                        .filter( entry -> requestedAttributeIds.contains(entry.getKey()) )
                        .collect( Collectors.toMap( entry -> entry.getKey(), entry -> entry.getValue()) );
                    LOGGER.info( "requestedAttributeMaps: ", requestedAttributeMaps);
                    
                    ProductAttributes productAttributes = new ProductAttributes();
                    productAttributes.setId(productId);
                    productAttributes.setAttributes(requestedAttributeMaps);
                    return productAttributes;
                } );
    }
}
