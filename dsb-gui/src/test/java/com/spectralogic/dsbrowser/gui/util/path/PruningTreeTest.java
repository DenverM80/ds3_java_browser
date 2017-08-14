package com.spectralogic.dsbrowser.gui.util.path;/*
 * ****************************************************************************
 *    Copyright 2014-2017 Spectra Logic Corporation. All Rights Reserved.
 *    Licensed under the Apache License, Version 2.0 (the "License"). You may not use
 *    this file except in compliance with the License. A copy of the License is located at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *    or in the "license" file accompanying this file.
 *    This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 *    CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *    specific language governing permissions and limitations under the License.
 *  ****************************************************************************
 */
import org.junit.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.CoreMatchers.is;

public class PruningTreeTest {
    private final PruningTree<String, String> pt = new PruningTree<>();

    @Test
    public void EmptyTreeIsEmpty() {
        assertThat(pt.toList().size(), is(0));
    }

    @Test
    public void SingleItemTest() {
        pt.add("foo".split("/"), "bar");
        assertThat(pt.toList(), containsInAnyOrder("bar"));
    }
}
