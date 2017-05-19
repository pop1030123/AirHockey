package com.android.airhockey.programs;

import android.content.Context;

import com.android.airhockey.R;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by pengfu on 19/05/2017.
 */

public class ColorShaderProgram extends ShaderProgram {



    // uniform locations
    private final int uMatrixLocation ;

    // attribute locations
    private final int aPositionLocation ;
    private final int aColorLocation ;

    public ColorShaderProgram(Context context){
        super(context , R.raw.simple_vertex_shader ,R.raw.simple_fragment_shader);
        uMatrixLocation = glGetUniformLocation(program ,U_MATRIX) ;
        aPositionLocation = glGetAttribLocation(program ,A_POSITION) ;

        aColorLocation = glGetAttribLocation(program ,A_COLOR) ;

    }

    public void setUniforms(float[] matrix){
        glUniformMatrix4fv(uMatrixLocation ,1 ,false ,matrix ,0);

    }

    public int getPositionAttributeLocation(){
        return aPositionLocation ;
    }

    public int getColorAttributeLocation(){
        return aColorLocation ;
    }
}
