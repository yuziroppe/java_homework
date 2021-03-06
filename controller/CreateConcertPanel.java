package controller;

import components.ColleagueButton;
import components.ColleagueTextField;
import lib.GUILibrary;
import model.UserReader;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class CreateConcertPanel extends JPanel implements ReloadPanel, Mediator {
    public static int ALL_PANEL_WIDTH = 400;
    public static int WIDTH = 300;
    public static int PANEL_HEIGHT = 600;

    private ColleagueTextField textConcertTitle;
    private ColleagueTextField textConcertGenre;
    private ColleagueTextField textConcertDay;
    private ColleagueTextField textConcertPlace;
    private ColleagueTextField textConcertFee;
    private ColleagueTextField textConcertCapacity;
    private ColleagueButton buttonOk;
    private ColleagueButton buttonBack;
    private final MainFrame mainFrame;

    public CreateConcertPanel(MainFrame mf, String title) {
        this.mainFrame = mf;
        this.setName(title);

        this.setSize(ALL_PANEL_WIDTH, PANEL_HEIGHT);

        this.setLayout(new FlowLayout());

        this.createColleagues();

        //JPanel for Main Message
        JPanel spacePanel = new JPanel();
        spacePanel.setLayout(new GridLayout(1, 1));
        spacePanel.setPreferredSize(new Dimension(WIDTH, 100));
        JLabel subject = new JLabel(title.substring(0, 1).toUpperCase() + title.substring(1).toLowerCase());
        subject.setHorizontalAlignment(JLabel.CENTER);
        subject.setFont(new Font("Arial", Font.PLAIN, 30));
        spacePanel.add(subject);

        // JPanel for Input
        JPanel inputPanel = new JPanel();
        inputPanel.setPreferredSize(new Dimension(WIDTH, 100));

        GridLayout inputLayout = new GridLayout(3, 2);
        inputLayout.setHgap(10);
        inputPanel.setLayout(inputLayout);

        inputPanel.add(new JLabel("Title:"));
        inputPanel.add(textConcertTitle);
        inputPanel.add(new JLabel("Genre:"));
        inputPanel.add(textConcertGenre);
        inputPanel.add(new JLabel("Day"));
        inputPanel.add(textConcertDay);
        inputPanel.add(new JLabel("Place"));
        inputPanel.add(textConcertPlace);
        inputPanel.add(new JLabel("Fee"));
        inputPanel.add(textConcertFee);
        inputPanel.add(new JLabel("Capacity"));
        inputPanel.add(textConcertCapacity);


        // JPanel for Buttons
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setPreferredSize(new Dimension(WIDTH, 100));

        GridLayout buttonLayout = new GridLayout(0, 1);
        buttonLayout.setVgap(20);
        buttonsPanel.setLayout(buttonLayout);
        buttonsPanel.add(buttonOk);
        buttonsPanel.add(buttonBack);

        this.add(spacePanel);
        this.add(GUILibrary.getHr(WIDTH, 0));
        this.add(inputPanel);
        this.add(GUILibrary.getHr(WIDTH, 0));
        this.add(buttonsPanel);
    }

    public void reload() {
        textConcertTitle.setText("");
        textConcertGenre.setText("");
        textConcertDay.setText("");
        textConcertPlace.setText("");
        textConcertFee.setText("");
        textConcertCapacity.setText("");
    }

    @Override
    public void createColleagues() {
        textConcertTitle = new ColleagueTextField("", 10);
        textConcertGenre = new ColleagueTextField("", 10);
        textConcertDay = new ColleagueTextField("", 10);
        textConcertPlace = new ColleagueTextField("", 10);
        textConcertFee = new ColleagueTextField("", 10);
        textConcertCapacity = new ColleagueTextField("", 10);

        buttonOk = new ColleagueButton("Register");
        buttonBack = new ColleagueButton("Back");

        buttonOk.setMediator(this);
        buttonBack.setMediator(this);

        buttonOk.addActionListener(buttonOk);
        buttonBack.addActionListener(buttonBack);
    }

    @Override
    public void colleagueChanged() {
        if (this.buttonOk.nowAction()) {
            ArrayList<String> newConcert = new ArrayList<>();

            if (textConcertTitle.getText().equals("")
                    || textConcertGenre.getText().equals("")
                    || textConcertDay.getText().equals("")
                    || textConcertPlace.getText().equals("")
                    || textConcertFee.getText().equals("")
                    || textConcertCapacity.getText().equals("")
            ) {
                JOptionPane.showMessageDialog(this, "Please input more than one character!!", "error", JOptionPane.ERROR_MESSAGE);

                this.mainFrame.setNextPanelName(MainFrame.CreateConcertPanelName);
            } else {
                newConcert.add(textConcertTitle.getText());
                newConcert.add(textConcertGenre.getText());
                newConcert.add(textConcertDay.getText());
                newConcert.add(textConcertPlace.getText());
                newConcert.add(textConcertFee.getText());
                newConcert.add(textConcertCapacity.getText());

                UserReader.makeConcert(newConcert);

                JOptionPane.showMessageDialog(this, "Created!", "info", JOptionPane.INFORMATION_MESSAGE);

                this.mainFrame.setNextPanelName(MainFrame.AdminPanelName);
            }
        } else if (this.buttonBack.nowAction()) {
            this.mainFrame.setNextPanelName(MainFrame.AdminPanelName);
        }

        this.mainFrame.colleagueChanged();
    }
}
