package main;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

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
				break;
			case VERTEX_ARRAYS:
				break;
			case VERTEX_BUFFER_OBJECTS:
				break;
		}
	}

	private void update() {
		// if the '1' key is pressed, switch to immediate mode
		if (Keyboard.isKeyDown(Keyboard.KEY_1)) {
			drawingMode = DRAWING_MODE.IMMEDIATE_MODE;
			System.out.println("Now drawing in immediate Mode");
		}
	}

	private void exitProgram() {
		destroyBuffers();
		Display.destroy();
		System.exit(0);
	}

	private void destroyBuffers() {
		// TODO: destroy buffers
	}

	public static void main(String[] args) {
		new Main();
	}

}
