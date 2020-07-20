package com.example.truedemo.controllers;


import com.example.truedemo.models.Product;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.Response;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ProductServiceController {

    private static Map<String, Product> productMap = new HashMap<>();
    static {
        Product product1 = new Product("1", "honey");
        productMap.put(product1.getId(), product1);

        Product product2 = new Product("2", "almond");
        productMap.put(product2.getId(), product2);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/products/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") String id){
        productMap.remove(id);
        return new ResponseEntity<>("Product deleted successfully.", HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/products/{id}")
    public ResponseEntity<Object> getProduct(@PathVariable("id") String id ){
        Product product = productMap.get(id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/products")
    public ResponseEntity<Object> getProducts(){
        return new ResponseEntity<>(productMap.values(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/products")
    public ResponseEntity<Object> createProduct(@RequestBody Product product){
        productMap.put(product.getId(), product);
        return new ResponseEntity<>("Product successfully created.", HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/products")
    public ResponseEntity<Object> updateProduct(@PathVariable String id, @RequestBody Product product){
        productMap.remove(id);
        product.setId(id);
        productMap.put(id, product);
        return new ResponseEntity<>("Product updated successfully.", HttpStatus.OK);
    }


}
