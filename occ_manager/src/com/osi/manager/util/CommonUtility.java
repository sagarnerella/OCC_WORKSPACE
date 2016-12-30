/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.osi.manager.util;

import java.util.StringTokenizer;


public class CommonUtility {
  
    
 public static String[][] Split(String str,String maintoken,String subtoken)
	{
		int i,j,nmaintokenCount,nsubtokenCount;
		StringTokenizer strMainToken, strSubToken;
		String[] result[],subtokens;

        strMainToken = new StringTokenizer(str,maintoken);
        nmaintokenCount = strMainToken.countTokens();
        result = new String[nmaintokenCount][];

		for(i=0; i<nmaintokenCount ;i++)
		{
			strSubToken = new StringTokenizer(strMainToken.nextToken(),subtoken);
			nsubtokenCount = strSubToken.countTokens();
			subtokens = new String[nsubtokenCount];

			for(j=0; j<nsubtokenCount;j++)
			{
				subtokens[j] = strSubToken.nextToken();
			}
			result[i] = subtokens;
		}
		return result;
	}   
 
 
public static String[] Split(String str,String token)
	{
		int i,ntokenCount;
		StringTokenizer tokenizer;
		String[] result;

        tokenizer = new StringTokenizer(str,token);
        ntokenCount = tokenizer.countTokens();

        result = new String[ntokenCount];

		for(i=0; i<ntokenCount ;i++)
		{
				result[i] = tokenizer.nextToken();
		}
		return result;
	} 
}
