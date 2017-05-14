package com.android.airhockey.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by pengfu on 14/05/2017.
 */

public class TextResourceReader {

    public static String readTextFileFromResource(Context context ,int resourceID){
        StringBuilder body = new StringBuilder() ;

        InputStream inputStream = context.getResources().openRawResource(resourceID) ;
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream) ;
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader) ;
        String nextLine ;
        try {
            while((nextLine = bufferedReader.readLine()) != null){
                body.append(nextLine) ;
                body.append("\n") ;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not open resource : "+ resourceID ,e) ;
        }

        return body.toString() ;
    }
}
