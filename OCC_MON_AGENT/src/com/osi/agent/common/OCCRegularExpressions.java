package com.osi.agent.common;

import java.util.regex.Pattern;

import org.apache.log4j.Logger;
public class OCCRegularExpressions  {
	private static final Logger LOGGER = Logger.getLogger(OCCRegularExpressions.class);
	public static boolean verifyThreshold(double resultValue,String thresholdValue) {
		boolean flag = false;
		String operator="";
		String singleWordOPerator="";
		try {

			Pattern p = Pattern.compile("[\\s]+");
			String[] operators = p.split("< !< <= > !> >= = != <> ^=");
			//String[] operators = p.split("< !< <= > !> >= = != <> ^= + - * / % and or not like in between exists any all is null some unique union intersect minus ");
			String[] words = p.split(thresholdValue);
			
			for(int i=0;i<words.length;i++){
				for (int j=0; j<operators.length; j++){
					if(words[i].equalsIgnoreCase(operators[j]))
						operator = operator.concat(operators[j])+" ";
				}
			}
			
			operator=operator.trim();
			
			String[]noOfWordsInOperator=operator.split(" ");
			 if (noOfWordsInOperator.length==1){
				 singleWordOPerator = noOfWordsInOperator[0];
			 }
			 
			
			int i=0;
			if(singleWordOPerator.equals("<")||singleWordOPerator.equals("<=")||singleWordOPerator.equals("!<")
				    		||singleWordOPerator.equals(">")||singleWordOPerator.equals(">=")||singleWordOPerator.equals("!>")
				    		||singleWordOPerator.equals("=")||singleWordOPerator.equals("<>")||singleWordOPerator.equals("!=")||singleWordOPerator.equals("^=")){
				
				String operand2 =words[i+1];
				flag = verifyThresholdWithResultValue(resultValue,operand2,singleWordOPerator); 
			}
				 
		} catch(NumberFormatException ne){
			LOGGER.error("", ne);
		} catch(Exception e){
			LOGGER.error("", e);
		}
		 return flag;
	}
	
	private static boolean verifyThresholdWithResultValue(double resultValue,String thresholdValue,String singleWordOPerator){
		LOGGER.info("calculateRelationalOperators:: resultValue : "+resultValue+" :: thresholdValue :: "+thresholdValue+" ::Operator :: "+singleWordOPerator);
		boolean flag = false;
		try	{
			  if(singleWordOPerator.equals(">")){
			  	  if(resultValue > Double.parseDouble(thresholdValue))
			  		  flag = true;
			  }
			    
			  if(singleWordOPerator.equals("<")) {
			   	  if(resultValue < Double.parseDouble(thresholdValue))
			   		  flag = true;
			  }
			      
			  if(singleWordOPerator.equals(">=") || singleWordOPerator.equals("!<")) {
			  	  if(resultValue >= Double.parseDouble(thresholdValue))
			  		  flag = true;
			  }
			     
			  if(singleWordOPerator.equals("<=") || singleWordOPerator.equals("!>")) {
			  	  if(resultValue <= Double.parseDouble(thresholdValue))
			  		  flag = true;
			  }
			     
			  if(singleWordOPerator.equals("=")) {
			  	  if(resultValue == Double.parseDouble(thresholdValue))
			  		  flag = true;
			  }
			     
			  if(singleWordOPerator.equals("<>") || singleWordOPerator.equals("!=")|| singleWordOPerator.equals("^=")) {
			  	  if(resultValue != Double.parseDouble(thresholdValue))
			  		  flag = true;
			  }
		} catch(NumberFormatException ne){
			LOGGER.error("", ne);
		} catch(Exception e){
			LOGGER.error("", e);
		}
		return flag;
		
	}
	
}

  

