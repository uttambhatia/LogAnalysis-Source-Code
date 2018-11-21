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
package com.cs.log.persistence.api;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.cs.log.persistent.entity.PersistentLog;

public interface LogParserDao {

	/**
	 * @param entity
	 * @return
	 */
	public <T> T save (T entity);
	
	/**
	 * @param entities
	 * @return
	 */
	public <T> void saveAll(List<T> entities);
	/**
	 * @param entity
	 * @return
	 */
	public <T> T update (T entity);
	/**
	 * @param entity
	 * @param id
	 * @return
	 */
	public <T> T findById (Class<?> entity, Integer id);
	/**
	 * @param clazz
	 * @param id
	 */
	public <T> void delete(Class<?> clazz, Integer id);
	
	/**
	 * @param entity
	 * @param id
	 * @return
	 */
	public List<PersistentLog> findAll() ;
	
	/**
	 * @param page
	 * @return
	 */
	public List<PersistentLog> findAll(Pageable page) ;
	
	/**
	 * @param clazz
	 * @return
	 */
	public Long totalCount(Class<?> clazz);
	
	/**
	 * @param eventId
	 * @return
	 */
	public Boolean isProcessed(final String eventId);
	
}
