package vn.zalopay.benchmark.util;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.engine.util.ValueReplacer;
import org.apache.jmeter.gui.GuiPackage;
import org.apache.jmeter.gui.JMeterGUIComponent;
import org.apache.jmeter.testelement.TestElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * JMeter user variable load util. Realize that user variables can be easily used when operating in
 * Gui or when the Test Plan is not running.
 *
 * @author ylyue
 * @since 2021/12/20
 */
public class JMeterVariableUtils {

    private static final Logger log = LoggerFactory.getLogger(JMeterVariableUtils.class);

    private JMeterVariableUtils() {
        throw new IllegalStateException("Utility class");
    }

    /** Gets the value of a private member variable. */
    public static Object getPrivateField(Object instance, String filedName)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = instance.getClass().getDeclaredField(filedName);
        field.setAccessible(true);
        return field.get(instance);
    }

    /**
     * Get a ValueReplacer for the test tree.
     *
     * @return a ValueReplacer configured for the test tree
     */
    public static ValueReplacer getReplacer() {
        GuiPackage guiPackage = GuiPackage.getInstance();
        if (guiPackage == null) {
            return new ValueReplacer();
        }

        ValueReplacer replacer = guiPackage.getReplacer();
        try {
            Map<TestElement, JMeterGUIComponent> nodesToGui =
                    (Map<TestElement, JMeterGUIComponent>)
                            getPrivateField(guiPackage, "nodesToGui");
            nodesToGui.forEach(
                    (key, value) -> {
                        if (key instanceof Arguments) {
                            replacer.addVariables(((Arguments) key).getArgumentsAsMap());
                        }
                    });
        } catch (Exception e) {
            log.warn("Value replacer in JMeter has error", e);
        }

        return replacer;
    }

    /**
     * Replaces ${key} by value extracted from {@link org.apache.jmeter.threads.JMeterVariables
     * JMeterVariables} if any
     *
     * @param el {@link TestElement} in which values should be replaced
     */
    public static void undoVariableReplacement(TestElement el) {
        try {
            getReplacer().undoReverseReplace(el);
        } catch (Exception e) {
            log.warn("Value replacer in JMeter has error", e);
        }
    }
}
