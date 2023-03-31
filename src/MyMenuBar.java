import models.Favorites;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MyMenuBar extends JMenuBar implements IObserver, ActionListener{
    JMenuItem favItem;
    JMenuItem evA_Z;
    JMenuItem evZ_A;
    JMenuItem veA_Z;
    JMenuItem veZ_A;
    private final Comparator<String> stringComparator;
    public MyMenuBar() {
        stringComparator = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {

                String s1 = unicodeToASCII(o1);
                String s2 = unicodeToASCII(o2);
                return s1.compareTo(s2);
            }

            public @NotNull String unicodeToASCII(String s) {
                String s1 = Normalizer.normalize(s, Normalizer.Form.NFKD);
                String regex =
                        "[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+";
                String s2;
                s2 = new String(s1.replaceAll(regex, "").getBytes(StandardCharsets.US_ASCII),
                        StandardCharsets.US_ASCII);
                return s2;
            }
        };

        JMenu editMenu = new JMenu("Edit");
        JMenu favMenu = new JMenu("Favourite");
        JMenu statisticsMenu = new JMenu("Statistics");

        //
        JMenu addMenu = new JMenu("Add");

        JMenuItem addToEngVietItem = new JMenuItem("Add To Eng-Vie Dictionary");
        addToEngVietItem.addActionListener(e -> {
            NewWordFrame frame = new NewWordFrame(NewWordFrame.ENG_VIE);
            frame.pack();
            frame.setVisible(true);
        });
        JMenuItem addToVieEngItem = new JMenuItem("Add To Vie-Eng Dictionary");
        addToVieEngItem.addActionListener(e -> {
            NewWordFrame frame = new NewWordFrame(NewWordFrame.VIE_ENG);
            frame.pack();
            frame.setVisible(true);
        });

        favItem = new JMenuItem("Add To Favourite List");
        favItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = favItem.getText();

                if(Main.getCurrentWord() == null){
                    return;
                }

                if(text.equals("Add To Favourite List")){
                    if(Main.getCurrentDict() == Main.ENG_VIE){
                        Favorites.getInstance().getEngFavList().add(Main.getCurrentWord());
                    }
                    else{
                        Favorites.getInstance().getVieFavList().add(Main.getCurrentWord());
                    }
                    favItem.setText("Remove To Favourite List");
                }
                else if(text.equals("Remove To Favourite List")){
                    if(Main.getCurrentDict() == Main.ENG_VIE){
                        Favorites.getInstance().getEngFavList().remove(Main.getCurrentWord());
                    }
                    else{
                        Favorites.getInstance().getVieFavList().remove(Main.getCurrentWord());
                    }
                    favItem.setText("Add To Favourite List");
                }
            }
        });

//        addToFavItem.setText("Remove From Favourite List");
        JMenu engVieMenu = new JMenu("Eng-Viet Favorite List");
        evA_Z = new JMenuItem("A to Z");
        evA_Z.addActionListener(this);
        evZ_A = new JMenuItem("Z to A");
        evZ_A.addActionListener(this);

        JMenuItem vietEngMenu = new JMenu("Vie-Eng Favorite List");
        veA_Z = new JMenuItem("A to Z");
        veA_Z.addActionListener(this);
        veZ_A = new JMenuItem("Z to A");
        veZ_A.addActionListener(this);

        JMenuItem statisticsItem = new JMenuItem("Statistics");
        statisticsItem.addActionListener(e -> {
            SelectDateFrame frame = new SelectDateFrame();
            frame.pack();
            frame.setVisible(true);
        });

        addMenu.add(addToEngVietItem);
        addMenu.add(addToVieEngItem);

        favMenu.add(favItem);
        favMenu.add(engVieMenu);
        favMenu.add(vietEngMenu);
        engVieMenu.add(evA_Z);
        engVieMenu.add(evZ_A);
        vietEngMenu.add(veA_Z);
        vietEngMenu.add(veZ_A);

        editMenu.add(addMenu);

        statisticsMenu.add(statisticsItem);

        this.add(editMenu);
        this.add(favMenu);
        this.add(statisticsMenu);
    }

    @Override
    public void updateFavText(int type) {
        if(type == REMOVE){
            favItem.setText("Remove From Favourite List");
        }
        if(type == ADD){
            favItem.setText("Add To Favourite List");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        List<String> favList = null;
        if(e.getSource() == veA_Z || e.getSource() == veZ_A){
            favList = Favorites.getInstance().getVieFavList();
            favList.sort(stringComparator);
            if(e.getSource() == veZ_A){
                Collections.reverse(favList);
            }
        }
        else if(e.getSource() == evA_Z || e.getSource() == evZ_A){
            favList = Favorites.getInstance().getEngFavList();
            favList.sort(stringComparator);
            if(e.getSource() == evZ_A){
                Collections.reverse(favList);
            }
        }
        else{
            favList = new ArrayList<>();
        }
        FavListFrame favListFrame = new FavListFrame(favList);
        favListFrame.pack();
        favListFrame.setLocationRelativeTo(null);
        favListFrame.setVisible(true);
    }

    private static class FavListFrame extends JFrame{

        private JList<String> jList;
        public FavListFrame(@NotNull List<String> favList) {
            super();
            String[] a = favList.toArray(new String[0]);
            for (String i: a){
                System.out.println(i);
            }
            jList = new JList<>(favList.toArray(new String[0]));
            jList.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if(SwingUtilities.isRightMouseButton(e)){
                        String word = jList.getSelectedValue();
                        FavPopupMenu popupMenu = new FavPopupMenu(new FavPopupMenu.IEditFavList() {
                            @Override
                            public boolean remove() {
                                boolean res = favList.remove(word);
                                jList.setListData(favList.toArray(new String[0]));
                                jList.updateUI();
                                return res;
                            }
                        });
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            });
            JScrollPane jScrollPane = new JScrollPane(jList);
            this.add(jScrollPane);
        }

        private class FavPopupMenu extends JPopupMenu{
            interface IEditFavList{
                boolean remove();
            }
            public FavPopupMenu(IEditFavList editFavList) {
                JMenuItem removeItem = new JMenuItem("Remove");
                removeItem.addActionListener(e -> {
                    editFavList.remove();
                });
                this.add(removeItem);
            }
        }
    }
}
