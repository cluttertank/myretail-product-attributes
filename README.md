# myretail-product-attributes

It's a RESTful service that retrieves product attribute by ID.
Request: GET /product/{id}/fields?ids= (where {id} the product id and is a number, and ids is comma separated list of attribute ids)
Example response:
{
  "id": 12345678,
  "attributes": {
    "description": "Z-Boy 2000"
  }
}

It reads the pricing information from an in-memory map based data store and combines it all together in a single response.

# How To
Requirements:
* Java 8
* Tomcat 8 (or 9)

To Run:
* deploy ROOT.war in tomcat and start

# Test Reports
* test-reports.zip
    - Contains and jacoco code-coverage report and unit test reports
* test-results.zip
    - Unit test results
