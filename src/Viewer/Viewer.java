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

        // loads the content to be displayed
        String xml = ReadTextFile("xml/billboards/15.xml");
        DisplayPanel content = new DisplayPanel(xml);

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
        viewer.getContentPane().add(content);
        viewer.setVisible(true);
    }


    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        javax.swing.SwingUtilities.invokeLater(new Viewer());
//        String xml = ReadTextFile("xml/billboards/16.xml");
//        BillboardXML XML = new BillboardXML(xml);
//        System.out.println(XML.getPictureData());
//        BufferedImage image = decodeImage(XML.getPictureData());
//        //ImageIO.write(image,"jpg", new File("xiaohai.jpg"));
//        JFrame f = new JFrame();
//        JPanel panel = new JPanel(){
//            @Override
//            protected void paintComponent(Graphics g) {
//                g.drawImage(image,0,0,null);
//            }
//        };
//        f.getContentPane().add(panel);
//        f.setVisible(true);
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

    private BillboardXML XML;

    public DisplayPanel(String xml) {
        this.XML = new BillboardXML(xml);
    }

    /**
     * Draws the content on the panel
     *
     * @param g
     */
    @Override
    protected void paintComponent(Graphics g) {
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

        g.drawImage(image, 0, 0, null);
    }


}