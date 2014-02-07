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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.io.HessianRemoteObject;

/**
 * Factory for {@link HessianTestProxy} that extends {@link HessianProxyFactory} in order to 
 * reuse most of it's functionality.<p/>
 * 
 * Especially {@link #create(Class, ClassLoader, HessianSkeleton)} is supposed to be used in 
 * test-cases in order to specify a {@link HessianSkeleton}, acting as (fictional) server-side
 * instance.
 * 
 * @see HessianTestProxy
 * @see HessianProxyFactory
 * 
 * @author hessdroid@gmail.com
 *
 */
public class HessianTestProxyFactory extends HessianProxyFactory {

	/**
	 * Creates a new proxy for the specified service. This is the main method intented
	 * for testing the serialization/deserialization of this library.
	 * 
	 * @return a proxy to the object with the specified interface.
	 */
	public <T> T create(Class<T> api, ClassLoader loader, HessianSkeleton server) {
		
		if (api == null)
			throw new NullPointerException("api must not be null for HessianTestProxyFactory.create()");
		
		InvocationHandler handler = new HessianTestProxy(this, server);

		return (T) Proxy.newProxyInstance(loader, new Class[] { api, HessianRemoteObject.class }, handler);
	}
}
