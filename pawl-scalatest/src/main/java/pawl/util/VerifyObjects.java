/*
 * Copyright 2015 Geeoz Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pawl.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * Provide simplification support for #equals(Object), #hashCode() and
 * #toString() verifications.
 *
 * @author Alex Voloshyn
 */
@Deprecated
public final class VerifyObjects {
    /**
     * The main object for checking.
     */
    private final transient Object primary;
    /**
     * An object to be compared with.
     */
    private transient Object secondary = new Object();

    /**
     * Default constructor for objects verification support.
     *
     * @param object an object
     */
    public VerifyObjects(final Object object) {
        primary = object;
    }

    /**
     * Sets an object to be compared with.
     *
     * @param object an object to be compared with
     */
    public void setCompareObject(final Object object) {
        secondary = object;
    }

    /**
     * Provide verification check for difference for the two objects.
     *
     * @param andHashCodes boolean flag for the hash codes check
     */
    public void verifyDifference(final boolean andHashCodes) {
        assertThat("An objects should be different.",
                primary.equals(secondary), is(equalTo(false)));
        if (andHashCodes) {
            assertThat("An objects should have different hash codes.",
                    primary.hashCode(), not(equalTo(secondary.hashCode())));
        }
    }

    /**
     * Provide verification check for equality for the two objects and their
     * hash codes.
     */
    public void verifyEquality() {
        assertThat("An objects should be the same.",
                primary.equals(secondary), is(equalTo(true)));
        assertThat("An objects should have the same hash codes.",
                primary.hashCode(), is(equalTo(secondary.hashCode())));
    }

    /**
     * Provide verification check for equality for the specified object with
     * java.lang.Object.
     * <p>
     * Equals and hashCodes should be different.
     */
    public void verifyInstanceOfCheck() {
        final Object object = new Object();
        assertThat("An object shouldn't be equals to new Object().",
                primary.equals(object), is(equalTo(false)));
        assertThat("An object should return the different hash codes.",
                primary.hashCode(), not(equalTo(object.hashCode())));
    }

    /**
     * Provide verification check for equality for the same object.
     * <p>
     * In this case object1 == object1 should be true and hash codes should be
     * the same, too.
     */
    public void verifyMemoryLinksEquality() {
        assertThat("An object should be equals to itself.",
                primary.equals(primary), is(equalTo(true)));
        assertThat("An object should return the same hash codes.",
                primary.hashCode(), is(equalTo(primary.hashCode())));
    }

    /**
     * Provide verification check for equality for the to string result with
     * expected to string value.
     *
     * @param string expected to string value
     */
    public void verifyToString(final String string) {
        assertThat("Strings should be the same.",
                primary.toString(), is(equalTo(string)));
    }
}
