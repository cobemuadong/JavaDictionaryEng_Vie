import models.Favorites;
import models.Records;
import models.SearchHistory;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class Main {

    public static final int ENG_VIE = 0;
    public static final int VIE_ENG = 1;
    private static String currentWord;
    private static int currentDict;

    public static int getCurrentDict() {
        return currentDict;
    }

    public static String getCurrentWord() {
        return currentWord;
    }

    public static void setCurrentWord(String currentWord) {
        Main.currentWord = currentWord;
    }
    public static void setCurrentDict(int currentDict) {
        Main.currentDict = currentDict;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
        {
            currentDict = ENG_VIE;

            try {
                Records.getInstance().parsingEngVieXML("xml/Anh_Viet.xml");
                Records.getInstance().parsingVieEngXML("xml/Viet_Anh.xml");
            } catch (ParserConfigurationException | IOException | SAXException e) {
                throw new RuntimeException(e);
            }

            try {
                Favorites.getInstance().parseFavoriteList("json/favorites.json");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                SearchHistory.getInstance().parseJson("json/search_history.json");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            createShowGUI();
        });
    }

    private static void createShowGUI(){
        JFrame jFrame = new JFrame();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    Favorites.getInstance().saveFavoriteList(Favorites.getInstance(),"json/favorites.json");
                    Records.getInstance().saveRecords("xml/Anh_Viet.xml","xml/Viet_Anh.xml");
                    SearchHistory.getInstance().saveHistory("json/search_history.json");
                } catch (IOException | ParserConfigurationException | TransformerException ex) {
                    throw new RuntimeException(ex);
                }
                super.windowClosing(e);
            }
        });

        //Create window and set layout
//        Container container = jFrame.getContentPane();
//        container.setLayout(new BorderLayout(20, 15));

        MyMenuBar myMenuBar = new MyMenuBar();
        MainPanel mainPanel = new MainPanel();
        mainPanel.attach(myMenuBar);

        jFrame.setPreferredSize(new Dimension(600,600));

        // initialize interface component
        jFrame.setJMenuBar(myMenuBar);
        jFrame.add(mainPanel);

        //Display window
        jFrame.pack();
        jFrame.setVisible(true);
    }
}