/*
 * Copyright (c) 2018, APAR and/or its affiliates. All rights reserved.
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
package com.cs.log.persistent.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.cs.log.service.util.Log;

@Entity
@Table(name="log")
@NamedQuery(name="PersistentLog.findAll", query="SELECT todo FROM PersistentLog todo")
public class PersistentLog {

	@Id
	@GeneratedValue
	@Column(name="id")
	private Integer id;
	
	@Column(name="logid")
	private String logId;
	
	@Column(name="duration")
	private Long duration;
	
	@Column(name="host")
	private String host;
	
	@Column(name="applType")
	private String type;
	
	@Column(name="alert")
	private Boolean alert;
	
	
	private transient Log started;
	private transient Log finished;
	
	public PersistentLog(){}
	
	public PersistentLog(final String logId, final Long duration, final Boolean alert,String host,String type){
		this.logId = logId;
		this.duration = duration;
		this.alert = alert;
		this.host = host;
		this.type = type;
	}
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * @return the taskTitle
	 */
	public Long getDuration() {
		return duration;
	}
	/**
	 * @param taskTitle the taskTitle to set
	 */
	public void setDuration(Long duration) {
		this.duration = duration;
	}

	public String getLogId() {
		return logId;
	}

	public void setLogId(String logId) {
		this.logId = logId;
	}

	public Log getStarted() {
		return started;
	}

	public void setStarted(Log started) {
		this.started = started;
	}

	public Log getFinished() {
		return finished;
	}

	public void setFinished(Log finished) {
		this.finished = finished;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the alert
	 */
	public Boolean getAlert() {
		return alert;
	}

	/**
	 * @param alert the alert to set
	 */
	public void setAlert(Boolean alert) {
		this.alert = alert;
	}

	
}
