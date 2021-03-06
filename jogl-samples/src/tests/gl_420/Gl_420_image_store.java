/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests.gl_420;

import com.jogamp.opengl.GL;
import static com.jogamp.opengl.GL2GL3.*;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.util.GLBuffers;
import framework.BufferUtils;
import framework.Profile;
import framework.Semantic;
import framework.Test;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.IntBuffer;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author GBarbieri
 */
public class Gl_420_image_store extends Test {

    public static void main(String[] args) {
        Gl_420_image_store gl_420_image_store = new Gl_420_image_store();
    }

    public Gl_420_image_store() {
        super("gl-420-image-store", Profile.CORE, 4, 2);
    }

    private final String SHADERS_SOURCE_SAVE = "image-store-write";
    private final String SHADERS_SOURCE_READ = "image-store-read";
    private final String SHADERS_ROOT = "src/data/gl_420";

    private class Program {

        public static final int VERT_SAVE = 0;
        public static final int FRAG_SAVE = 1;
        public static final int VERT_READ = 2;
        public static final int FRAG_READ = 3;
        public static final int MAX = 4;
    }

    private class Pipeline {

        public static final int READ = 0;
        public static final int SAVE = 1;
        public static final int MAX = 2;
    }

    private IntBuffer vertexArrayName = GLBuffers.newDirectIntBuffer(1), textureName = GLBuffers.newDirectIntBuffer(1),
            pipelineName = GLBuffers.newDirectIntBuffer(Pipeline.MAX);
    private int[] programName = new int[Program.MAX];

    @Override
    protected boolean begin(GL gl) {

        GL4 gl4 = (GL4) gl;

        boolean validated = true;

        logImplementationDependentLimit(gl4, GL_MAX_IMAGE_UNITS, "GL_MAX_IMAGE_UNITS");
        logImplementationDependentLimit(gl4, GL_MAX_VERTEX_IMAGE_UNIFORMS, "GL_MAX_VERTEX_IMAGE_UNIFORMS");
        logImplementationDependentLimit(gl4, GL_MAX_TESS_CONTROL_IMAGE_UNIFORMS, "GL_MAX_TESS_CONTROL_IMAGE_UNIFORMS");
        logImplementationDependentLimit(gl4, GL_MAX_TESS_EVALUATION_IMAGE_UNIFORMS,
                "GL_MAX_TESS_EVALUATION_IMAGE_UNIFORMS");
        logImplementationDependentLimit(gl4, GL_MAX_GEOMETRY_IMAGE_UNIFORMS, "GL_MAX_GEOMETRY_IMAGE_UNIFORMS");
        logImplementationDependentLimit(gl4, GL_MAX_FRAGMENT_IMAGE_UNIFORMS, "GL_MAX_FRAGMENT_IMAGE_UNIFORMS");
        logImplementationDependentLimit(gl4, GL_MAX_COMBINED_IMAGE_UNIFORMS, "GL_MAX_COMBINED_IMAGE_UNIFORMS");
        logImplementationDependentLimit(gl4, GL_MAX_ARRAY_TEXTURE_LAYERS, "GL_MAX_ARRAY_TEXTURE_LAYERS");
        logImplementationDependentLimit(gl4, GL_MAX_TEXTURE_IMAGE_UNITS, "GL_MAX_TEXTURE_IMAGE_UNITS");
        logImplementationDependentLimit(gl4, GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS, "GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS");
        logImplementationDependentLimit(gl4, GL_MAX_COMBINED_IMAGE_UNITS_AND_FRAGMENT_OUTPUTS,
                "GL_MAX_COMBINED_IMAGE_UNITS_AND_FRAGMENT_OUTPUTS");
        //this->logImplementationDependentLimit(GL_MAX_TEXTURE_UNITS, "GL_MAX_TEXTURE_UNITS");

        if (validated) {
            validated = initTexture(gl4);
        }
        if (validated) {
            validated = initProgram(gl4);
        }
        if (validated) {
            validated = initVertexArray(gl4);
        }

        return validated;
    }

    private boolean initProgram(GL4 gl4) {

        boolean validated = true;

        try {

            if (validated) {

                String[] vertexSourceContent = new String[]{new Scanner(new File(SHADERS_ROOT + "/"
                    + SHADERS_SOURCE_READ + ".vert")).useDelimiter("\\A").next()};
                programName[Program.VERT_READ] = gl4.glCreateShaderProgramv(GL_VERTEX_SHADER, 1, vertexSourceContent);
            }

            if (validated) {

                String[] fragmentSourceContent = new String[]{new Scanner(new File(SHADERS_ROOT + "/"
                    + SHADERS_SOURCE_READ + ".frag")).useDelimiter("\\A").next()};
                programName[Program.FRAG_READ] = gl4.glCreateShaderProgramv(GL_FRAGMENT_SHADER, 1, fragmentSourceContent);
            }

            if (validated) {

                String[] vertexSourceContent = new String[]{new Scanner(new File(SHADERS_ROOT + "/"
                    + SHADERS_SOURCE_SAVE + ".vert")).useDelimiter("\\A").next()};
                programName[Program.VERT_SAVE] = gl4.glCreateShaderProgramv(GL_VERTEX_SHADER, 1, vertexSourceContent);
            }

            if (validated) {

                String[] fragmentSourceContent = new String[]{new Scanner(new File(SHADERS_ROOT + "/"
                    + SHADERS_SOURCE_SAVE + ".frag")).useDelimiter("\\A").next()};
                programName[Program.FRAG_SAVE] = gl4.glCreateShaderProgramv(GL_FRAGMENT_SHADER, 1, fragmentSourceContent);
            }

            if (validated) {

                validated = validated && framework.Compiler.checkProgram(gl4, programName[Program.VERT_READ]);
                validated = validated && framework.Compiler.checkProgram(gl4, programName[Program.FRAG_READ]);
                validated = validated && framework.Compiler.checkProgram(gl4, programName[Program.VERT_SAVE]);
                validated = validated && framework.Compiler.checkProgram(gl4, programName[Program.FRAG_SAVE]);
            }

            if (validated) {

                gl4.glGenProgramPipelines(Pipeline.MAX, pipelineName);
                gl4.glUseProgramStages(pipelineName.get(Pipeline.READ), GL_VERTEX_SHADER_BIT,
                        programName[Program.VERT_READ]);
                gl4.glUseProgramStages(pipelineName.get(Pipeline.READ), GL_FRAGMENT_SHADER_BIT,
                        programName[Program.FRAG_READ]);
                gl4.glUseProgramStages(pipelineName.get(Pipeline.SAVE), GL_VERTEX_SHADER_BIT,
                        programName[Program.VERT_SAVE]);
                gl4.glUseProgramStages(pipelineName.get(Pipeline.SAVE), GL_FRAGMENT_SHADER_BIT,
                        programName[Program.FRAG_SAVE]);
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Gl_420_image_store.class.getName()).log(Level.SEVERE, null, ex);
        }

        return validated && checkError(gl4, "initProgram");
    }

    private boolean initTexture(GL4 gl4) {

        gl4.glGenTextures(1, textureName);
        gl4.glActiveTexture(GL_TEXTURE0);
        gl4.glBindTexture(GL_TEXTURE_2D, textureName.get(0));
        gl4.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_BASE_LEVEL, 0);
        gl4.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, 1);
        gl4.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_SWIZZLE_R, GL_RED);
        gl4.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_SWIZZLE_G, GL_GREEN);
        gl4.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_SWIZZLE_B, GL_BLUE);
        gl4.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_SWIZZLE_A, GL_ALPHA);
        gl4.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        gl4.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        gl4.glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA8, windowSize.x, windowSize.y);
        gl4.glBindTexture(GL_TEXTURE_2D, 0);

        return true;
    }

    private boolean initVertexArray(GL4 gl4) {

        gl4.glGenVertexArrays(1, vertexArrayName);
        gl4.glBindVertexArray(vertexArrayName.get(0));
        gl4.glBindVertexArray(0);

        return true;
    }

    @Override
    protected boolean render(GL gl) {

        GL4 gl4 = (GL4) gl;

        gl4.glViewportIndexedf(0, 0, 0, windowSize.x, windowSize.y);
        gl4.glDrawBuffer(GL_BACK);
        gl4.glClearBufferfv(GL_COLOR, 0, clearColor.put(0, 0).put(1, 0.5f).put(2, 1).put(3, 1));

        // Renderer to image
        {
            gl4.glDrawBuffer(GL_NONE);

            gl4.glBindProgramPipeline(pipelineName.get(Pipeline.SAVE));
            gl4.glBindImageTexture(Semantic.Image.DIFFUSE, textureName.get(0), 0, false, 0, GL_WRITE_ONLY, GL_RGBA8);
            gl4.glBindVertexArray(vertexArrayName.get(0));
            gl4.glDrawArraysInstancedBaseInstance(GL_TRIANGLES, 0, 3, 1, 0);
        }

        // Read from image
        {
            int border = 8;
            gl4.glEnable(GL_SCISSOR_TEST);
            gl4.glScissorIndexed(0, border, border, (windowSize.x - 2) * border, (windowSize.y - 2) * border);

            gl4.glDrawBuffer(GL_BACK);

            gl4.glBindProgramPipeline(pipelineName.get(Pipeline.READ));
            gl4.glBindImageTexture(Semantic.Image.DIFFUSE, textureName.get(0), 0, false, 0, GL_READ_ONLY, GL_RGBA8);
            gl4.glBindVertexArray(vertexArrayName.get(0));
            gl4.glDrawArraysInstancedBaseInstance(GL_TRIANGLES, 0, 3, 1, 0);

            gl4.glDisable(GL_SCISSOR_TEST);
        }

        return true;
    }

    @Override
    protected boolean end(GL gl) {

        GL4 gl4 = (GL4) gl;

        gl4.glDeleteTextures(1, textureName);
        gl4.glDeleteVertexArrays(1, vertexArrayName);
        gl4.glDeleteProgram(programName[Program.VERT_SAVE]);
        gl4.glDeleteProgram(programName[Program.FRAG_SAVE]);
        gl4.glDeleteProgram(programName[Program.VERT_READ]);
        gl4.glDeleteProgram(programName[Program.FRAG_READ]);
        gl4.glDeleteProgramPipelines(Pipeline.MAX, pipelineName);

        BufferUtils.destroyDirectBuffer(textureName);
        BufferUtils.destroyDirectBuffer(vertexArrayName);
        BufferUtils.destroyDirectBuffer(pipelineName);

        return true;
    }
}
