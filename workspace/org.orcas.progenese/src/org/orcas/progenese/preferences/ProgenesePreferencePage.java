package org.orcas.progenese.preferences;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.orcas.progenese.Activator;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class ProgenesePreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	private FileFieldEditor dirMetamodel;
	private StringFieldEditor dirProfile;
	private static final String[] EXTENSIONS_ALLOWED = new String[]{"*.uml"};
	private IPreferenceStore preferenceStore;
	
	public ProgenesePreferencePage() {
		super(GRID);
		preferenceStore = Activator.getDefault().getPreferenceStore();
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Configure the models Paths in order to transform a meta-model to its Profile representation");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {
		dirMetamodel = new FileFieldEditor(PreferenceConstants.P_PATH, "&Input Metamodel File Path:",getFieldEditorParent());
		dirMetamodel.setFileExtensions(EXTENSIONS_ALLOWED);
		addField(dirMetamodel);

		dirProfile = new StringFieldEditor(PreferenceConstants.P_STRING, "&Output Profile Path:",
				getFieldEditorParent());
		addField(dirProfile);
		//getApplyButton().setEnabled(true);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
/*		dirMetamodel = new FileFieldEditor(PreferenceConstants.P_PATH, "&Input Metamodel File Path:",getFieldEditorParent());
		dirMetamodel.setFileExtensions(EXTENSIONS_ALLOWED);
		dirMetamodel.setStringValue(preferenceStore.getString("dirMetamodelPath"));
		addField(dirMetamodel);
		
		dirProfile = new StringFieldEditor(PreferenceConstants.P_STRING, "&Output Profile Path:",
				getFieldEditorParent());
		dirProfile.setStringValue(preferenceStore.getString("dirProfilePath"));
		addField(dirProfile);*/
	}

	@Override
	public boolean performOk() {
		//performApply();
		return super.performOk();
	}
	
	@Override
	protected void performApply() {
		String metamodelPath = dirMetamodel.getStringValue();
		String profilePath = dirProfile.getStringValue();
		URI metamodelURI = URI.createFileURI(metamodelPath);
		URI profileURI = URI.createFileURI(profilePath);
		preferenceStore.putValue("dirMetamodelPath", metamodelURI.toString());
		preferenceStore.putValue("dirProfilePath", profileURI.toString());	
		super.performApply();
	}
	
	@Override
	protected void checkState() {
		
	/*	if (dirMetamodel.getStringValue() != null && !dirMetamodel.getStringValue().equals("")
				&& dirProfile.getStringValue() != null && !dirProfile.getStringValue().equals("")) {
			dirMetamodel.setErrorMessage(null);
			setValid(true);
			getApplyButton().setEnabled(true);
		} else {
			setErrorMessage("Please chose a valid metamodel file");
			setValid(true);
			getApplyButton().setEnabled(true);
		}
		
		if (dirProfile.getStringValue() != null && !dirProfile.getStringValue().equals("")) {
			dirProfile.setErrorMessage(null);
			setValid(true);
			getApplyButton().setEnabled(true);
		} else {
			setErrorMessage("Please choose the generated profile path");
			setValid(true);
		}*/
	}
}