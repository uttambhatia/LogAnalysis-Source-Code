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
package com.cs.log.service.spi;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cs.log.persistence.api.LogParserDao;
import com.cs.log.persistent.entity.PersistentLog;
import com.cs.log.service.util.JsonFileChunker;
import com.cs.log.service.util.RecursiveLogAction;
import com.cs.log.sevice.api.DataReceiverCallback;
import com.cs.log.sevice.api.FileProcessorEvent;
import com.cs.log.sevice.api.LogParserStatusService;
import com.cs.log.util.api.ILogJsonParser;
import com.cs.log.util.api.LogFileProcessingStatus;
import com.cs.log.web.config.PropertyConfigurer;

/**
 * @author Uttam
 *
 */
@Service
@Qualifier("fileProcessor")
public class FileProcessorEventService implements FileProcessorEvent{

	Logger logger = LoggerFactory.getLogger(FileProcessorEventService.class);
	
	@Autowired
	private LogParserDao parserDao;
	
	@Autowired 
	private ILogJsonParser fileReader;
	
	@Autowired
	private LogParserStatusService statusService;
	
	@Autowired
	private PropertyConfigurer config;
	
	/* (non-Javadoc)
	 * @see com.cs.log.service.spi.FileProcessorEvent#saveBulkData(java.util.Map, java.lang.String)
	 */
	@Override
	@Transactional
	public void saveBulkData(final Map<String, List<PersistentLog>> bulkData, String filePath) {

		bulkData.entrySet().stream().forEach((E)->{
			fileReader.setFileName(filePath);
			parserDao.saveAll(E.getValue());
		});
		
		//statusService.setProcessedIdList(filePath, new ArrayList<>(bulkData.keySet()));
		logger.info("Listened the event for file ::: "+filePath);
		
	}

	/* (non-Javadoc)
	 * @see com.cs.log.service.spi.FileProcessorEvent#onFileChange(java.lang.String)
	 */
	@Override
	public void onFileChange(final String filePath) {
		
		if(Boolean.valueOf(config.getEnableFileChunk())){
			new JsonFileChunker(filePath, Integer.valueOf(config.getFileChunkSize())).start();
		}
		
		fileReader.setFileName(filePath);
		fileReader.getUniqueIds(new DataReceiverCallback() {
			
			@Override
			public void processData(final List<String> result) {
				ForkJoinPool.commonPool().invoke(new RecursiveLogAction(result,0,result.size(),filePath));
			}
			
		});
	
		String file = Paths.get(filePath).getFileName().toString();
		statusService.setFileDataParsingStatus(file, LogFileProcessingStatus.SUCCESS.name());
	}
	
	
}
