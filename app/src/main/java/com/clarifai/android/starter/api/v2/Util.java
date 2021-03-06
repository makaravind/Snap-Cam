package com.clarifai.android.starter.api.v2;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Util {

    static List<String> strings= Arrays.asList("car", "bike", "tree", "are", "red",
            "unpulsating",
            "landlike",
            "histographer",
            "donbass",
            "quebecois",
            "cyzicus",
            "shockley",
            "acholic",
            "physiology",
            "keyserling",
            "tumuluses",
            "mora",
            "advertized",
            "kinetic",
            "discarnation",
            "inclinational",
            "biobibliographic",
            "rockne",
            "aeneolithic",
            "cere"
    );

    @NonNull
    public static boolean isValid(String conceptName){

        String ingore[] = {"Person"};
        boolean isOneWord = conceptName.split(" ").length <= 1;
        boolean isNotIgnorable = true;

        for (String item: ingore){
            if (conceptName.equalsIgnoreCase(item)){
                isNotIgnorable = false;
                break;
            }
        }

        return isOneWord && isNotIgnorable;
    }

    @NonNull
   public static String generateRandomWord() {

       int n = new Random().nextInt(strings.size());
       return strings.get(n);
   }

    public static List<String> getWords(){

        return strings;
    }

    public static int getMinToMilli(int time){
        return time * 1000 * 60;
    }

}
