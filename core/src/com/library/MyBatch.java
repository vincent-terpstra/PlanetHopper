package com.library;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;

/**
 * Built from the SimpleBatch class
 *  Remove many unnecessary components such as
 *      Texture and Shader Program Dependency
 *      Matrix4 projection
 *      Unnecessarily convoluted and limited draw function implementations
 */
public class MyBatch {
    static final int VERTEX_SIZE = 2 + 1 + 2;
    static final int SPRITE_SIZE = 4 * VERTEX_SIZE;
    /** @deprecated Do not use, this field is for testing only and is likely to be removed. Sets the {@link Mesh.VertexDataType} to be
     *             used when gles 3 is not available, defaults to {@link Mesh.VertexDataType#VertexArray}. */
    @Deprecated public static Mesh.VertexDataType defaultVertexDataType = Mesh.VertexDataType.VertexArray;

    private Mesh mesh;

    final float[] vertices;
    int idx = 0;


    boolean drawing = false;

    private boolean blendingDisabled = false;
    private int blendSrcFunc = GL20.GL_SRC_ALPHA;
    private int blendDstFunc = GL20.GL_ONE_MINUS_SRC_ALPHA;
    private int blendSrcFuncAlpha = GL20.GL_SRC_ALPHA;
    private int blendDstFuncAlpha = GL20.GL_ONE_MINUS_SRC_ALPHA;

    private final ShaderProgram shader;
    private ShaderProgram customShader = null;
    private boolean ownsShader;

    private final Color color = new Color(1, 1, 1, 1);
    float colorPacked = Color.WHITE_FLOAT_BITS;

    /** Number of render calls since the last {@link #begin()}. **/
    public int renderCalls = 0;

    /** Number of rendering calls, ever. Will not be reset unless set manually. **/
    public int totalRenderCalls = 0;

    /** The maximum number of sprites rendered in one batch so far. **/
    public int maxSpritesInBatch = 0;

    /** Constructs a new SpriteBatch with a size of 1000, one buffer, and the default shader.
     * @see SpriteBatch#SpriteBatch(int, ShaderProgram) */
    public MyBatch () {
        this(100, null);
    }

    /** Constructs a SpriteBatch with one buffer and the default shader.
     * @see SpriteBatch#SpriteBatch(int, ShaderProgram) */
    public MyBatch (int size) {
        this(size, null);
    }

    /** Constructs a new SpriteBatch. Sets the projection matrix to an orthographic projection with y-axis point upwards, x-axis
     * point to the right and the origin being in the bottom left corner of the screen. The projection will be pixel perfect with
     * respect to the current screen resolution.
     * <p>
     * The defaultShader specifies the shader to use. Note that the names for uniforms for this default shader are different than
     * the ones expect for shaders set with {@link #setShader(ShaderProgram)}. See {@link #createDefaultShader()}.
     * @param size The max number of sprites in a single batch. Max of 8191.
     * @param defaultShader The default shader to use. This is not owned by the SpriteBatch and must be disposed separately. */
    public MyBatch (int size, ShaderProgram defaultShader) {
        // 32767 is max vertex index, so 32767 / 4 vertices per sprite = 8191 sprites max.
        if (size > 8191) throw new IllegalArgumentException("Can't have more than 8191 sprites per batch: " + size);

        Mesh.VertexDataType vertexDataType = (Gdx.gl30 != null) ? Mesh.VertexDataType.VertexBufferObjectWithVAO : defaultVertexDataType;

        mesh = new Mesh(vertexDataType, false, size * 4, size * 6,
                new VertexAttribute(VertexAttributes.Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE),
                new VertexAttribute(VertexAttributes.Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE),
                new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));

        vertices = new float[size * SPRITE_SIZE];

        int len = size * 6;
        short[] indices = new short[len];
        short j = 0;
        for (int i = 0; i < len; i += 6, j += 4) {
            indices[i] = j;
            indices[i + 1] = (short)(j + 1);
            indices[i + 2] = (short)(j + 2);
            indices[i + 3] = (short)(j + 2);
            indices[i + 4] = (short)(j + 3);
            indices[i + 5] = j;
        }
        mesh.setIndices(indices);

        if (defaultShader == null) {
            shader = createDefaultShader();
            ownsShader = true;
        } else
            shader = defaultShader;

        setSize(1,1);
    }

    /** Returns a new instance of the default shader used by SpriteBatch for GL2 when no shader is specified. */
    static public ShaderProgram createDefaultShader () {
        String vertexShader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
                + "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
                + "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
                + "uniform mat4 u_projTrans;\n" //
                + "varying vec4 v_color;\n" //
                + "varying vec2 v_texCoords;\n" //
                + "\n" //
                + "void main()\n" //
                + "{\n" //
                + "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
                + "   v_color.a = v_color.a * (255.0/254.0);\n" //
                + "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
                + "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
                + "}\n";
        String fragmentShader = "#ifdef GL_ES\n" //
                + "#define LOWP lowp\n" //
                + "precision mediump float;\n" //
                + "#else\n" //
                + "#define LOWP \n" //
                + "#endif\n" //
                + "varying LOWP vec4 v_color;\n" //
                + "varying vec2 v_texCoords;\n" //
                + "uniform sampler2D u_texture;\n" //
                + "void main()\n"//
                + "{\n" //
                + "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n" //
                + "}";

        ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
        if (!shader.isCompiled()) throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
        return shader;
    }

    public void begin (Texture texture) {
        if (drawing) throw new IllegalStateException("SpriteBatch.end must be called before begin.");
        renderCalls = 0;
        texture.bind();
        Gdx.gl.glDepthMask(false);
        if (customShader != null)
            customShader.begin();
        else
            shader.begin();
        setupMatrices();

        drawing = true;
    }

    public void end () {
        if (!drawing) throw new IllegalStateException("SpriteBatch.begin must be called before end.");
        if (idx > 0) flush();
        drawing = false;

        GL20 gl = Gdx.gl;
        gl.glDepthMask(true);
        if (isBlendingEnabled()) gl.glDisable(GL20.GL_BLEND);

        if (customShader != null)
            customShader.end();
        else
            shader.end();
    }


    public void setPackedColor (float packedColor) {
        Color.abgr8888ToColor(color, packedColor);
        this.colorPacked = packedColor;
    }

    public float getPackedColor () {
        return colorPacked;
    }

    public void draw (float[] region, float x, float y, float width, float height){
        draw(region, x, y, width, height, 1, 0);
    }

    public void draw (float[] region, float x, float y, float width, float height, float cos, float sin) {
        if (!drawing) throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
        width /= 2;
        height /= 2;

        float[] vertices = this.vertices;

        if (idx == vertices.length) //
            flush();

        final float u = region[0];
        final float v = region[3];
        final float u2 = region[2];
        final float v2 = region[1];

        final float[] vert = {
              -width, -height, u, v,
              -width, height,  u, v2,
               width, height, u2, v2,
               width,-height, u2, v
        };
        float color = this.colorPacked;
        int idx = this.idx;
        for(int vIdx = 0; vIdx < vert.length; ){
            float w = vert[vIdx++];
            float h = vert[vIdx++];
            vertices[idx++] = x + w * cos + h * sin;
            vertices[idx++] = y + h * cos - w * sin;
            vertices[idx++] = color;
            vertices[idx++] = vert[vIdx++];
            vertices[idx++] = vert[vIdx++];
        }

        this.idx = idx;
    }

    public void flush () {
        if (idx == 0) return;

        renderCalls++;
        totalRenderCalls++;
        int spritesInBatch = idx / 20;
        if (spritesInBatch > maxSpritesInBatch) maxSpritesInBatch = spritesInBatch;
        int count = spritesInBatch * 6;

        Mesh mesh = this.mesh;
        mesh.setVertices(vertices, 0, idx);
        mesh.getIndicesBuffer().position(0);
        mesh.getIndicesBuffer().limit(count);

        if (blendingDisabled) {
            Gdx.gl.glDisable(GL20.GL_BLEND);
        } else {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            if (blendSrcFunc != -1) Gdx.gl.glBlendFuncSeparate(blendSrcFunc, blendDstFunc, blendSrcFuncAlpha, blendDstFuncAlpha);
        }

        mesh.render(customShader != null ? customShader : shader, GL20.GL_TRIANGLES, 0, count);

        idx = 0;
    }

    public void disableBlending () {
        if (blendingDisabled) return;
        flush();
        blendingDisabled = true;
    }

    public void enableBlending () {
        if (!blendingDisabled) return;
        flush();
        blendingDisabled = false;
    }

    public void setBlendFunction (int srcFunc, int dstFunc) {
        setBlendFunctionSeparate(srcFunc, dstFunc, srcFunc, dstFunc);
    }

    public void setBlendFunctionSeparate (int srcFuncColor, int dstFuncColor, int srcFuncAlpha, int dstFuncAlpha) {
        if (blendSrcFunc == srcFuncColor && blendDstFunc == dstFuncColor && blendSrcFuncAlpha == srcFuncAlpha
                && blendDstFuncAlpha == dstFuncAlpha) return;
        flush();
        blendSrcFunc = srcFuncColor;
        blendDstFunc = dstFuncColor;
        blendSrcFuncAlpha = srcFuncAlpha;
        blendDstFuncAlpha = dstFuncAlpha;
    }

    public int getBlendSrcFunc () {
        return blendSrcFunc;
    }

    public int getBlendDstFunc () {
        return blendDstFunc;
    }

    public int getBlendSrcFuncAlpha () {
        return blendSrcFuncAlpha;
    }

    public int getBlendDstFuncAlpha () {
        return blendDstFuncAlpha;
    }

    public void dispose () {
        mesh.dispose();
        if (ownsShader && shader != null) shader.dispose();
    }

    public void setSize(float width, float height){
        matrix = new float[]{
                2 / width, 0,0,0,
                0, 2/height,0,0,
                0,0, -2.0f, 0, 0, 0, 0, 1
        };
    }

    float[] matrix;
    private void setupMatrices () {
        ShaderProgram program = customShader != null ? customShader : shader;

        int loc = program.getUniformLocation("u_projTrans");
        Gdx.gl20.glUniformMatrix4fv(loc, 1, false, matrix, 0);
        program.setUniformi("u_texture", 0);

    }


    public void setShader (ShaderProgram shader) {
        if (drawing) {
            flush();
            if (customShader != null)
                customShader.end();
            else
                this.shader.end();
        }
        customShader = shader;
        if (drawing) {
            if (customShader != null)
                customShader.begin();
            else
                this.shader.begin();
            setupMatrices();
        }
    }

    public ShaderProgram getShader () {
        if (customShader == null) {
            return shader;
        }
        return customShader;
    }

    public boolean isBlendingEnabled () {
        return !blendingDisabled;
    }

    public boolean isDrawing () {
        return drawing;
    }
}
