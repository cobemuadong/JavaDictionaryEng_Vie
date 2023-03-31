import models.Records;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

public class NewWordFrame extends JFrame {
    public static int ENG_VIE = 0;
    public static int VIE_ENG = 1;
    private JLabel wordLabel;
    private JLabel meaningLabel;
    private JTextField wordField;
    private JTextArea meaningArea;
    private JButton submitButton;
    private JButton cancelButton;
    private int currentState;
    public NewWordFrame(int currentState) throws HeadlessException {
        super();
        this.currentState = currentState;
//        GroupLayout groupLayout = new GroupLayout(this.getContentPane());
//        this.setLayout(groupLayout);

        wordLabel = new JLabel("Word");
        meaningLabel = new JLabel("Meanings");

        wordField = new JTextField(30);
        meaningArea = new JTextArea(10,30);

        submitButton = new JButton("Submit");
        cancelButton = new JButton("cancel");

        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(4,4,4,4);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        add(wordLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        add(meaningLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(wordField, gbc);


        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(new JScrollPane(meaningArea), gbc);

        gbc.gridy++;
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.fill = GridBagConstraints.NONE;
        add(submitButton,gbc);

        gbc.gridx = 2;
        add(cancelButton,gbc);

        submitButton.addActionListener(e -> {
            String word = wordField.getText();
            String meaning = meaningArea.getText();
            boolean result = false;
            if(currentState == ENG_VIE){
                result = Records.getInstance().addToEngVie(word, meaning);
            } else if (currentState == VIE_ENG) {
                result = Records.getInstance().addToVieEng(word, meaning);
            }
            if(result){
                JOptionPane.showMessageDialog(this, "Added Successfully", "Add New Word", JOptionPane.INFORMATION_MESSAGE);
                this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
            }
            else{
                JOptionPane.showMessageDialog(this, "Added Failed", "Add New Word", JOptionPane.WARNING_MESSAGE);
            }
        });
        cancelButton.addActionListener(e -> {
            this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        });
    }
}

/* Using Group Layout
          groupLayout.setAutoCreateGaps(true);
          groupLayout.setAutoCreateContainerGaps(true);
          groupLayout.setVerticalGroup(
                  groupLayout.createSequentialGroup()
                          .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                  .addComponent(wordLabel)
                                  .addComponent(wordField))
                          .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                  .addComponent(meaningLabel)
                                  .addComponent(meaningArea))
          );



          groupLayout.setHorizontalGroup(
                  groupLayout.createSequentialGroup()
                          .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                  .addComponent(wordLabel)
                                  .addComponent(meaningLabel))
                          .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                  .addComponent(wordField)
                                  .addComponent(meaningArea))
          );
  */
