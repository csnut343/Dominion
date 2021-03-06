package dominion.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * Component that displays a list of images, with images later in the list
 * overlapping the images earlier in the list.
 * 
 * @author Matt Spikes (csnut343)
 */
@SuppressWarnings("serial")
public class CardList extends JPanel implements ListDataListener {

    /** Logical model of the data to be represented graphically by this component */
    private ListModel data;
    
    /** 
     * Vertical offset of cards draw on the stack,
     * i.e. How much of the top of each card is shown 
     */
    private int vgap;
    
    /**
     * Creates a CardList with the specified data model and a default vertical
     * gap value.
     * 
     * <p>The default vertical gap is 55 which is optimal for displaying just the name of
     * the full dominion images.
     * 
     * @param data model of data displayed by this component
     */
    public CardList(ListModel data) {
        this(data, 55);
    }
    
    /**
     * Creates a CardList with the specified data model and vertical gap.
     * 
     * @param data model of data displayed by this component
     * @param vgap vertical gap between images 
     * @throws IllegalArgumentException if vgap is zero or negative
     */
    public CardList(ListModel data, int vgap) {
        if(vgap <= 0)
            throw new IllegalArgumentException("Vgap must be positive");
        this.vgap = vgap;
        
        this.data = data;
        this.data.addListDataListener(this);
        
        // set a non-null value, just to trigger the display of tooltips
        // actual value of tooltip determined by getToolTipText() function
        this.setToolTipText("");
    }
    
    /**
     * Sets the data model of this component.
     * 
     * @param data model of data displayed by this component
     */
    public void setModel(ListModel data) {
        this.data = data;
        this.data.addListDataListener(this);
    }
    
    /**
     * Gets the vertical gap of this component.
     * 
     * @return the current vertical gap value
     */
    public int getVgap() {
        return vgap;
    }
    
    /**
     * Sets the data model of this component.
     * 
     * @param vgap vertical gap between images 
     * @throws IllegalArgumentException if vgap is zero or negative
     */
    public void setVgap(int vgap) {
        if(vgap <= 0)
            throw new IllegalArgumentException("Vgap must be positive");
        this.vgap = vgap;
    }
    
    /* does nothing
     * this is to prevent components added to this CardList from interfering
     * with the drawing of this component
     */
    @Override
    protected void paintChildren(Graphics g) {
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Insets ins = this.getInsets();
        for(int i = 0; i < data.getSize(); i++)
            g.drawImage(getCardImage(data.getElementAt(i)), ins.left, ins.top+(i*vgap), this);
    }
    
    static final public String IMAGE_PATH = "C:/Eclipse/workspace/dominion/images/";
    private Map<Object, Image> images = new HashMap<Object, Image>();
    /** internal utility, maps a card name to a card image */
    private Image getCardImage(Object o) {
        Image img = images.get(o);
        if(img == null) {
             img = Toolkit.getDefaultToolkit().getImage(IMAGE_PATH + o.toString().toLowerCase() + ".jpg")
                                              .getScaledInstance(150, -1, 0);
             images.put(o, img);
        }
        return img;
    }
    
    @Override
    public String getToolTipText(MouseEvent e) {
        Point p = new Point(e.getPoint());
        p.x -= this.getX();
        p.y -= this.getY();
        //System.out.println("Mouse at: " + p);
        Insets ins = this.getInsets();
        
        p.x -= ins.left;
        p.y -= ins.top;
        
        int i = p.y/vgap;
        if(i >= data.getSize())
            i = data.getSize() - 1;
        p.y -= (i-1)*vgap;
        
        Image img = getCardImage(data.getElementAt(i));
        int width = img.getWidth(this);
        int height = img.getHeight(this);
        if(p.x < 0 || p.x > width || p.y < 0 || p.y > height)
            return null;
        else
            return "<html><img src=\"" +
                "file:" + IMAGE_PATH + data.getElementAt(i).toString().toLowerCase() + ".jpg" +  
                "\"></img></html>";
    }
    
    @Override
    public Dimension getPreferredSize() {
        // if user has set a preferred size, defer to superclass to return that value
        if(isPreferredSizeSet())
            return super.getPreferredSize();
        
        // otherwise, calculate the proper size
        int maxX = 0;
        int maxY = 0;
        for(int i = 0; i < data.getSize(); i++) {
            Image img = getCardImage(data.getElementAt(i));
            maxX = Math.max(maxX, img.getWidth(this));
            maxY = Math.max(maxY, img.getHeight(this) + (i*vgap));
            //System.out.printf("image of %s has dimensions [%d, %d]%n", data.getElementAt(i), img.getWidth(this), img.getHeight(this));
        }
        Insets ins = this.getInsets();
        maxX += ins.left + ins.right;
        maxY += ins.top  + ins.bottom;
        
        return new Dimension (maxX, maxY);
    }
    
    @Override
    public void contentsChanged(ListDataEvent e) {
        // TODO maybe do something more intelligent here
        repaint();
    }

    @Override
    public void intervalAdded(ListDataEvent e) {
        // TODO maybe do something more intelligent here
        repaint();
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
        // TODO maybe do something more intelligent here
        repaint();
    }
    
    /*static public void main(String[] args) {
        javax.swing.DefaultListModel model = new javax.swing.DefaultListModel();
        model.addElement("Bazaar");
        model.addElement("Market");
        model.addElement("Bazaar");
        model.addElement("Festival");
        model.addElement("PirateShip");
        
        javax.swing.JFrame frame = new javax.swing.JFrame("CardList Test");
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new CardList(model, 25));
        frame.pack(); //(new java.awt.Dimension(305, 750));
        frame.setVisible(true);        
    }//*/
}
