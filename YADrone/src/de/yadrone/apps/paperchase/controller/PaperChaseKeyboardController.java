package de.yadrone.apps.paperchase.controller;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

import de.yadrone.apps.controlcenter.plugins.keyboard.KeyboardCommandManager;
import de.yadrone.base.IARDrone;

public class PaperChaseKeyboardController extends PaperChaseAbstractController
{
	private KeyboardCommandManager keyboardCommandManager;
	
	public PaperChaseKeyboardController(IARDrone drone)
	{
		super(drone);
	}
	
	public void run()
	{
        keyboardCommandManager = new KeyboardCommandManager(drone);
		
		// CommandManager handles (keyboard) input and dispatches events to the drone		
		System.out.println("PaperChaseKeyboardController: grab the whole keyboard input from now on ...");
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(keyEventDispatcher);
	}

	private KeyEventDispatcher keyEventDispatcher = new KeyEventDispatcher() {
    	
    	public boolean dispatchKeyEvent(KeyEvent e)
		{
			if (e.getID() == KeyEvent.KEY_PRESSED) 
			{
                keyboardCommandManager.keyPressed(e);
            } 
			else if (e.getID() == KeyEvent.KEY_RELEASED) 
            {
                keyboardCommandManager.keyReleased(e);
            }
            return false;
		}
	};
}
