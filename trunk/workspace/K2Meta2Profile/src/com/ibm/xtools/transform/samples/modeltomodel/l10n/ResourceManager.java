/*
 *+--------------------------------------------------------------------------------------+
 *| Licensed Materials - Use restricted, please refer to the "Samples Gallery" terms and |
 *| conditions in the IBM International Program License Agreement.						 |	
 *| © Copyright IBM Corporation 2003, 2004. All Rights Reserved.						 |
 *+--------------------------------------------------------------------------------------+
 */

package com.ibm.xtools.transform.samples.modeltomodel.l10n;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * An utility class to manage localized strings.
 */
public class ResourceManager {

	private static final String BUNDLE_NAME = "com.ibm.xtools.transform.samples.modeltomodel.l10n.messages";//$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
		.getBundle(BUNDLE_NAME);

	/**
	 * Default constructor
	 */
	private ResourceManager() {

		// Auto-generated constructor stub
	}

	/**
	 * Gets the localized string for the key. If the key is not found, it
	 * returns the key.
	 * 
	 * @param key
	 *            Key for the localized striung
	 * @return Localized string if key is found. Otherwise, key itself is
	 *         returned.
	 */
	public static String getString(String key) {
		// Auto-generated method stub
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}