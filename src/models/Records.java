package models;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class Records {
    private static Records instance = null;

    private Records() {
    }

    public static Records getInstance() {
        if (instance == null) {
            instance = new Records();
        }
        return instance;
    }

    private TreeMap<String, List<String>> engToVieDict = new TreeMap<>();
    private TreeMap<String, List<String>> vieToEngDict = new TreeMap<>();

    /**
     * Static function
     */
    private TreeMap<String, List<String>> parsingXML(String filename) throws ParserConfigurationException, IOException, SAXException {
        File file = new File(filename);
        TreeMap<String, List<String>> records = new TreeMap<>();

        //instance document builder
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.parse(file);
        document.getDocumentElement().normalize();

        NodeList nodeList = document.getElementsByTagName("record");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String word = element.getElementsByTagName("word").item(0).getTextContent();
                String meaning = element.getElementsByTagName("meaning").item(0).getTextContent();
//                Record newRecord = new Record(word, meaning);
//                records.add(newRecord);

                if (records.get(word) == null) {
                    List<String> meanings = new ArrayList<>();
                    records.put(word, meanings);
                }
                records.get(word).add(meaning);
            }
        }
        return records;
    }

    public TreeMap<String, List<String>> parsingEngVieXML(String filename) throws ParserConfigurationException, IOException, SAXException {
        engToVieDict = parsingXML(filename);
        return engToVieDict;
    }

    public void parsingVieEngXML(String filename) throws ParserConfigurationException, IOException, SAXException {
        vieToEngDict = parsingXML(filename);
    }

    public TreeMap<String, List<String>> getEngToVieDict(String filename) throws ParserConfigurationException, IOException, SAXException {
        if (engToVieDict.isEmpty()) {
            engToVieDict = parsingEngVieXML(filename);
        }
        return engToVieDict;
    }

    public TreeMap<String, List<String>> getVieToEngDict(String filename) throws ParserConfigurationException, IOException, SAXException {
        if (vieToEngDict.isEmpty()) {
            vieToEngDict = parsingEngVieXML(filename);
        }
        return vieToEngDict;
    }

    public TreeMap<String, List<String>> getEngToVieDict() {
        return engToVieDict;
    }

    public TreeMap<String, List<String>> getVieToEngDict() {
        return vieToEngDict;
    }
//
//
//    private Record(){}
//    private Record instance = null;
//    public Record getInstance(){
//        if(instance == null){
//            instance = new Record();
//        }
//        return instance;
//    }

    public boolean addToEngVie(String word, String meaning) {
        engToVieDict.computeIfAbsent(word, k -> new ArrayList<>());
        return engToVieDict.get(word).add(meaning);
    }

    public boolean addToVieEng(String word, String meaning) {
        vieToEngDict.computeIfAbsent(word, k -> new ArrayList<>());
        return vieToEngDict.get(word).add(meaning);
    }

    public boolean removeFromEngVie(String word, String meaning) {
        List<String> meanings = this.engToVieDict.get(word);
        if (meanings == null) {
            return false;
        }
        boolean res = meanings.remove(meaning);
        if (!res) {
            return false;
        }
        if (meanings.isEmpty()) {
            engToVieDict.remove(word);
        }
        return true;
    }

    public boolean removeFromVieEng(String word, String meaning) {
        List<String> meanings = this.vieToEngDict.get(word);
        if (meanings == null) {
            return false;
        }
        boolean res = meanings.remove(meaning);
        if (!res) {
            return false;
        }
        if (meanings.isEmpty()) {
            vieToEngDict.remove(word);
        }
        return true;
    }

    public void saveRecords(String evPath, String vePath) throws ParserConfigurationException, TransformerException {
        saveEachRecord(engToVieDict, evPath);
        saveEachRecord(vieToEngDict, vePath);
    }

    private void saveEachRecord(@NotNull TreeMap<String, List<String>> dict, String path) throws ParserConfigurationException, TransformerException {

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        Document doc = dbFactory.newDocumentBuilder().newDocument();
        Element rootElement = doc.createElement("dictionary");
        doc.appendChild(rootElement);

        for (String word : dict.keySet()) {
            for (String meaning : dict.get(word)) {
                rootElement.appendChild(createRecordElement(doc, word, meaning));
            }
        }
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource domSource = new DOMSource(doc);

        StreamResult file = new StreamResult(new File(path));

        transformer.transform(domSource, file);
    }

    private @NotNull Node createRecordElement(@NotNull Document document, String word, String meaning) {
        Element record = document.createElement("record");
        record.appendChild(createRecordElements(document, "word", word));
        record.appendChild(createRecordElements(document, "meaning", meaning));
        return record;
    }

    private @NotNull Node createRecordElements(@NotNull Document document, String name, String value) {
        Element childNode = document.createElement(name);
        childNode.appendChild(document.createTextNode(value));
        return childNode;
    }
}
