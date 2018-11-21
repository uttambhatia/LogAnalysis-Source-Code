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
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.cs.log.persistent.entity.PersistentLog;
import com.cs.log.sevice.api.ChunkDataProcessor;

/**
 * @author Uttam
 *
 */
public class RecursiveLogAction extends AbstractRecursiveLogAction{

	private static final long serialVersionUID = 1L;
	/**
	 * @param arr
	 * @param lo
	 * @param hi
	 * @param filename
	 */
	public RecursiveLogAction(List<String> arr, int lo, int hi, String filename) {
		super(arr, lo, hi, filename);
	}

	/* (non-Javadoc)
	 * @see com.cs.log.service.util.AbstractRecursiveLogAction#partitionTasks(java.util.List, int, int, java.lang.String)
	 */
	@Override
	public AbstractRecursiveLogAction partitionTasks(List<String> array, int low, int high, String filePath) {
		return new RecursiveLogAction(array, low, high, filePath);
	}

	/* (non-Javadoc)
	 * @see com.cs.log.service.util.AbstractRecursiveLogAction#processTasks(int, int)
	 */
	@Override
	public void processTasks(int low, int high) {

		fileReader.setFileName(filePath);
		fileReader.jsonToObjectMapping(array.subList(low, high), new ChunkDataProcessor() {

			@Override
			public void processChunkData(List<Log> chunkData) {
				
				CompletableFuture<Map<String, List<PersistentLog>>> result = util.sortGroupByLogs(chunkData).thenCompose(groupedMap -> util.filterLogsWithThreshold(groupedMap));
				try {
					
					Map<String, List<PersistentLog>> mapData = result.get();
					fileProcessorEvent.saveBulkData(mapData, filePath);
					
				} catch (InterruptedException | ExecutionException e) {
					logger.error(e.getMessage());
				}


			}
		});

	}


}
