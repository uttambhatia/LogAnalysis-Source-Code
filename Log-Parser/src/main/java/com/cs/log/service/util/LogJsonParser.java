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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cs.log.sevice.api.ChunkDataProcessor;
import com.cs.log.sevice.api.DataPreparedCallback;
import com.cs.log.sevice.api.DataReceiverCallback;
import com.cs.log.sevice.api.LogParserStatusService;
import com.cs.log.util.api.CommonUtil;
import com.cs.log.util.api.ILogJsonParser;
import com.cs.log.util.api.LogFileProcessingStatus;
import com.cs.log.util.api.LogStatus;
import com.cs.log.web.config.PropertyConfigurer;

/**
 * @author Uttam
 *
 */
@Component
public class LogJsonParser implements ILogJsonParser{

	Logger logger = LoggerFactory.getLogger(LogJsonParser.class);

	@Autowired
	private PropertyConfigurer config;

	@Autowired
	private CommonUtil utility;

	@Autowired
	private LogParserStatusService statusService;
	
	/**
	 * ThreadLocal thread for storing per request data.
	 */
	private static ThreadLocal<String> FILE = new ThreadLocal<>();


	/**
	 * @param processData
	 */
	public void getUniqueIds(final DataReceiverCallback processData) {

		Set<String> uniqueIds = new HashSet<>();
		try (JsonParser parser = getJsonParser()){

			while (parser.nextToken() != JsonToken.END_ARRAY) {

				while (parser.nextToken() != JsonToken.END_OBJECT) {
					JsonNode node = parser.readValueAsTree();

					if(node!=null && node.get(Log.ID)!=null){
						
						String id = node.get(Log.ID).getTextValue();
						
						//if(!statusService.isProcessedIdPresent(getFile().getPath(), id)){
							uniqueIds.add(id);
						//}
						
					}else{

						List<String>  result = new ArrayList<String>(uniqueIds);
						processData.processData(result);
						uniqueIds.clear();
						return;
					}

					if (uniqueIds.size() > Integer.valueOf(config.getSeqProcessingLimits())){
						List<String>  result = new ArrayList<String>(uniqueIds);
						processData.processData(result);
						uniqueIds.clear();
					}

				}

			}

		} catch (IOException ex) {
			logger.error(ex.getMessage());
		} 


	}


	/**
	 * @param ids
	 * @return
	 */
	public void jsonToObjectMapping(final List<String> ids, final ChunkDataProcessor processData) {

		File originalFile = getFile();
		//return CompletableFuture.supplyAsync(() -> {

		File[] files ;
		ExecutorService chunkThreadService;

		if(!Boolean.valueOf(config.getEnableFileChunk())){
			files = new File[]{originalFile};
			chunkThreadService = Executors.newSingleThreadExecutor();
		}else{
			files = utility.listChunkFilesForOriginalFile(originalFile);
			chunkThreadService = Executors.newCachedThreadPool();
		}

		List<Future<List<Log>>> chunkCallables = new ArrayList<>();

		for (File file : files){
			Future<List<Log>> result = chunkThreadService.submit(new ChunkJsonFileCallable(ids, file.getPath()));
			chunkCallables.add(result);
		}

		for(Future<List<Log>> callable :chunkCallables){
			try {

				List<Log> result = callable.get();
				if(!result.isEmpty()){
					processData.processChunkData(result);
				}
				//logs.addAll(callable.get());

			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}

		//return logs;

		//});


	}


	/**
	 * @param path
	 * @param fileSize
	 */
	public CompletableFuture<Void> prepareJsonData(final String path, final Long fileSize, DataPreparedCallback callBack) {

		return CompletableFuture.supplyAsync(() -> {
			try {

				JsonGenerator generator = getJsonGenerator(path);
				generator.useDefaultPrettyPrinter();
				Random rand = new Random((int) Math.pow(10, 2));

				List<String> statuses = Arrays.asList(new String[]{LogStatus.STARTED.name(),LogStatus.FINISHED.name()});

				generator.writeStartArray();

				for (int i = 0; i < 6000*fileSize; i++){

					for(String status :statuses){

						Integer num = rand.nextInt();

						generator.writeStartObject();
						generator.writeStringField(Log.ID, "EVENT_"+i);
						generator.writeStringField(Log.STATUS, status);
						generator.writeStringField(Log.HOST, Math.abs(num)%2==0?""+Math.abs(num)/1000:null);
						generator.writeStringField(Log.TYPE, Math.abs(num)%2==0?"APPLICATION":null);
						generator.writeNumberField(Log.TIMESTAMP, Math.abs(num));
						generator.writeEndObject();
					}

				}

				generator.writeEndArray();
				generator.close();

				callBack.dataPrepared(LogFileProcessingStatus.SUCCESS.name());
				logger.info("JSON file created successfully at - " + path);

			} catch (IOException ioex) {
				ioex.printStackTrace();
			} 

			return null;

		});

	}


	/**
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	private JsonGenerator getJsonGenerator(final String filePath) throws IOException {
		JsonFactory jsonfactory = new JsonFactory();
		File jsonDoc = new File(filePath);
		JsonGenerator generator = jsonfactory.createJsonGenerator(jsonDoc, JsonEncoding.UTF8);
		return generator;
	}




	/**
	 * @param fileName
	 */
	public  void setFileName(final String fileName){
		FILE.set(fileName);
	}

	/**
	 * @return
	 */
	public static File getFile(){
		return new File(FILE.get()); 
	}

	/**
	 * @return
	 * @throws IOException
	 * @throws JsonParseException
	 */
	public JsonParser getJsonParser() {

		JsonFactory jsonFactory = new MappingJsonFactory();
		JsonParser parser = null;
		try {
			parser = jsonFactory.createJsonParser(getFile());
		} catch (IOException e) {

		}
		return parser;

	}

}

