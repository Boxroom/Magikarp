package save;

/**
 * @author lucas, nilsw
 */

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


public class Save {

    private File dir = new File("./reports");

    // starting the saving process
    public static void save(double[] statsbef, double[] statsaft) {
        Save savi = new Save();
        savi.createDir();
        savi.export(statsbef, statsaft);
    }

    private void createDir() {
        if (!dir.exists()) {
            if (dir.mkdir()) {
                System.out.println("Dir created");
            }
            else {
                System.out.println(dir + " couldn't be created");
            }
        }
    }

    // exporting the xml
    private void export(double[] statsbef, double[] statsaft) {
        //creating timestamp as the name
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String tmstamp = dateFormat.format(new Date());

        //creating the xml structure
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("Dhimulate");
            doc.appendChild(rootElement);

            // staff elements
            Element report = doc.createElement("Report");
            rootElement.appendChild(report);

            Attr attr = doc.createAttribute("time");
            attr.setValue(tmstamp);
            report.setAttributeNode(attr);

            String[] tags = {"CountBefore", "CountAfter", "AlcoholBefore", "AlcoholAfter", "LeadershipBefore", "LeadershipAfter", "LearningBefore", "LearningAfter", "TeamBefore", "TeamAfter", "PartyBefore", "PartyAfter"};

            Element element;
            for (int i = 0; i < tags.length; ++i) {
                element = doc.createElement(tags[i]);
                if (i % 2 == 0) {
                    element.appendChild(doc.createTextNode(String.valueOf(statsbef[i / 2])));
                }
                else {
                    element.appendChild(doc.createTextNode(String.valueOf(statsaft[i / 2])));
                }
                report.appendChild(element);
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(dir.getAbsolutePath() + "/" + tmstamp + ".xml"));


            transformer.transform(source, result);
        }
        catch (ParserConfigurationException | TransformerException ex) {
            ex.printStackTrace();
        }
    }
}
