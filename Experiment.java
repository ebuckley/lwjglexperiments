import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

class Experiment {


    // time when last frame was called
    private long lastFrame;
    private int fps;
    private int lastfps;

    public Experiment() {
        lastFrame = 0;
        fps = 0;
        lastfps = 0;
    }
    /**
     * get time elapsed in game in milliseconds
     *
     * @return time elapsed since game started
     */
    public long getTime() {
        return Sys.getTime();
    }

    /**
     * get the time in ms since the last frame was called
     * @param return get time since the last frame was called in ms
     */
    public int getDelta() {
        long time = getTime();
        int delta = (int) (time - lastFrame);
        lastFrame = time;

        return delta;

    }

    /**
     * set the title to show game fps
     *
     */
    public void displayfps() {
        if ((int)getTime() - lastfps > 1000) {
            Display.setTitle("fps: " + fps );
            fps = 0;
            lastfps += 1000;
        }
        fps++;
    }
    /**
     * Set the display mode to be used
     *
     * @param width The width of the display required
     * @param height The height of the display required
     * @param fullscreen True if we want fullscreen mode
     */
    public void setDisplayMode(int width, int height, boolean fullscreen) {

        // return if requested DisplayMode is already set
        if ((Display.getDisplayMode().getWidth() == width) &&
            (Display.getDisplayMode().getHeight() == height) &&
        (Display.isFullscreen() == fullscreen)) {
            return;
        }

        try {
            DisplayMode targetDisplayMode = null;

            if (fullscreen) {
                DisplayMode[] modes = Display.getAvailableDisplayModes();
                int freq = 0;

                for (int i=0;i<modes.length;i++) {
                    DisplayMode current = modes[i];

                    System.out.println(current.getWidth() + ", " + current.getHeight());
                    if ((current.getWidth() == width) && (current.getHeight() == height)) {
                        if ((targetDisplayMode == null) || (current.getFrequency() >= freq)) {
                            if ((targetDisplayMode == null) || (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel())) {
                            targetDisplayMode = current;
                            freq = targetDisplayMode.getFrequency();
                                    }
                                }

                        // if we've found a match for bpp and frequence against the
                        // original display mode then it's probably best to go for this one
                        // since it's most likely compatible with the monitor
                        if ((current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel()) &&
                                    (current.getFrequency() == Display.getDesktopDisplayMode().getFrequency())) {
                                        targetDisplayMode = current;
                                        break;
                                }
                            }
                }
            } else {
                targetDisplayMode = new DisplayMode(width,height);
            }

            if (targetDisplayMode == null) {
                System.out.println("Failed to find value mode: "+width+"x"+height+" fs="+fullscreen);
                return;
            }

            Display.setDisplayMode(targetDisplayMode);
            Display.setFullscreen(fullscreen);

        } catch (LWJGLException e) {
            System.out.println("Unable to setup mode "+width+"x"+height+" fullscreen="+fullscreen + e);
        }
    }
    public void update(int delta) {

        displayfps();
    }

    public void run() {
        setDisplayMode(800, 600, false);

        // display fps in titlebar
        try {
            Display.create();
        } catch(LWJGLException exception) {
            System.out.println(exception);
            System.exit(0);
        }


        // opengl init will go here eventually

        //called once to init the frame timers
        getDelta();
        lastfps = (int)getTime();
        while(true) {

            if (Display.isCloseRequested())
                break;


            // update the game with delta time
            int delta = getDelta();
            update(delta);

            //render opengl
            Display.update();
            //set framerate
            Display.sync(60);

        }

        Display.destroy();

    }
    public static void main (String [] args)
    {
        System.out.println("starting experiment");
        Experiment doStuff = new Experiment();
        doStuff.run();
        System.out.println("finished experiment");

    }
}
