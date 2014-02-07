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
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.ast.tests.api.TestReferenceTypes;
import org.ast.tests.api.TestType;
import org.ast.tests.base.HessianTest;
import org.junit.Test;

/**
 * {@link HessianTest} that is intented to test method calls with reference data types.
 * 
 * @author hessdroid@gmail.com
 */
public class ReferenceTypeTests extends HessianTest<TestReferenceTypes> {

	private static final Date testDate = new Date();
	
	@SuppressWarnings("serial")
	public ReferenceTypeTests() throws Exception {
		super(new TestReferenceTypes()  {
			public TestType getTestType() {
				return new TestType()  {
					public String getName() {
						return "name";
					}
				};
			}

			public Date getDate() {
				return testDate;
			}
		});
	}
	
	@Test public void callObject() throws Exception {
		assertNotNull(client().getTestType());
		assertEquals("name", client().getTestType().getName());
		assertEquals(testDate, client().getDate());
	}

}
