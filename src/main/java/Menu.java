import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class Menu extends JPanel {
    public static final int DELAY_TIME = 4000, BUTTON_WIDTH = 150, BUTTON_HEIGHT = 60, FONT_SIZE = 20, BUTTON_Y = 100;
    public static final String IMAGE_NAME = "THE TABLE DATA.png", ONE_WEB = "https://www.one.co.il/";
    private ImageIcon backGround;
    private ArrayList<JButton> leaguesNameButtons;
    private ArrayList<Element> leaguesElement;
    private ArrayList<String> leagueName;
    private JComboBox<Integer> ranking;
    private JButton chooseRank;
    private JButton returnButton;
    private JLabel resultLabel;
    private String websiteUrl;


    public Menu(int x, int y, int width, int height) {
        this.websiteUrl = ONE_WEB;
        leaguesNameButtons = new ArrayList<>();
        initialLeague();
        this.setBounds(x, y, width, height);
        this.setLayout(null);
        this.backGround = new ImageIcon(IMAGE_NAME);
        for (int i = 0; i < this.leagueName.size(); i++) {
            JButton button = addButton(this.leagueName.get(i), this.getWidth() / 2 - BUTTON_WIDTH / 2, (i + 1) * BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT);
            leaguesButtonListener(button, i);
            this.leaguesNameButtons.add(button);

        }
        this.setDoubleBuffered(true);
        this.setVisible(true);
    }

    private void initialLeague() {
        try {
            this.leaguesElement = getLeagues();
            this.leagueName = getLeaguesName(this.leaguesElement);
        } catch (IOException e) {
            initialLeague();
        }

    }


    private void leaguesButtonListener(JButton button, int leagueIndex) {
        button.addActionListener((event) -> {
            try {
                unVisibleButton();
                comboBox(leagueIndex);
                this.chooseRank = addButton("Ranking", this.getWidth() / 2 - BUTTON_WIDTH / 2, 200, BUTTON_WIDTH, BUTTON_HEIGHT);
                chooseRankListener(leagueIndex);
                this.returnButton = addButton("return", 750, 550, 100, 65);
                repaint();
                this.returnButton.addActionListener((event2) -> {
                    restart();
                    deleteButton();
                });
            } catch (IOException e) {
                System.out.println("l");
                leaguesButtonListener(button, leagueIndex);
            }

        });
    }

    private void chooseRankListener(int leagueIndex) {
        this.chooseRank.addActionListener((event1) -> {
            new Thread(() -> {
                deleteButton();
            }).start();
            new Thread(() -> {
                try {
                    progress(this.ranking.getSelectedIndex(), leagueIndex);
                } catch (Exception e) {
                    System.out.println("p");
                    progress(this.ranking.getSelectedIndex(), leagueIndex);
                }
            }).start();


        });
    }

    private void comboBox(int leagueIndex) throws IOException {
        int leagueSize = 0;
        leagueSize = getLeagueSize(leagueIndex);
        this.ranking = new JComboBox<>(teamsInLeague(leagueSize));
        this.ranking.setBounds(200, 200, 50, BUTTON_HEIGHT);
        this.add(this.ranking);

    }

    private void unVisibleButton() {
        for (int i = 0; i < this.leaguesNameButtons.size(); i++) {
            this.leaguesNameButtons.get(i).setVisible(false);
        }
    }

    private JButton addButton(String buttonText, int x, int y, int width, int height) {
        JButton button = new JButton(buttonText);
        Font font = new Font("Ariel", Font.BOLD, FONT_SIZE);
        button.setBounds(x, y, width, height);
        button.setVisible(true);
        button.setFont(font);
        this.add(button);
        return button;
    }

    private ArrayList<Element> getLeagues() throws IOException {
        Document website = Jsoup.connect(this.websiteUrl).get();
        ArrayList<Element> allLeagues = website.getElementsByClass("one-navigation-right-container");
        Element leagues = allLeagues.get(0);
        ArrayList<Element> relevantLeagues = new ArrayList<>();
        int i = 0;
        while (!leagues.child(i).text().equals("כדורגל עולמי")) {
            i++;
        }
        while (!leagues.child(i).text().equals("ליגה בלגית")) {
            if (ignore(leagues.child(i).text())) {
                relevantLeagues.add(leagues.child(i));
            }
            i++;
        }
        return relevantLeagues;

    }

    private boolean ignore(String leagueName) {
        return (!leagueName.equals("חדשות") && !leagueName.equals("ליגת האלופות")
                && !leagueName.equals("הליגה האירופית") && !leagueName.equals("קונפרנס ליג")
                && !leagueName.equals("כדורגל עולמי")) && !leagueName.equals("");
    }

    private ArrayList<String> getLeaguesName(ArrayList<Element> leagues) throws IOException {
        ArrayList<String> leaguesName = new ArrayList<>();
        for (int i = 0; i < leagues.size(); i++) {
            leaguesName.add(leagues.get(i).text());
        }
        return leaguesName;
    }


    private int getLeagueSize(int leagueIndex) throws IOException {
        int size = 0;
        Element table = getTable(leagueIndex);
        size = table.childNodeSize();
        return size;
    }

    private Document getLeagueUrl(int leagueIndex) {
        String leagueLink = this.leaguesElement.get(leagueIndex).attr("href");
        Document leagueUrl = null;
        try {
            leagueUrl = Jsoup.connect(this.websiteUrl + leagueLink).get();

        } catch (IOException e) {
            System.out.println("URl");
            leagueUrl=getLeagueUrl(leagueIndex);
        }

        return leagueUrl;
    }

    private Element tableSearch(Document leagueUrl) {
        ArrayList<Element> allTables = leagueUrl.getElementsByClass("common-table league-table");
        ArrayList<Element> table = allTables.get(0).getElementsByClass("table");
        Element teams = table.get(0).child(1);
        return teams;
    }

    private Integer[] teamsInLeague(Integer numOfTeams) {
        Integer[] teams = new Integer[numOfTeams];
        int rank = 1;
        for (int i = 0; i < teams.length; i++) {
            teams[i] = rank;
            rank++;

        }
        return teams;
    }

    private void progress(int rank, int leagueIndex) {
        Element table = null;
        table = getTable(leagueIndex);
        Element team = table.child(rank);
        String teamName = team.getElementsByClass("teamname").text();
        String teamPoints = team.getElementsByClass("points").text();
        System.out.println("  שם הקבוצה: " + teamName + " ניקוד: " + " " + teamPoints);
        this.resultLabel = addLabel(teamName + " " + teamPoints + " " + "נקודות", 200, 90, 600, 200);

        repaint();
        try {
            Thread.sleep(DELAY_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        restart();
    }

    private Element getTable(int leagueIndex) {
        Document leagueUrl = getLeagueUrl(leagueIndex);
        Element table = tableSearch(leagueUrl);
        return table;
    }

    private void restart() {
        for (int i = 0; i < this.leaguesNameButtons.size(); i++) {
            this.leaguesNameButtons.get(i).setVisible(true);
        }
        if (this.resultLabel != null) {
            this.resultLabel.setVisible(false);
        }


    }

    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        graphics.drawImage(this.backGround.getImage(), this.getX(), this.getY(), this.getWidth(), this.getHeight(), null);
    }

    private void deleteButton() {
        this.chooseRank.setVisible(false);
        this.ranking.setVisible(false);
        this.returnButton.setVisible(false);
    }

    public JLabel addLabel(String labelText, int x, int y, int width, int height) {
        JLabel label = new JLabel(labelText);
        label.setForeground(Color.black);
        Font font = new Font("Ariel", Font.BOLD, 50);
        label.setFont(font);
        label.setBounds(x, y, width, height);
        this.add(label);
        return label;

    }

}