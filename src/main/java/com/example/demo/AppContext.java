package com.example.demo;

import org.springframework.context.ConfigurableApplicationContext;

public class AppContext {
    private static ConfigurableApplicationContext context;

    public static void setContext(ConfigurableApplicationContext ctx) {
        context = ctx;
    }

    public static ConfigurableApplicationContext getContext() {
        return context;
    }
}