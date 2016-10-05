package com.myretail.api.controller;

import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.myretail.api.model.ProductAttributes;
import com.myretail.api.service.ProductService;

@RestController
@RequestMapping("/")
public class ProductController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);
    
    @Autowired
    ProductService productService;

    @RequestMapping(value = { "product/{productId}/fields" }, method = { RequestMethod.GET }, produces = { MediaType.APPLICATION_JSON_VALUE } )
    public DeferredResult<ProductAttributes> getProduct( @PathVariable BigInteger productId,
            @RequestParam("ids") String ids) {
        
        LOGGER.info( "Input -> productId: {}", productId );
        LOGGER.info( "Input -> field ids: {}", ids );
        
        DeferredResult<ProductAttributes> deffered = new DeferredResult<>();
        
        productService.getProductAttributes(productId, ids)
            .subscribe(
                productAttributes -> {
                    deffered.setResult(productAttributes);
                    LOGGER.info( "Response -> product: {}", productAttributes );
                },
                error -> {
                    deffered.setErrorResult(error);
                    LOGGER.error( "Error Occurred", error );
                } );

        return deffered;
    }

}
