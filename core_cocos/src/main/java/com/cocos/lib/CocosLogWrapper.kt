package com.cocos.lib

object CocosLogWrapper : ILog {

    private var logImpl: ILog? = null

    override fun log(tag: String?, content: String?) {
        logImpl?.log(tag, content)
    }

    override fun reportScriptException(location: String?, message: String?, stack: String?) {
        logImpl?.reportScriptException(location, message, stack)
    }

    override fun log(content: String?) {
        logImpl?.log(content)
    }

    override fun logJs(tag: String?, content: String?) {
        logImpl?.logJs(tag, content)
    }

    fun injectLogImpl(impl: ILog) {
        logImpl = impl
    }

}

interface ILog {
    fun log(content: String?)
    fun log(tag: String?, content: String?)
    fun logJs(tag: String?, content: String?)
    fun reportScriptException(location: String?, message: String?, stack: String?)
}