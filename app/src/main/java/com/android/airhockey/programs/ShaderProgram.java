package com.android.airhockey.programs;

import android.content.Context;

import com.android.airhockey.util.ShaderHelper;
import com.android.airhockey.util.TextResourceReader;

import static android.opengl.GLES20.glUseProgram;

/**
 * Created by pengfu on 19/05/2017.
 */

public class ShaderProgram {

    protected static final String U_COLOR = "u_Color" ;

    // uniform constants

    protected static final String U_MATRIX = "u_Matrix" ;
    protected static final String U_TEXTURE_UNIT = "u_TextureUnit" ;


    // attribute constants
    protected static final String A_POSITION = "a_Position"  ;
    protected static final String A_COLOR = "a_Color" ;
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates" ;

    // shader program
    protected final int program ;
    protected ShaderProgram(Context context ,int vertexShaderResourceId ,int fragmentShaderResourceId){
        program = ShaderHelper.buildProgram(
                 TextResourceReader.readTextFileFromResource(context ,vertexShaderResourceId)
                ,TextResourceReader.readTextFileFromResource(context ,fragmentShaderResourceId)) ;

    }
    public void useProgram(){
        glUseProgram(program) ;
    }
}
