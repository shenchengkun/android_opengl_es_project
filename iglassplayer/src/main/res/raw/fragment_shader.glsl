#extension GL_OES_EGL_image_external : require
precision highp float;
varying vec2 vTexCoord;
uniform samplerExternalOES sTexture;
void main() {
      //float oriX = vTexCoord.x;
      //float oriY =  vTexCoord.y;

      //oriX=1.0-oriX;   //flip

    gl_FragColor=texture2D(sTexture, vTexCoord);
}