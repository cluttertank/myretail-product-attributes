package com.myretail.api;

import static com.jayway.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static com.jayway.restassured.module.mockmvc.config.AsyncConfig.withTimeout;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;
import com.myretail.api.model.ProductAttributes;
import com.myretail.api.service.ProductService;
import com.myretail.core.app.AppSpringConfiguration;
import com.myretail.core.datastore.DataAccessObject;
import com.myretail.core.ipc.IPCClient;
import com.myretail.core.ipc.IPCClientException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppSpringConfiguration.class)
@WebAppConfiguration
public class ProductControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Mock
    private DataAccessObject<Map<String, String>> productAttributesDAO;

    @Autowired
    @InjectMocks
    private ProductService productService;

    @Before
    public void setUp() throws SQLException {
        MockitoAnnotations.initMocks(this);

        RestAssuredMockMvc.webAppContextSetup(context);
        RestAssuredMockMvc.config().asyncConfig(withTimeout(10, TimeUnit.SECONDS));
        setupData();
    }

    @After
    public void tearDown() {
        RestAssuredMockMvc.reset();
    }

    @Test
    public void testGetProductDescription() {
        given()
            .accept(ContentType.JSON)
        .when().async().timeout(5000)
            .get("/product/12345678/fields?ids=description")
         .then().apply(print())
             .contentType(ContentType.JSON)
             .statusCode(200)
             .body("id", equalTo(12345678))
             .body("attributes.description", equalTo("Twin Peaks CD"));
    }

    @Test
    public void testIncorrectURI() {
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/product/1/1")
        .then().apply(print())
            .statusCode(404);
    }

    @Test
    public void testStringProductId() {
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/product/xyz/fields")
        .then().apply(print())
            .contentType(ContentType.JSON)
            .statusCode(400);
    }

    @Test
    public void testDataStoreError() {
        given()
            .accept(ContentType.JSON)
        .when().async().timeout(5000)
            .get("/product/321/fields?ids=description")
         .then().apply(print())
             .contentType(ContentType.JSON)
             .statusCode(500)
             .body("message", equalTo("Error while fetching product attributes from DataStore"));
    }

    @Test
    public void testResourceNotFoundError() {
        given()
            .accept(ContentType.JSON)
        .when().async().timeout(5000)
            .get("/product/654/fields?ids=description")
         .then().apply(print())
             .contentType(ContentType.JSON)
             .statusCode(404)
             .body("message", equalTo("No attributes found in datastore"));
    }

    public void setupData() {

        Map<String, String> attributeMap = Collections.unmodifiableMap(new HashMap<String, String>() {{
            put("description", "Twin Peaks CD");
        }} );

        Mockito
            .doReturn(attributeMap)
            .when(productAttributesDAO).getItemById(Mockito.contains("12345678"));

        Mockito
            .doThrow(new NullPointerException("Data interrupted"))
            .when(productAttributesDAO).getItemById("321");

    }

}