/****************************************************************************
 * Copyright (c) 2018-2021 Xiamen Yaji Software Co., Ltd.
 *
 * http://www.cocos.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated engine source code (the "Software"), a limited,
 * worldwide, royalty-free, non-assignable, revocable and non-exclusive license
 * to use Cocos Creator solely to develop games on your target platforms. You shall
 * not use Cocos Creator software for developing other software or tools that's
 * used for developing games. You are not granted to publish, distribute,
 * sublicense, and/or sell copies of Cocos Creator.
 *
 * The software or tools in this License Agreement are licensed, not sold.
 * Xiamen Yaji Software Co., Ltd. reserves all rights not expressly granted to you.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.cocos.lib

import java.util.*

object JsbBridgeWrapper {
    //Interface for listener, should be implemented and dispatched
    interface OnScriptEventListener {
        fun onScriptEvent(arg: String?)
    }

    interface GlobalOnScriptEventListener {
        fun onScriptEvent(arg1: String?, arg2: String?)
    }

    /**
     * Add a listener to specified event, if the event does not exist, the wrapper will create one. Concurrent listener will be ignored
     */
    fun addScriptEventListener(eventName: String, listener: OnScriptEventListener) {
        if (eventMap[eventName] == null) {
            eventMap[eventName] = ArrayList()
        }
        eventMap[eventName]!!.add(listener)
    }

    /**
     * Remove listener for specified event, concurrent event will be deleted. Return false only if the event does not exist
     */
    fun removeScriptEventListener(eventName: String, listener: OnScriptEventListener): Boolean {
        val arr = eventMap[eventName] ?: return false
        arr.remove(listener)
        return true
    }

    /**
     * Remove all listener for event specified.
     */
    fun removeAllListenersForEvent(eventName: String) {
        eventMap.remove(eventName)
    }

    /**
     * Remove all event registered. Use it carefully!
     */
    fun removeAllListeners() {
        eventMap.clear()
    }

    /**
     * Dispatch the event with argument, the event should be registered in javascript, or other script language in future.
     */
    fun dispatchEventToScript(eventName: String?, arg: String?) {
        JsbBridge.sendToScript(eventName, arg)
    }

    /**
     * Dispatch the event which is registered in javascript, or other script language in future.
     */
    fun dispatchEventToScript(eventName: String?) {
        JsbBridge.sendToScript(eventName)
    }

    private val globalListeners = mutableListOf<GlobalOnScriptEventListener>()
    fun addGlobalListener(listener: GlobalOnScriptEventListener) {
        if (!globalListeners.contains(listener)) {
            globalListeners.add(listener)
        }
    }
    fun removeGlobalListener(listener: GlobalOnScriptEventListener) {
        globalListeners.remove(listener)
    }

    private val eventMap = HashMap<String, ArrayList<OnScriptEventListener>?>()
    private fun triggerEvents(eventName: String, arg: String) {
        val arr = eventMap[eventName] ?: return
        for (m in arr) {
            m.onScriptEvent(arg)
        }
    }

    init {
        JsbBridge.setCallback { arg0, arg1 ->
            globalListeners.forEach {
                it.onScriptEvent(arg0, arg1)
            }
            triggerEvents(arg0, arg1)
        }
    }
}