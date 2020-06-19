package pw.yumc.JsonDoclet;

import javax.script.*;
import java.io.File;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;

/**
 * 喵式脚本引擎
 *
 * @author 喵♂呜
 * @since 2016年8月29日 上午7:51:43
 */
public class MiaoScriptEngine implements ScriptEngine, Invocable {
    private static MiaoScriptEngine DEFAULT;
    private static ScriptEngineManager manager;

    static {
        manager = new ScriptEngineManager(ClassLoader.getSystemClassLoader());
    }

    private ScriptEngine engine;

    public MiaoScriptEngine() {
        this("js");
    }

    public MiaoScriptEngine(final String engineType) {
        this(manager, engineType);
    }

    public MiaoScriptEngine(ScriptEngineManager engineManager) {
        this(engineManager, "js");
    }

    public MiaoScriptEngine(ScriptEngineManager engineManager, final String engineType) {
        try {
            engine = engineManager.getEngineByName(engineType);
        } catch (final NullPointerException ignored) {
        }
        if (engine == null) {
            String[] dirs = System.getProperty("java.ext.dirs").split(File.pathSeparator);
            for (String dir : dirs) {
                File nashorn = new File(dir, "nashorn.jar");
                if (nashorn.exists()) {
                    try {
                        Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                        // 设置方法的访问权限
                        method.setAccessible(true);
                        // 获取系统类加载器
                        URL url = nashorn.toURI().toURL();
                        method.invoke(Thread.currentThread().getContextClassLoader(), url);
                        engineManager = new ScriptEngineManager();
                        engine = engineManager.getEngineByName(engineType);
                    } catch (NoSuchMethodException | MalformedURLException | IllegalAccessException | InvocationTargetException | NullPointerException ignored) {
                    }
                    return;
                }
            }
            throw new UnsupportedOperationException("当前环境不支持 " + engineType + " 脚本类型!");
        }
    }

    public static Bindings getBindings() {
        return manager.getBindings();
    }

    public static void setBindings(Bindings bindings) {
        manager.setBindings(bindings);
    }

    public static MiaoScriptEngine getDefault() {
        if (DEFAULT == null) {
            DEFAULT = new MiaoScriptEngine();
        }
        return DEFAULT;
    }

    @Override
    public Bindings createBindings() {
        return new SimpleBindings(new HashMap<>(engine.getBindings(ScriptContext.GLOBAL_SCOPE)));
    }

    @Override
    public Object eval(final Reader reader) throws ScriptException {
        return engine.eval(reader);
    }

    @Override
    public Object eval(final Reader reader, final Bindings n) throws ScriptException {
        return engine.eval(reader, n);
    }

    @Override
    public Object eval(final Reader reader, final ScriptContext context) throws ScriptException {
        return engine.eval(reader, context);
    }

    @Override
    public Object eval(final String script) throws ScriptException {
        return engine.eval(script);
    }

    @Override
    public Object eval(final String script, final Bindings n) throws ScriptException {
        return engine.eval(script, n);
    }

    @Override
    public Object eval(final String script, final ScriptContext context) throws ScriptException {
        return engine.eval(script, context);
    }

    @Override
    public Object get(final String key) {
        return engine.get(key);
    }

    @Override
    public Bindings getBindings(final int scope) {
        return engine.getBindings(scope);
    }

    @Override
    public ScriptContext getContext() {
        return engine.getContext();
    }

    @Override
    public void setContext(final ScriptContext context) {
        engine.setContext(context);
    }

    @Override
    public ScriptEngineFactory getFactory() {
        return engine.getFactory();
    }

    @Override
    public <T> T getInterface(final Class<T> clasz) {
        return ((Invocable) engine).getInterface(clasz);
    }

    @Override
    public <T> T getInterface(final Object thiz, final Class<T> clasz) {
        return ((Invocable) engine).getInterface(thiz, clasz);
    }

    @Override
    public Object invokeFunction(final String name, final Object... args) throws ScriptException, NoSuchMethodException {
        return ((Invocable) engine).invokeFunction(name, args);
    }

    @Override
    public Object invokeMethod(final Object thiz, final String name, final Object... args) throws ScriptException, NoSuchMethodException {
        return ((Invocable) engine).invokeMethod(thiz, name, args);
    }

    @Override
    public void put(final String key, final Object value) {
        engine.put(key, value);
    }

    @Override
    public void setBindings(final Bindings bindings, final int scope) {
        engine.setBindings(bindings, scope);
    }
}
