package com.android.airhockey;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.android.airhockey.objects.Mallet;
import com.android.airhockey.objects.Puck;
import com.android.airhockey.objects.Table;
import com.android.airhockey.programs.ColorShaderProgram;
import com.android.airhockey.programs.TextureShaderProgram;
import com.android.airhockey.util.Geometry;
import com.android.airhockey.util.Geometry.Point;
import com.android.airhockey.util.Geometry.Vector;
import com.android.airhockey.util.L;
import com.android.airhockey.util.MatrixHelper;
import com.android.airhockey.util.ShaderHelper;
import com.android.airhockey.util.TextResourceReader;
import com.android.airhockey.util.TextureHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniform4fv;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.invertM;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.orthoM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.setLookAtM;
import static android.opengl.Matrix.translateM;

/**
 * Created by pengfu on 13/05/2017.
 */

public class AirHockeyRenderer implements GLSurfaceView.Renderer {


    private final float [] viewMatrix                       = new float[16] ;
    private final float [] viewProjectionMatrix             = new float[16] ;
    private final float [] modelViewProjectionMatrix        = new float[16] ;

    private final float[] invertedViewProjectionMatrix      = new float[16] ;

    private final float [] projectionMatrix                 = new float[16] ;
    private final float [] modelMatrix                      = new float[16] ;

    private final float leftBound = -0.5f ;
    private final float rightBound = 0.5f ;
    private final float farBound = -0.8f ;
    private final float nearBound = 0.8f ;

    private boolean malletPressed = false ;
    private Point blueMalletPosition ;

    private Point previousBlueMalletPosition ;

    private Point puckPosition ;
    private Vector puckVector ;

    private Context mContext ;

    private Table table ;
    private Mallet mallet ;
    private Puck puck ;

    private TextureShaderProgram textureShaderProgram ;
    private ColorShaderProgram colorShaderProgram ;

    private int texture ;

    public AirHockeyRenderer(Context context){
        mContext = context ;
    }
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0f, 0f, 0f, 0f);

        table = new Table() ;
        mallet = new Mallet(0.08f ,0.15f ,64) ;
        puck = new Puck(0.06f ,0.02f ,32) ;

        puckPosition = new Point(0f ,puck.height / 2f ,0f) ;
        puckVector = new Vector(0f ,0f ,0f) ;

        blueMalletPosition = new Point(0f , mallet.height / 2f ,0.4f) ;

        textureShaderProgram = new TextureShaderProgram(mContext) ;
        colorShaderProgram = new ColorShaderProgram(mContext) ;

        texture = TextureHelper.loadTexture(mContext ,R.drawable.air_hockey_surface) ;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0,0,width ,height);

        MatrixHelper.perspectiveM(projectionMatrix ,45 ,width/(height*1f) ,1f ,10f);
//        setIdentityM(modelMatrix ,0);
//        translateM(modelMatrix ,0 ,0 ,0 ,-2.5f);
//        rotateM(modelMatrix ,0 ,-50f ,1 ,0 ,0);
//
//        final float[] temp = new float[16] ;
//        multiplyMM(temp ,0 ,projectionMatrix ,0 ,modelMatrix ,0);
//        System.arraycopy(temp ,0 ,projectionMatrix ,0 ,temp.length);
        setLookAtM(viewMatrix ,0 ,
                0f ,1.2f , 2.2f , // eye position
                0f ,0f   , 0f ,   // center position
                0f ,1f   , 0f);   // up position
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);

        // 让冰球移动到新的位置
        puckPosition = puckPosition.translate(puckVector) ;

        // 碰壁检测
        if(puckPosition.x < leftBound + puck.radius
                ||
            puckPosition.x > rightBound - puck.radius){

            // 碰壁反转
            puckVector = new Vector(-puckVector.x ,puckVector.y , -puckVector.z) ;
            // 碰壁后增加阻尼，降低移动速度
            puckVector = puckVector.scale(0.9f) ;
        }
        if(puckPosition.z < farBound + puck.radius
                ||
                puckPosition.z > nearBound - puck.radius){
            // 碰壁反转
            puckVector = new Vector(puckVector.x ,puckVector.y ,-puckVector.z) ;
            // 碰壁后增加阻尼，降低移动速度
            puckVector = puckVector.scale(0.9f) ;
        }

        puckVector = puckVector.scale(0.99f) ;

        // clamp the puck position
        puckPosition = new Point(
                clamp(puckPosition.x  ,leftBound + puck.radius , rightBound - puck.radius) ,
                puckPosition.y ,
                clamp(puckPosition.z ,farBound + puck.radius ,nearBound - puck.radius)
        ) ;

        multiplyMM(viewProjectionMatrix ,0 ,projectionMatrix ,0 ,viewMatrix ,0);

        invertM(invertedViewProjectionMatrix ,0 ,viewProjectionMatrix ,0) ;

        // draw table
        positionTableInScene();
        textureShaderProgram.useProgram();
        textureShaderProgram.setUniform(modelViewProjectionMatrix ,texture);
        table.bindData(textureShaderProgram);
        table.draw();

        // draw mallet
        positionObjectInScene(0f ,mallet.height / 2f , -0.4f);
        colorShaderProgram.useProgram();
        colorShaderProgram.setUniforms(modelViewProjectionMatrix ,1f ,0f ,0f);
        mallet.bindData(colorShaderProgram);
        mallet.draw();

        // draw another mallet
        positionObjectInScene(blueMalletPosition.x ,blueMalletPosition.y ,blueMalletPosition.z) ;
        colorShaderProgram.setUniforms(modelViewProjectionMatrix ,0f ,0f ,1f);
        mallet.draw();

        // draw the puck
        positionObjectInScene(puckPosition.x ,puckPosition.y ,puckPosition.z) ;
        colorShaderProgram.setUniforms(modelViewProjectionMatrix ,0.8f ,0.8f ,1f);
        puck.bindData(colorShaderProgram);
        puck.draw();
    }

    private void positionTableInScene(){
        setIdentityM(modelMatrix ,0);
        rotateM(modelMatrix ,0 ,-90f ,1f ,0f ,0f);
        multiplyMM(modelViewProjectionMatrix ,0 ,viewProjectionMatrix ,0 ,modelMatrix ,0);
    }

    private void positionObjectInScene(float x ,float y ,float z){
        setIdentityM(modelMatrix ,0);
        translateM(modelMatrix ,0 ,x , y ,z);
        multiplyMM(modelViewProjectionMatrix ,0 ,viewProjectionMatrix ,0 ,modelMatrix ,0);

    }

    public void handleTouchPress(float normalizedX, float normalizedY) {
        Geometry.Ray ray = convertNormalized2DPointToRay(normalizedX , normalizedY) ;

        Geometry.Sphere malletBoundingSphere = new Geometry.Sphere(
                new Point(
                        blueMalletPosition.x ,
                        blueMalletPosition.y ,
                        blueMalletPosition.z) ,
                mallet.height / 2f) ;

        malletPressed = Geometry.intersects(malletBoundingSphere ,ray) ;
        L.d("handleTouchPress:"+malletPressed);
    }

    private Geometry.Ray convertNormalized2DPointToRay(float normalizedX, float normalizedY) {

        final float[] nearPointNdc = {normalizedX ,normalizedY ,-1 ,1} ;
        final float[] farPointNdc = {normalizedX ,normalizedY ,1 ,1} ;

        final float[] nearPointWorld = new float[4] ;
        final float[] farPointWorld = new float[4] ;

        multiplyMV(nearPointWorld ,0 ,invertedViewProjectionMatrix ,0 ,nearPointNdc ,0);
        multiplyMV(farPointWorld ,0 ,invertedViewProjectionMatrix ,0 ,farPointNdc ,0);

        divideByW(nearPointWorld) ;
        divideByW(farPointWorld) ;

        Point nearPointRay = new Point(nearPointWorld[0] ,nearPointWorld[1] ,nearPointWorld[2]) ;
        Point farPointRay  = new Point(farPointWorld[0] ,farPointWorld[1] ,farPointWorld[2]) ;

        return new Geometry.Ray(nearPointRay ,Geometry.vectorBetween(nearPointRay ,farPointRay)) ;
    }

    private void divideByW(float[] vector) {
        vector[0] /= vector[3] ;
        vector[1] /= vector[3] ;
        vector[2] /= vector[3] ;
    }

    public void handleTouchDrag(float normalizedX, float normalizedY) {
        if(malletPressed){
            Geometry.Ray ray = convertNormalized2DPointToRay(normalizedX ,normalizedY) ;

            Geometry.Plane plane = new Geometry.Plane(new Point(0 ,0 ,0) ,new Vector(0 ,1 ,0)) ;

            Point touchedPoint = Geometry.intersectionPoint(ray ,plane) ;

            previousBlueMalletPosition = blueMalletPosition ;

            blueMalletPosition = new Point(
                    clamp(touchedPoint.x ,
                            leftBound + mallet.radius ,
                            rightBound - mallet.radius) ,
                    mallet.height / 2f ,
                    clamp(touchedPoint.z ,
                            0f + mallet.radius ,
                            nearBound - mallet.radius)
            ) ;
            float distance = Geometry.vectorBetween(blueMalletPosition ,puckPosition).length() ;

            if(distance < (puck.radius + mallet.radius)){
                puckVector = Geometry.vectorBetween(previousBlueMalletPosition , blueMalletPosition) ;
            }
        }
    }

    private float clamp(float value ,float min ,float max){
        return Math.min(max ,Math.max(value ,min)) ;
    }
}
