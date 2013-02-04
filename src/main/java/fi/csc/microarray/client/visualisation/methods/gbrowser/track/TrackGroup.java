package fi.csc.microarray.client.visualisation.methods.gbrowser.track;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import fi.csc.microarray.client.visualisation.methods.gbrowser.gui.GBrowserView;
import fi.csc.microarray.client.visualisation.methods.gbrowser.gui.LayoutComponent;
import fi.csc.microarray.client.visualisation.methods.gbrowser.gui.LayoutContainer;
import fi.csc.microarray.client.visualisation.methods.gbrowser.gui.LayoutTool;
import fi.csc.microarray.client.visualisation.methods.gbrowser.gui.LayoutTool.LayoutMode;

/**
 * <p>A collection of tracks representing a single data source or
 * related in any other logical way and displayed one after
 * another. Typically one group object corresponds to what
 * user considers as a "track".</p>
 * 
 * <p>The track group is also responsible for drawing the side menu,
 * switching tracks inside the group and changing properties of
 * individual groups.</p>
 * 
 * @author Rimvydas Naktinis, Petri Klemelä
 *
 */
public class TrackGroup implements LayoutComponent, LayoutContainer {
    
    protected List<Track> tracks = new LinkedList<Track>();
    protected GBrowserView view;
    protected boolean menuVisible = false;
    protected boolean visible = true;
    public SideMenu menu;
    private JButton resize;
	private int layoutHeight;
        
    public class SideMenu extends JPanel implements ActionListener {
        
        // Width of side menu in pixels
        public static final int WIDTH = 110;
        public static final int COLLAPSED_WIDTH = 20;
        
        // Position of this menu
        int x, y;
        
        // Inner panel for holding controls
        private JPanel controls = new JPanel();
        int startControlsAt = 25;
        int lastControlAt = 0;
        
        protected boolean menuCollapsed = true;
        protected TrackGroup group = TrackGroup.this;
        
        public SideMenu() {
            // Absolute positioning inside the menu
            this.setLayout(null);
            try {
                resize = new JButton(
                        new ImageIcon(this.getClass().
                        getResource("/arrow_left.png").toURI().toURL()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            resize.setBackground(new Color(0, 0, 0, 0));
            resize.setBorder(null);
            resize.setBounds(1, 1, 20, 20);
            resize.setOpaque(false);
            resize.setFocusPainted(false);
            resize.addActionListener(this);

            // Panel with controls
            controls.setLayout(new BoxLayout(controls, BoxLayout.PAGE_AXIS));
            controls.setBounds(5, startControlsAt, 100, 100);
            controls.setVisible(!menuCollapsed);
            add(controls);
            
            // Button for resizing side menu
            add(resize);
        }
        
        public void addItem(JComponent component) {
            controls.add(component);
        }
        
        public int getWidth() {
            return menuCollapsed ? COLLAPSED_WIDTH : WIDTH;
        }
        
        /**
         * Sets the absolute position and draws the menu.
         * 
         * @param x rightmost pixel of the menu
         * @param y topmost pixel of the menu
         */
        public void setPosition(int x, int y) {
            this.x = x;
            this.y = y;
            this.setBounds(x - getWidth(), y, getWidth(), group.getHeight());
        }
        
        /**
         * Redraw menu after collapsing.
         */
        public void redraw() {
            controls.setVisible(!menuCollapsed);
            setPosition(x, y);
        }

        public void actionPerformed(ActionEvent event) {
            if (event.getSource() == resize) {
                menuCollapsed = !menuCollapsed;
                redraw();
            }
        }
    }

    public TrackGroup(GBrowserView view) {
        this.view = view;
        
        // Add side menu
        menu = new SideMenu();
        
        //FIXME Memory leak, the reference isn't removed when the visualization is changed to none.
        //Fix if the side panel is needed
        //this.view.parentPlot.chartPanel.add(menu);
    }
    
    /**
     * It is quite common to have track group consisting of a
     * single data track.
     * 
     * @param view
     * @param track
     */
    public TrackGroup(Track track) {
        this(track.view);
        tracks.add(track);
    }
    
    /**
     * Add a track.
     */
    public void addTrack(Track track) {
        tracks.add(track);
    }
    
    /**
     * Return a list of tracks in this group including separator tracks.
     * 
     * @return a list of tracks do be drawn.
     */
    public List<Track> getTracks() {
        // TODO add separator tracks
        return tracks;
    }
    
    public GBrowserView getView() {
        return view;
    }
    
    /**
     * Set visibility of this group.
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
        menu.setVisible(visible);
    }
    
    /**
     * Get visibility of this group.
     */
    public boolean isVisible() {
        return visible;
    }
    
    /**
     * Determine if a side menu should be shown for this track.
     * 
     * @return true if a menu should be shown, false otherwise.
     */
    public boolean isMenuVisible() {
        return visible && menuVisible;
    }
    
    /**
     * Set side menu visibility. 
     */
    public void setMenuVisible(boolean isVisible) {
        menuVisible = isVisible;
    }
    
    public String getName() {
    	return "Track Group";
    }
    
    /**
     * Sets the visibility of a track, if it is a need of SNP highlighting, method should be overridden.
     * @param track name
     * @param state
     */
    public void showOrHide(String name, boolean state) {
    	
		for (Track track : tracks) {
    		if (track.getName().equals(name)) {
    			track.setVisible(state);
    			track.getView().redraw();
    		}
    	}
    }

	@Override
	public Collection<? extends LayoutComponent> getLayoutComponents() {
		return tracks;
	}

	@Override
	public int getHeight() {
		return LayoutTool.getHeight(this, layoutHeight);
	}

	@Override
	public void setHeight(int height) {
		this.layoutHeight = height;
	}

	@Override
	public int getMinHeight() {
		return LayoutTool.getMinHeightSum(this);
	}
	
	@Override
	public int getFullHeight() {
		return LayoutTool.getFullHeight(this);
	}

	@Override
	public LayoutMode getLayoutMode() {
		return LayoutTool.inferLayoutMode(this);
	}

	@Override
	public void setLayoutMode(LayoutMode mode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDefaultLayoutMode() {
		// TODO Auto-generated method stub
		
	}
}
