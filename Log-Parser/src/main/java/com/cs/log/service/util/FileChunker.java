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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Uttam
 *
 */
public abstract class FileChunker {

	private Logger logger = LoggerFactory.getLogger(FileChunker.class);

	private String filePath;

	public FileChunker(){}

	/**
	 * @param filePath
	 */
	public FileChunker(String filePath){
		this.filePath = filePath;

	}

	/**
	 * @param maxRowsPerChunk
	 * @throws IOException
	 */
	public void chunkFile(final Integer maxRowsPerChunk) throws IOException
	{

		String header = setHeader();
		String footer = setFooter();
		String targetDir = setChunkFileStorageDir();


		File bigFile = new File(filePath);
		int i = 1;
		String ext = filePath.substring(filePath.lastIndexOf("."));

		String fileNoExt = bigFile.getName().replace(ext, "");
		File newDir = null;
		if(targetDir != null)
		{
			newDir = new File(targetDir);           
		}
		else
		{
			newDir = new File(bigFile.getParent() + "\\" + fileNoExt + "_split");
		}
		newDir.mkdirs();
		try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath)))
		{
			String line = null;
			int lineNum = 1;
			Path splitFile = Paths.get(newDir.getPath() + "\\" +  fileNoExt + "_" + String.format("%02d", i) + ext);
			BufferedWriter writer = Files.newBufferedWriter(splitFile, StandardOpenOption.CREATE);
			while ((line = reader.readLine()) != null)
			{
				if(lineNum == 1)
				{
					logger.info("new file created '" + splitFile.toString());
					if(header != null && header.length()> 0 && i > 1)
					{
						writer.append(header);
						writer.newLine();
					}
				}

				if(!line.contains("}, {") && lineNum >= maxRowsPerChunk){
					lineNum--;
				}

				if(!(lineNum >= maxRowsPerChunk)){
					writer.append(line);
				}else if((lineNum >= maxRowsPerChunk) && (i >= 1)){
					logger.info(i+" --> " + (i >= 1));
					if(footer != null && footer.length() > 0)
					{
						writer.newLine();
						writer.append(footer);
					}

				}

				if (lineNum >= maxRowsPerChunk)
				{

					writer.close();
					logger.info(", " + lineNum + " lines written to file");
					lineNum = 1;
					i++;
					splitFile = Paths.get(newDir.getPath() + "\\" + fileNoExt + "_" + String.format("%02d", i) + ext);
					writer = Files.newBufferedWriter(splitFile, StandardOpenOption.CREATE);
				}
				else
				{
					writer.newLine();
					lineNum++;
				}
			}

			writer.close();
			logger.info(", " + lineNum + " lines written to file");
		}

		logger.info("file '" + bigFile.getName() + "' split into " + i + " files");
	}

	/**
	 * @return
	 */
	public abstract String setHeader();
	/**
	 * @return
	 */
	public abstract String setFooter();
	/**
	 * @return
	 */
	public abstract String setChunkFileStorageDir();

}
