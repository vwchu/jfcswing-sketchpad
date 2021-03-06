package app.component;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

/**
 * The class implements a popup panel,
 * a popup containing a single component.
 * This is a customized popup. Not using the
 * standard JMenu and JMenuItem.
 */
public class PopupPane
    extends JPopupMenu
    implements PopupMenuListener, ActionListener {

    public static final int TOP     = -1;
    public static final int BOTTOM  = 1;
    public static final int LEFT    = 1;
    public static final int CENTER  = 2;
    public static final int RIGHT   = 3;

    public static final int TOP_LEFT      = TOP    * LEFT;
    public static final int TOP_CENTER    = TOP    * CENTER;
    public static final int TOP_RIGHT     = TOP    * RIGHT;
    public static final int BOTTOM_LEFT   = BOTTOM * LEFT;
    public static final int BOTTOM_CENTER = BOTTOM * CENTER;
    public static final int BOTTOM_RIGHT  = BOTTOM * RIGHT;

    private final Component popupPanel;

    private InvokerMouseListener mouseListener = new InvokerMouseListener();

    private Component      invoker         = null;
    private ButtonGroup    bgroup          = null;
    private boolean        deselectOnClose = false;
    private int            orientation     = LEFT;

    /**
     * Construct a new popup with the given content.
     *
     * @param popupPanel        the content pane.
     */
    public PopupPane(Component popupPanel) {
        this.popupPanel = popupPanel;
        setLayout(new BorderLayout());
        add(popupPanel, BorderLayout.CENTER);
        setBorder(new LineBorder(Color.BLACK, 1));
        setFocusable(false);
        pack();
    }

    /**
     * Unset the invoker.
     */
    public void unsetInvoker() {
        if (invoker == null) {return;}
        orientation = LEFT;
        super.setInvoker(null);
        if (invoker instanceof AbstractButton) {
            AbstractButton b = (AbstractButton)invoker;
            b.removeActionListener(this);
            removePopupMenuListener(this);
            bgroup = null;
            deselectOnClose = false;
        } else {
            invoker.removeMouseListener(mouseListener);
        }
        invoker = null;
    }

    /**
     * Sets the invoker of this popup menu -- the button in which
     * the popup menu menu is to be displayed. Orientation: Bottom left
     *
     * @param invoker           the <code>AbstractButton</code> in which
     *                          the popup menu is displayed when clicked
     * @param bgroup            the button group for which invoker belongs
     * @param deselectOnClose   flag to deselect invoker, when popup closed
     */
    public void configInvoker(AbstractButton invoker,
                           ButtonGroup bgroup,
                           boolean deselectOnClose) {
        unsetInvoker();
        this.invoker         = invoker;
        this.bgroup          = bgroup;
        this.deselectOnClose = deselectOnClose;
        setInvoker(invoker);
        invoker.addActionListener(this);
        addPopupMenuListener(this);
        setVisible(false);
    }

    /**
     * Sets the invoker of this popup menu -- the button in which
     * the popup menu menu is to be displayed.
     *
     * @param invoker           the <code>AbstractButton</code> in which
     *                          the popup menu is displayed when clicked
     * @param bgroup            the button group for which invoker belongs
     * @param deselectOnClose   flag to deselect invoker, when popup closed
     * @param orientation       for which the popup will be displayed
     *                          relative to the invoker
     */
    public void configInvoker(AbstractButton invoker,
                           ButtonGroup bgroup,
                           boolean deselectOnClose,
                           int orientation) {
        configInvoker(invoker, bgroup, deselectOnClose);
        this.orientation = orientation;
    }

    /**
     * Sets the invoker of this popup menu -- the component in which
     * the popup menu menu is to be displayed. Orientation: Bottom left
     *
     * @param invoker       the <code>Component</code> in which the popup
     *                      menu is displayed
     */
    public void configInvoker(Component invoker) {
        unsetInvoker();
        if (invoker instanceof AbstractButton) {
            configInvoker((AbstractButton)invoker, null, false);
        } else {
            this.invoker = invoker;
            setInvoker(invoker);
            invoker.addMouseListener(mouseListener);
        }
        setVisible(false);
    }

    /**
     * Sets the invoker of this popup menu -- the component in which
     * the popup menu menu is to be displayed.
     *
     * @param invoker           the <code>Component</code> in which
     *                          the popup menu is displayed
     * @param orientation       for which the popup will be displayed
     *                          relative to the invoker
     */
    public void configInvoker(Component invoker, int orientation) {
        configInvoker(invoker);
        this.orientation = orientation;
    }

    @Override public void revalidate() {
        if (popupPanel == null) {return;}
        setVisible(false);
        super.revalidate();
        repaint();
        invokePopup();
    }

    @Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {unselect(e);}
    @Override public void popupMenuCanceled(PopupMenuEvent e) {unselect(e);}
    @Override public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
    private void unselect(PopupMenuEvent e) {
        if (!deselectOnClose) {return;}
        if (invoker == null) {return;}
        ((AbstractButton)invoker).setSelected(false);
        if (bgroup != null) {
            bgroup.clearSelection();
        }
    } // unselect

    @Override public void actionPerformed(ActionEvent e) {
        if (!deselectOnClose || ((AbstractButton)invoker).isSelected()) {
            invokePopup();
        }
    }

    /**
     * Invoke / Show the popup.
     */
    private void invokePopup() {
        if (invoker == null) {
            show(null, 0, 0);
            return;
        }
        int width = getWidth();
        int height = getHeight();
        int prefWidth = popupPanel.getPreferredSize().width;
        int prefHeight = popupPanel.getPreferredSize().height;
        int x = 0;
        if (height <= 0) {
            height = prefHeight;
        }
        if (width <= 0) {
            width = prefWidth;
        }
        switch (Math.abs(orientation)) {
            case CENTER:
                x = (invoker.getWidth() - width) / 2;
                break;
            case RIGHT:
                x = invoker.getWidth() - width;
                break;
        }
        show(invoker, x, orientation < 0 ? -height : invoker.getHeight());
    }

    /**
     * The mouse handler to attach to non button
     * invokers.
     */
    private class InvokerMouseListener extends MouseAdapter {
        @Override public void mousePressed(MouseEvent e) {
            if (!isShowing()) {
                invokePopup();
            }
        }
    } // InvokerMouseListener

} // PopupPanel
