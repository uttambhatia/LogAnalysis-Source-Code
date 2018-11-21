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
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cs.log.sevice.api.FileProcessorEvent;

/**
 * @author Uttam
 *
 */
public abstract class MyWatchQueueReader implements Runnable {

	Logger logger = LoggerFactory.getLogger(MyWatchQueueReader.class);
	
	protected List<String> fileExtToBeWatched = new ArrayList<>();
	private WatchService myWatcher;
	private Path toWatch;

	private FileProcessorEvent fileProcessorEvent;

	/**
	 * @param myWatcher
	 * @param toWatch
	 */
	public MyWatchQueueReader(WatchService myWatcher, Path toWatch) {
		this.myWatcher = myWatcher;
		this.toWatch = toWatch;
		fileProcessorEvent  =  getFileProcessor();
	}
	
	/**
	 * @param toWatch
	 * @throws IOException
	 */
	public MyWatchQueueReader( Path toWatch) throws IOException {
		this.myWatcher = toWatch.getFileSystem().newWatchService();
		this.toWatch = toWatch;
		fileProcessorEvent  =  getFileProcessor();
	}
	/**
	 * In order to implement a file watcher, we loop forever
	 * ensuring requesting to take the next item from the file
	 * watchers queue.
	 */
	@Override
	public void run() {

		try {

			WatchKey key = myWatcher.take();
			while(key != null) {
				
				for (WatchEvent event : key.pollEvents()) {
					System.out.printf("Received %s event for file: %s\n", event.kind(), event.context() );

					String fileModified = toWatch.toString()+File.separator+event.context().toString();
					String[] extension = event.context().toString().split("\\.(?=[^\\.]+$)");
					if("ENTRY_MODIFY".equalsIgnoreCase(event.kind().name()) && fileExtToBeWatched.contains(extension[1])){
						fileProcessorEvent.onFileChange(fileModified);						
					}

				}

				key.reset();
				key = myWatcher.take();
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
		System.out.println("Stopping thread");
	}

	/**
	 * @return
	 */
	public abstract FileProcessorEvent getFileProcessor ();
	
	/**
	 * @return
	 */
	/**
	 * @return
	 */
	protected Path getPathToWatch(){
		return toWatch;
	}
	
	/**
	 * @return
	 */
	protected WatchService getWatchService(){
		return myWatcher;
	}
	
	/**
	 * @param files
	 */
	protected void fileExtToBeWatched(List<String> files){
		fileExtToBeWatched.addAll(files);
	}

}
