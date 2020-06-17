package Test.Viewer;
import static org.junit.jupiter.api.Assertions.*;

import Common.BillboardXML;
import org.junit.jupiter.api.*;
public class ViewerTests {

    @Test
    void backgroundColorTest1(){
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<billboard background=\"#0000FF\">\n" +
                "    <message>Billboard with message, GIF and information</message>\n" +
                "    <picture url=\"https://cloudstor.aarnet.edu.au/plus/s/A26R8MYAplgjUhL/download\" />\n" +
                "    <information>This billboard has a message tag, a picture tag (linking to a URL with a GIF image) and an information tag. The picture is drawn in the centre and the message and information text are centred in the space between the top of the image and the top of the page, and the space between the bottom of the image and the bottom of the page, respectively.</information>\n" +
                "</billboard>";
        BillboardXML XML = new BillboardXML(xml);

        assertEquals("#0000FF",XML.getBackground());
    }

    @Test
    void backgroundColorTest2(){
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<billboard>\n" +
                "    <message>Billboard with message, GIF and information</message>\n" +
                "    <picture url=\"https://cloudstor.aarnet.edu.au/plus/s/A26R8MYAplgjUhL/download\" />\n" +
                "    <information>This billboard has a message tag, a picture tag (linking to a URL with a GIF image) and an information tag. The picture is drawn in the centre and the message and information text are centred in the space between the top of the image and the top of the page, and the space between the bottom of the image and the bottom of the page, respectively.</information>\n" +
                "</billboard>";
        BillboardXML XML = new BillboardXML(xml);

        assertEquals("#383242",XML.getBackground());
    }
    @Test
    void backgroundColorTest3(){
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<billboard background=\"#00FFFF\">\n" +
                "    <message>Basic message-only billboard</message>\n" +
                "</billboard>";
        BillboardXML XML = new BillboardXML(xml);

        assertEquals("#00FFFF",XML.getBackground());
    }

    @Test
    void messageContentTest1(){
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<billboard>\n" +
                "    <message>Billboard with message, GIF and information</message>\n" +
                "    <picture url=\"https://cloudstor.aarnet.edu.au/plus/s/A26R8MYAplgjUhL/download\" />\n" +
                "    <information>This billboard has a message tag, a picture tag (linking to a URL with a GIF image) and an information tag. The picture is drawn in the centre and the message and information text are centred in the space between the top of the image and the top of the page, and the space between the bottom of the image and the bottom of the page, respectively.</information>\n" +
                "</billboard>";
        BillboardXML XML = new BillboardXML(xml);

        assertEquals("Billboard with message, GIF and information",XML.getMessageContent());
    }

    @Test
    void messageContentTest2(){
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<billboard>\n" +
                "    <message>Billboard with message and info</message>\n" +
                "    <information>Billboard with a message tag, an information tag, but no picture tag. The message is centred within the top half of the screen while the information is centred within the bottom half.</information>\n" +
                "</billboard>";
        BillboardXML XML = new BillboardXML(xml);

        assertEquals("Billboard with message and info",XML.getMessageContent());
        assertEquals(true,XML.isHasMessage());
    }

    @Test
    void messageContentTest3(){
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<billboard>\n" +
                "    <information>Billboard with a message tag, an information tag, but no picture tag. The message is centred within the top half of the screen while the information is centred within the bottom half.</information>\n" +
                "</billboard>";
        BillboardXML XML = new BillboardXML(xml);

        assertEquals(null,XML.getMessageContent());
        assertEquals(false,XML.isHasMessage());
    }

    @Test
    void messageContentTest4(){
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<billboard>\n" +
                "    <message colour=\"#FFC457\">Billboard with default background and custom-coloured message</message>\n" +
                "</billboard>";
        BillboardXML XML = new BillboardXML(xml);

        assertEquals("#FFC457",XML.getMessageColour());

    }


    @Test
    void pictureURLTest1(){
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<billboard>\n" +
                "    <picture url=\"https://cloudstor.aarnet.edu.au/plus/s/vYipYcT3VHa1uNt/download\" />\n" +
                "    <information>Billboard with picture (with URL attribute) and information text only. The picture is now centred within the top 2/3 of the image and the information text is centred in the remaining space below the image.</information>\n" +
                "</billboard>";
        BillboardXML XML = new BillboardXML(xml);

        assertEquals("https://cloudstor.aarnet.edu.au/plus/s/vYipYcT3VHa1uNt/download",XML.getPictureURL());
        assertEquals(true,XML.isHasPicture());
        assertFalse(XML.isHasMessage());
    }

    @Test
    void noPictureTest(){
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<billboard>\n" +
                "    <information>Billboard with picture (with URL attribute) and information text only. The picture is now centred within the top 2/3 of the image and the information text is centred in the remaining space below the image.</information>\n" +
                "</billboard>";
        BillboardXML XML = new BillboardXML(xml);

        assertEquals(false,XML.isHasPicture());
    }

    @Test
    void pictureDataTest1(){
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<billboard>\n" +
                "    <picture data=\"asdqwdjqeLKJSDKLNSDK23JKSADBWQKJEN2KJ3H\" />\n" +
                "    <information>Billboard with picture (with URL attribute) and information text only. The picture is now centred within the top 2/3 of the image and the information text is centred in the remaining space below the image.</information>\n" +
                "</billboard>";
        BillboardXML XML = new BillboardXML(xml);

        assertEquals("asdqwdjqeLKJSDKLNSDK23JKSADBWQKJEN2KJ3H",XML.getPictureData());
        assertEquals(null, XML.getPictureURL());
        assertEquals(true,XML.isHasPicture());
    }

    @Test
    void errorTest1(){
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<bilboard>\n" +
                "    <picture data=\"asdqwdjqeLKJSDKLNSDK23JKSADBWQKJEN2KJ3H\" />\n" +
                "    <information>Billboard with picture (with URL attribute) and information text only. The picture is now centred within the top 2/3 of the image and the information text is centred in the remaining space below the image.</information>\n" +
                "</billboard>";
        BillboardXML XML = new BillboardXML(xml);

        assertEquals(true,XML.isHasError());
    }

    @Test
    void errorTest2(){
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<billboard>\n" +
                "    <picture data=\"asdqwdjqeLKJSDKLNSDK23JKSADBWQKJEN2KJ3H\" />\n" +
                "    <information>Billboard with picture (with URL attribute) and information text only. The picture is now centred within the top 2/3 of the image and the information text is centred in the remaining space below the image.</information>\n" +
                "</billboard>";
        BillboardXML XML = new BillboardXML(xml);

        assertEquals("asdqwdjqeLKJSDKLNSDK23JKSADBWQKJEN2KJ3H",XML.getPictureData());
        assertEquals(false,XML.isHasError());
    }





}
