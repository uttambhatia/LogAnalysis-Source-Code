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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cs.log.persistence.api.LogParserDao;
import com.cs.log.sevice.api.LogParserStatusService;
import com.cs.log.web.config.PropertyConfigurer;

/**
 * @author Uttam
 *
 */
@Service
public class LogParserStatusServiceImpl implements LogParserStatusService{

	private Map<String,String> dataPreparationStatus = new  ConcurrentHashMap<>();
	private Map<String,String> dataParsingStatus = new  ConcurrentHashMap<>();
	private Map<String,List<String>> processedDataMap = new  ConcurrentHashMap<>();

	@Autowired
	private LogParserDao parserDao;
	
	@Autowired
	private PropertyConfigurer config;
	
	/** (non-Javadoc)
	 * @see com.cs.log.sevice.api.LogParserStatusService#getFileDataPreparationStatus(java.lang.String)
	 */
	@Override
	public String getFileDataPreparationStatus(String fileName) {

		return  dataPreparationStatus.get(fileName);

	}

	/** (non-Javadoc)
	 * @see com.cs.log.sevice.api.LogParserStatusService#getFileDataParsingStatus(java.lang.String)
	 */
	@Override
	public String getFileDataParsingStatus(String fileName) {

		return dataParsingStatus.get(fileName);

	}

	/** (non-Javadoc)
	 * @see com.cs.log.sevice.api.LogParserStatusService#setFileDataPreparationStatus(java.lang.String, java.lang.String)
	 */
	@Override
	public void setFileDataPreparationStatus(String fileName, String status) {
		dataPreparationStatus.put(fileName, status);
	}

	/** (non-Javadoc)
	 * @see com.cs.log.sevice.api.LogParserStatusService#setFileDataParsingStatus(java.lang.String, java.lang.String)
	 */
	@Override
	public void setFileDataParsingStatus(String fileName, String status) {
		dataParsingStatus.put(fileName, status);
	}

	@Override
	public void setProcessedIdList(final String fileName, final List<String> idList){

		if(!processedDataMap.get(fileName).isEmpty()){
			
			List<String> currentList = processedDataMap.get(fileName);
			Integer totalSize = currentList.size() + idList.size();
			
			if(totalSize >= Integer.valueOf(config.getCacheSize())) {
				List<String> toRemoveList = currentList.subList(0, idList.size()-1);
				
				currentList.removeAll(toRemoveList);
				currentList.addAll(idList);
				processedDataMap.put(fileName,currentList);
				
			}else{
				processedDataMap.get(fileName).addAll(idList);
			}
			
		}else {
			processedDataMap.put(fileName, idList);
		}

	}

	@Override
	public Boolean isProcessedIdPresent(final String filePath, final String id){

		if(processedDataMap.get(filePath)!=null && !processedDataMap.get(filePath).isEmpty()){
			return processedDataMap.get(filePath).contains(id);
		}else {
			return parserDao.isProcessed(id);
		}

	}

}
