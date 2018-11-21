/*
 * Copyright (c) 2018, CS and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of VirtusaPolaris or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 
package com.cs.log.service.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.MappingJsonFactory;

import com.cs.log.service.util.Log;

/**
 * @author Uttam
 *
 */
public class ChunkJsonFileCallable implements Callable<List<Log>> {

	private List<String> idList = Collections.emptyList(); 
	private String fileName = null;
	
	public ChunkJsonFileCallable(){}
	
	/**
	 * @param IdList
	 * @param fileName
	 */
	public ChunkJsonFileCallable(List<String> IdList, final String fileName){
		this.idList = IdList;
		this.fileName = fileName;
	}
	
	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public List<Log> call() throws Exception {

		JsonParser parser = getJsonParser(fileName);

		List<Log> logs = new ArrayList<>();
		try  {

			while (parser.nextToken() != JsonToken.END_ARRAY) {

				while (parser.nextToken() != JsonToken.END_OBJECT) {
					JsonNode node = parser.readValueAsTree();

					if(node!=null && node.get(Log.ID)!=null){

						String id = node.get(Log.ID).getTextValue();
						if(idList.contains(id)){

							String status = node.get(Log.STATUS).getTextValue();
							Long timestamp = node.get(Log.TIMESTAMP).getLongValue();
							String host = node.get(Log.HOST)!=null ? node.get(Log.HOST).getTextValue():null;
							String type = node.get(Log.TYPE)!=null ? node.get(Log.TYPE).getTextValue(): null;

							Log log = new Log(id,timestamp,status, host,type); 
							logs.add(log);

						}
					}else{
						return logs;
					}
				}

			}

			return logs;

		} catch (IOException ioex) { }

		return Collections.emptyList();

		
	}
	
	/**
	 * @param fileName
	 * @return
	 */
	protected JsonParser getJsonParser(final String fileName) {

		JsonFactory jsonFactory = new MappingJsonFactory();
		JsonParser parser = null;
		try {
			parser = jsonFactory.createJsonParser(new File(fileName));
		} catch (IOException e) {

		}
		return parser;

	}

}
