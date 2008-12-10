/*
 *+--------------------------------------------------------------------------------------+
 *| Licensed Materials - Use restricted, please refer to the "Samples Gallery" terms and |
 *| conditions in the IBM International Program License Agreement.						 |	
 *| © Copyright IBM Corporation 2003, 2004. All Rights Reserved.						 |
 *+--------------------------------------------------------------------------------------+
 */
 
package com.ibm.xtools.transform.samples.modeltomodel;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;

import com.ibm.xtools.transform.core.AbstractTransformationProvider;
import com.ibm.xtools.transform.samples.modeltomodel.l10n.ResourceManager;


/**
 * An utility class to contain methods for generating the statuses returned by
 * various transform related code. 
 */
public class StatusUtility {

	/**
	 * Constructor. Being a utility class with all static methods and no
	 * state of its own, this class does not need its constaructor to be public.
	 */
	private StatusUtility() {
		super();
	}

	/**
	 * Creates a status object or a multi-status object from the results of
	 * validation of transformation source, target, and property values.
	 * 
	 * @param pluginId
	 *            Unique identifier of the relevant plugin
	 * @param transformName
	 *            Transform name
	 * @param sourceOk
	 *            Indicates validity of the transformation source
	 * @param sourceErrorMsg
	 *            Error message for indicating invalid source
	 * @param targetOk
	 *            Indicates validity of the transformation target
	 * @param targetErrorMsg
	 *            Error message for indicating invalid target
	 * @param propertiesOk
	 *            Indicates validity of the values of transformation properties
	 * @return A status or a multistatus object
	 */
	public static IStatus createTransformContextValidationStatus(
			String pluginId, boolean sourceOk, String sourceErrorMsg,
			boolean targetOk, String targetErrorMsg, boolean propertiesOk) {
	
		MultiStatus multiErrorStatus = new MultiStatus(pluginId,
			AbstractTransformationProvider.CODE_SOURCE,
			ResourceManager.getString("StatusUtility.invalidContextMsg"), //$NON-NLS-1$
			null);
	
		if (!sourceOk) {
			multiErrorStatus.add(new Status(IStatus.ERROR, pluginId,
				AbstractTransformationProvider.CODE_SOURCE, sourceErrorMsg,
				null));
		}
	
		if (!targetOk) {
			multiErrorStatus.add(new Status(IStatus.ERROR, pluginId,
				AbstractTransformationProvider.CODE_TARGET_CONTAINER,
				targetErrorMsg, null));
		}
	
		if (multiErrorStatus.getChildren().length == 0) {
			// No error - create and return a new Status object with severity
			// IStatus.OK.
			return new Status(IStatus.OK, pluginId,
				AbstractTransformationProvider.CODE_SOURCE, ResourceManager.getString("StatusUtility.validContextMsg"), //$NON-NLS-1$
				null);
		} else if (multiErrorStatus.getChildren().length == 1) {
			// Only one error - send the enclosed status
			return (multiErrorStatus.getChildren())[0];
		} else {
			// Multiple errors - return the multistatus
			return multiErrorStatus;
		}
	}
}
