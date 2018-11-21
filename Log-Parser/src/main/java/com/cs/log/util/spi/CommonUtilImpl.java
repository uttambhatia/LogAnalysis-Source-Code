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
package com.cs.log.util.spi;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cs.log.persistent.entity.PersistentLog;
import com.cs.log.service.util.Log;
import com.cs.log.util.api.CommonUtil;
import com.cs.log.util.api.LogStatus;
import com.cs.log.web.config.PropertyConfigurer;

/**
 * @author Uttam Kumar Bhatia
 *
 */

@Component
public class CommonUtilImpl implements CommonUtil{

	@Autowired
	private PropertyConfigurer config;

	static public LocalDateTime toLdt(Date date) {
	    GregorianCalendar cal = new GregorianCalendar();
	    cal.setTime(date);
	    ZonedDateTime zdt = cal.toZonedDateTime();
	    return zdt.toLocalDateTime();
	}

	static public Date fromLdt(LocalDateTime ldt) {
		ZonedDateTime zdt = ZonedDateTime.of(ldt, ZoneId.systemDefault());
		GregorianCalendar cal = GregorianCalendar.from(zdt);
		return cal.getTime();
	}

	/**
	 * @param groupedMap
	 * @return
	 */
	public CompletableFuture<Map<String, List<Log>>> sortGroupByLogs(List<Log> groupedMap) {

		return CompletableFuture.supplyAsync(() -> {
			return groupedMap.stream().sorted((log1,log2)->log1.getTimestamp().compareTo(log2.getTimestamp())).collect(Collectors.groupingBy(Log::getId));
		});
	}

	/**
	 * @param groupedMap
	 * @return
	 */
	public CompletableFuture<Map<String, List<Log>>> filterLogs(Map<String, List<Log>> groupedMap) {

		return CompletableFuture.supplyAsync(() -> {
			return groupedMap.entrySet().parallelStream().filter((E) -> (E.getValue().get(0).getStatus().equals(LogStatus.STARTED.name())
					&& E.getValue().get(E.getValue().size()-1).getStatus().equals(LogStatus.FINISHED.name())))
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		});

	}

	/**
	 * @param groupedMap
	 * @return
	 */
	public CompletableFuture<Map<String, List<PersistentLog>>> filterLogsWithThreshold(Map<String, List<Log>> groupedMap) {

		Long maxEventDuration = Long.valueOf(config.getEventDurationThreshold());

		return CompletableFuture.supplyAsync(() -> {

			Map<String, List<PersistentLog>> result = new HashMap<>();

			groupedMap.entrySet().stream().forEach((E) -> {
				List<Log> logList = E.getValue();

				List<PersistentLog> persistentList = new ArrayList<>();
				for(int i = 0 ; i < logList.size() ; i++){
					if(logList.size()-1 >= i+1){

						Log current = logList.get(i);
						Log next = logList.get(i+1);

						String host = current.getHost()!=null?current.getHost():next.getHost();
						String type = current.getType()!=null?current.getType():next.getType();

						if( (current.getStatus().equalsIgnoreCase(LogStatus.STARTED.name()) && next.getStatus().equalsIgnoreCase(LogStatus.FINISHED.name()))){
							Long diff = (next.getTimestamp() - current.getTimestamp());
							if ( (diff)/1000 >= maxEventDuration){

								PersistentLog persistentLog = new PersistentLog(current.getId(),diff,Boolean.TRUE,host,type);
								persistentList.add(persistentLog);

							}
						}
					}
				}

				result.put(E.getKey(), persistentList);

			});

			return result;
		});

	}


	@Override
	public String getFileNameNoExtension(final File file) {

		String fileName = null;
		String orginalFilPath = file!=null? file.getPath():null;
		if(orginalFilPath!=null){
			String ext = orginalFilPath.substring(orginalFilPath.lastIndexOf("."));
			fileName = file.getName().replace(ext, "");
		}
		
		return fileName;
	}

	@Override
	public File[] listChunkFilesForOriginalFile(final File orginalFile) {

		String fileNoExt = getFileNameNoExtension(orginalFile);
		String dir = config.getDataStagingDir() + File.separator + fileNoExt + "_split" + File.separator ;
		File[] files = new File(dir).listFiles();
		
		return files;
	}
	
	/**
	 * @return
	 */
	@Override
	public PropertyConfigurer getConfigProps(){
		return config;
	}

}
