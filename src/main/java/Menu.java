import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class Menu extends JPanel {
    public static final int DELAY_TIME = 4000,
    WIDTH_CHOOSE_BUTTON = 150,
    HEIGHT_CHOOSE_BUTTON = 60, FONT_SIZE = 20,
            Y_CHOOSE_BUTTON = 300;
    public static final String IMAGE_NAME = "01 THE TABLE DATA.png", ONE_WEB = "https://www.one.co.il/";
    public static final int X_RETURN_BUTTON = 750;
    public static final int Y_RETURN_BUTTON = 550;
    public static final int WIDTH_RETURN_BUTTON = 100;
    public static final int HEIGHT_RETURN_BUTTON = 65;
    public static final int X_COMBO_BOX = 200;
    public static final int Y_COMBO_BOX = 300;
    public static final int WIDTH_COMBO_BOX = 50;
    public static final int HEIGHT_COMBO_BOX = 60;
    public static final int X_RESULT_LABEL = 200;
    public static final int Y_RESULT_LABEL = 250;
    public static final int WIDTH_RESULT_LABEL = 500;
    public static final int HEIGHT_RESULT_LABEL = 200;
    public static final int SIZE_FONT_LABEL = 40;
    public static final int Y_RANDOM_BUTTON = 100;
    public static final String KIND_OF_FONT = "Ariel";
    public static final int WIDTH_RANDOM_BUTTON = 150;
    public static final int HEIGHT_RANDOM_BUTTON = 60;
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
            JButton button = addButton(this.leagueName.get(i), this.getWidth() / 2 - WIDTH_RANDOM_BUTTON / 2, (i + 1) * Y_RANDOM_BUTTON, WIDTH_RANDOM_BUTTON, HEIGHT_RANDOM_BUTTON);
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
                this.chooseRank = addButton("Ranking", this.getWidth() / 2 - WIDTH_CHOOSE_BUTTON / 2, Y_CHOOSE_BUTTON, WIDTH_CHOOSE_BUTTON, HEIGHT_CHOOSE_BUTTON);
                chooseRankListener(leagueIndex);
                this.returnButton = addButton("return", X_RETURN_BUTTON, Y_RETURN_BUTTON, WIDTH_RETURN_BUTTON, HEIGHT_RETURN_BUTTON);
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
        this.ranking.setBounds(X_COMBO_BOX, Y_COMBO_BOX, WIDTH_COMBO_BOX, HEIGHT_COMBO_BOX);
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
            System.out.println("URL");
            leagueUrl = getLeagueUrl(leagueIndex);
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
        this.resultLabel = addLabel(teamName + " " + teamPoints + " " + "נקודות", X_RESULT_LABEL, Y_RESULT_LABEL, WIDTH_RESULT_LABEL, HEIGHT_RESULT_LABEL);

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
        Font font = new Font(KIND_OF_FONT, Font.BOLD, SIZE_FONT_LABEL);
        label.setFont(font);
        label.setBounds(x, y, width, height);
        this.add(label);
        return label;

    }

}