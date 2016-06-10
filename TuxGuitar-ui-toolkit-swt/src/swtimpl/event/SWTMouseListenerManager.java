package swtimpl.event;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.herac.tuxguitar.ui.event.UIMouseDoubleClickListener;
import org.herac.tuxguitar.ui.event.UIMouseDoubleClickListenerManager;
import org.herac.tuxguitar.ui.event.UIMouseDownListener;
import org.herac.tuxguitar.ui.event.UIMouseDownListenerManager;
import org.herac.tuxguitar.ui.event.UIMouseEvent;
import org.herac.tuxguitar.ui.event.UIMouseUpListener;
import org.herac.tuxguitar.ui.event.UIMouseUpListenerManager;
import org.herac.tuxguitar.ui.resource.UIPosition;

import swtimpl.SWTComponent;

public class SWTMouseListenerManager implements MouseListener {
	
	private SWTComponent<?> control;
	private UIMouseUpListenerManager mouseUpListener;
	private UIMouseDownListenerManager mouseDownListener;
	private UIMouseDoubleClickListenerManager mouseDoubleClickListener;
	
	public SWTMouseListenerManager(SWTComponent<?> control) {
		this.control = control;
		this.mouseUpListener = new UIMouseUpListenerManager();
		this.mouseDownListener = new UIMouseDownListenerManager();
		this.mouseDoubleClickListener = new UIMouseDoubleClickListenerManager();
	}
	
	public boolean isEmpty() {
		return (this.mouseUpListener.isEmpty() && this.mouseDownListener.isEmpty() && this.mouseDoubleClickListener.isEmpty());
	}
	
	public void addListener(UIMouseUpListener listener) {
		this.mouseUpListener.addListener(listener);
	}
	
	public void addListener(UIMouseDownListener listener) {
		this.mouseDownListener.addListener(listener);
	}
	
	public void addListener(UIMouseDoubleClickListener listener) {
		this.mouseDoubleClickListener.addListener(listener);
	}
	
	public void removeListener(UIMouseUpListener listener) {
		this.mouseUpListener.removeListener(listener);
	}
	
	public void removeListener(UIMouseDownListener listener) {
		this.mouseDownListener.removeListener(listener);
	}
	
	public void removeListener(UIMouseDoubleClickListener listener) {
		this.mouseDoubleClickListener.removeListener(listener);
	}

	public void mouseDoubleClick(MouseEvent e) {
		this.mouseDoubleClickListener.onMouseDoubleClick(new UIMouseEvent(this.control, new UIPosition(e.x, e.y), e.button));
	}

	public void mouseDown(MouseEvent e) {
		this.mouseDownListener.onMouseDown(new UIMouseEvent(this.control, new UIPosition(e.x, e.y), e.button));
	}

	public void mouseUp(MouseEvent e) {
		this.mouseUpListener.onMouseUp(new UIMouseEvent(this.control, new UIPosition(e.x, e.y), e.button));
	}
}
