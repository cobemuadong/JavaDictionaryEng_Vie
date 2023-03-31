import models.Favorites;
import models.History;
import models.Records;
import models.SearchHistory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class MainPanel extends JPanel implements ISubject{
    private JLabel wordLabel;
    private JTextField searchField;
    private final InnerPanel innerPanel;
    private final JScrollPane scrollPane;
    List<IObserver> observers = new ArrayList<>();
    public MainPanel() {
        super(new BorderLayout());
        this.setPreferredSize(new Dimension(600,600));
        innerPanel = new InnerPanel();
//        innerPanel.setPreferredSize(new Dimension(600, 900));
        this.add(new SearchPanel(), BorderLayout.PAGE_START);
//        this.add(innerPanel, BorderLayout.CENTER);

        scrollPane = new JScrollPane(innerPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(Box.createRigidArea(new Dimension(0,8)), BorderLayout.PAGE_END);
    }

    @Override
    public void notifyAllObserver(int type) {
        for(IObserver observer: observers){
            observer.updateFavText(type);
        }
    }

    @Override
    public void attach(IObserver observer) {
        observers.add(observer);
    }

    @Override
    public void detach(IObserver observer) {
        observers.remove(observer);
    }

    private class InnerPanel extends JPanel{
        private final JPanel meaningsPanel;
        List<Component> meaningComps = new ArrayList<>();
        public InnerPanel(/*String word, String meaning*/) {
            super();
            //Set Spring layout
//            layout = new SpringLayout();
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            //2 label
            wordLabel = new JLabel();
            wordLabel.setFont(new Font(wordLabel.getFont().getFontName(), Font.BOLD, 20));
            meaningsPanel = new JPanel();

            this.add(wordLabel);
            this.add(meaningsPanel);
        }

        public void addLabel(Component component) {
            Component rigidComp = Box.createRigidArea(new Dimension(0, 4));
            meaningsPanel.add(rigidComp);
            meaningsPanel.add(component);
            this.add(component);
            meaningComps.add(component);
            meaningComps.add(rigidComp);
            this.add(rigidComp);
        }

        public void removeMeanings(){
            for(Component item: meaningComps){
                this.remove(item);
            }
            this.updateUI();
        }
    }

    private class SearchPanel extends JPanel{

        private final JButton changeLangBtn;
        private final ImageIcon engToVieIcon;
        private final ImageIcon vieToEngIcon;
        public SearchPanel() {
            super();
            searchField = new JTextField(20);
            JButton searchButton = new JButton("Search");

            JLabel engLabel = new JLabel("Anh");
            JLabel vieLabel = new JLabel("Việt");

            engToVieIcon = new ImageIcon(getClass().getResource("icons/arrow_right.png"));
            vieToEngIcon = new ImageIcon(getClass().getResource("icons/arrow_left.png"));
            changeLangBtn = new JButton(engToVieIcon);
            changeLangBtn.setPreferredSize(new Dimension(30,24));
            changeLangBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(Main.getCurrentDict() == Main.ENG_VIE){
                        changeLangBtn.setIcon(vieToEngIcon);
                        Main.setCurrentDict(Main.VIE_ENG);
                    }else{
                        changeLangBtn.setIcon(engToVieIcon);
                        Main.setCurrentDict(Main.ENG_VIE);
                    }
                }
            });
            this.add(searchField);
            this.add(searchButton);
            this.add(Box.createRigidArea(new Dimension(40,0)));
            this.add(engLabel);
            this.add(changeLangBtn);
            this.add(vieLabel);

            searchButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String currentWord = searchField.getText();
                    TreeMap<String, List<String>> records;
                    List<String> meanings;

                    int currentDict = Main.getCurrentDict();

                    if(Main.getCurrentDict() == Main.ENG_VIE){
                        records = Records.getInstance().getEngToVieDict();
                        meanings = records.get(currentWord);
                        if(Favorites.getInstance().getEngFavList().contains(currentWord)){
                            MainPanel.this.notifyAllObserver(IObserver.REMOVE);
                        }
                        else{
                            MainPanel.this.notifyAllObserver(IObserver.ADD);
                        }
                    }
                    else{
                        records = Records.getInstance().getVieToEngDict();
                        meanings = records.get(currentWord);
                        if(Favorites.getInstance().getVieFavList().contains(currentWord)){
                            MainPanel.this.notifyAllObserver(IObserver.REMOVE);
                        }
                        else{
                            MainPanel.this.notifyAllObserver(IObserver.ADD);
                        }
                    }

                    if(meanings == null){
                        JOptionPane.showMessageDialog(null, "Not found", "Warnings", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    //search text is exist in database
                    wordLabel.setText(currentWord);
                    Main.setCurrentWord(currentWord);
                    SearchHistory.getInstance().addHistory(new History(currentWord, new Date()));

                    int height = 0;

                    innerPanel.removeMeanings();
                    for (int i = meanings.size() - 1; i >= 0; i--) {
                        String item = meanings.get(i);
                        JTextArea jTextArea = new JTextArea();
                        jTextArea.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                if(SwingUtilities.isRightMouseButton(e)){
                                    MeaningPopUpMenu popUpMenu = new MeaningPopUpMenu(() -> {
                                        boolean res = false;
                                        if(currentDict == Main.ENG_VIE){
                                            res = Records.getInstance().removeFromEngVie(currentWord, item);
                                        }
                                        if(currentDict == Main.VIE_ENG){
                                            res = Records.getInstance().removeFromVieEng(currentWord, item);
                                        }
                                        return res;
                                    });
                                    popUpMenu.show(e.getComponent(), e.getX(), e.getY());
                                }
                            }
                        });
                        jTextArea.setText(item);
                        jTextArea.append("\n");
                        jTextArea.setCaretPosition(0);
                        jTextArea.setEditable(false);
                        innerPanel.addLabel(jTextArea);
                        height += jTextArea.getPreferredSize().height;
                    }
                    innerPanel.setPreferredSize(null);
                    innerPanel.setPreferredSize(new Dimension(600, height));

                    scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMinimum());

                }
            });
        }

        private class MeaningPopUpMenu extends JPopupMenu{

            interface RemoveAnItem{
                boolean remove();
            }
            JMenuItem remove;
            public MeaningPopUpMenu(RemoveAnItem removeAnItem) {
                remove = new JMenuItem("Remove");
                add(remove);
                remove.addActionListener(e -> {
                    if(removeAnItem.remove()){
                        System.out.println("Remove An Item: " + "successful");
                        JOptionPane.showMessageDialog(this, "Successful to Remove a meaning");
                    }
                    else{
                        System.out.println("Remove An Item: " + "failed");
                        JOptionPane.showMessageDialog(this, "Failed to Remove a meaning");
                    }
                });
            }
        }

//        private @NotNull String formatString(@NotNull String longString){
//            StringBuilder result = new StringBuilder();
//            String[] lines = longString.split("\r\n|\n|\r");
//            for (String line : lines) {
//                result.append(line);
//                result.append("<br>");
//            }
//            return result.toString();
//        }
    }
}
