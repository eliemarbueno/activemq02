package com.ebueno.interfaces.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import com.ebueno.domain.Category;
import com.ebueno.domain.Product;
import com.ebueno.interfaces.controllers.CategoryController;
import com.ebueno.interfaces.controllers.ProductController;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * REST API for handling product and category submissions to queues
 */
public class RestApi {
    private final HttpServer server;
    private final ProductController productController;
    private final CategoryController categoryController;

    public RestApi(int port) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        this.productController = new ProductController();
        this.categoryController = new CategoryController();
        
        server.createContext("/api/product", new ProductHandler());
        server.createContext("/api/category", new CategoryHandler());
        server.setExecutor(null);
    }

    public void start() {
        server.start();
        System.out.println("REST API started on port " + server.getAddress().getPort());
    }

    private class ProductHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    // Parse XML from request body
                    JAXBContext context = JAXBContext.newInstance(Product.class);
                    Unmarshaller unmarshaller = context.createUnmarshaller();
                    Product product = (Product) unmarshaller.unmarshal(exchange.getRequestBody());

                    // Send to queue
                    productController.sendProduct(product);

                    // Send response
                    String response = "Product sent to queue successfully";
                    exchange.sendResponseHeaders(200, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                } catch (Exception e) {
                    String response = "Error: " + e.getMessage();
                    exchange.sendResponseHeaders(500, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                }
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }
    }

    private class CategoryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    // Parse XML from request body
                    JAXBContext context = JAXBContext.newInstance(Category.class);
                    Unmarshaller unmarshaller = context.createUnmarshaller();
                    Category category = (Category) unmarshaller.unmarshal(exchange.getRequestBody());

                    // Send to queue
                    categoryController.sendCategory(category);

                    // Send response
                    String response = "Category sent to queue successfully";
                    exchange.sendResponseHeaders(200, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                } catch (Exception e) {
                    String response = "Error: " + e.getMessage();
                    exchange.sendResponseHeaders(500, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                }
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }
    }
}