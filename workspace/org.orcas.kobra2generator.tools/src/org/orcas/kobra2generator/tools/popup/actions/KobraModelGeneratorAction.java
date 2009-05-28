package org.orcas.kobra2generator.tools.popup.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.orcas.kobra2generator.KobraModelGenerator;

public class KobraModelGeneratorAction implements IObjectActionDelegate {

	private Shell shell;
	
	/**
	 * Constructor for Action1.
	 */
	public KobraModelGeneratorAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		
		String uml2ModelPath = PlatformUI.getPreferenceStore().getString("umlModelPath");
		String k2ModelPath =  PlatformUI.getPreferenceStore().getString("k2ModelPath");
		KobraModelGenerator kg = new KobraModelGenerator(uml2ModelPath, k2ModelPath);
		try {
			kg.transform(k2ModelPath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MessageDialog.openInformation(
			shell,
			"Kobra2 Models Generator Plug-in",
			"Convert to KobrA2 Model was executed.");
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
