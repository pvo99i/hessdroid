/*
 * Copyright (C) 2009 hessdroid@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ast.tests.base;

import java.io.Serializable;

import org.junit.Before;


/**
 * Base class for all hessdroid tests. The formal generic parameter <tt>T</tt> is used to specify the type
 * of the web service's interface. The interface must not implement or extends another interface except
 * {@link Serializable} if reference types are used in method signatures.
 * 
 * @author hessdroid@gmail.com
 *
 * @param <T> the web service's interface
 */
public abstract class HessianTest<T> {

	// Server-Side
	private T _serviceImpl;
	private Class<T> _serviceImplClass;
	private HessianSkeleton service;
	
	// Client-Side
	private T testApiClient;
	
	@SuppressWarnings("unchecked")
	public HessianTest(T testApiInstance) throws Exception  {
		_serviceImpl = testApiInstance;
		_serviceImplClass = (Class<T>) testApiInstance.getClass().getInterfaces()[0];
	}
	
	@Before public void setUp() throws Exception {
		HessianTestProxyFactory factory = new HessianTestProxyFactory();
		
		service = new HessianSkeleton(_serviceImpl, _serviceImplClass);
		testApiClient = factory.create(_serviceImplClass, 
				Thread.currentThread().getContextClassLoader(), 
				service);
	}
	
	public T client() { return testApiClient; };
}
