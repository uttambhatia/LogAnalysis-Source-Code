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

import java.io.IOException;

public class JsonFileChunker extends FileChunker {

	private Integer chunkSize;
	/**
	 * @param filePath
	 * @param chunkSize
	 */
	public JsonFileChunker(final String filePath, final Integer chunkSize){
		super(filePath);
		this.chunkSize = chunkSize;
	}

	/**
	 * 
	 */
	public void start(){
		Integer maxRowsPerChunk = determineRowsPerChunk(chunkSize);
		try {
			chunkFile(maxRowsPerChunk);
		} catch (IOException e) {
		}
	}

	/**
	 * @param chunkSize
	 * @return
	 */
	private Integer determineRowsPerChunk(Integer chunkSize) {
		return 65000 * chunkSize;
	}

	/* (non-Javadoc)
	 * @see com.cs.log.service.util.FileChunker#setHeader()
	 */
	@Override
	public String setHeader() {
		return "[{";
	}

	/* (non-Javadoc)
	 * @see com.cs.log.service.util.FileChunker#setFooter()
	 */
	@Override
	public String setFooter() {
		return "}]";
	}

	/* (non-Javadoc)
	 * @see com.cs.log.service.util.FileChunker#setChunkFileStorageDir()
	 */
	@Override
	public String setChunkFileStorageDir() {
		return null;
	}

}
