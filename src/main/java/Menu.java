import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class Menu extends JPanel {
    public static final int DELAY_TIME = 10000,FIRST=0,SECOND=1,FIRST_RANK=1,
            WIDTH_CHOOSE_BUTTON = 150, HEIGHT_CHOOSE_BUTTON = 60,
            X_RETURN_BUTTON = 750, Y_RETURN_BUTTON = 550, WIDTH_RETURN_BUTTON = 100, HEIGHT_RETURN_BUTTON = 65,
            WIDTH_COMBO_BOX = 50, HEIGHT_COMBO_BOX = 60,
            WIDTH_RESULT_LABEL = 600, HEIGHT_RESULT_LABEL = 200,
            SIZE_FONT_LABEL = 45, FONT_SIZE_BUTTON = 20,
            Y_RANDOM_BUTTON = 100, WIDTH_RANDOM_BUTTON = 150, HEIGHT_RANDOM_BUTTON = 60;

    public static final String IMAGE_NAME = "01 THE TABLE DATA.png", ONE_WEB = "https://www.one.co.il/",
            NEWS = "חדשות", CHAMPIONS_LEAGUE = "ליגת האלופות", EUROPE_LEAGUE = "הליגה האירופית",
            CONFERENCE_LEAGUE = "קונפרנס ליג", WORLD_FOOTBALL = "כדורגל עולמי", BELGIAN_LEAGUE = "ליגה בלגית",
            KIND_OF_FONT = "Ariel", TEAM_NAME_CLASS = "teamname", TEAM_POINTS_CLASS = "points",
            LEAGUE_LINK="href",ALL_LEAGUES="one-navigation-right-container",ALL_TABLE="common-table league-table",TABLE="table";

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
        initialButton();
        this.setDoubleBuffered(true);
        this.setVisible(true);
    }
    private void initialButton() {
        for (int i = 0; i < this.leagueName.size(); i++) {
            JButton button = addButton(this.leagueName.get(i), this.getWidth() / 2 - WIDTH_RANDOM_BUTTON / 2, (i + 1) * Y_RANDOM_BUTTON, WIDTH_RANDOM_BUTTON, HEIGHT_RANDOM_BUTTON);
            leaguesButtonListener(button, i);
            this.leaguesNameButtons.add(button);
        }
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

            unVisibleButton();
            comboBox(leagueIndex);
            this.chooseRank = addButton("Ranking", this.getWidth() / 2 - WIDTH_CHOOSE_BUTTON / 2, this.getHeight()/2-HEIGHT_CHOOSE_BUTTON/2, WIDTH_CHOOSE_BUTTON, HEIGHT_CHOOSE_BUTTON);
            chooseRankListener(leagueIndex);
            this.returnButton = addButton("Return", X_RETURN_BUTTON, Y_RETURN_BUTTON, WIDTH_RETURN_BUTTON, HEIGHT_RETURN_BUTTON);
            repaint();
            returnButtonListener();
        });
    }
    private void returnButtonListener() {
        this.returnButton.addActionListener((event2) -> {
            returnButton();
            restart();
        });
    }
    private void chooseRankListener(int leagueIndex) {
        this.chooseRank.addActionListener((event1) -> {
            new Thread(() -> {
                returnButton();
                progress(this.ranking.getSelectedIndex(), leagueIndex);
            }).start();
        });
    }

    private void comboBox(int leagueIndex) {
        int leagueSize = 0;
        leagueSize = getLeagueSize(leagueIndex);
        this.ranking = new JComboBox<>(teamsInLeague(leagueSize));
        this.ranking.setBounds(this.getWidth()/3-WIDTH_COMBO_BOX/2, this.getHeight()/2-HEIGHT_COMBO_BOX/2, WIDTH_COMBO_BOX, HEIGHT_COMBO_BOX);
        this.add(this.ranking);

    }

    private ArrayList<Element> getLeagues() throws IOException {
        Document website = Jsoup.connect(this.websiteUrl).get();
        ArrayList<Element> allLeagues = website.getElementsByClass(ALL_LEAGUES);
        Element leagues = allLeagues.get(FIRST);
        ArrayList<Element> relevantLeagues = new ArrayList<>();
        int i = 0;
        while (!leagues.child(i).text().equals(WORLD_FOOTBALL)) {
            i++;
        }
        while (!leagues.child(i).text().equals(BELGIAN_LEAGUE)) {
            if (ignore(leagues.child(i).text())) {
                relevantLeagues.add(leagues.child(i));
            }
            i++;
        }
        return relevantLeagues;

    }
    private boolean ignore(String leagueName) {
        return (!leagueName.equals(NEWS) && !leagueName.equals(CHAMPIONS_LEAGUE)
                && !leagueName.equals(EUROPE_LEAGUE) && !leagueName.equals(CONFERENCE_LEAGUE)
                && !leagueName.equals(WORLD_FOOTBALL)) && !leagueName.equals("");
    }
    private ArrayList<String> getLeaguesName(ArrayList<Element> leagues) {
        ArrayList<String> leaguesName = new ArrayList<>();
        for (int i = 0; i < leagues.size(); i++) {
            leaguesName.add(leagues.get(i).text());
        }
        return leaguesName;
    }

    private int getLeagueSize(int leagueIndex) {
        int size = 0;
        Element table = getTable(leagueIndex);
        size = table.childNodeSize();
        return size;
    }
    private Document getLeagueUrl(int leagueIndex) {
        String leagueLink = this.leaguesElement.get(leagueIndex).attr(LEAGUE_LINK);
        Document leagueUrl = null;
        try {
            leagueUrl = Jsoup.connect(this.websiteUrl + leagueLink).get();

        } catch (IOException e) {
            System.out.println("Unstable internet" + "\n" + "reloading");
            leagueUrl = getLeagueUrl(leagueIndex);
        }

        return leagueUrl;
    }
    private Element tableSearch(Document leagueUrl) {
        ArrayList<Element> allTables = leagueUrl.getElementsByClass(ALL_TABLE);
        ArrayList<Element> table = allTables.get(FIRST).getElementsByClass(TABLE);
        Element teams = table.get(FIRST).child(SECOND);
        return teams;
    }
    private Integer[] teamsInLeague(Integer numOfTeams) {
        Integer[] teams = new Integer[numOfTeams];
        int rank = FIRST_RANK;
        for (int i = 0; i < teams.length; i++) {
            teams[i] = rank;
            rank++;

        }
        return teams;
    }
    private Element getTable(int leagueIndex) {
        Document leagueUrl = getLeagueUrl(leagueIndex);
        Element table = tableSearch(leagueUrl);
        return table;
    }

    private void progress(int rank, int leagueIndex) {
        Element table = null;
        table = getTable(leagueIndex);
        Element team = table.child(rank);
        String teamName = team.getElementsByClass(TEAM_NAME_CLASS).text();
        String teamPoints = team.getElementsByClass(TEAM_POINTS_CLASS).text();
        System.out.println("  שם הקבוצה: " + teamName + " ניקוד: " + " " + teamPoints);
        this.resultLabel = addLabel(teamName + " " + teamPoints + " " + "נקודות",this.getWidth()/2-WIDTH_RESULT_LABEL/2, this.getHeight()/2-HEIGHT_RESULT_LABEL/2, WIDTH_RESULT_LABEL, HEIGHT_RESULT_LABEL);
        repaint();
        try {
            Thread.sleep(DELAY_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        restart();
    }

    private void returnButton() {
        this.chooseRank.setVisible(false);
        this.ranking.setVisible(false);
        this.returnButton.setVisible(false);

    }
    private void restart() {
        for (int i = 0; i < this.leaguesNameButtons.size(); i++) {
            this.leaguesNameButtons.get(i).setVisible(true);
        }
        if (this.resultLabel != null) {
            this.resultLabel.setVisible(false);
        }


    }
    private void unVisibleButton() {
        for (int i = 0; i < this.leaguesNameButtons.size(); i++) {
            this.leaguesNameButtons.get(i).setVisible(false);
        }
    }

    private JButton addButton(String buttonText, int x, int y, int width, int height) {
        JButton button = new JButton(buttonText);
        Font font = new Font(KIND_OF_FONT, Font.BOLD, FONT_SIZE_BUTTON);
        button.setBounds(x, y, width, height);
        button.setVisible(true);
        button.setFont(font);
        this.add(button);
        return button;
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

    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        graphics.drawImage(this.backGround.getImage(), this.getX(), this.getY(), this.getWidth(), this.getHeight(), null);
    }
}