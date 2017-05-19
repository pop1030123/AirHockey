package com.android.airhockey.objects;

import com.android.airhockey.data.VertexArray;
import com.android.airhockey.programs.ColorShaderProgram;

import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.glDrawArrays;
import static com.android.airhockey.Constants.BYTES_PER_FLOAT;

/**
 * Created by pengfu on 19/05/2017.
 */

public class Mallet {
    private static final int POSITION_COMPONENT_COUNT = 2 ;
    private static final int COLOR_COMPONENT_COUNT = 3 ;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT ;


    private static final float[] VERTEX_DATA = {

            // X ,Y ,R ,G ,B

            // mallets
            0f ,-0.4f ,0f,0f,1f,
            0f , 0.4f ,1f,0f,0f
    } ;

    private final VertexArray vertexArray ;

    public Mallet(){
        vertexArray = new VertexArray(VERTEX_DATA) ;
    }
    public void bindData(ColorShaderProgram colorShaderProgram){
        vertexArray.setVertexAttribPointer(
                0 ,
                colorShaderProgram.getPositionAttributeLocation() ,
                POSITION_COMPONENT_COUNT ,
                STRIDE
        );
        vertexArray.setVertexAttribPointer(
                0 ,
                colorShaderProgram.getColorAttributeLocation() ,
                COLOR_COMPONENT_COUNT ,
                STRIDE
        );
    }
    public void draw(){
        glDrawArrays(GL_POINTS ,0 ,2) ;
    }
}
