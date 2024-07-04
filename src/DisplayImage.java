import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;
import java.util.Random;

public class DisplayImage extends JPanel {
    private volatile BufferedImage bufferedImage;
    private final Line line;
    private int width;
    private int height;
    private Raster originalData;
    private boolean isClicked;
    private volatile boolean imageReady;
    private int appliedFilter;
    private final Thread imageThread;
    private static final int BLACK_AND_WHITE = 1;
    private static final int GRAYSCALE = 2;
    private static final int NEGATIVE = 3;
    private static final int TINT = 4;
    private static final int DRAW_BORDERS = 5;
    private static final int DARKER = 6;
    private static final int COLOR_SHIFT_RIGHT = 7;
    private static final int MIRROR = 8;
    private static final int ADD_NOISE = 9;
    private static final int SEPIA = 10;
    private static final int VINTAGE = 11;

    public DisplayImage(int width, int height){
        JButton uploadButton = new JButton("Upload Image");
        uploadButton.setBounds(3 * width/8,height,width/4,height/4);
        uploadButton.addActionListener(e -> openFileChooser());
        this.add(uploadButton, BorderLayout.NORTH);
        imageReady = false;
        imageThread = new Thread(() -> {
            while (!imageReady) {
                Thread.onSpinWait();
            }
            originalData = bufferedImage.getData();
            this.remove(uploadButton);
            this.width = bufferedImage.getWidth();
            this.height = bufferedImage.getHeight();
            this.setBounds(0,0,this.width,this.height);
        });
        imageThread.start();
        isClicked = false;

        line = new Line(new MyPoint(0,0), new MyPoint(0,0));
        this.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isClicked){
                    line.setCoordinates(Math.max(e.getX(),0), 0, Math.max(e.getX(),0), bufferedImage.getHeight());
                    reapplyFilters();
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }
        });
        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                isClicked = true;
                line.setCoordinates(Math.max(e.getX(),0), 0, Math.max(e.getX(),0), bufferedImage.getHeight());
                reapplyFilters();
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isClicked = false;
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        this.setVisible(true);
        this.setBounds(0,0,width,height);
    }
    public boolean isImageReady(){
        return imageReady;
    }
    public Thread getImageThread(){
        return this.imageThread;
    }
    private void openFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.addChoosableFileFilter(new CustomImageFilter());
        fileChooser.setAcceptAllFileFilterUsed(false);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            Dimension screenSize = Toolkit. getDefaultToolkit(). getScreenSize();
            try {
                BufferedImage tempImage = ImageIO.read(fileChooser.getSelectedFile());
                Image scaledImage = tempImage.getScaledInstance(tempImage.getWidth() * 2,
                        tempImage.getHeight() * 2,Image.SCALE_SMOOTH);
                while (scaledImage.getWidth(null) > screenSize.width ||
                        scaledImage.getHeight(null) > screenSize.height)
                {
                    int newWidth = scaledImage.getWidth(null);
                    if (scaledImage.getWidth(null) > screenSize.width){
                        newWidth = (int) (scaledImage.getWidth(null)/1.5);
                    }

                    int newHeight = scaledImage.getHeight(null);
                    if (scaledImage.getHeight(null) > screenSize.height){
                        newHeight = (int) (scaledImage.getHeight(null)/1.5);
                    }
                    scaledImage = scaledImage.getScaledInstance(newWidth,newHeight,Image.SCALE_SMOOTH);
                }
                bufferedImage = new BufferedImage(scaledImage.getWidth(null),
                        scaledImage.getHeight(null),BufferedImage.TYPE_INT_ARGB);
                Graphics2D bGr = bufferedImage.createGraphics();
                bGr.drawImage(scaledImage, 0, 0, null);
                bGr.dispose();
                imageReady = true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void paintComponent(Graphics graphics){
        super.paintComponent(graphics);
        graphics.drawImage(bufferedImage,0,0, width,height,null);
        graphics.setColor(Color.red);
        line.drawLine(graphics);
    }
    public void resetImage(){
        bufferedImage.setData(originalData);
    }
    public void hardReset(){
        appliedFilter = 0;
        resetImage();
    }
    public void reapplyFilters(){
        resetImage();
        switch (appliedFilter){
            case BLACK_AND_WHITE -> blackAndWhite();
            case GRAYSCALE -> grayscale();
            case NEGATIVE -> negative();
            case TINT -> tint();
            case DRAW_BORDERS -> drawBorders();
            case DARKER -> darker();
            case COLOR_SHIFT_RIGHT -> colorShiftRight();
            case MIRROR -> mirror();
            case ADD_NOISE -> addNoise();
            case SEPIA -> sepia();
            case VINTAGE -> vintage();
        }
    }
    public void blackAndWhite(){
        for (int x = line.getX(); x < bufferedImage.getWidth(); x++){
            for (int y = 0; y < bufferedImage.getHeight(); y++){
                Color current = new Color(bufferedImage.getRGB(x,y));
                int average = (current.getBlue() + current.getRed() + current.getBlue())/3;
                if (average <= 128)
                    bufferedImage.setRGB(x,y,Color.BLACK.getRGB());
                else
                    bufferedImage.setRGB(x,y,Color.WHITE.getRGB());
            }
        }
        appliedFilter = BLACK_AND_WHITE;
    }
    public void grayscale(){
        for (int x = line.getX(); x < bufferedImage.getWidth(); x++){
            for (int y = 0; y < bufferedImage.getHeight(); y++){
                Color current = new Color(bufferedImage.getRGB(x,y));
                int average = (current.getBlue() + current.getRed() + current.getBlue())/3;
                Color updated = new Color(average,average,average);
                bufferedImage.setRGB(x,y,updated.getRGB());
            }
        }
        appliedFilter = GRAYSCALE;
    }
    public void negative(){
        for (int x = line.getX(); x < bufferedImage.getWidth(); x++){
            for (int y = 0; y < bufferedImage.getHeight(); y++){
                Color current = new Color(bufferedImage.getRGB(x,y));
                int red = 255 - current.getRed();
                int green = 255 - current.getGreen();
                int blue = 255 - current.getBlue();
                Color updated = new Color(red,green,blue);
                bufferedImage.setRGB(x,y,updated.getRGB());
            }
        }
        appliedFilter = NEGATIVE;
    }
    public void tint(){
        Color shade = new Color(120,10,70);
        for (int x = line.getX(); x < bufferedImage.getWidth(); x++){
            for (int y = 0; y < bufferedImage.getHeight(); y++){
                Color current = new Color(bufferedImage.getRGB(x,y));
                int red = current.getRed() + shade.getRed();
                if (red > 255)
                    red = 255;
                int green = current.getGreen() + shade.getGreen();
                if (green > 255)
                    green = 255;
                int blue = current.getBlue() + shade.getBlue();
                if (blue > 255)
                    blue = 255;
                Color updated = new Color(red,green,blue);
                bufferedImage.setRGB(x,y,updated.getRGB());
            }
        }
        appliedFilter = TINT;
    }
    public void drawBorders(){
        for (int x = line.getX(); x < bufferedImage.getWidth() - 1; x++){
            for (int y = 0; y < bufferedImage.getHeight() - 1; y++){
                Color current = new Color(bufferedImage.getRGB(x,y));
                Color rightNeighbour = new Color(bufferedImage.getRGB(x+1,y));
                Color downNeighbour = new Color(bufferedImage.getRGB(x,y+1));
                if (areColorsDifferent(current, downNeighbour) || areColorsDifferent(current, rightNeighbour)){
                    bufferedImage.setRGB(x,y,Color.BLACK.getRGB());
                }
                else{
                    bufferedImage.setRGB(x,y,Color.WHITE.getRGB());
                }
            }
        }
        appliedFilter = DRAW_BORDERS;
    }
    private boolean areColorsDifferent(Color color1, Color color2){
        int threshold = 20;
        int redDif = Math.abs(color1.getRed() - color2.getRed());
        int blueDif = Math.abs(color1.getBlue() - color2.getBlue());
        int greenDif = Math.abs(color1.getGreen() - color2.getGreen());
        return redDif >= threshold || blueDif >= threshold || greenDif >= threshold;
    }
    public void darker(){
        for (int x = line.getX(); x < bufferedImage.getWidth(); x++){
            for (int y = 0; y < bufferedImage.getHeight(); y++){
                Color current = new Color(bufferedImage.getRGB(x,y));
                int red = current.getRed() /4;
                int green = current.getGreen() /4;
                int blue = current.getBlue() /4;
                Color updated = new Color(red,green,blue);
                bufferedImage.setRGB(x,y,updated.getRGB());
            }
        }
        appliedFilter = DARKER;
    }
    public void colorShiftRight(){
        for (int x = line.getX(); x < bufferedImage.getWidth(); x++){
            for (int y = 0; y < bufferedImage.getHeight(); y++){
                Color current = new Color(bufferedImage.getRGB(x,y));
                int red = current.getGreen();
                int green = current.getBlue();
                int blue = current.getRed();
                Color updated = new Color(red,green,blue);
                bufferedImage.setRGB(x,y,updated.getRGB());
            }
        }
        appliedFilter = COLOR_SHIFT_RIGHT;
    }
    public void mirror(){
        int left = 0;
        int right = bufferedImage.getWidth() - 1;
        for (int y = 0; y < bufferedImage.getHeight(); y++){
            while (right > line.getX() && right > bufferedImage.getWidth()/2){
                Color leftColor = new Color(bufferedImage.getRGB(left,y));
                Color rightColor = new Color(bufferedImage.getRGB(right,y));
                bufferedImage.setRGB(left,y,rightColor.getRGB());
                bufferedImage.setRGB(right,y,leftColor.getRGB());
                left++;
                right--;
            }
            left = 0;
            right = bufferedImage.getWidth() - 1;
        }
        appliedFilter = MIRROR;
    }
    public void addNoise(){
        float distortMin = 0.2f;
        float distortMax = 1.6f;
        Random random = new Random();
        for (int x = line.getX(); x < bufferedImage.getWidth(); x++){
            for (int y = 0; y < bufferedImage.getHeight(); y++){
                Color current = new Color(bufferedImage.getRGB(x,y));
                int red = (int) (current.getRed() * random.nextFloat(distortMin,distortMax));
                if (red > 255){
                    red = 255;
                }

                int green = (int) (current.getGreen() * random.nextFloat(distortMin,distortMax));
                if (green > 255){
                    green = 255;
                }

                int blue = (int) (current.getBlue() * random.nextFloat(distortMin,distortMax));
                if (blue > 255){
                    blue = 255;
                }
                Color updated = new Color(red,green,blue);
                bufferedImage.setRGB(x,y,updated.getRGB());
            }
        }
        appliedFilter = ADD_NOISE;
    }
    public void sepia(){
        for (int x = line.getX(); x < bufferedImage.getWidth(); x++){
            for (int y = 0; y < bufferedImage.getHeight(); y++){
                Color current = new Color(bufferedImage.getRGB(x,y));
                int red = (int)(0.393*current.getRed() + 0.769*current.getGreen() + 0.189*current.getBlue());
                int green = (int)(0.349*current.getRed() + 0.686*current.getGreen() + 0.168*current.getBlue());
                int blue = (int)(0.272*current.getRed() + 0.534*current.getGreen() + 0.131*current.getBlue());

                red = Math.min(red,255);
                green = Math.min(green,255);
                blue = Math.min(blue,255);
                Color updated = new Color(red,green,blue);
                bufferedImage.setRGB(x,y,updated.getRGB());
            }
        }
        appliedFilter = SEPIA;
    }
    public void vintage(){
        addNoise();
        sepia();
        appliedFilter = VINTAGE;
    }
}

