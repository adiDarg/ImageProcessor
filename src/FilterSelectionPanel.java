import javax.swing.*;

public class FilterSelectionPanel extends JPanel {
    private int buttonCount;
    private final static int BUTTONS_PER_ROW = 5;
    private final static int BUTTONS_PER_COLUMN = 3;
    private final int x;
    private final int y;

    public FilterSelectionPanel(int x,int y, int width, int height, DisplayImage displayImage) {
        this.x = x;
        this.y = y;
        setBounds(x, y, width, height);
        buttonCount = 0;

        JButton resetImage = createFilterButton("Reset Image");
        resetImage.addActionListener((e)-> displayImage.hardReset());
        add(resetImage);

        JButton blackAndWhite = createFilterButton("Black And White");
        blackAndWhite.addActionListener((e)-> {
            displayImage.resetImage();
            displayImage.blackAndWhite();
        });
        add(blackAndWhite);

        JButton grayScale = createFilterButton("Gray Scale");
        grayScale.addActionListener((e)-> {
            displayImage.resetImage();
            displayImage.grayscale();
        });
        add(grayScale);

        JButton negative = createFilterButton("Negative");
        negative.addActionListener((e)-> {
            displayImage.resetImage();
            displayImage.negative();
        });
        add(negative);

        JButton tint = createFilterButton("Tint");
        tint.addActionListener((e)-> {
            displayImage.resetImage();
            displayImage.tint();
        });
        add(tint);

        JButton drawBorders = createFilterButton("Draw Borders");
        drawBorders.addActionListener((e)-> {
            displayImage.resetImage();
            displayImage.drawBorders();
        });
        add(drawBorders);

        JButton darker = createFilterButton("Darker");
        darker.addActionListener((e)-> {
            displayImage.resetImage();
            displayImage.darker();
        });
        add(darker);

        JButton colorShiftRight = createFilterButton("Color Shift Right");
        colorShiftRight.addActionListener((e)-> {
            displayImage.resetImage();
            displayImage.colorShiftRight();
        });
        add(colorShiftRight);

        JButton mirror = createFilterButton("Mirror");
        mirror.addActionListener((e)-> {
            displayImage.resetImage();
            displayImage.mirror();
        });
        add(mirror);

        JButton addNoise = createFilterButton("Add Noise");
        addNoise.addActionListener((e)-> {
            displayImage.resetImage();
            displayImage.addNoise();
        });
        add(addNoise);

        JButton sepia = createFilterButton("Sepia");
        sepia.addActionListener((e)-> {
            displayImage.resetImage();
            displayImage.sepia();
        });
        add(sepia);

        JButton vintage = createFilterButton("Vintage");
        vintage.addActionListener((e)-> {
            displayImage.resetImage();
            displayImage.vintage();
        });
        add(vintage);

        this.setVisible(true);
        this.setFocusable(true);
    }
    public JButton createFilterButton(String filterName){
        JButton jButton = new JButton();
        int x = this.x + getWidth() * (buttonCount % BUTTONS_PER_ROW + 1) / BUTTONS_PER_ROW;
        int y = this.y + (getHeight() * (buttonCount % BUTTONS_PER_COLUMN + 1) / BUTTONS_PER_COLUMN);
        int width = getWidth() / BUTTONS_PER_ROW;
        int height = getHeight() /BUTTONS_PER_COLUMN;
        buttonCount++;
        jButton.setText(filterName);
        jButton.setBounds(x,y,width,height);
        if (jButton.getY() >= this.getHeight() + this.getY()){
            this.setBounds(this.getX(),this.getY(),this.getWidth(),this.getHeight() + jButton.getHeight());
        }
        return jButton;
    }
}
