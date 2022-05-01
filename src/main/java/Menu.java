import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.swing.*;
import java.awt.*;
import java.awt.Window;
import java.io.IOException;
import java.util.ArrayList;

public class Menu extends JPanel {
    public static final int INITIAL_ARRAY_CAPACITY = 5, BUTTON_WIDTH = 150, BUTTON_HEIGHT = 60, FONT_SIZE = 20, BUTTON_Y = 100;
    public static final String LA_LIGA = "512", PREMIER_LIG = "1383", FRENCH_LIG = "29242", BUNDESLIGA = "19796", SERIA_A = "4909", ONE_WEB = "https://www.one.co.il/";
    private ImageIcon backGround;
    private ArrayList<JButton> leaguesNameButtons = new ArrayList<>();
    private JButton chooseLocation;
    private JComboBox<Integer> ranking;
    private String websiteUrl;
    private String[] leagueName;
    private String[] leagueSerial;

    public Menu(int x, int y, int width, int height) {

        this.leagueName = new String[]{"ליגה ספרדית", "ליגה אנגלית", "ליגה צרפתית", "ליגה גרמנית", "ליגת איטלקית"};
        this.leagueSerial = new String[]{LA_LIGA, PREMIER_LIG, FRENCH_LIG, BUNDESLIGA, SERIA_A};
        this.websiteUrl = ONE_WEB;
        this.setBounds(x, y, width, height);
        this.setLayout(null);
        this.backGround = new ImageIcon("THE TABLE DATA.png");
//        this.setBackground(Color.blue);
        for (int i = 0; i < INITIAL_ARRAY_CAPACITY; i++) {
            JButton button = addButton(this.leagueName[i], this.getWidth() / 2 - BUTTON_WIDTH / 2, (i + 1) * BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT);
            leaguesButtonListener(button, this.leagueSerial[i]);
            this.leaguesNameButtons.add(button);
        }
        this.setDoubleBuffered(true);
        this.setVisible(true);
    }

    private void leaguesButtonListener(JButton button, String leagueSerial) {
        button.addActionListener((event) -> {
            unVisibleButton();
            comboBox(leagueSerial);
            this.chooseLocation = addButton("Ranking", this.getWidth() / 2 - BUTTON_WIDTH / 2, 200, BUTTON_WIDTH, BUTTON_HEIGHT);
            this.chooseLocation.addActionListener((event1) -> {
                progress(this.ranking.getSelectedIndex(), leagueSerial);
            });

        });
    }

    private void comboBox(String leagueSerial) {
        int leagueSize = 0;

        try {
            leagueSize = getLeagueSize(leagueSerial);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.ranking = new JComboBox<Integer>(teamsInLeague(leagueSize));
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

    private int getLeagueSize(String leagueSerial) throws IOException {
        int size = 0;
        Element table=getTable(leagueSerial);
        size = table.childNodeSize();
        return size;
    }

    private Document leagueSearch(String leagueSerial) throws IOException {
        Document website = Jsoup.connect(this.websiteUrl).get();
        ArrayList<Element> leagueList = website.getElementsByClass("one-navigation-right-item one-navigation-right-item-" + leagueSerial);
        String leagueLink = leagueList.get(0).attr("href");
        Document leagueUrl = Jsoup.connect(this.websiteUrl + leagueLink).get();
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
        int location = 1;
        for (int i = 0; i < teams.length; i++) {
            teams[i] = location;
            location++;

        }
        return teams;
    }

    private void progress(int rank, String leagueSerial) {
        try {
            Element table=getTable(leagueSerial);
            Element team = table.child(rank);
            String teamName = team.getElementsByClass("teamname").text();
            String teamPoints = team.getElementsByClass("points").text();
           System.out.println("  שם הקבוצה: "+teamName +"ניקוד : "+ "  " + teamPoints);
//            JLabel resultReview=new JLabel();
//            resultReview.setText("teamName"+"  "+"teamPoints");
//            Font fontResult = new Font("Ariel", Font.BOLD, 50);
//            resultReview.setBounds(100,100,200  ,100);
//            resultReview.setFont(fontResult);
//           this.add(resultReview);
            Thread.sleep(10000);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        restart();
    }
    private Element getTable(String leagueSerial) throws IOException {
        Document leagueUrl = leagueSearch(leagueSerial);
        Element table = tableSearch(leagueUrl);
        return table;

    }

    private void restart() {
        for (int i = 0; i < this.leaguesNameButtons.size(); i++) {
            this.leaguesNameButtons.get(i).setVisible(true);
        }
        this.chooseLocation.setVisible(false);
        this.ranking.setVisible(false);
    }

    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        graphics.drawImage(this.backGround.getImage(), this.getX(), this.getY(), this.getWidth(), this.getHeight(), null);
    }

}