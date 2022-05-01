import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class Menu extends JPanel {
    public static final int INITIAL_ARRAY_CAPACITY=5,BUTTON_WIDTH=150,BUTTON_HEIGHT=60,FONT_SIZE=20,BUTTON_Y=100;

    private ArrayList <JButton> buttons=new ArrayList<>();
    private JComboBox<Integer> location;
    private String websiteUrl;
    private String[] leagueName;
    private String[] leagueSerial;

    public Menu(int x, int y, int width, int height) {
        this.leagueName=new String[]{"ליגה ספרדית","ליגה אנגלית","ליגה צרפתית","ליגה גרמנית","ליגת איטלקית"};
        this.leagueSerial= new String[]{"512","1383","29242","19796","4909"};
        this.websiteUrl="https://www.one.co.il/";
        this.setBounds(x, y, width, height);
        this.setLayout(null);
        this.setBackground(Color.blue);

        for (int i = 0; i <INITIAL_ARRAY_CAPACITY ; i++) {
            JButton button=addButton(this.leagueName[i],(i+1)*BUTTON_Y);
            buttonListener(button,this.leagueSerial[i]);
            this.buttons.add(button);
        }
        this.setDoubleBuffered(true);
        this.setVisible(true);

    }
    private void buttonListener(JButton button,String leagueSerial){
        button.addActionListener((event)->{
            unVisibleButton();
            comboBox(leagueSerial);
//            Program program=new Program(this.getX(),this.getY(),this.getWidth(),this.getHeight());
//            this.add(program);
//            repaint();


            System.out.println(leagueSerial);
        });
    }
    private void comboBox(String leagueSerial){
        int leagueSize=0;
        try {
            leagueSize=getLeagueSize(leagueSerial);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.location= new JComboBox<Integer>(teamsInLeague(leagueSize));
        this.location.setBounds(100,300,40,40);
        this.add(this.location);
    }
    private void unVisibleButton(){
        for (int i = 0; i <this.buttons.size() ; i++) {
            this.buttons.get(i).setVisible(false);
        }
    }

    private JButton addButton( String buttonText, int y) {
        JButton button = new JButton(buttonText);
        Font font=new Font("Ariel",Font.BOLD,FONT_SIZE);
        button.setBounds(this.getWidth()/2-BUTTON_WIDTH/2, y, BUTTON_WIDTH, BUTTON_HEIGHT);
        button.setVisible(true);
        button.setFont(font);
        this.add(button);
        return button;
    }
    private int getLeagueSize(String leagueSerial) throws IOException {
        int size=0;
        Document leagueUrl=leagueSearch(leagueSerial);
        Element table=tableSearch(leagueUrl);
        size=table.childNodeSize();
        return size;
    }
    private Document leagueSearch(String leagueSerial) throws IOException {
        Document website= Jsoup.connect(this.websiteUrl).get();
        ArrayList<Element> leagueList=website.getElementsByClass("one-navigation-right-item one-navigation-right-item-"+leagueSerial);
        String leagueLink=leagueList.get(0).attr("href");
        Document leagueUrl=Jsoup.connect(this.websiteUrl+leagueLink).get();
        return leagueUrl;
    }
    private Element tableSearch(Document leagueUrl){
        ArrayList<Element> allTables=leagueUrl.getElementsByClass("common-table league-table");
        ArrayList<Element> table=allTables.get(0).getElementsByClass("table");
        Element teams=table.get(0).child(1);
        return teams;
    }
    private Integer[] teamsInLeague(Integer numOfTeams){
        Integer[] teams=new Integer[numOfTeams];
        int location=1;
        for (int i = 0; i <teams.length ; i++) {
            teams[i]=location;
            location++;

        }
        return teams;
    }
}