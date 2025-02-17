/*
 * Copyright (c) 2019-2022 GeyserMC. http://geysermc.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @author GeyserMC
 * @link https://github.com/GeyserMC/Geyser
 */

package org.geysermc.geyser.api.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation used to signify the given method is
 * an {@link Event}. Only should be applied to methods
 * where the class containing them is designated for
 * events specifically.
 *
 * When using {@link EventBus#subscribe}, this annotation should
 * not be applied whatsoever as it will have no use and potentially
 * throw errors due to it being used wrongly.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Subscribe {

    /**
     * The {@link PostOrder} of the event
     *
     * @return the post order of the event
     */
    Subscribe.PostOrder postOrder() default PostOrder.NORMAL;

    /**
     * Represents the post order of an event.
     */
    enum PostOrder {

        /**
         * The lowest priority. Called first to
         * allow for other events to customize
         * the outcome
         */
        FIRST(net.kyori.event.PostOrders.FIRST),

        /**
         * The second lowest priority.
         */
        EARLY(net.kyori.event.PostOrders.EARLY),

        /**
         * Normal priority. Event is neither
         * important nor unimportant
         */
        NORMAL(net.kyori.event.PostOrders.NORMAL),

        /**
         * The second highest priority
         */
        LATE(net.kyori.event.PostOrders.LATE),

        /**
         * The highest of importance! Event is called
         * last and has the final say in the outcome
         */
        LAST(net.kyori.event.PostOrders.LAST);

        private final int postOrder;

        PostOrder(int postOrder) {
            this.postOrder = postOrder;
        }

        /**
         * The numerical post order value.
         *
         * @return numerical post order value
         */
        public int postOrder() {
            return this.postOrder;
        }
    }
}