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
package com.cs.log.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.cs.log.dto.DataTableProps;
import com.cs.log.dto.LogDto;
import com.cs.log.dto.ProcessStatus;
import com.cs.log.sevice.api.LogParserServices;

/**
 * @author Uttam Kumar Bhatia
 *
 */
@RestController
public class LogParserController extends AbstractLogParserController{

	@Autowired
	LogParserServices logParserService;


	/**
	 * @param taskId
	 * @return
	 */
	@RequestMapping(value = "/startFileProcessor", method = RequestMethod.GET)
	public  @ResponseBody ResponseEntity<Void> startFileProcessor(@RequestParam ("fileName") String fileName) {

		if(StringUtils.isEmpty(fileName)){
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}

		try {
			logParserService.startFileProcessor(fileName);
		} catch (Exception e) {	
			logger.error(e.getMessage());
			return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<Void>(HttpStatus.OK);

	}	


	/**
	 * @param pageable
	 * @return
	 */
	@RequestMapping(value="/eventList",method=RequestMethod.GET)
	public @ResponseBody ModelAndView  list( DataTableProps page, ModelAndView model){
		
		try {
			
			List<LogDto> logs = logParserService.listAllByPage(new DataTableProps(page.getStart()+1, page.getLength()));
			Long totalCount = logParserService.totalEventCount();
			
			model.addObject("recordsTotal", totalCount);
			model.addObject("recordsFiltered", totalCount);
			model.addObject("data", logs);

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
		return model;

	} 


	/**
	 * @param taskId
	 * @return 
	 * @return
	 */
	@RequestMapping(value = "/prepareJsonData", method = RequestMethod.GET)
	public  ResponseEntity<Void> prepareJsonData(@RequestParam ("fileName") String fileName, @RequestParam ("fileSize") Long fileSize) {

		if(fileSize <=0 && StringUtils.isEmpty(fileName)){
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}

		try {
			logParserService.prepareData(fileName, fileSize);
		} catch (Exception e) {	
			logger.error(e.getMessage());
			return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<Void>(HttpStatus.OK);

	}	

	/**
	 * @param fileName
	 * @return
	 */
	@RequestMapping(value = "/getDataPreparationStatusForFile", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<ProcessStatus> getDataPreparationStatusForFile(@RequestParam ("fileName")String fileName) {
		if(StringUtils.isEmpty(fileName)){
			return new ResponseEntity<ProcessStatus>(HttpStatus.BAD_REQUEST);
		}

		String status =  logParserService.getDataPreparationStatusForFile(fileName);
		return new ResponseEntity<ProcessStatus>(new ProcessStatus(status), HttpStatus.OK);
	}

	/**
	 * @param fileName
	 * @return
	 */
	@RequestMapping(value = "/getDataParsingStatusForFile", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<ProcessStatus> getDataParsingStatusForFile(@RequestParam ("fileName")String fileName) {
		if(StringUtils.isEmpty(fileName)){
			return new ResponseEntity<ProcessStatus>(HttpStatus.BAD_REQUEST);
		}

		String status =  logParserService.getDataParsingStatusForFile(fileName);
		return new ResponseEntity<ProcessStatus>(new ProcessStatus(status), HttpStatus.OK);
	}


}

