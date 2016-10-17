package com.totris.zebra.utils;

/**
 * Created by thomaslecoeur on 16/10/2016.
 */

public class StringUtils {
    public static String leetify(String str) { // TODO: limiter un peu la puissance du leet parce que là c'est chaud (https://github.com/AnsgarKlein/LeetConverter/blob/master/src/SwingWindow.java)
        str = str.toLowerCase();

        //hacker -> h4xX0r
        str = str.replace("hacker", "h4xX0r");

        //hacked -> h4cKed
        str = str.replace("hacked", "h4cKed");

        //noob -> n00b / b00n
        if (Math.random()<0.5) {
            str = str.replace(" noob ", " n00b ");
        } else {
            str = str.replace(" noob ", " b00n ");
        }

        //leet -> 1337
        str = str.replace(" leet ", " 1337 ");

        //leetspeak / leet speak -> 1337 5P34K
        str = str.replace(" leetspeak ", " 1337 5P34K ");
        str = str.replace(" leet speak ", " 1337 5P34K ");

        //skills -> skills
        str = str.replace(" skills ", " 5k!11Zz ");
        str = str.replace(" skilled ", " 5k!113D ");

        //suck -> 5uxX
        str = str.replace(" suck ", " 5uxX ");
        str = str.replace(" sucks ", " 5uxXz ");

        //-s --> -z
        str = str.replace("s ", "z ");

        //omg -> 0m9
        str = str.replace(" omg ", " 0m9 ");

        //at -> @
        str = str.replace(" at ", " @ ");

        //dude -> d00d
        str = str.replace("dude", "d00d");

        //cool -> kewl
        str = str.replace("cool", "kewl");

        //porn -> pr0n
        str = str.replace("porn", "pr0n");

        //the -> teh
        str = str.replace(" the ", " teh ");

        //you -> u
        str = str.replace(" you ", " u ");

        //are -> r
        str = str.replace(" are ", " r ");

        //why -> y
        str = str.replace(" why ", " y ");

        //nice -> n1
        str = str.replace(" nice ", " n1 ");

        //winner -> winnar
        str = str.replace(" winner ", " winnar ");

        if (Math.random()<0.5) {
            str = str.replace("a", "/-\\");
        } else {
            str = str.replace("a", "4");
        }

        if (Math.random()<0.5) {
            str = str.replace("b", "|3");
        } else {
            str = str.replace("b", "8");
        }

        if (Math.random()<0.5) {
            str = str.replace("e", "€");
        } else {
            str = str.replace("e", "3");
        }

        str = str.replace("g", "9");

        if (Math.random()<0.5) {
            str = str.replace("i", "!");
        } else {
            str = str.replace("i", "1");
        }

        str = str.replace("o", "0");

        str = str.replace("s", "5");

        str = str.replace("t", "7");

        str = str.replace("z", "2");

        str = str.replace("c", "(");

        str = str.replace("d", "|)");

        if (Math.random()<0.5) {
            str = str.replace("f", "|=");
        } else {
            str = str.replace("f", "|\"");
        }

        if (Math.random()<0.5) {
            str = str.replace("h", "|-|");
        } else {
            str = str.replace("h", "#");
        }

        str = str.replace("j", "_|");

        if (Math.random()<0.5) {
            str = str.replace("k", "|<");
        } else {
            str = str.replace("k", "|(");
        }

        str = str.replace("l", "|_");

        str = str.replace("m", "/\\/\\");

        if (Math.random()<0.5) {
            str = str.replace("n", "/\\/");
        } else {
            str = str.replace("n", "|\\|");
        }

        str = str.replace("p", "|°");

        str = str.replace("q", "0,");

        str = str.replace("r", "|2");

        str = str.replace("u", "|_|");

        str = str.replace("v", "\\/");

        if (Math.random()<0.5) {
            str = str.replace("w", "\\/\\/");
        } else {
            str = str.replace("w", "VV");
        }

        str = str.replace("x", "><");

        str = str.replace("y", "°/");

        return str;
    }
}
