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
package com.cs.log.util.api;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.codehaus.jackson.JsonParser;

import com.cs.log.sevice.api.ChunkDataProcessor;
import com.cs.log.sevice.api.DataPreparedCallback;
import com.cs.log.sevice.api.DataReceiverCallback;

/**
 * @author Uttam
 *
 */
public interface ILogJsonParser {

	/**
	 * @param processData
	 */
	public void getUniqueIds(final DataReceiverCallback processData);
	
	/**
	 * @param ids
	 * @param processData
	 */
	public void jsonToObjectMapping(final List<String> ids, final ChunkDataProcessor processData);
	
	/**
	 * @param path
	 * @param fileSize
	 * @param callBack
	 * @return
	 */
	public CompletableFuture<Void> prepareJsonData(final String path, final Long fileSize, DataPreparedCallback callBack);
	
	/**
	 * @return
	 */
	public JsonParser getJsonParser();
	
	/**
	 * @param fileName
	 */
	public  void setFileName(final String fileName);
	
}
