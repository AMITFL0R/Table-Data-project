import javax.swing.*;
import java.awt.*;

public class Program extends JPanel {

    private JTextField textField;
    private JComboBox<Integer> location;

    public Program(int x, int y, int width, int height) {
        this.setLayout(null);
        this.setBounds(x,y,width,height);
        this.setBackground(Color.green);
        this.location= new JComboBox<Integer>(new Integer[20]);
        this.location.setBounds(100,300,40,40);
        this.add(this.location);
//        JComboBox<Integer> location=new JComboBox<Integer>();
//        location.setBounds(100,200,50,50);
//        location.setSelectedIndex(20);
//        location.setVisible(true);
//        this.add(location);
//        location.addActionListener((event)->{
//
//        });

        this.setDoubleBuffered(true);
        this.setVisible(true);

    }

}
