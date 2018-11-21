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
package com.cs.log.persistence.spi;


import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.cs.log.controller.LogParserController;
import com.cs.log.persistence.api.LogParserDao;
import com.cs.log.persistent.entity.PersistentLog;

@Repository
public class LogParserDaoImpl implements LogParserDao {

	Logger logger = LoggerFactory.getLogger(LogParserController.class);
	
	@PersistenceContext
	private EntityManager entityManager;

	/** (non-Javadoc)
	 * @see com.cs.log.persistence.api.LogParserDao#save(java.lang.Object)
	 */
	@Override
	public <T> T save(T entity) {
		entityManager.persist(entity);
		return entity;
	}

	@Override
	public <T> void saveAll(List<T> entities) {
		
		for(int i = 0 ; i < entities.size() ; i++){
			if(i % 20 == 0){
				entityManager.flush();
			}
			
			entityManager.persist(entities.get(i));
		}
		
		
	}
	
	/** (non-Javadoc)
	 * @see com.cs.log.persistence.api.LogParserDao#update(java.lang.Object)
	 */
	@Override
	public <T> T update(T entity) {
		return entityManager.merge(entity);
	}

	/** (non-Javadoc)
	 * @see com.cs.log.persistence.api.LogParserDao#findById(java.lang.Class, java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T findById(Class<?> entity, Integer id) {
		return (T) entityManager.find(entity, id);
	}

	/**
	 * @param clazz
	 * @param id
	 */
	public <T> void delete(Class<?> clazz, Integer id) {
		T entity = findById(clazz,id);
		if (entity != null) {
			entityManager.remove(entity);
		}
	}

	//@Override
	@SuppressWarnings("unchecked")
	public List<PersistentLog> findAll() {

		Query query = entityManager.createQuery("select log from PersistentLog log");
		return query.getResultList();
	    
	}

	/** (non-Javadoc)
	 * @see com.cs.log.persistence.api.LogParserDao#findAll(org.springframework.data.domain.Pageable)
	 */
	public List<PersistentLog> findAll(Pageable page) {

		Query query = entityManager.createQuery("select log from PersistentLog log");
		int pageNumber = page.getPageNumber();
		int pageSize = page.getPageSize();
		query.setFirstResult(pageNumber + 1); 
		query.setMaxResults(pageSize);
		List <PersistentLog> result = query.getResultList();
		return result;
	    
	}

	/** (non-Javadoc)
	 * @see com.cs.log.persistence.api.LogParserDao#totalCount(java.lang.Class)
	 */
	@Override
	public Long totalCount(Class<?> clazz) {
		Query query = entityManager.createQuery("select count(log) from PersistentLog log");
		return (Long) query.getSingleResult();
	}
	
	@Override
	public Boolean isProcessed(final String eventId) {
		
		Query query = entityManager.createQuery("select count(log) from PersistentLog log where log.logId=:eventId");
		query.setParameter("eventId", eventId);
		Long result =  (Long) query.getSingleResult();
		return result > 0 ? true : false;
		
	}

}
