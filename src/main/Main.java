package main;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluPerspective;

public class Main {

	// display constants
	private static final int    DISPLAY_WIDTH     = 1280;
	private static final int    DISPLAY_HEIGHT    = 720;
	private static final String DISPLAY_TITLE     = "Advanced OpenGL Rendering";
	private static final int    TARGET_FRAME_RATE = 60;

	// perspective constants
	private static final float FIELD_OF_VIEW = 70.0f;
	private static final float NEAR_PLANE    = 0.001f;
	private static final float FAR_PLANE     = 100.0f;

	private enum DRAWING_MODE {
		IMMEDIATE_MODE, DISPLAY_LISTS, VERTEX_ARRAYS, VERTEX_BUFFER_OBJECTS
	};
	private DRAWING_MODE drawingMode;

	// display list specific variables
	private int displayListHandle;

	// vertex array specific variables
	FloatBuffer vertexFloatBuffer;
	FloatBuffer colorFloatBuffer;

	// vertex array and buffer object specific variables
	private final int numberOfVertices = 3;
	private final int vertexDimensions = 3;
	private final int colorDimensions = 3;

	public Main() {
		initializeProgram();
		programLoop();
		exitProgram();
	}

	private void initializeProgram() {
		initializeDisplay();
		initializeGL();
		initializeVariables();
	}

	private void initializeDisplay() {
		// create the display
		try {
			Display.setDisplayMode(new DisplayMode(DISPLAY_WIDTH, DISPLAY_HEIGHT));
			Display.setTitle(DISPLAY_TITLE);
			Display.create();
		} catch(LWJGLException exception) {
			exception.printStackTrace();
			Display.destroy();
			System.exit(1);
		}
	}

	private void initializeGL() {
		// edit the projection matrix
		glMatrixMode(GL_PROJECTION);
		// reset the projection matrix
		glLoadIdentity();
		gluPerspective(FIELD_OF_VIEW, (float) (DISPLAY_WIDTH / DISPLAY_HEIGHT), NEAR_PLANE, FAR_PLANE);

		// switch back to the model view matrix
		glMatrixMode(GL_MODELVIEW);
	}

	private void initializeVariables() {
		// initially set the drawing mode to immediate mode
		drawingMode = DRAWING_MODE.IMMEDIATE_MODE;
		System.out.println("Now drawing in immediate mode");

		// initialize the display list
		initializeDisplayList();

		// initialize the vertex array
		initializeVertexArray();
	}

	private void initializeDisplayList() {
		// create the display list
		displayListHandle = glGenLists(1);
		glNewList(displayListHandle, GL_COMPILE); {
			glBegin(GL_TRIANGLES); {
				glColor3f(1, 0, 0);
				glVertex3f(- 0.5f, - 0.5f, - 1.0f);
				glColor3f(0, 1, 0);
				glVertex3f(0.5f, - 0.5f, - 1.0f);
				glColor3f(0, 0, 1);
				glVertex3f(0.5f, 0.5f, - 1.0f);
			} glEnd();
		} glEndList();
	}

	private void initializeVertexArray() {
		vertexFloatBuffer = BufferUtils.createFloatBuffer(numberOfVertices * vertexDimensions);
		float[] vertexData = new float[] {
			-0.5f, -0.5f, -1.0f,
			0.5f, -0.5f, -1.0f,
			0.5f, 0.5f, -1.0f
		};
		vertexFloatBuffer.put(vertexData);
		vertexFloatBuffer.flip();

		colorFloatBuffer = BufferUtils.createFloatBuffer(numberOfVertices * colorDimensions);
		float[] colorData = new float[] {
			1, 0, 0,
			0, 1, 0,
			0, 0, 1
		};
		colorFloatBuffer.put(colorData);
		colorFloatBuffer.flip();
	}

	private void programLoop() {
		while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			renderGL();
			update();

			Display.update();
			Display.sync(TARGET_FRAME_RATE);
		}
	}

	private void renderGL() {
		// clear both buffers
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		// render a triangle using whichever mode is selected
		switch (drawingMode) {
			case IMMEDIATE_MODE:
				// render triangle using immediate mode
				glBegin(GL_TRIANGLES); {
					glColor3f(1, 0, 0);
					glVertex3f(- 0.5f, - 0.5f, - 1.0f);
					glColor3f(0, 1, 0);
					glVertex3f(0.5f, - 0.5f, - 1.0f);
					glColor3f(0, 0, 1);
					glVertex3f(0.5f, 0.5f, - 1.0f);
				} glEnd();
				break;
			case DISPLAY_LISTS:
				// render triangle using display lists
				glCallList(displayListHandle);
				break;
			case VERTEX_ARRAYS:
				// render triangle using vertex arrays
				// enable vertex arrays
				glEnableClientState(GL_VERTEX_ARRAY);
				glEnableClientState(GL_COLOR_ARRAY);

				// tell opengl where the data is
				glVertexPointer(vertexDimensions, 0, vertexFloatBuffer);
				glColorPointer(colorDimensions, 0, colorFloatBuffer);

				// draw the triangle
				glDrawArrays(GL_TRIANGLES, 0, numberOfVertices);

				// disable the vertex arrays
				glDisableClientState(GL_VERTEX_ARRAY);
				glDisableClientState(GL_COLOR_ARRAY);
				break;
			case VERTEX_BUFFER_OBJECTS:
				break;
		}
	}

	private void update() {
		while (Keyboard.next()) {
			// if the '1' key is pressed, switch to immediate mode
			if (Keyboard.isKeyDown(Keyboard.KEY_1)) {
				if (drawingMode != DRAWING_MODE.IMMEDIATE_MODE) {
					drawingMode = DRAWING_MODE.IMMEDIATE_MODE;
					System.out.println("Now drawing in immediate Mode");
				}
			}

			// if the '2' key is pressed, switch to using display lists
			if (Keyboard.isKeyDown(Keyboard.KEY_2)) {
				if (drawingMode != DRAWING_MODE.DISPLAY_LISTS) {
					drawingMode = DRAWING_MODE.DISPLAY_LISTS;
					System.out.println("Now drawing using display lists");
				}
			}

			// if the '3' key is pressed, switch to using vertex arrays
			if (Keyboard.isKeyDown(Keyboard.KEY_3)) {
				if (drawingMode != DRAWING_MODE.VERTEX_ARRAYS) {
					drawingMode = DRAWING_MODE.VERTEX_ARRAYS;
					System.out.println("Now drawing using vertex arrays");
				}
			}
		}
	}

	private void exitProgram() {
		destroyBuffers();
		Display.destroy();
		System.exit(0);
	}

	private void destroyBuffers() {
		glDeleteLists(displayListHandle, 1);
	}

	public static void main(String[] args) {
		new Main();
	}

}
