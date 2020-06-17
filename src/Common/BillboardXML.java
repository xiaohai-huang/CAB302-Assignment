package Common;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class BillboardXML {
    private String background = "#e5e3e8";// default colour
    /**
     * <message colour="#FFF00">
     */
    private String messageColour = "#191c21";

    private String messageContent;

    /**
     * <picture url="www.123.com/ss.jpg" />
     */
    private String pictureURL;
    /**
     * <picture data="WD21ESD221SDSFRG" />
     */
    private String pictureData;

    private String errorPicture = "https://dab1nmslvvntp.cloudfront.net/wp-content/uploads/2015/12/1450973046wordpress-errors.png";


    /**
     * <information colour="#00FFFF" />
     */
    private String informationColour = "#89858f";

    private String informationContent;

    private boolean hasPicture;

    private boolean hasMessage;

    private boolean hasInformation;

    private boolean hasError;

    public BillboardXML(String xml) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newDefaultInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e){
            hasError = true;
            pictureURL = errorPicture;
            return;
        }

        // turn xml string to byte array
        ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes());

        // retrieves the DOM
        Document document;
        try {
            document = builder.parse(bais);
            bais.close();
        }
        catch (IOException | SAXException e){
            hasError = true;
            pictureURL = errorPicture;
            return;
        }

        // billboard tag
        Element billboardTag = getTag(document,"billboard");
        String background;
        try {
            background = billboardTag.getAttribute("background");
        }
        catch (NullPointerException e){
            hasError = true;
            pictureURL = errorPicture;
            return;
        }

        if(!background.isEmpty()){
            this.background = background;
        }

        // message Tag
        Element messageTag = getTag(document,"message");
        if(messageTag!=null){
            String messageColour = messageTag.getAttribute("colour");
            String messageContent = messageTag.getTextContent();
            if(!messageColour.isEmpty()){
                this.messageColour = messageColour;
            }
            this.messageContent = messageContent;
            hasMessage = true;
        }
        else {
            hasMessage = false;
        }

        // picture Tag
        Element pictureTag = getTag(document,"picture");
        if(pictureTag != null){
            String pictureURL = pictureTag.getAttribute("url");
            String pictureData = pictureTag.getAttribute("data");
            // both are empty
            if(pictureData.isEmpty()&&pictureURL.isEmpty()){
                this.pictureURL = errorPicture;
            }
            // both are provided
            if(!pictureData.isEmpty() && !pictureURL.isEmpty()){
                this.pictureURL = errorPicture;
            }

            // normal situations
            if(pictureURL.isEmpty()){
                this.pictureData = pictureData;
            }
            else{
                this.pictureURL =pictureURL;
            }
            hasPicture =true;
        }
        else {
            hasPicture = false;
        }

        // information tag
        Element infoTag = getTag(document,"information");
        if(infoTag != null){
            String informationColour = infoTag.getAttribute("colour");
            String informationContent = infoTag.getTextContent();
            if(!informationColour.isEmpty()){
                this.informationColour = informationColour;
            }
            this.informationContent = informationContent;
            hasInformation = true;
        }
        else {
            hasInformation = false;
        }

    }

    private Element getTag(Document document,String tagName){
        return (Element) document.getElementsByTagName(tagName).item(0);
    }

    public boolean isHasPicture() {
        return hasPicture;
    }

    public boolean isHasMessage() {
        return hasMessage;
    }

    public boolean isHasInformation() {
        return hasInformation;
    }

    public String getInformationContent() {
        return informationContent;
    }

    public String getInformationColour() {
        return informationColour;
    }

    public String getPictureData() {
        return pictureData;
    }

    public String getPictureURL() {
        return pictureURL;
    }

    public String getBackground() {
        return background;
    }

    public String getMessageColour() {
        return messageColour;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public boolean isHasError() {
        return hasError;
    }
}
