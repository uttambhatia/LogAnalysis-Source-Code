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

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cs.log.dto.LogDto;
import com.cs.log.persistence.api.LogParserDao;
import com.cs.log.persistent.entity.PersistentLog;
import com.cs.log.sevice.api.DataPreparedCallback;
import com.cs.log.sevice.api.FileProcessorEvent;
import com.cs.log.sevice.api.LogParserServices;
import com.cs.log.sevice.api.LogParserStatusService;
import com.cs.log.util.api.ILogJsonParser;
import com.cs.log.util.api.LogFileProcessingStatus;
import com.cs.log.web.config.PropertyConfigurer;

/**
 * @author Uttam
 *
 */
@Service
@Qualifier("parserService")
public class LogParserServicesImpl implements LogParserServices {

	Logger logger = LoggerFactory.getLogger(FileProcessorEventService.class);

	@Autowired 
	private PropertyConfigurer config;

	@Autowired
	private LogParserDao logParserDao;

	@Autowired
	private ILogJsonParser logJsonParser;

	@Autowired
	private LogParserStatusService statusService;

	@Autowired
	private FileProcessorEvent fileProcessor;
	
	/** (non-Javadoc)
	 * @see com.cs.log.sevice.api.LogParserServices#loadAllProcessedLog()
	 */
	@Override
	@Transactional
	public Long totalEventCount() throws Exception {

		Long count = logParserDao.totalCount(PersistentLog.class);
		return count;
		
	}

	/** (non-Javadoc)
	 * @see com.cs.log.sevice.api.LogParserServices#markCompleted(com.cs.log.dto.LogDto)
	 */
	@Override
	@Transactional
	public void startFileProcessor(final String fileName) throws Exception {

		String dir = config.getDataStagingDir();
		if(Boolean.valueOf(config.getEnableFileWatcher())){
			dir = config.getDirectoryToWatch();
		}
		
		statusService.setFileDataParsingStatus(fileName, LogFileProcessingStatus.INPROGRESS.name());
		fileProcessor.onFileChange(dir + File.separator + fileName);

	}

	/** (non-Javadoc)
	 * @see com.cs.log.sevice.api.LogParserServices#listAllByPage(org.springframework.data.domain.Pageable)
	 */
	@Override
	@Transactional
	public List<LogDto> listAllByPage(final Pageable pageable) {

		List<PersistentLog> result = logParserDao.findAll(pageable);
		return result.stream().map(obj -> new LogDto(obj.getLogId(),obj.getDuration(),obj.getType(),obj.getHost(),obj.getAlert())).collect(Collectors.toList());

	}

	/** (non-Javadoc)
	 * @see com.cs.log.sevice.api.LogParserServices#prepareData(java.lang.String, java.lang.Long)
	 */
	@Override
	@Transactional
	public void prepareData(final String fileName, final Long fileSize) throws Exception {

		String dir = config.getDataStagingDir();
		if(Boolean.valueOf(config.getEnableFileWatcher())){
			dir = config.getDirectoryToWatch();
		}
		
		logJsonParser.prepareJsonData(dir + File.separator + fileName, fileSize, new DataPreparedCallback() {

			@Override
			public void dataPrepared(String status) {
				statusService.setFileDataPreparationStatus(fileName, status);

			}
		});
		
		statusService.setFileDataPreparationStatus(fileName, LogFileProcessingStatus.INPROGRESS.name());

	}

	/** (non-Javadoc)
	 * @see com.cs.log.sevice.api.LogParserServices#getDataPreparationStatusForFile(java.lang.String)
	 */
	@Override
	public String getDataPreparationStatusForFile(String fileName) {
		return statusService.getFileDataPreparationStatus(fileName);
	}

	/** (non-Javadoc)
	 * @see com.cs.log.sevice.api.LogParserServices#getDataParsingStatusForFile(java.lang.String)
	 */
	@Override
	public String getDataParsingStatusForFile(String fileName) {
		return statusService.getFileDataParsingStatus(fileName);
	}
	

}
