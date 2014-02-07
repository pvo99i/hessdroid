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

import com.ast.util.CookieParser;
import com.ast.util.CookieParser.Cookie;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class CookieParserTests {

    @Test
    public void testWithCookieValueAndPath() throws Exception {

        Cookie c = CookieParser.parse("myhost", "Set-Cookie: cookie-value; path=path");


        assertEquals(c.host, "myhost");
        assertEquals(c.path, "path");
    }

    @Test
    public void testWithCompleteCookiePath() throws Exception {
        // Set-Cookie: cookie-value; expires=date; path=path; domain=domain-name; secure

        Cookie c = CookieParser.parse("myhost", "Set-Cookie: cookie-value; expires=date; path=path; domain=domain-name; secure");


        assertEquals(c.host, "myhost");
        assertEquals(c.path, "path");
        assertEquals(c.value, "cookie-value");
        assertEquals(c.expires, "date");
        assertEquals(c.domain, "domain-name");
        assertEquals(c.secure, true);
    }

    @Test
    public void testWithRealWorldCookie() throws Exception {

        // simple real-world cookie:
        Cookie c = CookieParser.parse("myhost", "Set-Cookie: JSESSIONID=9126DE0DA91BD7D3C1A1A0608EC66645; Path=/Server");
        assertEquals(c.path, "/Server");
        assertEquals(c.value, "JSESSIONID=9126DE0DA91BD7D3C1A1A0608EC66645");
        assertEquals(c.sessionId, "9126DE0DA91BD7D3C1A1A0608EC66645");

        // simple real-world cookie:
        c = CookieParser.parse("myhost", "Set-Cookie: JSESSIONID=A5BCBA5F3B50EC39E3CA4CE3EE86210C; Path=/");
        assertEquals(c.path, "/");
        assertEquals(c.value, "JSESSIONID=A5BCBA5F3B50EC39E3CA4CE3EE86210C");
        assertEquals(c.sessionId, "A5BCBA5F3B50EC39E3CA4CE3EE86210C");
    }

    @Test
    public void testEdgeCases() throws Exception {
        Cookie c;
        try {
            c = CookieParser.parse("myhost", null);
            assertEquals(c.host, "myhost"); // never executed
        } catch (Exception e) {
            assertTrue("Expect Exception to be thrown in CookieParser.parse()", true);
        }
        try {
            c = CookieParser.parse(null, "Set-Cookie: cookie-value; path=path");
            assertEquals(c.path, "path"); // never executed
        } catch (Exception e) {
            assertTrue("Expect Exception to be thrown in CookieParser.parse()", true);
        }

        try {
            c = CookieParser.parse("myhost", "");
            assertEquals(c.host, "myhost");
            assertEquals(c.value,"");
            c = CookieParser.parse("", "Set-Cookie: cookie-value; path=path");
            assertEquals(c.host, "");            
            assertEquals(c.value,"cookie-value");
            c = CookieParser.parse("myhost", "0123456789");
            assertEquals(c.value, "0123456789");
        } catch (Exception e) {
            assertTrue("Exception thrown in CookieParser.parse()", false);
        }
    }

}
