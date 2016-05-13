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

    private final File dir = new File("./reports");

    // starting the saving process
    public static void save(double[] statsbef, double[] statsaft) {
        final Save savi = new Save();
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
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        final String tmstamp = dateFormat.format(new Date());

        //creating the xml structure
        try {
            final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            final Document doc = docBuilder.newDocument();
            final Element rootElement = doc.createElement("Dhimulate");
            doc.appendChild(rootElement);

            // staff elements
            final Element report = doc.createElement("Report");
            rootElement.appendChild(report);

            final Attr attr = doc.createAttribute("time");
            attr.setValue(tmstamp);
            report.setAttributeNode(attr);

            final String[] tags = {"CountBefore", "CountAfter", "PartyBefore", "PartyAfter", "LeadershipBefore", "LeadershipAfter", "TeamBefore", "TeamAfter", "LearningBefore", "LearningAfter", "AlcoholBefore", "AlcoholAfter"};

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
            final TransformerFactory transformerFactory = TransformerFactory.newInstance();
            final Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            final DOMSource source = new DOMSource(doc);
            final StreamResult result = new StreamResult(new File(dir.getAbsolutePath() + "/" + tmstamp + ".xml"));

            transformer.transform(source, result);
        }
        catch (ParserConfigurationException | TransformerException ex) {
            ex.printStackTrace();
        }
    }
}
