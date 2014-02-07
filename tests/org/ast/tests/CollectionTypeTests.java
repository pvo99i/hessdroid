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

import org.ast.tests.api.TestCollectionTypes;
import org.ast.tests.base.HessianTest;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * {@link HessianTest} that is intended to test method calls with collection data types.
 *
 * @author <a href="mailto:wolf@wolfpaulus.com">Wolf Paulus</a>
 */
public class CollectionTypeTests extends HessianTest<TestCollectionTypes> {

    @SuppressWarnings("serial")
    public CollectionTypeTests() throws Exception {
        super(new TestCollectionTypes() {

            public List<Number> getList() {
                List<Number> list = new ArrayList<Number>();
                list.add(1);
                list.add(2f);
                list.add(3d);
                return list;
            }

            public Map<Integer, Date> getMap() {
                Map<Integer, Date> map = new HashMap<Integer, Date>();
                map.put(1, new Date(1l));
                map.put(2, new Date(2l));
                map.put(3, new Date(3l));
                return map;
            }

            public Set<String> getSet() {
                Set<String> set = new HashSet<String>();
                set.add("1");
                set.add("2");
                set.add("3");
                return set;
            }

            public Collection<Iterable<?>> getCollection() {
                Collection<Iterable<?>> c = new ArrayList<Iterable<?>>();
                c.add(getList());
                c.add(getSet());
                return c;
            }
        });
    }

    @Test
    public void callSet() throws Exception {
        Set<String> set = client().getSet();
        int k = 0;
        for (String s : set) {
            if ("1".equals(s)) k++;
            if ("2".equals(s)) k++;
            if ("3".equals(s)) k++;
        }
        assertEquals(3, k);
    }

    @Test
    public void callList() throws Exception {
        Iterator<Number> it = client().getList().iterator();
        Number n = it.next();
        assertTrue(Integer.class.equals(n.getClass()));
        assertTrue(new Integer(1).equals(1));

        n = it.next();
        assertTrue(Double.class.equals(n.getClass()));
        assertTrue(new Float(2).equals(2f));

        n = it.next();
        assertTrue(Double.class.equals(n.getClass()));
        assertTrue(new Double(3).equals(3d));

        assertTrue(!it.hasNext());
    }

    @Test
    public void callMap() throws Exception {
        Map<Integer, Date> map = client().getMap();
        assertEquals(3, map.size());
        assertEquals(new Date(1l), map.get(1));
        assertEquals(new Date(2l), map.get(2));
        assertEquals(new Date(3l), map.get(3));
    }

    @Test
    public void callCollection() throws Exception {
        Collection<Iterable<?>> c = client().getCollection();
        Iterator<?> it = c.iterator();
        Object obj = it.next();
        assertTrue(ArrayList.class.equals(obj.getClass()));
        obj = it.next();
        assertTrue(HashSet.class.equals(obj.getClass()));
        assertTrue(!it.hasNext());
    }
}