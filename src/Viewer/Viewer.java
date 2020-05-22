package Viewer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Viewer implements Runnable{
    protected final JFrame viewer;
    private final Dimension screenSize;

    public Viewer() {
        // makes the app full screen
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        viewer = new JFrame();
        viewer.setUndecorated(true);
        viewer.setSize(screenSize);

        // loads the image
        BufferedImage rawImage = getImageSource();
        ImagePanel img = new ImagePanel(rawImage, 300,300,300,300);

        // handle ESC press, exit the program
        viewer.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(1);
                }
            }
        });

        // handles mouse click, exit the program
        viewer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.exit(1);
            }
        });

        // hides the cursor
        // Transparent 16 x 16 pixel cursor image.
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

        // Create a new blank cursor.
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImg, new Point(0, 0), "blank cursor");

        // Set the blank cursor to the viewer.
        viewer.getContentPane().setCursor(blankCursor);

        // displays the content
        viewer.getContentPane().add(img);
        viewer.setVisible(true);
    }

    /**
     * Returns the image source which will be displayed.
     * This method can be overridden, when the class needs to  act like a previewer
     * @return an image to be displayed
     */
    public BufferedImage getImageSource()
    {
        BufferedImage img = null;
        try {
             img =  ImageIO.read(new File("billboard.jpg"));
        }
        catch (IOException ignore){}
        return img;
    }

    public static void main(String[] args) throws IOException {
        javax.swing.SwingUtilities.invokeLater(new Viewer());
    }

    @Override
    public void run() {
        new Viewer();
    }
}

class ImagePanel extends JPanel {
    private final int x0;
    private final int x1;
    private final int y0;
    private final int y1;
    private BufferedImage image;

    public ImagePanel(BufferedImage image,int x,int y,int width,int height) {
        this.image = image;
        this.x0 = x;
        this.x1 = width;
        this.y0 = y;
        this.y1 = height;

    }

    /**
     * Draws the image on the panel
     * @param g
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, x0, y0,x1,y1, null);
    }
}