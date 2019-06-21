package com.example.placa.Help;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.text.FirebaseVisionText;

public class TexyGraphy extends  GraphyOver.Graphic {
    private  static  final int TEXT_COLOR= Color.BLUE;
    private  static final  float TEXT_SIZE=54.0f;
    private  static  final  float STOKE_WIDTH=4.0f;
    private final Paint rectPaint,textPaint;
    private  final FirebaseVisionText.Element text;

    public TexyGraphy(GraphyOver overlay,FirebaseVisionText.Element text) {
        super(overlay);

        this.text=text;
        rectPaint=new Paint();
        rectPaint.setColor(TEXT_COLOR);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(STOKE_WIDTH);

        textPaint=new Paint();
        textPaint.setColor(TEXT_COLOR);
        textPaint.setTextSize(TEXT_SIZE);
       postInvalidate();
    }

    @Override
    public void draw(Canvas canvas) {
     if (text==null){
         throw new  IllegalStateException("Attenting do draw a null text");
     }
        RectF rectF=new RectF(text.getBoundingBox());
        rectF.left=translateX(rectF.left);
        rectF.top=translateY(rectF.top);
        rectF.right=translateX(rectF.right);
        rectF.left=translateY(rectF.bottom);

        canvas.drawRect(rectF,rectPaint);
        canvas.drawText(text.getText(),rectF.left,rectF.bottom,textPaint);
    }
}
