package org.orcas.progenese.popup.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.orcas.progenese.Activator;
import org.orcas.progenese.ProfileGenerator;

public class ProfileConvertionActionDelegate implements IObjectActionDelegate {

	private Shell shell;

	/**
	 * Constructor for Action1.
	 */
	public ProfileConvertionActionDelegate() {
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
		final String metamodelPath;
		final String profilePath;

		try {
			IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();
			metamodelPath = preferences.getString("dirMetamodelPath");
			profilePath = preferences.getString("dirProfilePath");

			if (metamodelPath == null || metamodelPath.equals("")
					|| profilePath == null || profilePath.equals(""))
				MessageDialog
						.openError(
								shell,
								"Error",
								"Please, assure that both metamodel and profile path are set in Window -> Preferences -> Progenese Tool.");
			else {
				final ProgressMonitorDialog progressMonitorDialog = new ProgressMonitorDialog(
						shell);
				final IRunnableWithProgress irunnablewithprogress = new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor)
							throws InvocationTargetException,
							InterruptedException {
						monitor.beginTask("Converting to Profile, wait..",
								IProgressMonitor.UNKNOWN);
						try {
							ProfileGenerator pg = new ProfileGenerator(
									metamodelPath, profilePath);
							pg.transform(profilePath);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
				
				shell.getDisplay().syncExec(new Runnable() {

					@Override
					public void run() {

						try {
							progressMonitorDialog.run(true, true,
									irunnablewithprogress);
							progressMonitorDialog.close();
							MessageDialog.openInformation(shell,
									"Progenese Tool",
									"The profile was successfully generated.");
						} catch (Exception e) {
							MessageDialog.openError(shell, "Progenese Tool",
									"Error occured while executed the transformation. Error message: "
											+ e.getMessage());

							e.printStackTrace();
						}
					}

				});
			}
		} catch (Exception e) {
			MessageDialog.openError(shell, "Progenese Tool",
					"Error occured while executed the transformation. Error message: "
							+ e);
			e.printStackTrace();
		}

	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	
	}
}