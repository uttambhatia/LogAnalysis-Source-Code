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
package com.cs.log.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Uttam
 *
 */
@Component
public class PropertyConfigurer {

	@Value("${log.fileToBeWatched.dir}")
	protected String directoryToWatch;
	
	@Value("${log.fileToBeWatched.extensions}")
	protected String[] fileExtensions;

	@Value("${log.processing.seq.limit}")
	protected String seqProcessingLimits;
	
	@Value("${log.event.duration.threshold}")
	protected String eventDurationThreshold;
	
	@Value("${log.fileWatcher.enable}")
	protected String enableFileWatcher;
	
	@Value("${log.datastaging.dir}")
	protected String dataStagingDir;

	@Value("${log.processing.enableFileChunk}")
	protected String enableFileChunk;
	
	@Value("${log.processing.fileChunkSize}")
	protected String fileChunkSize;
	
	@Value("${log.processing.cacheSize}")
	protected String cacheSize;

	/**
	 * @return
	 */
	public String getDirectoryToWatch() {
		return directoryToWatch;
	}

	/**
	 * @return
	 */
	public String[] getFileExtensions() {
		return fileExtensions;
	}

	/**
	 * @return
	 */
	public String getSeqProcessingLimits() {
		return seqProcessingLimits;
	}

	/**
	 * @return
	 */
	public String getEventDurationThreshold() {
		return eventDurationThreshold;
	}

	/**
	 * @return the enableFileWatcher
	 */
	public String getEnableFileWatcher() {
		return enableFileWatcher;
	}

	/**
	 * @return the dataStagingDir
	 */
	public String getDataStagingDir() {
		return dataStagingDir;
	}

	/**
	 * @return the enableFileChunk
	 */
	public String getEnableFileChunk() {
		return enableFileChunk;
	}

	/**
	 * @return the fileChunkSize
	 */
	public String getFileChunkSize() {
		return fileChunkSize;
	}

	/**
	 * @return the cacheSize
	 */
	public String getCacheSize() {
		return cacheSize;
	}

	
}
