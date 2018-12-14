package com.stevesouza.camel.experiment3.utils;

public class ThrowException {

    public void throwException() throws Exception {
        throw new CustomerValidation("Simulating a customer validation exception to show how camel maps exceptions to http status codes");

    }

    public class CustomerValidation extends Exception {
        public CustomerValidation(String message) {
            super(message);
        }
    }
}
