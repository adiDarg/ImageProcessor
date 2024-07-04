import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {
    private int width = 500;
    private int height = 300;
    public Window(){
        DisplayImage displayImage = new DisplayImage(width, height);
        this.add(displayImage);
        this.setSize(width, (int) (height *1.5));
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setResizable(false);
        this.setVisible(true);
        while (!displayImage.isImageReady()){
            Thread.onSpinWait();
        }
        try {
            displayImage.getImageThread().join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        width = displayImage.getWidth();
        height = displayImage.getHeight();
        FilterSelectionPanel filterSelectionPanel = new FilterSelectionPanel(displayImage.getX(),
                displayImage.getY() + displayImage.getHeight(), displayImage.getWidth(),
                displayImage.getHeight(),displayImage);

        this.add(filterSelectionPanel);

        new Thread(() -> {
            while (true){
                displayImage.repaint();
            }
        }).start();

        this.setLocationRelativeTo(null);
        Dimension screenSize = getToolkit().getScreenSize();
        this.setBounds(screenSize.width/2 - width/2,0,width,filterSelectionPanel.getHeight());
        this.setVisible(true);
    }
}
