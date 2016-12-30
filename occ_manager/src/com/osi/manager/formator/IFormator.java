package com.osi.manager.formator;

import com.osi.manager.domain.UMObject;

public interface IFormator {

	public String formatValueWithUM(String value, UMObject umObject) throws Exception;
}
