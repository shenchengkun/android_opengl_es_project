package com.iglassus.epf;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import com.iglassus.epf.chooser.EConfigChooser;
import com.iglassus.epf.contextfactory.EContextFactory;
import com.iglassus.epf.filter.GlFilter;
import com.google.android.exoplayer2.SimpleExoPlayer;


/**
 * Created by sudamasayuki on 2017/05/16.
 */
public class EPlayerView extends GLSurfaceView implements SimpleExoPlayer.VideoListener {

    private final EPlayerRenderer renderer;
    private SimpleExoPlayer player;

    public EPlayerView(Context context) {
        this(context,null);
    }

    public EPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextFactory(new EContextFactory());
        setEGLConfigChooser(new EConfigChooser());

        renderer = new EPlayerRenderer(this);
        setRenderer(renderer);
    }

    public EPlayerView setSimpleExoPlayer(SimpleExoPlayer player, MyGrid myGrid) {
        if (this.player != null) {
            this.player.release();
            this.player = null;
        }
        this.player = player;
        this.player.addVideoListener(this);
        this.renderer.setSimpleExoPlayer(player,myGrid);
        return this;
    }

    public void setGlFilter(GlFilter glFilter) {
        renderer.setGlFilter(glFilter);
    }

    //@Override
    //protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    //    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//
    //    int width = getDefaultSize(0, widthMeasureSpec);//得到默认的大小（0，宽度测量规范）
    //    int height = getDefaultSize(0, heightMeasureSpec);//得到默认的大小（0，高度度测量规范）
    //    setMeasuredDimension(width, height); //设置测量尺寸,将高和宽放进去
    //}


    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        requestLayout();
    }

    @Override
    public void onRenderedFirstFrame() {
    }

    //@Override
    //public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    //    super.surfaceChanged(holder, format, w, h);
    //    holder.setFixedSize(3000,3000);
    //}
}
