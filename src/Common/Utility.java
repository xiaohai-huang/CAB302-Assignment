package Common;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;

public class Utility {
    public static String createBillboardXML(String billboardColour,
                                            String msg, String msgColour,
                                            String pic, String picType,
                                            String info, String infoColour) {
        String template =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<billboard bg>\n" +
                        "msg\n" +
                        "pic\n" +
                        "info\n" +
                        "</billboard>";
        String msgTag = "";
        String picTag = "";
        String infoTag = "";

        // replace bg colour
        if (!billboardColour.isBlank()) {
            template = template.replace("bg", "background=\"" + billboardColour + "\"");
        }


        if (!msg.isBlank()) {
            msgTag = createXMLTag("message", "colour", msgColour, msg);
        }
        if (!pic.isBlank()) {
            if (picType.toLowerCase().equals("url")) {// handle cases
                picTag = String.format("<picture url=\"%s\" />", pic);
            } else {
                picTag = String.format("<picture data=\"%s\" />", pic);
            }
        }
        if (!info.isBlank()) {
            infoTag = createXMLTag("information", "colour", infoColour, info);
        }

        template = template.replace("bg", "");
        template = template.replace("msg", msgTag);
        template = template.replace("pic", picTag);
        template = template.replace("info", infoTag);

        return template;
    }

    private static String createXMLTag(String tagName,
                                       String attributeName, String attributeValue,
                                       String text) {
        String xml = "";
        if (!attributeValue.isBlank()) {
            xml = String.format("<%s %s=\"%s\">%s</%s>", tagName, attributeName, attributeValue, text, tagName);
        } else {
            xml = String.format("<%s>%s</%s>", tagName, text, tagName);
        }
        return xml;
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
