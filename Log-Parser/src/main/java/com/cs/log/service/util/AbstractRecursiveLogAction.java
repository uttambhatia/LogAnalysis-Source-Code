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

import java.util.List;
import java.util.concurrent.RecursiveAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cs.log.persistence.spi.LogApplicationContextAware;
import com.cs.log.sevice.api.FileProcessorEvent;
import com.cs.log.util.api.CommonUtil;
import com.cs.log.web.config.PropertyConfigurer;

public abstract class AbstractRecursiveLogAction extends RecursiveAction {


	Logger logger = LoggerFactory.getLogger(FileWatchWorker.class);
	private static final long serialVersionUID = 1L;
	private int SEQUENTIAL_THRESHOLD = 50;

	protected FileProcessorEvent fileProcessorEvent;
	protected PropertyConfigurer config;
	protected LogJsonParser fileReader;
	protected CommonUtil util;
	
	protected int low;
	protected int high;
	protected List<String> array;
	protected String filePath;

	/**
	 * @param arr
	 * @param lo
	 * @param hi
	 * @param filename
	 */
	public AbstractRecursiveLogAction(List<String> arr, int lo, int hi, String filePath) {

		array = arr;
		low   = lo;
		high  = hi;
		this.filePath = filePath;
		fileProcessorEvent  =  LogApplicationContextAware.getBeanByClass(FileProcessorEvent.class);
		
		config = LogApplicationContextAware.getBeanByClass(PropertyConfigurer.class);
		SEQUENTIAL_THRESHOLD = (int) (Integer.valueOf(config.getSeqProcessingLimits())*0.1);
		
		fileReader = LogApplicationContextAware.getBeanByClass(LogJsonParser.class);
		
		util = LogApplicationContextAware.getBeanByClass(CommonUtil.class);
		
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.RecursiveAction#compute()
	 */
	@Override
	protected void compute() {

		logger.info("Current Threshold  --> "+SEQUENTIAL_THRESHOLD);
		
		if( (high - low) <= SEQUENTIAL_THRESHOLD) {
			
			logger.debug("Low  -- : " + low +" to High --: "+high);
			processTasks( low, high);
			logger.debug("Processed ==::: Low  -- : " + low +" to High --: "+high);
			
		} else {

			int mid = low + (high - low) / 2;
			AbstractRecursiveLogAction left  = partitionTasks(array, low, mid,filePath);
			AbstractRecursiveLogAction right = partitionTasks(array, mid, high,filePath);
			left.fork();
			right.compute();
			left.join();

		}

	}
	
	/**
	 * @param array
	 * @param low
	 * @param high
	 * @param fileName
	 * @return
	 */
	public abstract AbstractRecursiveLogAction partitionTasks(List<String> array,int low, int high, String fileName);
	
	/**
	 * @param low
	 * @param high
	 */
	public abstract void processTasks(int low, int high);

}
