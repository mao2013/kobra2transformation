/*
 *+--------------------------------------------------------------------------------------+
 *| Licensed Materials - Use restricted, please refer to the "Samples Gallery" terms and |
 *| conditions in the IBM International Program License Agreement.						 |	
 *| © Copyright IBM Corporation 2003, 2004. All Rights Reserved.						 |
 *+--------------------------------------------------------------------------------------+
 */


package com.ibm.xtools.transform.samples.modeltomodel;

import org.eclipse.core.runtime.*;
import java.util.*;

/**
 * The main plugin class to be used in the desktop.
 */
public class ModelToModelPlugin extends Plugin {
	//The shared instance.
	private static ModelToModelPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	
	/**
	 * The constructor.
	 */
	public ModelToModelPlugin() {
		super();
		plugin = this;
		try {
			resourceBundle   = ResourceBundle.getBundle("com.ibm.xtools.transform.samples.modeltomodel.Sample2PluginResources"); //$NON-NLS-1$
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}

	/**
	 * Returns the shared instance.
	 */
	public static ModelToModelPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = ModelToModelPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
	
	/**
	 * Gets the plugin id.
	 * 
	 * @return Plugin id.
	 */
	public static String getPluginId() {
		return getDefault().getBundle().getSymbolicName();
	}
}
