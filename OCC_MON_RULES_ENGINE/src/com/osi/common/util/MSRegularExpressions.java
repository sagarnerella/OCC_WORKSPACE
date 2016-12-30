package com.osi.common.util;

public class MSRegularExpressions  {
	public static boolean calculateRelationalOperators(String resultValue,String thresholdValue, String operator) throws Exception{
		boolean flag =false;
			if (resultValue.equalsIgnoreCase("") || resultValue.equalsIgnoreCase(" ") || resultValue.equalsIgnoreCase("Null")) {
				resultValue = "0";
			}
			if (thresholdValue.equalsIgnoreCase("") || thresholdValue.equalsIgnoreCase(" ") || thresholdValue.equalsIgnoreCase("Null")) {
				thresholdValue = "0";
			}
			if(null != resultValue && null != thresholdValue){
				if("-1".equalsIgnoreCase(resultValue)) {
					flag = true;
				}
				else if(operator.equals(">") && !"".equalsIgnoreCase(resultValue)) {
					flag = Double.parseDouble(resultValue) > Double.parseDouble(thresholdValue);
				}  else if(operator.equals("<") && !"".equalsIgnoreCase(resultValue)) {
					flag = Double.parseDouble(resultValue) < Double.parseDouble(thresholdValue);
				} else if((operator.equals(">=") || operator.equals("!<")) && !"".equalsIgnoreCase(resultValue)) {
					flag = Double.parseDouble(resultValue) >= Double.parseDouble(thresholdValue);
				} else if((operator.equals("<=") || operator.equals("!>")) && !"".equalsIgnoreCase(resultValue)) {
					flag = Double.parseDouble(resultValue) <= Double.parseDouble(thresholdValue);
				} else if((operator.equals("==") || operator.equals("=")) && !"".equalsIgnoreCase(resultValue)) {
					flag = Double.parseDouble(resultValue) == Double.parseDouble(thresholdValue);
				} else if((operator.equals("<>") || operator.equals("!=")|| operator.equals("^=")) && !"".equalsIgnoreCase(resultValue)) {
					flag = Double.parseDouble(resultValue) != Double.parseDouble(thresholdValue);
				} else if(operator.equalsIgnoreCase("matches")) {
					if(resultValue.matches(thresholdValue)) { //Str.matches(Str1)
			    		 flag = true;
			    	 }
				} else {
					flag = false;
				}
				
			} else {
				throw new Exception("Invalid Comparison");
			}
		
		return flag;
		
	}
}

  

