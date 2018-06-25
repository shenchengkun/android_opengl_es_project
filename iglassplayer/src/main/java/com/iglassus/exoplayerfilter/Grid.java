package com.iglassus.exoplayerfilter;

/**
 * Created by AdminUser on 3/14/2018.
 */

/**
 * Created by AdminUser on 3/14/2018.
 */

public class Grid {
    private int height,width;
    float[] vertices, texels, verticesNoDistortion;
    float[] leftTexels,rightTexels;
    float k1=0.0686f,b1=-k1,k2=0.165f,b2=0f;

    public float[] getLeftTexels() {
        return leftTexels;
    }

    public float[] getRightTexels() {
        return rightTexels;
    }

    int[] indices;

    public Grid(int height, int width) {
        this.height = height;
        this.width = width;
        initVertices();
        initTexels();
        initIndices();
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public float[] getVertices() {
        return vertices;
    }

    public float[] getVerticesNoDistortion() {
        return verticesNoDistortion;
    }

    public float[] getTexels() {
        return texels;
    }

    public int[] getIndices() {
        return indices;
    }

    private void initVertices() {

        vertices = new float[height*width*3];
        verticesNoDistortion = new float[height*width*3];
        int i = 0;
/**/
        //////////////////////////////normal//////////////////////////////
        float h = (float)height-1;
        float w = (float) width - 1;
        for(int row = 0; row < height; row++) {
            float roww=2*row/h-1;
            //roww=k*(roww-1)+1;
            float k=0.5f*((k2-k1)*roww+k2+k1);
            float b=0.5f*((b2-b1)*roww+b2+b1);
            for(int col = 0; col < width; col++) {
                float coll=2*col/w-1;
                //vertices[i++] = coll/(2-row/(height-1));
                verticesNoDistortion[i]=coll;vertices[i++] = coll;
                //vertices[i++] = roww-(row==0?ratioDown[col]:ratioUp[col]);
                //vertices[i++] = roww-(0.135f*(coll*coll)-0.001f);
                verticesNoDistortion[i]=roww;vertices[i++] = roww-(k*coll*coll+b);
                verticesNoDistortion[i]=0.0f;vertices[i++] = 0.0f;
            }
        }

/*
        //////////////////////////////transform//////////////////////////////
        double r     = Math.sqrt(0.5*0.5 + 1.75*1.75);
        double theta = Math.atan2(1.75, -0.5);
        double r1,theta1;
        for(int row = 0; row < height; row++) {
            r1 = r + row * r/ (height - 1) ;   //这个地方，r必须放在除号前面，否则算之前并不会自动转double导致真个结果一直为零~！！！！！！！
            for (int col = 0; col < width; col++) {
                theta1=(Math.PI-2*theta)/(width-1)*col+theta;
                vertices[i] = ((float) (r1*Math.cos(theta1))-1)/2;verticesNoDistortion[i]=vertices[i]+1;i++;
                vertices[i] = (float) (r1*Math.sin(theta1)-2.75);verticesNoDistortion[i]=vertices[i];i++;
                vertices[i] = 0.0f;verticesNoDistortion[i]=vertices[i];i++;
            }
        } */
/*
        Sheet sheet = IGLassMainActivity.book.getSheet(0);
        for (int j=1;j<=441;j++){
            Float x=Float.valueOf(sheet.getCell(1,j).getContents()),y=Float.valueOf(sheet.getCell(12,j).getContents());
            vertices[i] = x;vertices[i] = ((j-1.0f)%21)/10-1;verticesNoDistortion[i]=vertices[i];i++;
            vertices[i] = y;verticesNoDistortion[i]=vertices[i];i++;
            vertices[i] = 0.0f;verticesNoDistortion[i]=vertices[i];i++;
            //Log.i("提取",String.valueOf(y));
        }
        */
    }


    private void initTexels() {
        texels = new float[height*width*2];
        leftTexels = new float[height*width*2];
        rightTexels = new float[height*width*2];
        int i = 0;
        float h = (float)height-1;
        float w = (float) width - 1;
        for(int row = 0; row < height; row++) {
            float roww=row/h;
            for(int col = 0; col < width; col++) {
                float coll=col/w;
                texels[i] = coll;leftTexels[i]=texels[i]/2;rightTexels[i]=(texels[i]+1)/2;i++;
                texels[i] = roww;leftTexels[i]=texels[i];rightTexels[i]=texels[i];i++;
            }
        }
    }

    private void initIndices() {
        // http://stackoverflow.com/questions/5915753/generate-a-plane-with-triangle-strips
        indices = new int[getIndicesCount()];
        int i = 0;

        // Indices for drawing cube faces using triangle strips. Triangle
        // strips can be connected by duplicating indices between the
        // strips. If connecting strips have opposite vertex order, then
        // the last index of the first strip and the first index of the
        // second strip need to be duplicated. If connecting strips have
        // same vertex order, then only the last index of the first strip
        // needs to be duplicated.
        for(int row = 0; row < height - 1; row++) {
            if(row % 2 == 0) { // even rows
                for(int col = 0; col < width; col++) {
                    indices[i++] = col + row * width;
                    indices[i++] = col + (row + 1) * width;
                }
            } else {           // odd rows
                for(int col = width - 1; col > 0; col--) {
                    indices[i++] = col + (row + 1) * width;
                    indices[i++] = (col - 1) + row * width;
                }
            }
        }
        if ( (height%2!=0) && height>2) {
            indices[i++] = (height-1) * width;
        }


    }

    public int getIndicesCount() {
        return (height * width) + (width - 1) * (height - 2);
    }

}