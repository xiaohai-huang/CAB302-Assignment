package Viewer;

import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Base64;

public class Viewer implements Runnable {
    protected final JFrame viewer;
    private final Dimension screenSize;

    public Viewer() {
        // makes the app full screen
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        viewer = new JFrame();
        viewer.setUndecorated(true);
        viewer.setSize(screenSize);


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


        // loads the content to be displayed
        String xml = ReadTextFile("xml/billboards/9.xml");
        DisplayPanel content = new DisplayPanel(xml,screenSize);

        // displays the content
        viewer.getContentPane().add(content);
        viewer.setVisible(true);
    }


    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Viewer());
    }

    @Override
    public void run() {
        new Viewer();
    }

    // from https://grokonez.com/java/java-advanced/java-8-encode-decode-an-image-base64
    public static String encodeImage(BufferedImage image) {
        String base64Image = "";
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", bos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] data = bos.toByteArray();

        base64Image = Base64.getEncoder().encodeToString(data);

        return base64Image;
    }

    public static BufferedImage decodeImage(String base64Image) {
        // Converting a Base64 String into Image byte array
        byte[] imageByteArray = Base64.getDecoder().decode(base64Image);
        ByteArrayInputStream bis = new ByteArrayInputStream(imageByteArray);
        BufferedImage image = null;
        try {
            image = ImageIO.read(bis);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    public static String ReadTextFile(String filePath) {
        String everything = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));

            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            everything = sb.toString();
            br.close();

        } catch (IOException e) {
            System.out.println("IO exception!");
        }
        return everything;
    }
}

class DisplayPanel extends JPanel {

    private final Dimension screenSize;
    private BillboardXML XML;

    public DisplayPanel(String xml, Dimension screenSize) {
        this.XML = new BillboardXML(xml);
        this.screenSize = screenSize;

        // loads background colour and draw the bg colour
        String backgroundColour = XML.getBackground();
        Color bg = Color.decode(backgroundColour);
        this.setBackground(bg);

    }

    /**
     * Draws the content on the panel
     *
     * @param g
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // handle error
        if (XML.isHasError()) {
            try {
                BufferedImage errorImage = ImageIO.read(new URL(XML.getPictureURL()));
                g.drawImage(errorImage, 0, 0, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }



        // loads image if exists
        BufferedImage image = null;
        if (XML.isHasPicture()) {
            // loads image from url
            if (XML.getPictureURL() != null) {
                try {
                    image = ImageIO.read(new URL(XML.getPictureURL()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else { // loads base 64 image
                image = Viewer.decodeImage(XML.getPictureData());
            }
        }

        // loads message if exists
        String message = null;
        Color messageColour = null;
        if (XML.isHasMessage()) {
            message = XML.getMessageContent();
            messageColour = Color.decode(XML.getMessageColour());
        }

        // loads information
        String information = null;
        Color informationColour = null;
        if (XML.isHasInformation()) {
            information = XML.getInformationContent();
            informationColour = Color.decode(XML.getInformationColour());
        }


        // only message is present
        if(XML.isHasMessage() && (!XML.isHasPicture() || !XML.isHasInformation()))
        {
            int fontSize = ((int)(screenSize.width/ message.length()/0.75)+2);
            String labelText = String.format("<html><h1 style=\"width:%dpx;font-size:%dpx;color:%s;text-align:center\">%s</h1></html>",
                    1200,fontSize,messageColour, message);
            JLabel msg = new JLabel(labelText);
            this.setLayout(new BorderLayout());
            this.add(msg,BorderLayout.CENTER);
        }
        // only picture is present
        else if(XML.isHasPicture() && (!XML.isHasMessage() || !XML.isHasInformation()))
        {
            //the image should be scaled up to half the width and height of the screen and displayed in the centre.
            // screenSize = 1000 x 750           ==>  500 x 375   1:1.33
            // imageSize =  100  x 100           ==>  375 x 375   1:1
            // imageSize =  100  x 50  (1:2)     ==>  500 x 250
            Dimension imageSize = getImageAloneDimension(screenSize,new Dimension(image.getWidth(),image.getHeight()));
            Point centerDrawingPoint = getCenterDrawingPoint(screenSize,imageSize);
            g.drawImage(image,centerDrawingPoint.x,centerDrawingPoint.y,imageSize.width,imageSize.height,null);
        }

    }

    private Dimension getImageAloneDimension(Dimension screenSize, Dimension imageSize){
        Dimension halfScreenSize = new Dimension(screenSize.width/2,screenSize.height/2);
        double imageRatio = (double) imageSize.width / imageSize.height;

            double y = 1;
            double x = imageRatio * y;

            double newImageHeight = halfScreenSize.width / y;
            if(newImageHeight > halfScreenSize.height){
                newImageHeight = halfScreenSize.height;
            }
            double newImageWidth = imageRatio * newImageHeight;

            return new Dimension((int)newImageWidth,(int)newImageHeight);
    }

    private Point getCenterDrawingPoint(Dimension screenSize, Dimension imageSize){
        Point center = new Point(screenSize.width/2,screenSize.height/2);
        Point leftUpPoint = new Point(center.x-imageSize.width/2,center.y-imageSize.height/2);

        return  leftUpPoint;
    }
}