#extension GL_OES_EGL_image_external : require
precision highp float;
varying vec2 vTexCoord;
uniform samplerExternalOES sTexture;
void main() {
      //float oriX = vTexCoord.x;
      //float oriY =  vTexCoord.y;

      //oriX=1.0-oriX;   //flip

    //mediump vec4 color = texture2D(sTexture, vTexCoord);
    //gl_FragColor = vec4(0.0-color.r,1.2-color.g,0.6-color.b, color.w);
    gl_FragColor=texture2D(sTexture, vTexCoord);
}