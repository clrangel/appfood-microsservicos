package br.com.appfood.ms_pagamentos.model;

import java.util.Random;

public class GeradorAutorizacao {
    private static final Random random = new Random();

    public static boolean getRandomBoolean() {
        return random.nextBoolean();
    }
}
