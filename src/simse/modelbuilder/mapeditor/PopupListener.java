package simse.modelbuilder.mapeditor;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

public class PopupListener extends MouseAdapter {
  private JPopupMenu popup;
  private boolean enabled;

  public PopupListener(JPopupMenu popup) {
    this.popup = popup;
    enabled = true;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean e) {
    enabled = e;
  }

  public void mousePressed(MouseEvent e) {
  }

  public void mouseReleased(MouseEvent e) {
    maybeShowPopup(e);
  }

  private void maybeShowPopup(MouseEvent e) {
    if (e.isPopupTrigger() && isEnabled()) {
      popup.show(e.getComponent(), e.getX(), e.getY());
    }
  }
}