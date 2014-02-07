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
package org.ast.tests;

import static org.junit.Assert.assertEquals;

import org.ast.tests.api.TestPrimitiveTypes;
import org.ast.tests.base.HessianTest;
import org.junit.Test;

/**
 * {@link HessianTest} that is intended to test method calls with primitive data types.
 * 
 * @author hessdroid@gmail.com
 */
public class PrimitiveTypeTests extends HessianTest<TestPrimitiveTypes> {
	
	@SuppressWarnings("serial")
	public PrimitiveTypeTests() throws Exception {
		super(new TestPrimitiveTypes()  {
			
			public boolean getBoolean() {
				return true;
			}

			public int getInt() {
				return 0;
			}

			public Integer getInteger() {
				return 0;
			}

			public String getString() {
				return "string";
			}
		});
	}
	
	@Test public void callInt() throws Exception {
		assertEquals(0, client().getInt());
	}
	
	@Test public void callString() throws Exception {
		assertEquals("string", client().getString());
	}
	
	@Test public void callBoolean() throws Exception {
		assertEquals(true, client().getBoolean());
	}
}
