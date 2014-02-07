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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.caucho.hessian.client.HessianProxy;
import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.HessianDebugInputStream;
import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.caucho.hessian.io.HessianProtocolException;
import com.caucho.services.server.AbstractSkeleton;

/**
 * Proxy implementation for Hessian test clients. It implements {@link InvocationHandler} in order
 * to act as proxy class for arbitrary interfaces. In contrast to {@link HessianProxy} this proxy's
 * constructor takes a {@link HessianSkeleton} that is used as fictional server-side service instance.<p/>
 * 
 * {@link #sendRequest(String, Object[])} forwards method invocations directly to the given {@link HessianSkeleton} without
 * using a transport protocol, indeed {@link ByteArrayInputStream} and {@link ByteArrayOutputStream} are used to push/pull hessian
 * byte array data.
 * 
 * @see InvocationHandler
 * @see HessianProxy
 * @see HessianSkeleton
 * 
 * @author hessdroid@gmail.com
 */
public class HessianTestProxy implements InvocationHandler {
	
	private static final Logger log = Logger.getLogger(HessianTestProxy.class.getName());

	protected HessianTestProxyFactory _factory;
	
	private WeakHashMap<Method, String> _mangleMap = new WeakHashMap<Method, String>();
	private HessianSkeleton _skeletonServer;
	
	/**
	 * Package protected constructor for factory
	 */
	HessianTestProxy(HessianTestProxyFactory factory, HessianSkeleton skeletonServer) {
		_factory = factory;
		_skeletonServer = skeletonServer;
	}

	/**
	 * Handles the object invocation.
	 * 
	 * @param proxy
	 *            the proxy object to invoke
	 * @param method
	 *            the method to call
	 * @param args
	 *            the arguments to the proxy object
	 */
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		String mangleName;

		synchronized (_mangleMap) {
			mangleName = _mangleMap.get(method);
		}

		if (mangleName == null) {
			String methodName = method.getName();
			Class[] params = method.getParameterTypes();

			// equals and hashCode are special cased
			if (methodName.equals("equals") && params.length == 1
					&& params[0].equals(Object.class)) {
				Object value = args[0];
				if (value == null || !Proxy.isProxyClass(value.getClass()))
					return Boolean.FALSE;

				HessianTestProxy handler = (HessianTestProxy) Proxy.getInvocationHandler(value);

				return Boolean.valueOf(equals(handler));
			} else if (methodName.equals("hashCode") && params.length == 0)
				return Integer.valueOf(hashCode());
			else if (methodName.equals("getHessianType"))
				return proxy.getClass().getInterfaces()[0].getName();
			else if (methodName.equals("getHessianURL"))
				return toString();
			else if (methodName.equals("toString") && params.length == 0)
				return "HessianTestProxy[" + hashCode() + "]";

			if (!_factory.isOverloadEnabled())
				mangleName = method.getName();
			else
				mangleName = mangleName(method);

			synchronized (_mangleMap) {
				_mangleMap.put(method, mangleName);
			}
		}

		InputStream is = null;

		try {
			if (log.isLoggable(Level.FINER))
				log.finer("Hessian[" + toString() + "] calling " + mangleName);

			is = sendRequest(mangleName, args);

			if (log.isLoggable(Level.FINEST)) {
				PrintWriter dbg = new PrintWriter(new LogWriter(log));
				is = new HessianDebugInputStream(is, dbg);
			}

			AbstractHessianInput in = _factory.getHessianInput(is);

			in.startReply();
			Object value = in.readObject(method.getReturnType());
			in.completeReply();

			return value;
		} catch (HessianProtocolException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (Exception e) {
				log.log(Level.FINE, e.toString(), e);
			}
		}
	}

	protected String mangleName(Method method) {
		Class[] param = method.getParameterTypes();

		if (param == null || param.length == 0)
			return method.getName();
		else
			return AbstractSkeleton.mangleName(method, false);
	}
	
	protected InputStream sendRequest(String methodName, Object[] args) throws Exception {
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		
//		if (log.isLoggable(Level.FINEST)) {
//			PrintWriter dbg = new PrintWriter(new LogWriter(log));
//			os = new HessianDebugOutputStream(os, dbg);
//		}

		AbstractHessianOutput out = _factory.getHessianOutput(os);

		out.call(methodName, args);
		out.flush();
		
		// Invoke the _skeletonServer - to see if serialization/deserialization works
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		_skeletonServer.invoke(new HessianInput(new ByteArrayInputStream(os.toByteArray())), new HessianOutput(result));
		return new ByteArrayInputStream(result.toByteArray()); 
	}

	static class LogWriter extends Writer {
		private Logger _log;
		private Level _level = Level.FINEST;
		private StringBuilder _sb = new StringBuilder();

		LogWriter(Logger log) {
			_log = log;
		}

		public void write(char ch) {
			if (ch == '\n' && _sb.length() > 0) {
				_log.fine(_sb.toString());
				_sb.setLength(0);
			} else
				_sb.append((char) ch);
		}

		public void write(char[] buffer, int offset, int length) {
			for (int i = 0; i < length; i++) {
				char ch = buffer[offset + i];

				if (ch == '\n' && _sb.length() > 0) {
					_log.log(_level, _sb.toString());
					_sb.setLength(0);
				} else
					_sb.append((char) ch);
			}
		}

		public void flush() {
		}

		public void close() {
			if (_sb.length() > 0)
				_log.log(_level, _sb.toString());
		}
	}
}
