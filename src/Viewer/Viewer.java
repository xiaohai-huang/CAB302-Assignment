package Viewer;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Base64;

public class Viewer  {
    public JFrame viewer;
    private Dimension screenSize;
    protected DisplayPanel displayPanel;

    public void close(){
        viewer.dispose();
    }
    public Viewer()  {
        // makes the app full screen
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        viewer = new JFrame();
        //viewer.setSize(screenSize);
        viewer.setExtendedState(JFrame.MAXIMIZED_BOTH);
        viewer.setUndecorated(true);


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
        String xml = getXMLSource();// the default billboard
        displayPanel = new DisplayPanel(xml, screenSize);

        // displays the content
        viewer.getContentPane().add(displayPanel);
        viewer.setVisible(true);
//        System.out.println("constructor finished");

    }

    public String getXMLSource() {
        return ReadTextFile("xml/billboards/10.xml");
    }


    public static void main(String[] args) throws InterruptedException {
        // background
        JFrame baseFrame = new JFrame();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        baseFrame.setUndecorated(true);
        baseFrame.setSize(screenSize);
        baseFrame.setBackground(Color.gray);
        baseFrame.setVisible(true);

        Viewer viewer = new Viewer(){
            @Override
            public String getXMLSource() {
                return ReadTextFile("xml/billboards/1.xml");
            }
        };
        Thread.sleep(3000);

        viewer.updatePanel(ReadTextFile("xml/billboards/6.xml"));

        Thread.sleep(3000);

        viewer.updatePanel(ReadTextFile("xml/billboards/9.xml"));


        Thread.sleep(3000);

        viewer.updatePanel(ReadTextFile("xml/billboards/10.xml"));


        Thread.sleep(3000);

        viewer.updatePanel(ReadTextFile("xml/billboards/13.xml"));


        Thread.sleep(3000);

        viewer.updatePanel(ReadTextFile("xml/billboards/11.xml"));

    }

    public void updatePanel(String xml){

        DisplayPanel newPanel = new DisplayPanel(xml,screenSize);
        this.viewer.getContentPane().removeAll();
        this.viewer.getContentPane().invalidate();

        this.viewer.getContentPane().add(newPanel);
        this.viewer.getContentPane().revalidate();
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
        String messageColour = null;
        if (XML.isHasMessage()) {
            message = XML.getMessageContent();
            messageColour = XML.getMessageColour();
        }

        // loads information
        String information = null;
        String informationColour = null;
        if (XML.isHasInformation()) {
            information = XML.getInformationContent();
            informationColour = XML.getInformationColour();
        }


        // only message is present
        if (XML.isHasMessage() && (!XML.isHasPicture() && !XML.isHasInformation())) {
            int fontSize = ((int) (screenSize.width / message.length() / 0.75) + 2);
            String labelText = String.format("<html><h1 style=\"width:%dpx;font-size:%dpx;color:%s;text-align:center\">%s</h1></html>",
                    1200, fontSize,
                    messageColour,
                    message);

            JLabel msg = new JLabel(labelText);
            this.setLayout(new BorderLayout());
            this.add(msg, BorderLayout.CENTER);
        }
        // only picture is present
        else if (XML.isHasPicture() && (!XML.isHasMessage() && !XML.isHasInformation())) {
            //the image should be scaled up to half the width and height of the screen and displayed in the centre.
            // screenSize = 1000 x 750           ==>  500 x 375   1:1.33
            // imageSize =  100  x 100           ==>  375 x 375   1:1
            // imageSize =  100  x 50  (1:2)     ==>  500 x 250
            Dimension imageSize = getImageDimensionFitHalfScreen(screenSize, new Dimension(image.getWidth(), image.getHeight()));
            Point centerDrawingPoint = getCenterDrawingPoint(screenSize, imageSize);
            //g.drawImage(image, centerDrawingPoint.x, centerDrawingPoint.y, imageSize.width, imageSize.height, null);
        }
        // only information is present
        else if (XML.isHasInformation() && (!XML.isHasMessage() && !XML.isHasPicture())) {
            int limitedWidth = (int) (0.75 * screenSize.width);

            int fontSize = (limitedWidth * 5 + 1000) / information.length() - 1;
            String infoText = String.format("<html><h3 style=\"width:%dpx;font-size:%dpx;color:%s;text-align:center\">%s</h3></html>",
                    limitedWidth - 345, fontSize,
                    informationColour,
                    information);
            JLabel info = new JLabel(infoText);
            this.setLayout(new GridBagLayout());
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            this.add(info);
//            System.out.println( "actual width"+msg.getPreferredSize().getWidth());
//            System.out.println("limited width"+limitedWidth);
//            System.out.println();
//            System.out.println("actual height"+ msg.getPreferredSize().getHeight());
//            System.out.println("limited height"+limitedHeight);
//            System.out.println();
        }
        // only message and picture are present
        else if (XML.isHasMessage() && XML.isHasPicture() && !XML.isHasInformation()) {
            int fontSize = ((int) (screenSize.width / message.length() / 0.75) + 2);
            String labelText = String.format("<html><h1 style=\"width:%dpx;font-size:%dpx;color:%s;text-align:center\">%s</h1></html>",
                    1200, fontSize,
                    messageColour,
                    message);
            this.setLayout(null);
            JLabel msg = new JLabel(labelText);
            msg.setBounds(0, 50, screenSize.width, 300);
            this.add(msg);

            // get new image size
            Dimension imageSize = getImageDimensionFitHalfScreen(screenSize, new Dimension(image.getWidth(), image.getHeight()));
            // it should be drawn in the middle of the bottom 2/3 of the screen.
            Point drawingPoint = getBottomDrawingPoint(screenSize, imageSize);
            // draw the image
           // g.drawImage(image, drawingPoint.x, drawingPoint.y, imageSize.width, imageSize.height, null);


        }
        // only message and information are present
        else if (XML.isHasMessage() && XML.isHasInformation() && !XML.isHasPicture()) {
            int messageFontSize = ((int) (screenSize.width / message.length() / 0.75) + 2);
            String messageText = String.format("<html><h1 style=\"width:%dpx;font-size:%dpx;color:%s;text-align:center\">%s</h1></html>",
                    1200, messageFontSize,
                    messageColour,
                    message);

            JLabel msg = new JLabel(messageText);
            this.add(msg);


            int limitedWidth = (int) (0.75 * screenSize.width);

            int infoFontSize = (limitedWidth / information.length()) + 25;
            if (infoFontSize > messageFontSize) {
                infoFontSize = messageFontSize - 5;
            }
            String infoText = String.format("<html><h3 style=\"width:%dpx;font-size:%dpx;color:%s;text-align:center\">%s</h3></html>",
                    limitedWidth - 345, infoFontSize,
                    informationColour,
                    information);
            JLabel info = new JLabel(infoText);
            JPanel bottomPanel = new JPanel();
            bottomPanel.setBackground(new Color(0, 0, 0, 0));
            bottomPanel.add(info);
            this.add(bottomPanel);

            this.setLayout(new GridLayout(2, 1, 10, 100));


        }
        // only picture and information are present
        else if (XML.isHasPicture() && XML.isHasInformation() && !XML.isHasMessage()) {
            // 5.xml
            // get new image size
            Dimension imageSize = getImageDimensionFitHalfScreen(screenSize, new Dimension(image.getWidth(), image.getHeight()));
            // get top center section location
            Point drawingPoint = getTopDrawingPoint(screenSize, imageSize);
            // draw the image
          //  g.drawImage(image, drawingPoint.x, drawingPoint.y, imageSize.width, imageSize.height, null);

            // draw information
            int limitedWidth = (int) (0.75 * screenSize.width);

            int fontSize = (limitedWidth * 5 + 1000) / information.length() - 4;
            String infoText = String.format("<html><h3 style=\"width:%dpx;font-size:%dpx;color:%s;text-align:center\">%s</h3></html>",
                    limitedWidth - 268, fontSize,
                    informationColour,
                    information);
            JLabel info = new JLabel(infoText);
            this.setLayout(new GridLayout(3, 1));
            JPanel row1 = new JPanel();
            JPanel row2 = new JPanel();
            JPanel infoPanel = new JPanel();
            row1.setBackground(new Color(0, 0, 0, 0));
            row2.setBackground(new Color(0, 0, 0, 0));
            infoPanel.setBackground(new Color(0, 0, 0, 0));
            infoPanel.add(info);

            this.add(row1);
            this.add(row2);
            this.add(infoPanel);


//            System.out.println("75% of screen width" + screenSize.width * 0.75);
//            System.out.println("label's width" + info.getPreferredSize().getWidth());


        }
        //  message, picture and information are all present
        else if (XML.isHasMessage() && XML.isHasInformation() && XML.isHasPicture()) {
            // 6.xml
            // 10.xml
            // get new image size which fits to 1/3 of screen
//            System.out.println("all present");
            Dimension imageSize = getImageDimensionFitOneThirdScreen(screenSize,
                    new Dimension(image.getWidth(), image.getHeight()));

            // get center drawing point
            Point drawingPoint = getCenterDrawingPoint(screenSize, imageSize);

            // draw the image
           // g.drawImage(image, drawingPoint.x, drawingPoint.y, imageSize.width, imageSize.height, null);

//            System.out.println(imageSize);
//            System.out.println(drawingPoint);
//            System.out.println(screenSize);

            // draw message
            int messageFontSize = ((int) (screenSize.width / message.length() / 0.75) + 2);
            String messageText = String.format("<html><h1 style=\"width:%dpx;font-size:%dpx;color:%s;text-align:center\">%s</h1></html>",
                    1200, messageFontSize,
                    messageColour,
                    message);

            JLabel msg = new JLabel(messageText);

            // draw information
            int limitedWidth = (int) (0.75 * screenSize.width);

            int fontSize = (limitedWidth * 5 + 1000) / information.length();
            if (fontSize > messageFontSize) {
                fontSize = (int) (messageFontSize * 0.60);
            }
            String infoText = String.format("<html><h3 style=\"width:%dpx;font-size:%dpx;color:%s;text-align:center\">%s</h3></html>",
                    limitedWidth - 268, fontSize,
                    informationColour,
                    information);
            JLabel info = new JLabel(infoText);


            this.setLayout(new GridLayout(3, 1));
            JPanel messagePanel = new JPanel();
            JPanel imagePanel = new JPanel();
            JPanel infoPanel = new JPanel();
            // make them transparent
            messagePanel.setBackground(new Color(0, 0, 0, 0));
            imagePanel.setBackground(new Color(0, 0, 0, 0));
            infoPanel.setBackground(new Color(0, 0, 0, 0));

            messagePanel.setLayout(new BorderLayout());
            messagePanel.add(msg, BorderLayout.CENTER);

            infoPanel.setLayout(new GridBagLayout());
            infoPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
            infoPanel.add(info);

            this.add(messagePanel);
            this.add(imagePanel);
            this.add(infoPanel);
        }


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
        String messageColour = null;
        if (XML.isHasMessage()) {
            message = XML.getMessageContent();
            messageColour = XML.getMessageColour();
        }

        // loads information
        String information = null;
        String informationColour = null;
        if (XML.isHasInformation()) {
            information = XML.getInformationContent();
            informationColour = XML.getInformationColour();
        }


        // only message is present
        if (XML.isHasMessage() && (!XML.isHasPicture() && !XML.isHasInformation())) {
            int fontSize = ((int) (screenSize.width / message.length() / 0.75) + 2);
            String labelText = String.format("<html><h1 style=\"width:%dpx;font-size:%dpx;color:%s;text-align:center\">%s</h1></html>",
                    1200, fontSize,
                    messageColour,
                    message);

            JLabel msg = new JLabel(labelText);
            this.setLayout(new BorderLayout());
            this.add(msg, BorderLayout.CENTER);
        }
        // only picture is present
        else if (XML.isHasPicture() && (!XML.isHasMessage() && !XML.isHasInformation())) {
            //the image should be scaled up to half the width and height of the screen and displayed in the centre.
            // screenSize = 1000 x 750           ==>  500 x 375   1:1.33
            // imageSize =  100  x 100           ==>  375 x 375   1:1
            // imageSize =  100  x 50  (1:2)     ==>  500 x 250
            Dimension imageSize = getImageDimensionFitHalfScreen(screenSize, new Dimension(image.getWidth(), image.getHeight()));
            Point centerDrawingPoint = getCenterDrawingPoint(screenSize, imageSize);
            g.drawImage(image, centerDrawingPoint.x, centerDrawingPoint.y, imageSize.width, imageSize.height, null);
        }
        // only information is present
        else if (XML.isHasInformation() && (!XML.isHasMessage() && !XML.isHasPicture())) {
            int limitedWidth = (int) (0.75 * screenSize.width);

            int fontSize = (limitedWidth * 5 + 1000) / information.length() - 1;
            String infoText = String.format("<html><h3 style=\"width:%dpx;font-size:%dpx;color:%s;text-align:center\">%s</h3></html>",
                    limitedWidth - 345, fontSize,
                    informationColour,
                    information);
            JLabel info = new JLabel(infoText);
            this.setLayout(new GridBagLayout());
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            this.add(info);
//            System.out.println( "actual width"+msg.getPreferredSize().getWidth());
//            System.out.println("limited width"+limitedWidth);
//            System.out.println();
//            System.out.println("actual height"+ msg.getPreferredSize().getHeight());
//            System.out.println("limited height"+limitedHeight);
//            System.out.println();
        }
        // only message and picture are present
        else if (XML.isHasMessage() && XML.isHasPicture() && !XML.isHasInformation()) {
            int fontSize = ((int) (screenSize.width / message.length() / 0.75) + 2);
            String labelText = String.format("<html><h1 style=\"width:%dpx;font-size:%dpx;color:%s;text-align:center\">%s</h1></html>",
                    1200, fontSize,
                    messageColour,
                    message);
            this.setLayout(null);
            JLabel msg = new JLabel(labelText);
            msg.setBounds(0, 50, screenSize.width, 300);
            this.add(msg);

            // get new image size
            Dimension imageSize = getImageDimensionFitHalfScreen(screenSize, new Dimension(image.getWidth(), image.getHeight()));
            // it should be drawn in the middle of the bottom 2/3 of the screen.
            Point drawingPoint = getBottomDrawingPoint(screenSize, imageSize);
            // draw the image
            g.drawImage(image, drawingPoint.x, drawingPoint.y, imageSize.width, imageSize.height, null);


        }
        // only message and information are present
        else if (XML.isHasMessage() && XML.isHasInformation() && !XML.isHasPicture()) {
            int messageFontSize = ((int) (screenSize.width / message.length() / 0.75) + 2);
            String messageText = String.format("<html><h1 style=\"width:%dpx;font-size:%dpx;color:%s;text-align:center\">%s</h1></html>",
                    1200, messageFontSize,
                    messageColour,
                    message);

            JLabel msg = new JLabel(messageText);
            this.add(msg);


            int limitedWidth = (int) (0.75 * screenSize.width);

            int infoFontSize = (limitedWidth / information.length()) + 25;
            if (infoFontSize > messageFontSize) {
                infoFontSize = messageFontSize - 5;
            }
            String infoText = String.format("<html><h3 style=\"width:%dpx;font-size:%dpx;color:%s;text-align:center\">%s</h3></html>",
                    limitedWidth - 345, infoFontSize,
                    informationColour,
                    information);
            JLabel info = new JLabel(infoText);
            JPanel bottomPanel = new JPanel();
            bottomPanel.setBackground(new Color(0, 0, 0, 0));
            bottomPanel.add(info);
            this.add(bottomPanel);

            this.setLayout(new GridLayout(2, 1, 10, 100));


        }
        // only picture and information are present
        else if (XML.isHasPicture() && XML.isHasInformation() && !XML.isHasMessage()) {
            // 5.xml
            // get new image size
            Dimension imageSize = getImageDimensionFitHalfScreen(screenSize, new Dimension(image.getWidth(), image.getHeight()));
            // get top center section location
            Point drawingPoint = getTopDrawingPoint(screenSize, imageSize);
            // draw the image
            g.drawImage(image, drawingPoint.x, drawingPoint.y, imageSize.width, imageSize.height, null);

            // draw information
            int limitedWidth = (int) (0.75 * screenSize.width);

            int fontSize = (limitedWidth * 5 + 1000) / information.length() - 4;
            String infoText = String.format("<html><h3 style=\"width:%dpx;font-size:%dpx;color:%s;text-align:center\">%s</h3></html>",
                    limitedWidth - 268, fontSize,
                    informationColour,
                    information);
            JLabel info = new JLabel(infoText);
            this.setLayout(new GridLayout(3, 1));
            JPanel row1 = new JPanel();
            JPanel row2 = new JPanel();
            JPanel infoPanel = new JPanel();
            row1.setBackground(new Color(0, 0, 0, 0));
            row2.setBackground(new Color(0, 0, 0, 0));
            infoPanel.setBackground(new Color(0, 0, 0, 0));
            infoPanel.add(info);

            this.add(row1);
            this.add(row2);
            this.add(infoPanel);


//            System.out.println("75% of screen width" + screenSize.width * 0.75);
//            System.out.println("label's width" + info.getPreferredSize().getWidth());


        }
        //  message, picture and information are all present
        else if (XML.isHasMessage() && XML.isHasInformation() && XML.isHasPicture()) {
            // 6.xml
            // 10.xml
            // get new image size which fits to 1/3 of screen
//            System.out.println("all present");
            Dimension imageSize = getImageDimensionFitOneThirdScreen(screenSize,
                    new Dimension(image.getWidth(), image.getHeight()));

            // get center drawing point
            Point drawingPoint = getCenterDrawingPoint(screenSize, imageSize);

            // draw the image
            g.drawImage(image, drawingPoint.x, drawingPoint.y, imageSize.width, imageSize.height, null);

//            System.out.println(imageSize);
//            System.out.println(drawingPoint);
//            System.out.println(screenSize);

            // draw message
            int messageFontSize = ((int) (screenSize.width / message.length() / 0.75) + 2);
            String messageText = String.format("<html><h1 style=\"width:%dpx;font-size:%dpx;color:%s;text-align:center\">%s</h1></html>",
                    1200, messageFontSize,
                    messageColour,
                    message);

            JLabel msg = new JLabel(messageText);

            // draw information
            int limitedWidth = (int) (0.75 * screenSize.width);

            int fontSize = (limitedWidth * 5 + 1000) / information.length();
            if (fontSize > messageFontSize) {
                fontSize = (int) (messageFontSize * 0.60);
            }
            String infoText = String.format("<html><h3 style=\"width:%dpx;font-size:%dpx;color:%s;text-align:center\">%s</h3></html>",
                    limitedWidth - 268, fontSize,
                    informationColour,
                    information);
            JLabel info = new JLabel(infoText);


            this.setLayout(new GridLayout(3, 1));
            JPanel messagePanel = new JPanel();
            JPanel imagePanel = new JPanel();
            JPanel infoPanel = new JPanel();
            // make them transparent
            messagePanel.setBackground(new Color(0, 0, 0, 0));
            imagePanel.setBackground(new Color(0, 0, 0, 0));
            infoPanel.setBackground(new Color(0, 0, 0, 0));

            messagePanel.setLayout(new BorderLayout());
            messagePanel.add(msg, BorderLayout.CENTER);

            infoPanel.setLayout(new GridBagLayout());
            infoPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
            infoPanel.add(info);

            this.add(messagePanel);
            this.add(imagePanel);
            this.add(infoPanel);
        }

    }

    private Dimension getImageDimensionFitHalfScreen(Dimension screenSize, Dimension imageSize) {
        Dimension halfScreenSize = new Dimension(screenSize.width / 2, screenSize.height / 2);
        double imageRatio = (double) imageSize.width / imageSize.height;

        double y = 1;
        double x = imageRatio * y;

        double newImageHeight = halfScreenSize.width / imageRatio;
        if (newImageHeight > halfScreenSize.height) {
            newImageHeight = halfScreenSize.height;
        }
        double newImageWidth = imageRatio * newImageHeight;

        return new Dimension((int) newImageWidth, (int) newImageHeight);
    }

    private Dimension getImageDimensionFitOneThirdScreen(Dimension screenSize, Dimension imageSize) {
        Dimension smallScreenSize = new Dimension(screenSize.width / 3, screenSize.height / 3);
        double imageRatio = (double) imageSize.width / imageSize.height;

        double y = 1;
        double x = imageRatio * y;

        double newImageHeight = smallScreenSize.width / imageRatio;
        if (newImageHeight > smallScreenSize.height) {
            newImageHeight = smallScreenSize.height;
        }
        double newImageWidth = imageRatio * newImageHeight;

        return new Dimension((int) newImageWidth, (int) newImageHeight);
    }

    private Point getCenterDrawingPoint(Dimension screenSize, Dimension imageSize) {
        Point center = new Point(screenSize.width / 2, screenSize.height / 2);
        Point leftUpPoint = new Point(center.x - imageSize.width / 2, center.y - imageSize.height / 2);

        return leftUpPoint;
    }

    private Point getBottomDrawingPoint(Dimension screenSize, Dimension imageSize) {
        Point bottomCenter = new Point(screenSize.width / 2, (int) ((double) (screenSize.height) * 2 / 3));
        Point leftUpPoint = new Point(bottomCenter.x - imageSize.width / 2, bottomCenter.y - imageSize.height / 2);
        return leftUpPoint;
    }

    private Point getTopDrawingPoint(Dimension screenSize, Dimension imageSize) {
        Point topCenter = new Point(screenSize.width / 2, (int) ((double) (screenSize.height) * 1 / 3));
        Point leftUpPoint = new Point(topCenter.x - imageSize.width / 2, topCenter.y - imageSize.height / 2);
        return leftUpPoint;
    }


}