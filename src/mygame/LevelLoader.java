/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import com.jme3.math.Vector3f;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Campbell Suter
 */
public class LevelLoader implements AssetLoader {

    public static String[] levels = {"lv1", "lv2", "lv3"};

    public Object load(AssetInfo assetInfo) {
        Document doc = createDocFromStream(assetInfo.openStream());
        Map<Vector3f, Class<? extends Cube>> list = new HashMap<Vector3f, Class<? extends Cube>>();
        if (doc != null) {
            // Process document here
            // doc.getElementsByTagName(“…”)

            doc.getDocumentElement().normalize();

//            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            NodeList nList = doc.getElementsByTagName("block");

//            System.out.println("----------------------------");

            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    try {
                        Element eElement = (Element) nNode;
                        int x = Integer.parseInt(eElement.getAttribute("x"));//getElementsByTagName("x").item(0).getTextContent());
                        int y = Integer.parseInt(eElement.getAttribute("y"));
                        int z = Integer.parseInt(eElement.getAttribute("z"));
                        Vector3f vec = new Vector3f(x, y, z);
                        list.put(vec, (Class<? extends Cube>) Class.forName("mygame.cubes." + eElement.getAttribute("type")));
                        // eElement.getElementsByTagName("type").item(0).getTextContent()

                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(LevelLoader.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (RuntimeException ex) {
                        Logger.getLogger(LevelLoader.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }

        // Return an instance of your result class X here
        return list;
    }

    public static Document createDocFromStream(InputStream inputStream) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(inputStream);
            return doc;
        } catch (Exception ex) {
            return null;
        }
    }
}